package it.unimib.disco.bigtwine.services.analysis.service;

import it.unimib.disco.bigtwine.services.analysis.domain.*;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisInputType;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisSettingVisibility;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisStatus;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisType;
import it.unimib.disco.bigtwine.services.analysis.repository.AnalysisDefaultSettingRepository;
import it.unimib.disco.bigtwine.services.analysis.repository.AnalysisSettingCollectionRepository;
import it.unimib.disco.bigtwine.services.analysis.repository.AnalysisSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing AnalysisSetting.
 */
@Service
public class AnalysisSettingService {

    private final Logger log = LoggerFactory.getLogger(AnalysisSettingService.class);

    private final AnalysisSettingRepository analysisSettingRepository;
    private final AnalysisDefaultSettingRepository analysisDefaultSettingRepository;
    private final AnalysisSettingCollectionRepository settingCollectionRepository;

    public AnalysisSettingService(
        AnalysisSettingRepository analysisSettingRepository,
        AnalysisDefaultSettingRepository analysisDefaultSettingRepository,
        AnalysisSettingCollectionRepository settingCollectionRepository) {
        this.analysisSettingRepository = analysisSettingRepository;
        this.analysisDefaultSettingRepository = analysisDefaultSettingRepository;
        this.settingCollectionRepository = settingCollectionRepository;
    }

    /**
     * Save a analysisSetting.
     *
     * @param analysisSetting the entity to save
     * @return the persisted entity
     */
    public AnalysisSetting save(AnalysisSetting analysisSetting) {
        log.debug("Request to save AnalysisSetting : {}", analysisSetting);
        return analysisSettingRepository.save(analysisSetting);
    }

    /**
     * Get all the analysisSettings.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    public Page<AnalysisSetting> findAll(Pageable pageable) {
        log.debug("Request to get all AnalysisSettings");
        return analysisSettingRepository.findAll(pageable);
    }

    /**
     * Get all the AnalysisSetting with eager load of many-to-many relationships.
     *
     * @return the list of entities
     */
    public Page<AnalysisSetting> findAllWithEagerRelationships(Pageable pageable) {
        return analysisSettingRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one analysisSetting by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    public Optional<AnalysisSetting> findOne(String id) {
        log.debug("Request to get AnalysisSetting : {}", id);
        return analysisSettingRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Find settings by name
     * @param name Name of the setting
     * @return A list of settings related to the analysis
     */
    public Optional<AnalysisSetting> findByName(String name) {
        return analysisSettingRepository.findOneByName(name);
    }

    /**
     * Find settings by visibility
     * @param visibility Type of visibility
     * @return A list of settings for the given analysis type
     */
    public List<AnalysisSetting> findByVisibility(AnalysisSettingVisibility visibility) {
        return analysisSettingRepository
            .findByVisibility(visibility);
    }

    /**
     * Find settings by analysis
     * @param analysis Analysis to filter settings
     * @return A list of settings for the given analysis type
     */
    public List<AnalysisSetting> findByAnalysis(Analysis analysis) {
        return this.findByAnalysisType(analysis.getType(), analysis.getInput().getType());
    }

    /**
     * Find settings by analysis
     * @param type Analysis type to filter settings
     * @param inputType Analysis input type to filter settings
     * @return A list of settings for the given analysis type
     */
    public List<AnalysisSetting> findByAnalysisType(AnalysisType type, AnalysisInputType inputType) {
        Optional<AnalysisSettingCollection> collection = settingCollectionRepository
            .findOneByAnalysisTypeAndAnalysisInputType(type, inputType);

        if (collection.isPresent()) {
            return Arrays.asList(collection.get().getSettings().toArray(new AnalysisSetting[0]));
        } else {
            return Collections.emptyList();
        }
    }

    /**
     * Delete the analysisSetting by id.
     *
     * @param id the id of the entity
     */
    public void delete(String id) {
        log.debug("Request to delete AnalysisSetting : {}", id);
        analysisSettingRepository.deleteById(id);
    }

    private List<AnalysisSettingResolved> doResolveAnalysisSettings(
        List<AnalysisSetting> settings,
        AnalysisType analysisType,
        AnalysisInputType analysisInputType,
        List<String> userRoles,
        boolean isAnalysisEditable) {
        List<AnalysisSettingResolved> resolvedSettings = new ArrayList<>();
        Set<String> distinctSettingNames = new HashSet<>();

        for (AnalysisSetting setting : settings) {
            if (distinctSettingNames.contains(setting.getName())) {
                continue;
            }

            AnalysisSettingResolved resolvedSetting = new AnalysisSettingResolved()
                .name(setting.getName())
                .label(setting.getLabel())
                .editable(isAnalysisEditable)
                .type(setting.getType())
                .description(setting.getDescription())
                .choices(setting.getChoices());


            Optional<AnalysisDefaultSetting> defaultSetting = this.analysisDefaultSettingRepository
                .findOneBySettingAndRestrictions(
                    setting,
                    analysisType,
                    analysisInputType,
                    userRoles);

            if (defaultSetting.isPresent()) {
                resolvedSetting
                    .defaultValue(defaultSetting.get().getDefaultValue())
                    .editable(isAnalysisEditable && defaultSetting.get().isUserCanOverride())
                    .isUserRolesRestricted(defaultSetting.get().getUserRoles() != null && defaultSetting.get().getUserRoles().size() > 0)
                    .isAnalysisTypeRestricted(defaultSetting.get().getAnalysisType() != null)
                    .isInputTypeRestricted(defaultSetting.get().getAnalysisInputTypes() != null && defaultSetting.get().getAnalysisInputTypes().size() > 0);
            }

            resolvedSettings.add(resolvedSetting);
            distinctSettingNames.add(setting.getName());
        }

        return resolvedSettings;
    }

    /**
     * Restituisce le impostazioni predefinite per l'analisi e i ruoli utente indicati
     *
     * @param analysis L'analisi di cui si vogliono le impostazioni predefinite
     * @param userRoles I ruoli utente con cui filtrare le impostazioni
     * @return le impostazioni associate all'analisi
     */
    public List<AnalysisSettingResolved> resolveAnalysisSettings(Analysis analysis, List<String> userRoles, boolean includeHidden) {
        boolean isAnalysisEditable = analysis.getStatus() == AnalysisStatus.READY;
        List<AnalysisSetting> settings = this.findByAnalysis(analysis)
            .stream()
            .filter((setting) -> !setting.isGlobal() && (includeHidden || setting.isUserVisible()))
            .collect(Collectors.toList());
        List<AnalysisSettingResolved> resolvedSettings = this
            .doResolveAnalysisSettings(settings, analysis.getType(), analysis.getInput().getType(), userRoles, isAnalysisEditable);

        if (analysis.getSettings() != null) {
            for (AnalysisSettingResolved resolvedSetting : resolvedSettings) {
                if (analysis.getSettings().containsKey(resolvedSetting.getName())) {
                    resolvedSetting.setCurrentValue(analysis.getSettings().get(resolvedSetting.getName()));
                }
            }
        }

        return resolvedSettings;
    }

    public Map<String, Object> getAnalysisSettingsDefaultValues(Analysis analysis, List<String> userRoles) {
        Map<String, Object> defaults = new HashMap<>();
        List<AnalysisSettingResolved> resolvedSettings = this.resolveAnalysisSettings(analysis, userRoles, true);

        for (AnalysisSettingResolved setting : resolvedSettings) {
            defaults.put(setting.getName(), setting.getDefaultValue());
        }

        return defaults;
    }

    public void cleanAnalysisSettings(Analysis analysis, List<String> userRoles) {
        if (analysis.getSettings() == null || analysis.getSettings().size() == 0) {
            return;
        }

        Map<String, AnalysisSettingResolved> settings = this
            .resolveAnalysisSettings(analysis, userRoles, true)
            .stream()
            .collect(Collectors.toMap(AnalysisSettingResolved::getName, Function.identity()));

        Map<String, Object> cleanedSettings = new HashMap<>(analysis.getSettings());
        for (Map.Entry<String, Object> option: analysis.getSettings().entrySet()) {
            if (settings.containsKey(option.getKey()) && !settings.get(option.getKey()).isEditable()) {
                cleanedSettings.replace(option.getKey(), settings.get(option.getKey()).getDefaultValue());
            }
        }

        analysis.setSettings(cleanedSettings);
    }

    public Optional<AnalysisSettingResolved> getGlobalSetting(String name, AnalysisType analysisType, AnalysisInputType inputType, List<String> userRoles) {
        AnalysisSetting setting = this.findByName(name).orElse(null);
        if (setting == null || !setting.isGlobal()) {
            log.debug("getGlobalSetting: requested setting is not global : {}", name);
            return Optional.empty();
        }

        List<AnalysisSettingResolved> resolvedSettings = this
            .doResolveAnalysisSettings(Collections.singletonList(setting), analysisType, inputType, userRoles, false);

        return Optional.of(resolvedSettings.get(0));
    }

    public Object getGlobalSettingValue(String name, AnalysisType analysisType, AnalysisInputType inputType, List<String> userRoles, Object defaultValue) {
        Optional<AnalysisSettingResolved> resolvedSetting = this
            .getGlobalSetting(name, analysisType, inputType, userRoles);

        if (resolvedSetting.isPresent()) {
            return resolvedSetting.get().getCurrentValue();
        } else {
            return defaultValue;
        }
    }

    public Integer getGlobalSettingInteger(String name, AnalysisType analysisType, AnalysisInputType inputType, List<String> userRoles, Integer defaultValue) {
        Object value = this.getGlobalSettingValue(name, analysisType, inputType, userRoles, defaultValue);

        try {
            return (Integer)value;
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }

    public String getGlobalSettingString(String name, AnalysisType analysisType, AnalysisInputType inputType, List<String> userRoles, String defaultValue) {
        Object value = this.getGlobalSettingValue(name, analysisType, inputType, userRoles, defaultValue);

        try {
            return (String)value;
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }

    public Boolean getGlobalSettingBoolean(String name, AnalysisType analysisType, AnalysisInputType inputType, List<String> userRoles, Boolean defaultValue) {
        Object value = this.getGlobalSettingValue(name, analysisType, inputType, userRoles, defaultValue);

        try {
            return (Boolean)value;
        } catch (ClassCastException e) {
            return defaultValue;
        }
    }
}
