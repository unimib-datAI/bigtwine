package it.unimib.disco.bigtwine.services.analysis.web.api;

import it.unimib.disco.bigtwine.services.analysis.AnalysisApp;
import it.unimib.disco.bigtwine.services.analysis.SpringSecurityWebAuxTestConfig;
import it.unimib.disco.bigtwine.services.analysis.WithMockCustomUser;
import it.unimib.disco.bigtwine.services.analysis.WithMockCustomUserSecurityContextFactory;
import it.unimib.disco.bigtwine.services.analysis.domain.*;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisStatus;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisType;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisVisibility;
import it.unimib.disco.bigtwine.services.analysis.domain.mapper.AnalysisResultMapperLocator;
import it.unimib.disco.bigtwine.services.analysis.repository.AnalysisRepository;
import it.unimib.disco.bigtwine.services.analysis.repository.AnalysisResultsRepository;
import it.unimib.disco.bigtwine.services.analysis.service.AnalysisAuthorizationManager;
import it.unimib.disco.bigtwine.services.analysis.service.AnalysisService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {
    AnalysisApp.class,
    SpringSecurityWebAuxTestConfig.class,
    WithMockCustomUserSecurityContextFactory.class
})
public class AnalysisResultsApiIntTest {

    @Autowired
    private AnalysisResultMapperLocator resultMapperLocator;

    @Autowired
    private AnalysisResultsRepository resultsRepository;

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private AnalysisAuthorizationManager analysisAuthManager;

    @Autowired
    private AnalysisRepository analysisRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private MockMvc restApiMvc;

    private Analysis ownedAnalysis;
    private Analysis privateAnalysis;
    private Analysis publicAnalysis;


    private Analysis createAnalysis() {
        AnalysisInput input = new QueryAnalysisInput()
            .tokens(Arrays.asList("query", "di", "prova"))
            .joinOperator(QueryAnalysisInput.JoinOperator.ALL);

        return new Analysis()
            .type(AnalysisType.TWITTER_NEEL)
            .createDate(Instant.now())
            .updateDate(Instant.now())
            .visibility(AnalysisVisibility.PUBLIC)
            .status(AnalysisStatus.READY)
            .owner(new User().uid("testuser-1").username("testuser-1"))
            .input(input);
    }

    private NeelProcessedTweet createProcessedTweet(int i) {
        TwitterUser user = new TwitterUser();
        user.setId("user" + i);
        user.setScreenName("user" + i);
        TwitterStatus status = new TwitterStatus();
        status.setId("" + i);
        status.setText("text" + i);
        status.setUser(user);

        NeelProcessedTweet tweet = new NeelProcessedTweet();
        tweet.setStatus(status);
        tweet.setEntities(Collections.emptyList());

        return tweet;
    }

    private AnalysisResult<?> createAnalysisResult(int i) {
        NeelProcessedTweet tweet = this.createProcessedTweet(i);
        return new AnalysisResult<>()
            .payload(tweet)
            .processDate(Instant.now());
    }

    private void clearRepositories() {
        this.analysisRepository.deleteAll();
        this.resultsRepository.deleteAll();
    }

    private void fillRepositories() {
        Analysis a1 = this.createAnalysis()
            .visibility(AnalysisVisibility.PRIVATE)
            .owner(new User().uid("testuser-1").username("testuser-1"));
        this.ownedAnalysis = this.analysisRepository.save(a1);

        int tweetCount = 3;
        for (int i = 1; i <= tweetCount; ++i) {
            AnalysisResult<?> result = this.createAnalysisResult(i)
                .analysis(ownedAnalysis)
                .saveDate(Instant.now());

            this.resultsRepository.save(result);
        }

        Analysis a2 = this.createAnalysis()
            .visibility(AnalysisVisibility.PRIVATE)
            .owner(new User().uid("testuser-2").username("testuser-2"));
        this.privateAnalysis = this.analysisRepository.save(a2);

        for (int i = 1; i <= tweetCount; ++i) {
            AnalysisResult<?> result = this.createAnalysisResult(i)
                .analysis(privateAnalysis)
                .saveDate(Instant.now());

            this.resultsRepository.save(result);
        }

        Analysis a3 = this.createAnalysis()
            .visibility(AnalysisVisibility.PUBLIC)
            .owner(new User().uid("testuser-2").username("testuser-2"));
        this.publicAnalysis = this.analysisRepository.save(a3);

        for (int i = 1; i <= tweetCount; ++i) {
            AnalysisResult<?> result = this.createAnalysisResult(i)
                .analysis(publicAnalysis)
                .saveDate(Instant.now());

            this.resultsRepository.save(result);
        }
    }

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AnalysisResultsApiDelegateImpl delegate = new AnalysisResultsApiDelegateImpl(
            null,
            analysisService,
            analysisAuthManager,
            resultsRepository,
            resultMapperLocator,
            mongoTemplate);
        AnalysisResultsApiController controller = new AnalysisResultsApiController(delegate);
        this.restApiMvc = MockMvcBuilders.standaloneSetup(controller)
            .build();

        this.clearRepositories();
        this.fillRepositories();
    }

    @Test
    @WithMockCustomUser(userId = "testuser-1")
    public void testListAnalysisResultEmpty() throws Exception {
        Analysis analysis = this.createAnalysis()
            .visibility(AnalysisVisibility.PUBLIC)
            .owner(new User().uid("testuser-1").username("testuser-1"));
        analysis = this.analysisRepository.save(analysis);

        this.restApiMvc.perform(get("/api/public/analysis-results/{id}", analysis.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.objects.length()").value(0));
    }

    @Test
    @WithMockCustomUser(userId = "testuser-1")
    public void testListAnalysisResultNotFound() throws Exception {
        this.restApiMvc.perform(get("/api/public/analysis-results/{id}", "invalid-id"))
            .andExpect(status().isNotFound());
    }

    @Test
    @WithMockCustomUser(userId = "testuser-1")
    public void testListAnalysisResultUnauthorized() throws Exception {
        this.restApiMvc.perform(get("/api/public/analysis-results/{id}", privateAnalysis.getId()))
            .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockCustomUser(userId = "testuser-1")
    public void testListAnalysisResult() throws Exception {
        this.restApiMvc.perform(get("/api/public/analysis-results/{id}", ownedAnalysis.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.totalCount").value(3))
            .andExpect(jsonPath("$.page").value(0))
            .andExpect(jsonPath("$.count").value(3))
            .andExpect(jsonPath("$.objects[0].analysisId").value(ownedAnalysis.getId()))
            .andExpect(jsonPath("$.objects[0].payload.status.text").value("text1"))
            .andExpect(jsonPath("$.objects.length()").value(3));
    }

    @Test
    @WithMockCustomUser(userId = "testuser-1")
    public void testSearchAnalysisResults() throws Exception {
        String query = "{$text: {$search: \"text1\", $caseSensitive: false}}";

        RequestBuilder requestBuilder = post("/api/public/analysis-results/{id}/search", ownedAnalysis.getId())
            .content(query)
            .contentType(MediaType.TEXT_PLAIN);

        this.restApiMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.objects.length()").value(1))
            .andExpect(jsonPath("$.objects[0].payload.status.text").value("text1"));
    }

    @Test
    @WithMockCustomUser(userId = "testuser-1")
    public void testSearchAnalysisResultsWithAnalysisIdOverwrite() throws Exception {
        String query = "{\"analysis.id\": {$oid: \"" + privateAnalysis.getId() + "\"}}";

        RequestBuilder requestBuilder = post("/api/public/analysis-results/{id}/search", ownedAnalysis.getId())
            .content(query)
            .contentType(MediaType.TEXT_PLAIN);

        this.restApiMvc.perform(requestBuilder)
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.objects.length()").value(3))
            .andExpect(jsonPath("$.objects.[*].analysisId", everyItem(is(ownedAnalysis.getId()))));
    }
}
