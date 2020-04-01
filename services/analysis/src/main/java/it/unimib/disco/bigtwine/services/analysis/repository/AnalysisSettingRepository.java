package it.unimib.disco.bigtwine.services.analysis.repository;

import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisSetting;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisSettingVisibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for the AnalysisSetting entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AnalysisSettingRepository extends MongoRepository<AnalysisSetting, String> {
    @Query("{}")
    Page<AnalysisSetting> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    List<AnalysisSetting> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Optional<AnalysisSetting> findOneWithEagerRelationships(String id);

    Optional<AnalysisSetting> findOneById(String id);

    Optional<AnalysisSetting> findOneByName(String name);

    List<AnalysisSetting> findByVisibility(AnalysisSettingVisibility visibility);
}
