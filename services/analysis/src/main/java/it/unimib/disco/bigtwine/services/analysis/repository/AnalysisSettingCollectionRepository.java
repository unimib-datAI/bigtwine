package it.unimib.disco.bigtwine.services.analysis.repository;

import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisSettingCollection;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisInputType;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisType;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data MongoDB repository for the AnalysisSettingCollection entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AnalysisSettingCollectionRepository extends MongoRepository<AnalysisSettingCollection, String> {
    Optional<AnalysisSettingCollection> findOneByAnalysisTypeAndAnalysisInputType(AnalysisType type, AnalysisInputType inputType);
}
