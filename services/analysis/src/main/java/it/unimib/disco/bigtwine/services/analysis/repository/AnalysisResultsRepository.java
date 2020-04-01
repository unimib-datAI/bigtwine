package it.unimib.disco.bigtwine.services.analysis.repository;

import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalysisResultsRepository extends MongoRepository<AnalysisResult<?>, String> {
    List<AnalysisResult> findByAnalysisId(String id);
    Page<AnalysisResult> findByAnalysisId(String id, Pageable page);
    long countByAnalysisId(String id);
}
