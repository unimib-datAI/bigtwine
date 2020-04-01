package it.unimib.disco.bigtwine.services.analysis.repository;

import it.unimib.disco.bigtwine.services.analysis.domain.Analysis;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisInputType;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisStatus;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisType;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;


/**
 * Spring Data MongoDB repository for the Analysis entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AnalysisRepository extends MongoRepository<Analysis, String> {
    List<Analysis> findByOwnerUid(String ownerId);
    Page<Analysis> findByOwnerUid(String ownerId, Pageable page);
    Page<Analysis> findByType(AnalysisType analysisType, Pageable page);
    Page<Analysis> findByOwnerUidOrVisibility(String ownerId, AnalysisVisibility visibility, Pageable page);
    Page<Analysis> findByTypeAndVisibility(AnalysisType analysisType, AnalysisVisibility visibility, Pageable page);
    Page<Analysis> findByTypeAndOwnerUid(AnalysisType analysisType, String ownerId, Pageable page);
    Long countByOwnerUidAndStatusAndTypeAndInputType(String ownerId, AnalysisStatus status, AnalysisType analysisType, AnalysisInputType inputType);
    Long countByOwnerUidAndStatusAndType(String ownerId, AnalysisStatus status, AnalysisType analysisType);
    Long countByOwnerUidAndStatus(String ownerId, AnalysisStatus status);

    @Query("{$and: [{'type' : ?1}, {$or: [{'owner.uid': ?1}, {'visibility': 'PUBLIC'}]}]}")
    Page<Analysis> findVisibleByType(String ownerId, AnalysisType analysisType, Pageable page);
    long countByOwnerUid(String owner);
    long countByStatus(AnalysisStatus status);
    Stream<Analysis> findByStatus(AnalysisStatus status, Pageable page);
}
