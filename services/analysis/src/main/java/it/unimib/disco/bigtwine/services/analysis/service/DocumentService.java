package it.unimib.disco.bigtwine.services.analysis.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.MongoGridFSException;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import it.unimib.disco.bigtwine.services.analysis.domain.Document;
import it.unimib.disco.bigtwine.services.analysis.domain.User;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {
    private final GridFsTemplate gridFsTemplate;
    private final MongoDbFactory dbFactory;

    public DocumentService(GridFsTemplate gridFsTemplate, MongoDbFactory dbFactory) {
        this.gridFsTemplate = gridFsTemplate;
        this.dbFactory = dbFactory;
    }

    private static class MetadataKey {
        private static final String USERID = "userid";
        private static final String USERNAME = "username";
        private static final String DOCTYPE = "doctype";
        private static final String ANALYSISID = "analysisid";
        private static final String ANALYSISTYPE = "analysistype";
        private static final String CATEGORY = "category";
    }

    private Document createDocumentFromFile(GridFSFile file) {
        GridFsResource resource = new GridFsResource(file);
        String contentType;
        try {
            contentType = resource.getContentType();
        } catch (MongoGridFSException e) {
            contentType = null;
        }

        return new Document()
            .id(file.getObjectId().toString())
            .type(file.getMetadata().getString(MetadataKey.DOCTYPE))
            .filename(file.getFilename())
            .size(file.getLength())
            .user(new User()
                .uid(file.getMetadata().getString(MetadataKey.USERID))
                .username(file.getMetadata().getString(MetadataKey.USERNAME)))
            .category(file.getMetadata().getString(MetadataKey.CATEGORY))
            .analysisType(file.getMetadata().getString(MetadataKey.ANALYSISTYPE))
            .analysisId(file.getMetadata().getString(MetadataKey.ANALYSISID))
            .uploadDate(file.getUploadDate().toInstant())
            .contentType(contentType);
    }

    private GridFSBucket getGridFs(String bucket) {
        MongoDatabase db = dbFactory.getDb();
        return bucket == null ? GridFSBuckets.create(db) : GridFSBuckets.create(db, bucket);
    }

    private GridFSFile getGridFSFile(ObjectId objectId) {
        Query query = new Query(Criteria.where("_id").is(objectId));
        return gridFsTemplate.findOne(query);
    }

    public Optional<Document> findOne(String id) {
        return this.findOne(new ObjectId(id));
    }

    public Optional<Document> findOne(ObjectId objectId) {
        GridFSFile file = getGridFSFile(objectId);

        if (file == null) {
            return Optional.empty();
        }

        return Optional.of(createDocumentFromFile(file));
    }

    public List<Document> findBy(String userId, String documentType, String analysisType, String category, Pageable page) {
        Query query = new Query(GridFsCriteria.whereMetaData(MetadataKey.USERID).is(userId));

        if (documentType != null) {
            query.addCriteria(GridFsCriteria.whereMetaData(MetadataKey.DOCTYPE).is(documentType));
        }

        if (analysisType != null) {
            query.addCriteria(GridFsCriteria.whereMetaData(MetadataKey.ANALYSISTYPE).is(analysisType));
        }

        if (category != null) {
            query.addCriteria(GridFsCriteria.whereMetaData(MetadataKey.CATEGORY).is(category));
        }

        query.with(page);

        GridFSFindIterable files = gridFsTemplate.find(query);
        final List<Document> documents = new ArrayList<>();
        files
            .iterator()
            .forEachRemaining((GridFSFile f) -> documents.add(this.createDocumentFromFile(f)));

        return documents;
    }

    public Document uploadFromStream(InputStream stream, Document doc) {
        DBObject metadata = new BasicDBObject();

        if (doc.getUser() != null) {
            metadata.put(MetadataKey.USERID, doc.getUser().getUid());
            metadata.put(MetadataKey.USERNAME, doc.getUser().getUsername());
        }

        if (StringUtils.isNotBlank(doc.getType())) {
            metadata.put(MetadataKey.DOCTYPE, doc.getType());
        }

        if (StringUtils.isNotBlank(doc.getAnalysisType())) {
            metadata.put(MetadataKey.ANALYSISTYPE, doc.getAnalysisType());
        }

        if (StringUtils.isNotBlank(doc.getAnalysisId())) {
            metadata.put(MetadataKey.ANALYSISID, doc.getAnalysisId());
        }

        if (StringUtils.isNotBlank(doc.getCategory())) {
            metadata.put(MetadataKey.CATEGORY, doc.getCategory());
        }

        ObjectId objectId = gridFsTemplate.store(stream, doc.getFilename(), doc.getContentType(), metadata);
        doc.setId(objectId.toHexString());

        return doc;
    }

    public Optional<Resource> getDownloadableResource(ObjectId objectId) {
        GridFSFile file = getGridFSFile(objectId);

        if ( file == null) {
            return Optional.empty();
        }

        InputStream stream = this.getGridFs(null).openDownloadStream(file.getObjectId());

        return Optional.of(new GridFsResource(file, stream));
    }

    public Optional<Resource> getDownloadableResource(String objectId) {
        return getDownloadableResource(new ObjectId(objectId));
    }

    public Optional<Resource> getDownloadableResource(Document doc) {
        return getDownloadableResource(doc.getId());
    }
}
