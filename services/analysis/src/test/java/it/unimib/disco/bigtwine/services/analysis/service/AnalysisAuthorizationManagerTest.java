package it.unimib.disco.bigtwine.services.analysis.service;

import it.unimib.disco.bigtwine.services.analysis.AnalysisApp;
import it.unimib.disco.bigtwine.services.analysis.SpringSecurityWebAuxTestConfig;
import it.unimib.disco.bigtwine.services.analysis.WithMockCustomUser;
import it.unimib.disco.bigtwine.services.analysis.WithMockCustomUserSecurityContextFactory;
import it.unimib.disco.bigtwine.services.analysis.domain.Analysis;
import it.unimib.disco.bigtwine.services.analysis.domain.User;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisVisibility;
import it.unimib.disco.bigtwine.services.analysis.web.api.errors.UnauthorizedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * Test class for the AnalysisUtil class.
 *
 * @see AnalysisAuthorizationManager
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
    AnalysisApp.class,
    SpringSecurityWebAuxTestConfig.class,
    WithMockCustomUserSecurityContextFactory.class
})
public class AnalysisAuthorizationManagerTest {

    @Autowired
    public AnalysisAuthorizationManager analysisAuthManager;

    @Test
    @WithMockCustomUser(userId = "testuser-1")
    public void checkAnalysisOwnershipOwnedPrivate() {
        Analysis analysis = new Analysis()
            .owner(new User().uid("testuser-1").username("testuser-1"))
            .visibility(AnalysisVisibility.PRIVATE);

        assertTrue(analysisAuthManager.checkAnalysisOwnership(analysis, AnalysisAuthorizationManager.AccessType.READ));
    }

    @Test(expected = UnauthorizedException.class)
    @WithMockCustomUser(userId = "testuser-1")
    public void checkAnalysisOwnershipUnownedPrivate() {
        Analysis analysis = new Analysis()
            .owner(new User().uid("testuser-2").username("testuser-2"))
            .visibility(AnalysisVisibility.PRIVATE);

        analysisAuthManager.checkAnalysisOwnership(analysis, AnalysisAuthorizationManager.AccessType.READ);
    }

    @Test
    @WithMockCustomUser(userId = "testuser-1")
    public void checkAnalysisOwnershipOwnedPublic() {
        Analysis analysis = new Analysis()
            .owner(new User().uid("testuser-1").username("testuser-1"))
            .visibility(AnalysisVisibility.PUBLIC);

        assertTrue(analysisAuthManager.checkAnalysisOwnership(analysis, AnalysisAuthorizationManager.AccessType.READ));
    }

    @Test
    @WithMockCustomUser(userId = "testuser-1")
    public void checkAnalysisOwnershipUnownedPublic() {
        Analysis analysis = new Analysis()
            .owner(new User().uid("testuser-2").username("testuser-2"))
            .visibility(AnalysisVisibility.PUBLIC);

        assertFalse(analysisAuthManager.checkAnalysisOwnership(analysis, AnalysisAuthorizationManager.AccessType.READ));
    }

    @Test
    public void checkAnalysisOwnershipUnloggedPublic() {
        Analysis analysis = new Analysis()
            .owner(new User().uid("testuser-1").username("testuser-1"))
            .visibility(AnalysisVisibility.PUBLIC);

        assertFalse(analysisAuthManager.checkAnalysisOwnership(analysis, AnalysisAuthorizationManager.AccessType.READ));
    }

    @Test(expected = UnauthorizedException.class)
    public void checkAnalysisOwnershipUnloggedPrivate() {
        Analysis analysis = new Analysis()
            .owner(new User().uid("testuser-1").username("testuser-1"))
            .visibility(AnalysisVisibility.PRIVATE);

        analysisAuthManager.checkAnalysisOwnership(analysis, AnalysisAuthorizationManager.AccessType.READ);
    }

    @Test(expected = UnauthorizedException.class)
    @WithMockCustomUser(userId = "testuser-1")
    public void checkAnalysisOwnershipUnownedPrivateDelete() {
        Analysis analysis = new Analysis()
            .owner(new User().uid("testuser-2").username("testuser-2"))
            .visibility(AnalysisVisibility.PRIVATE);

        analysisAuthManager.checkAnalysisOwnership(analysis, AnalysisAuthorizationManager.AccessType.DELETE);
    }

    @Test(expected = UnauthorizedException.class)
    @WithMockCustomUser(userId = "testuser-1")
    public void checkAnalysisOwnershipUnownedPublicDelete() {
        Analysis analysis = new Analysis()
            .owner(new User().uid("testuser-2").username("testuser-2"))
            .visibility(AnalysisVisibility.PUBLIC);

        analysisAuthManager.checkAnalysisOwnership(analysis, AnalysisAuthorizationManager.AccessType.DELETE);
    }

    @Test
    @WithMockCustomUser(userId = "testuser-1")
    public void checkAnalysisOwnershipOwnedDelete() {
        Analysis analysis = new Analysis()
            .owner(new User().uid("testuser-1").username("testuser-1"))
            .visibility(AnalysisVisibility.PRIVATE);

        assertTrue(analysisAuthManager.checkAnalysisOwnership(analysis, AnalysisAuthorizationManager.AccessType.DELETE));
    }

    @Test(expected = UnauthorizedException.class)
    @WithMockCustomUser(userId = "testuser-1")
    public void checkAnalysisOwnershipUnownedPrivateUpdate() {
        Analysis analysis = new Analysis()
            .owner(new User().uid("testuser-2").username("testuser-2"))
            .visibility(AnalysisVisibility.PRIVATE);

        analysisAuthManager.checkAnalysisOwnership(analysis, AnalysisAuthorizationManager.AccessType.UPDATE);
    }

    @Test
    @WithMockCustomUser(userId = "testuser-1")
    public void checkAnalysisOwnershipOwnedPrivateUpdate() {
        Analysis analysis = new Analysis()
            .owner(new User().uid("testuser-1").username("testuser-1"))
            .visibility(AnalysisVisibility.PRIVATE);

        analysisAuthManager.checkAnalysisOwnership(analysis, AnalysisAuthorizationManager.AccessType.UPDATE);
    }
}
