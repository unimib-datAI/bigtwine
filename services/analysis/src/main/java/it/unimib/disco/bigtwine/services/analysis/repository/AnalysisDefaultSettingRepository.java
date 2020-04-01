package it.unimib.disco.bigtwine.services.analysis.repository;

import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisDefaultSetting;
import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisSetting;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisInputType;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisType;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for the AnalysisDefaultSetting entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AnalysisDefaultSettingRepository extends MongoRepository<AnalysisDefaultSetting, String> {
    @Query("{}")
    Page<AnalysisDefaultSetting> findAllWithEagerRelationships(Pageable pageable);

    @Query("{}")
    List<AnalysisDefaultSetting> findAllWithEagerRelationships();

    @Query("{'id': ?0}")
    Optional<AnalysisDefaultSetting> findOneWithEagerRelationships(String id);

    @Query("{$and: [" +
            "{'setting.$id': ?0}, " +
            "{$or: [{analysis_type: null}, {analysis_type: ?1}]}," +
            "{$or: [{analysis_input_types: []}, {analysis_input_types: {$in: [ ?2 ]}}]}," +
            "{$or: [{user_roles: []}, {user_roles: {$in: ?3}}]}" +
        "]}")
    List<AnalysisDefaultSetting> findBySettingAndRestrictions(
        ObjectId settingId,
        AnalysisType analysisType,
        AnalysisInputType inputType,
        List<String> roles,
        Sort sort
    );

    default Optional<AnalysisDefaultSetting> findOneBySettingAndRestrictions(AnalysisSetting setting, AnalysisType analysisType, AnalysisInputType inputType, List<String> roles) {
        List<AnalysisDefaultSetting> defaults = findBySettingAndRestrictions(
            new ObjectId(setting.getId()),
            analysisType, inputType, roles,
            Sort.by(Sort.Direction.DESC, "priority"));

        if (defaults.size() > 0) {
            return Optional.of(defaults.get(0));
        } else {
            return Optional.empty();
        }
    }
}
