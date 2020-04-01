package it.unimib.disco.bigtwine.services.analysis.web.api;

import it.unimib.disco.bigtwine.services.analysis.domain.Analysis;
import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisExport;
import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisResult;
import it.unimib.disco.bigtwine.services.analysis.domain.mapper.AnalysisMapper;
import it.unimib.disco.bigtwine.services.analysis.domain.mapper.AnalysisResultMapper;
import it.unimib.disco.bigtwine.services.analysis.domain.mapper.AnalysisResultMapperLocator;
import it.unimib.disco.bigtwine.services.analysis.repository.AnalysisResultsRepository;
import it.unimib.disco.bigtwine.services.analysis.service.AnalysisService;
import it.unimib.disco.bigtwine.services.analysis.web.api.errors.BadRequestException;
import it.unimib.disco.bigtwine.services.analysis.web.api.errors.ForbiddenException;
import it.unimib.disco.bigtwine.services.analysis.web.api.errors.NoSuchEntityException;
import it.unimib.disco.bigtwine.services.analysis.web.api.model.AnalysisExportDTO;
import it.unimib.disco.bigtwine.services.analysis.web.api.model.AnalysisResultDTO;
import it.unimib.disco.bigtwine.services.analysis.web.api.model.AnalysisResultsCount;
import it.unimib.disco.bigtwine.services.analysis.web.api.model.PagedAnalysisResults;
import it.unimib.disco.bigtwine.services.analysis.service.AnalysisAuthorizationManager;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.NativeWebRequest;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AnalysisResultsApiDelegateImpl implements AnalysisResultsApiDelegate {


    private final Logger log = LoggerFactory.getLogger(AnalysesApiDelegateImpl.class);
    private final NativeWebRequest request;
    private final AnalysisService analysisService;
    private final AnalysisAuthorizationManager analysisAuthManager;
    private final AnalysisResultsRepository resultsRepository;
    private final AnalysisResultMapperLocator resultMapperLocator;
    private final MongoTemplate mongoTemplate;


    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public AnalysisResultsApiDelegateImpl(
        NativeWebRequest request,
        AnalysisService analysisService,
        AnalysisAuthorizationManager analysisAuthManager,
        AnalysisResultsRepository resultsRepository,
        AnalysisResultMapperLocator resultMapperLocator,
        MongoTemplate mongoTemplate) {
        this.request = request;
        this.analysisService = analysisService;
        this.analysisAuthManager = analysisAuthManager;
        this.resultsRepository = resultsRepository;
        this.resultMapperLocator = resultMapperLocator;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Optional<NativeWebRequest> getRequest() {
        return Optional.ofNullable(request);
    }

    private Analysis getAnalysisById(String analysisId) {
        Analysis analysis = this.analysisService.findOne(analysisId).orElseThrow(() ->
            new NoSuchEntityException(String.format("Analysis with id '%s' not found", analysisId))
        );

        analysisAuthManager.checkAnalysisOwnership(analysis, AnalysisAuthorizationManager.AccessType.READ);

        return analysis;
    }

    private ResponseEntity<PagedAnalysisResults> buildPagedResultsResponse(Analysis analysis, Page<AnalysisResult> pageObj) {
        List<AnalysisResult> results = pageObj.getContent();
        List<Object> resultsDtos = new ArrayList<>(results.size());
        results.forEach((AnalysisResult result) -> {
            if (result.getPayload() == null) {
                log.debug("Payload missing");
                return;
            }

            AnalysisResultMapper mapper = this.resultMapperLocator
                .getMapper(result.getPayload().getClass());

            if (mapper == null) {
                log.debug("Missing mapper for payload type: {}", result.getPayload().getClass());
                return;
            }

            AnalysisResultDTO resultDto = mapper.map(result);

            resultsDtos.add(resultDto);
        });

        PagedAnalysisResults pagedResults = new PagedAnalysisResults();
        pagedResults
            .objects(resultsDtos)
            .analysisId(analysis.getId())
            .page(pageObj.getPageable().getPageNumber())
            .pageSize(pageObj.getPageable().getPageSize())
            .count(pageObj.getNumberOfElements())
            .totalCount(pageObj.getTotalElements());

        return ResponseEntity.ok(pagedResults);
    }

    @Override
    public ResponseEntity<PagedAnalysisResults> getAnalysisResultsV1(String analysisId, Integer pageNum, Integer pageSize) {
        Analysis analysis = this.getAnalysisById(analysisId);
        Pageable page = PageRequest.of(pageNum, pageSize);

        Page<AnalysisResult> pageObj = resultsRepository.findByAnalysisId(analysis.getId(), page);

        return this.buildPagedResultsResponse(analysis, pageObj);
    }

    @Override
    public ResponseEntity<PagedAnalysisResults> searchAnalysisResultsV1(String analysisId, String body, Integer pageNum, Integer pageSize) {
        Analysis analysis = this.getAnalysisById(analysisId);
        Pageable page = PageRequest.of(pageNum, pageSize);
        BasicQuery query = new BasicQuery(body);
        query.with(page);
        query.addCriteria(Criteria.where("analysis.$id").is(new ObjectId(analysis.getId())));

        log.debug("Results search query: {}", query.getQueryObject().toJson());

        List<AnalysisResult> results = this.mongoTemplate.find(query, AnalysisResult.class);
        Page<AnalysisResult> pageObj = PageableExecutionUtils.getPage(results, page, () ->
            this.mongoTemplate.count(query, AnalysisResult.class));

        return this.buildPagedResultsResponse(analysis, pageObj);
    }

    @Override
    public ResponseEntity<AnalysisResultsCount> countAnalysisResultsV1(String analysisId) {
        Analysis analysis = this.getAnalysisById(analysisId);
        long count = resultsRepository.countByAnalysisId(analysisId);

        AnalysisResultsCount body = new AnalysisResultsCount()
            .analysisId(analysis.getId())
            .count(count);

        return ResponseEntity.ok(body);
    }

    @Override
    public ResponseEntity<AnalysisExportDTO> startAnalysisResultsExportV1(String analysisId, AnalysisExportDTO analysisExportDTO) {
        Analysis analysis = this.getAnalysisById(analysisId);
        boolean isOwner = analysisAuthManager.checkAnalysisOwnership(analysis, AnalysisAuthorizationManager.AccessType.READ);

        if (!isOwner) {
            throw new ForbiddenException("Only the analysis owner can start the export");
        }

        AnalysisExport export = AnalysisMapper.INSTANCE.analysisExportFromAnalysisExportDTO(analysisExportDTO);

        try {
            export = this.analysisService.startAnalysisResultsExport(analysis.getId(), export);
        } catch (ValidationException e) {
            throw new BadRequestException(e.getMessage());
        }

        AnalysisExportDTO exportDTO = AnalysisMapper.INSTANCE.analysisExportDTOFromAnalysisExport(export);

        return ResponseEntity.ok(exportDTO);
    }
}
