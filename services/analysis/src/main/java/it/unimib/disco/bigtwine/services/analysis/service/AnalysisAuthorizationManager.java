package it.unimib.disco.bigtwine.services.analysis.service;

import it.unimib.disco.bigtwine.services.analysis.config.AnalysisSettingConstants;
import it.unimib.disco.bigtwine.services.analysis.domain.Analysis;
import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisSettingResolved;
import it.unimib.disco.bigtwine.services.analysis.domain.User;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisStatus;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisVisibility;
import it.unimib.disco.bigtwine.services.analysis.repository.AnalysisRepository;
import it.unimib.disco.bigtwine.services.analysis.security.AuthoritiesConstants;
import it.unimib.disco.bigtwine.services.analysis.security.SecurityUtils;
import it.unimib.disco.bigtwine.services.analysis.web.api.errors.ForbiddenException;
import it.unimib.disco.bigtwine.services.analysis.web.api.errors.UnauthorizedException;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.Optional;

@Service
public class AnalysisAuthorizationManager {

    private final AnalysisRepository analysisRepository;
    private final AnalysisSettingService analysisSettingService;

    public AnalysisAuthorizationManager(
        AnalysisRepository analysisRepository,
        AnalysisSettingService analysisSettingService) {
        this.analysisRepository = analysisRepository;
        this.analysisSettingService = analysisSettingService;
    }

    public enum AccessType {
        READ, UPDATE, DELETE
    }

    public Optional<String> getCurrentUserIdentifier() {
        return SecurityUtils.getCurrentUserId();
    }

    public Optional<User> getCurrentUser() {
        String userId = SecurityUtils.getCurrentUserId().orElse(null);
        if (userId == null) {
            return Optional.empty();
        }

        User user = new User()
            .uid(userId)
            .username(SecurityUtils.getCurrentUserLogin().orElse(null));

        return Optional.of(user);
    }

    /**
     * Controlla che l'utente corrente sia il proprietario dell'analisi corrente o che l'analisi sia pubblica
     * in caso contrario lancia un eccesione
     *
     * @param analysis L'analisis da controllare
     * @return true se l'utente corrente è proprietario dell'analisi, false altrimenti
     * @throws UnauthorizedException Nel caso l'utente non sia autenticato
     * @throws ForbiddenException Nel caso l'analisi non sia accessibile dall'utente corrente
     */
    public boolean checkAnalysisOwnership(@NotNull Analysis analysis, AccessType accessType) {
        String ownerId = getCurrentUserIdentifier().orElseThrow(UnauthorizedException::new);
        boolean isOwner = ownerId != null && analysis.getOwner() != null && ownerId.equals(analysis.getOwner().getUid());
        boolean isSuperUser = SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN);

        if (isOwner || isSuperUser) {
            return isOwner;
        } else {
            // Non siamo né i proprietari dell'analisi né un admin
            if (accessType == AccessType.DELETE || accessType == AccessType.UPDATE) {
                throw new ForbiddenException();
            }

            if (analysis.getVisibility() == AnalysisVisibility.PRIVATE) {
                throw new ForbiddenException();
            }
        }

        return false;
    }

    /**
     * Controlla che l'utente corrente possa creare nuove analisi
     * @param analysis L'analisi che si vorrebbe create
     * @return Restituisce true se l'utente corrente può creare nuove analisi
     */
    public boolean canCreateAnalysis(@NotNull Analysis analysis) {
        boolean isDemoUser = SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.DEMO);
        return !isDemoUser;
    }

    /**
     * Controlla che l'utente corrente possa avviare nuove analisi
     *
     * Il controllo si basa sull'impostazione globale 'max-concurrent-analysis',
     * se questa impostazione non è definita viene utilizzato il valore indicato in
     * AnalysisSettingConstants. Se il valore è negativo non viene posto un limite alle
     * analisi che l'utente può avviare. Un valore pari a zero viene l'avvio di analisi
     * all'utente.
     *
     * Caso 1: Impostazione limitata sulla tipologia di input delle analisi
     *      Vengono contate tutte le analisi dello stesso tipo e della stessa
     *      tipologia di input appartenenti all'utente con stato 'STARTED'.
     *
     * Caso 2: Impostazione limitata solo sul tipo di analisi
     *      Vengono contate tutte le analisi dello stesso tipo (indifferentemente
     *      dalla tipologia di input) appartenenti all'utente con stato 'STARTED'.
     *
     * Caso 3: Impostazione senza limitati sul tipo di analisi o sulla tipologia di input
     *      Vengono contate tutte le analisi appartenenti all'utente con stato 'STARTED'.
     *
     *
     * @param analysis L'analisi che si vorrebbe avviare
     * @return Restituisce true se l'utente corrente può avviare nuove analisi
     *
     * @see it.unimib.disco.bigtwine.services.analysis.domain.AnalysisSetting
     * @see AnalysisSettingConstants
     */
    public boolean canStartAnalysis(@NotNull Analysis analysis) {
        int maxConcurrentAnalysis = AnalysisSettingConstants.DEFAULT_MAX_CONCURRENT_ANALYSES;
        boolean filterByType = false;
        boolean filterByInput = false;
        Optional<AnalysisSettingResolved> setting = this.analysisSettingService.getGlobalSetting(
            AnalysisSettingConstants.MAX_CONCURRENT_ANALYSES,
            analysis.getType(), analysis.getInput().getType(), SecurityUtils.getCurrentUserRoles());

        if (setting.isPresent()) {
            filterByType = setting.get().isAnalysisTypeRestricted();
            filterByInput = setting.get().isInputTypeRestricted();
            try {
                maxConcurrentAnalysis = (Integer) setting.get().getDefaultValue();
            } catch (ClassCastException e) {}
        }

        long startedAnalysisCount = 0;
        if (maxConcurrentAnalysis < 0) {
            // Illimitato
            return true;
        } else if (filterByInput) {
            startedAnalysisCount = this.analysisRepository
                .countByOwnerUidAndStatusAndTypeAndInputType(
                    analysis.getOwner().getUid(), AnalysisStatus.STARTED,
                    analysis.getType(), analysis.getInput().getType());
        } else if (filterByType) {
            startedAnalysisCount = this.analysisRepository
                .countByOwnerUidAndStatusAndType(
                    analysis.getOwner().getUid(), AnalysisStatus.STARTED,
                    analysis.getType());
        } else {
            startedAnalysisCount = this.analysisRepository
                .countByOwnerUidAndStatus(analysis.getOwner().getUid(), AnalysisStatus.STARTED);
        }

        return startedAnalysisCount < maxConcurrentAnalysis;
    }

    /**
     * Controlla che l'utente corrente possa terminare l'analisi indicata.
     * Con terminare si intende cambiare lo stato dell'analisi in uno degli stati terminali (COMPLETED, CANCELLED)
     * @param analysis L'analisi che si vorrebbe terminare
     * @return Restituisce true se l'utente corrente può terminare l'analisi
     */
    public boolean canTerminateAnalysis(@NotNull Analysis analysis) {
        boolean isDemoUser = SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.DEMO);
        return !isDemoUser;
    }
}
