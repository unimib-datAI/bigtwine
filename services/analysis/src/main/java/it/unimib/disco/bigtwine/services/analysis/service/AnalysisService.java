package it.unimib.disco.bigtwine.services.analysis.service;

import it.unimib.disco.bigtwine.commons.messaging.AnalysisStatusChangeRequestedEvent;
import it.unimib.disco.bigtwine.commons.messaging.JobControlEvent;
import it.unimib.disco.bigtwine.commons.models.JobActionEnum;
import it.unimib.disco.bigtwine.commons.models.JobTypeEnum;
import it.unimib.disco.bigtwine.services.analysis.domain.*;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisStatus;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisType;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisVisibility;
import it.unimib.disco.bigtwine.services.analysis.domain.mapper.AnalysisStatusMapper;
import it.unimib.disco.bigtwine.services.analysis.messaging.AnalysisStatusChangeRequestProducerChannel;
import it.unimib.disco.bigtwine.services.analysis.messaging.JobControlEventsProducerChannel;
import it.unimib.disco.bigtwine.services.analysis.repository.AnalysisRepository;
import it.unimib.disco.bigtwine.services.analysis.repository.AnalysisResultsRepository;
import it.unimib.disco.bigtwine.services.analysis.security.AuthoritiesConstants;
import it.unimib.disco.bigtwine.services.analysis.security.SecurityUtils;
import it.unimib.disco.bigtwine.services.analysis.validation.*;
import it.unimib.disco.bigtwine.services.analysis.validation.analysis.input.AnalysisInputTypeValidator;
import it.unimib.disco.bigtwine.services.analysis.validation.analysis.input.AnalysisInputValidatorLocator;
import it.unimib.disco.bigtwine.services.analysis.validation.analysis.input.InvalidAnalysisInputProvidedException;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

/**
 * Service Implementation for managing Analysis.
 */
@Service
public class AnalysisService {

    private final Logger log = LoggerFactory.getLogger(AnalysisService.class);

    private final AnalysisRepository analysisRepository;
    private final AnalysisResultsRepository analysisResultsRepository;
    private final AnalysisSettingService analysisSettingService;
    private final AnalysisAuthorizationManager analysisAuthManager;

    private final AnalysisStatusValidator analysisStatusValidator;
    private final AnalysisInputTypeValidator inputTypeValidator;
    private final AnalysisInputValidatorLocator inputValidatorLocator;
    private final AnalysisExportFormatValidator exportFormatValidator;

    private final MessageChannel statusChangeRequestsChannel;
    private final MessageChannel jobControlChannel;

    public AnalysisService(
        AnalysisRepository analysisRepository,
        AnalysisResultsRepository analysisResultsRepository,
        AnalysisSettingService analysisSettingService,
        AnalysisAuthorizationManager analysisAuthManager,
        AnalysisStatusValidator analysisStatusValidator,
        AnalysisInputTypeValidator inputTypeValidator,
        AnalysisInputValidatorLocator inputValidatorLocator,
        AnalysisExportFormatValidator exportFormatValidator,
        AnalysisStatusChangeRequestProducerChannel channel,
        JobControlEventsProducerChannel jobControlChannel) {
        this.analysisRepository = analysisRepository;
        this.analysisResultsRepository = analysisResultsRepository;
        this.analysisSettingService = analysisSettingService;
        this.analysisAuthManager = analysisAuthManager;
        this.analysisStatusValidator = analysisStatusValidator;
        this.exportFormatValidator = exportFormatValidator;
        this.inputTypeValidator = inputTypeValidator;
        this.inputValidatorLocator = inputValidatorLocator;
        this.statusChangeRequestsChannel = channel.analysisStatusChangeRequestsChannel();
        this.jobControlChannel = jobControlChannel.jobControlEventsChannel();
    }

    /**
     * Imposta i parametri predefiniti dell'analisis in caso siano null
     *
     * @param analysis L'analisi da impostare
     */
    private void setupAnalysisDefaults(Analysis analysis) {
        if (analysis.getCreateDate() == null) {
            analysis.setCreateDate(Instant.now());
        }

        if (analysis.getStatus() == null) {
            analysis.setStatus(Analysis.DEFAULT_STATUS);
        }

        if (analysis.getVisibility() == null) {
            analysis.setVisibility(Analysis.DEFAULT_VISIBILITY);
        }

        if (analysis.getStatusHistory() == null) {
            analysis.setStatusHistory(new ArrayList<>());
        }

        if (analysis.getSettings() == null) {
            Map<String, Object> defaults = this.analysisSettingService
                .getAnalysisSettingsDefaultValues(analysis, SecurityUtils.getCurrentUserRoles());
            analysis.setSettings(defaults);
        }
    }

    /**
     * Valida un'analisi e lancia eccezioni in caso di errori
     *
     * @param analysis Oggetto da validare
     * @throws InvalidAnalysisStatusException Eccezione lanciata in caso di update se lo status impostato non è valido
     * @throws InvalidAnalysisInputProvidedException Eccezione lanciata se non è stato fornito un input valido
     */
    private void validate(@NotNull Analysis analysis, Analysis oldAnalysis) {
        // Validate input
        if (analysis.getInput() == null || analysis.getInput().getType() == null) {
            throw new InvalidAnalysisInputProvidedException("Input not provided");
        }

        this.inputTypeValidator.validate(analysis.getType(), analysis.getInput().getType());
        this.inputValidatorLocator
            .getValidator(analysis.getInput().getType())
            .validate(analysis.getInput());

        // Validate status change
        if (oldAnalysis != null) {
            boolean isStatusChanged = oldAnalysis.getStatus() != analysis.getStatus();
            boolean statusChangeAllowed = this.analysisStatusValidator.validate(oldAnalysis.getStatus(), analysis.getStatus());
            if (isStatusChanged && !statusChangeAllowed) {
                throw new InvalidAnalysisStatusException(oldAnalysis.getStatus(), analysis.getStatus());
            }
        }

        // Validate user settings
        if (oldAnalysis != null && analysis.getStatus() != AnalysisStatus.READY) {
            if ((oldAnalysis.getSettings() == null && analysis.getSettings() != null && analysis.getSettings().size() > 0) ||
                (oldAnalysis.getSettings() != null && !oldAnalysis.getSettings().equals(analysis.getSettings()))) {
                throw new AnalysisUpdateNotApplicable("User settings cannot be changed when analysis status is not ready");
            }
        }
    }

    /**
     * Salva un analisi, registra l'eventuale cambio di stato e lo notifica con l'invio di un evento.
     *
     * @param analysis L'analisis da salvare
     * @return the persisted entity
     * @throws InvalidAnalysisStatusException Lancia un errore se lo stato non è associabile all'analisi
     * @throws InvalidAnalysisInputProvidedException Lancia un errore se non è stato fornito un input valido
     */
    public Analysis save(Analysis analysis) {
        log.debug("Request to save Analysis : {}", analysis);
        Optional<Analysis> oldAnalysis = analysis.getId() != null ? this.findOne(analysis.getId()) : Optional.empty();
        boolean isUpdate = oldAnalysis.isPresent();

        if (!isUpdate) {
            this.setupAnalysisDefaults(analysis);
        }

        analysis.setUpdateDate(Instant.now());

        this.validate(analysis, oldAnalysis.orElse(null));

        return analysisRepository.save(analysis);
    }

    /**
     * Get all the analyses.
     *
     * @return the list of entities
     */
    public List<Analysis> findAll() {
        log.debug("Request to get all Analyses");
        return analysisRepository.findAll();
    }

    /**
     * Get all the analyses (paged).
     *
     * @return the list of entities
     */
    public Page<Analysis> findAll(Pageable page) {
        log.debug("Request to get all Analyses");
        return analysisRepository.findAll(page);
    }

    /**
     * Return the total number of analysis in db
     *
     * @return Total number of analysis in db
     */
    public Long countAll() {
        return this.analysisRepository.count();
    }

    /**
     * Get all the analyses of an user.
     *
     * @return the list of entities
     */
    public List<Analysis> findByOwner(String owner) {
        log.debug("Request to get all Analyses of an user");
        return analysisRepository.findByOwnerUid(owner);
    }

    /**
     * Get all the analyses of an user (paged).
     *
     * @return the list of entities
     */
    public Page<Analysis> findByOwner(String owner, Pageable page) {
        log.debug("Request to get all Analyses of an user");
        return analysisRepository.findByOwnerUid(owner, page);
    }

    /**
     * Return the number of analyses owned by indicated user
     *
     * @param owner The owner of the analyses
     * @return the number of analyses owned by indicated user
     */
    public Long countByOwner(String owner) {
        return analysisRepository.countByOwnerUid(owner);
    }

    /**
     * Return the analyses in db with the status 'status'
     *
     * @param status The status to filter
     * @param page The page you want
     * @return A stream of analysis with the status 'status'
     */
    public Stream<Analysis> findByStatus(AnalysisStatus status, Pageable page) {
        return analysisRepository.findByStatus(status, page);
    }

    /**
     * Return the total number of analyses in db with the status 'status'
     *
     * @param status The status to filter
     * @return Total number of analysis with the status 'status'
     */
    public Long countByStatus(AnalysisStatus status) {
        return this.analysisRepository.countByStatus(status);
    }

    /**
     * Return the analyses in db of type 'analysisType'
     *
     * @param analysisType The type to filter
     * @param page The page you want
     * @return A list of analysis of type 'analysisType'
     */
    public Page<Analysis> findByType(AnalysisType analysisType, Pageable page) {
        log.debug("Request to get all Analyses of a type");
        if (analysisType == null) {
            return this.findAll(page);
        } else {
            return analysisRepository.findByType(analysisType, page);
        }
    }

    /**
     * Return the analyses in db of type 'analysisType' and owned by the user 'owner'
     *
     * @param owner Owner of the analysis
     * @param analysisType The type to filter
     * @param page The page you want
     * @return A list of analysis of type 'analysisType' and owned by the user 'owner'
     */
    public Page<Analysis> findByOwnerAndType(String owner, AnalysisType analysisType, Pageable page) {
        log.debug("Request to get all Analyses of a type owned by the user");
        if (analysisType == null) {
            return this.findByOwner(owner, page);
        } else {
            return analysisRepository.findByTypeAndOwnerUid(analysisType, owner, page);
        }
    }

    /**
     * Return the analyses in db visible to the user 'user'
     *
     * @param user Owner of the analysis
     * @param page The page you want
     * @return A list of analysis visible to the user 'user'
     */
    public Page<Analysis> findVisible(String user, Pageable page) {
        log.debug("Request to get all Analyses of a type owned by the user");
        boolean isAdmin = SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN);
        if (isAdmin) {
            return this.findAll(page);
        } else {
            return analysisRepository.findByOwnerUidOrVisibility(user, AnalysisVisibility.PUBLIC, page);
        }
    }

    /**
     * Return the analyses in db of type 'analysisType' and visible to the user 'user'
     *
     * @param user Owner of the analysis
     * @param analysisType The type to filter
     * @param page The page you want
     * @return A list of analysis of type 'analysisType' and visible to the user 'user'
     */
    public Page<Analysis> findVisibleByType(String user, AnalysisType analysisType, Pageable page) {
        log.debug("Request to get all Analyses of a type visible to the user");
        if (analysisType == null) {
            return this.findVisible(user, page);
        } else {
            boolean isAdmin = SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN);
            if (isAdmin) {
                return this.findByType(analysisType, page);
            } else {
                return analysisRepository.findVisibleByType(user, analysisType, page);
            }
        }
    }

    /**
     * Get one analysis by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    public Optional<Analysis> findOne(String id) {
        log.debug("Request to get Analysis : {}", id);
        return analysisRepository.findById(id);
    }

    /**
     * Delete the analysis by id.
     *
     * @param id the id of the entity
     */
    public void delete(String id) {
        log.debug("Request to delete Analysis : {}", id);
        analysisRepository.deleteById(id);
    }

    /**
     * Restituisce la lista di tutti i cambi di stato dell'analisi indicata
     *
     * @param id the id of the entity
     * @return A change list of the analysis status
     */
    public List<AnalysisStatusHistory> getStatusHistory(String id) {
        Analysis analysis = this.findOne(id).orElse(null);
        if (analysis == null) {
            return null;
        } else {
            return analysis.getStatusHistory();
        }
    }

    public void requestStatusChange(@NotNull Analysis analysis,@NotNull AnalysisStatus newStatus, boolean userRequested) {
        if (analysis.getId() == null) {
            throw new IllegalArgumentException("analysis hasn't an id");
        }

        if (!this.analysisStatusValidator.validate(analysis.getStatus(), newStatus)) {
            throw new InvalidAnalysisStatusException(analysis.getStatus(), newStatus);
        }

        if (analysis.getStatus() == newStatus) {
            return;
        }

        User user = null;
        if (userRequested) {
            user = analysisAuthManager.getCurrentUser().orElse(null);
        }

        AnalysisStatusChangeRequestedEvent event = new AnalysisStatusChangeRequestedEvent();
        event.setAnalysisId(analysis.getId());
        event.setDesiredStatus(AnalysisStatusMapper.INSTANCE.analysisStatusEventEnumFromDomain(newStatus));
        event.setUser(user);

        Message<AnalysisStatusChangeRequestedEvent> message = MessageBuilder
            .withPayload(event)
            .build();

        this.statusChangeRequestsChannel.send(message);
    }

    public AnalysisExport startAnalysisResultsExport(String analisysId, AnalysisExport export) {
        Analysis analysis = this.findOne(analisysId).orElse(null);
        if (analysis == null) {
            return null;
        }

        String format = export.getFormat();
        if (format == null || !this.exportFormatValidator.validate(analysis.getType(), format)) {
            throw new ValidationException("Unrecognized export format: " + format);
        }

        EnumSet<AnalysisStatus> acceptedStatuses = EnumSet.of(
            AnalysisStatus.COMPLETED,
            AnalysisStatus.FAILED,
            AnalysisStatus.CANCELLED);

        if (!acceptedStatuses.contains(analysis.getStatus())) {
            throw new ValidationException("Export not available, invalid analysis status: " + analysis.getStatus());
        }

        if (analysis.getExports() != null && analysis.getExports().size() > 0) {
            boolean formatPresent = analysis
                .getExports()
                .stream()
                .anyMatch(exp1 -> format.equals(exp1.getFormat()));

            if (formatPresent) {
                throw new ValidationException(String.format("Export with format '%s' already present", format));
            }
        }

        if (export.getDocumentId() == null || !ObjectId.isValid(export.getDocumentId()) ) {
            export.setDocumentId(ObjectId.get().toHexString());
        }

        analysis.addExport(export);
        this.save(analysis);

        JobControlEvent event = new JobControlEvent();
        event.setAnalysisId(analisysId);
        event.setAction(JobActionEnum.START);
        event.setJobType(JobTypeEnum.EXPORT);
        event.setReference(format);

        Message<JobControlEvent> message = MessageBuilder
            .withPayload(event)
            .build();

        this.jobControlChannel.send(message);

        return export;
    }

    public Analysis saveAnalysisStatusChange(String analysisId, AnalysisStatus newStatus, User user, String message) {
        Optional<Analysis> analysisOpt = this.findOne(analysisId);

        if(!analysisOpt.isPresent()) {
            return null;
        }

        Analysis analysis = analysisOpt.get();
        AnalysisStatus oldStatus = analysis.getStatus();

        if (newStatus == null) {
            // Status unchanged
            newStatus = oldStatus;
        }

        if (newStatus != oldStatus) {
            analysis.setStatus(newStatus);

            if (newStatus != AnalysisStatus.STARTED) {
                long resultsCount = this.analysisResultsRepository.countByAnalysisId(analysisId);
                analysis.setResultsCount(resultsCount);
            }
        }

        AnalysisStatusHistory statusChange = new AnalysisStatusHistory()
            .oldStatus(oldStatus)
            .newStatus(newStatus)
            .date(Instant.now())
            .user(user)
            .message(message);

        analysis.addStatusChange(statusChange);

        try {
            analysis = this.save(analysis);
        } catch (ValidationException e) {
            log.error("Cannot save status change", e);
            return null;
        }

        return analysis;
    }

    public Analysis saveAnalysisProgressUpdate(String analysisId, double progress) {
        Optional<Analysis> analysisOpt = this.findOne(analysisId);

        if(!analysisOpt.isPresent()) {
            return null;
        }

        Analysis analysis = analysisOpt.get();
        analysis.setProgress(progress);

        try {
            return this.save(analysis);
        } catch (ValidationException e) {
            log.error("Cannot save progress update", e);
            return null;
        }
    }

    public Analysis saveAnalysisExportProgressUpdate(
        String analysisId, String format, double progress, boolean isCompleted, boolean isFailed, String message) {
        if (analysisId == null || format == null) {
            return null;
        }

        Optional<Analysis> analysisOpt = this.findOne(analysisId);

        if(!(analysisOpt.isPresent())) {
            return null;
        }

        Analysis analysis = analysisOpt.get();
        if (analysis.getExports() == null || analysis.getExports().size() == 0) {
            return null;
        }

        AnalysisExport export = analysis
            .getExports()
            .stream()
            .filter(e -> format.equals(e.getFormat()))
            .findFirst()
            .orElse(null);

        if (export == null) {
            return null;
        }

        export.setProgress(progress);
        export.setCompleted(isCompleted);
        export.setFailed(isFailed);
        export.setMessage(message);

        try {
            return this.save(analysis);
        } catch (ValidationException e) {
            log.error("Cannot save progress update", e);
            return null;
        }
    }
}
