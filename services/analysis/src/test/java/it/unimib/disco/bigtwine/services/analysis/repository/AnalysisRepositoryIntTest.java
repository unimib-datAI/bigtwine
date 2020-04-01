package it.unimib.disco.bigtwine.services.analysis.repository;

import it.unimib.disco.bigtwine.services.analysis.AnalysisApp;
import it.unimib.disco.bigtwine.services.analysis.domain.Analysis;
import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisInput;
import it.unimib.disco.bigtwine.services.analysis.domain.QueryAnalysisInput;
import it.unimib.disco.bigtwine.services.analysis.domain.User;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisStatus;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisType;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisVisibility;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AnalysisApp.class)
public class AnalysisRepositoryIntTest {

    @Autowired
    private AnalysisRepository analysisRepository;

    @Before
    public void setUp() throws Exception {
        this.analysisRepository.deleteAll();
        String[] owners = new String[]{"user-1", "user-1", "user-1", "user-2", "user-3"};

        for (int i = 1; i <= owners.length; ++i) {
            AnalysisInput input = new QueryAnalysisInput()
                .tokens(Arrays.asList("query", "di", "prova", "" + i))
                .joinOperator(QueryAnalysisInput.JoinOperator.ALL);

            Analysis a = new Analysis()
                .owner(new User().uid(owners[i - 1]).username(owners[i - 1]))
                .status(AnalysisStatus.READY)
                .type(AnalysisType.TWITTER_NEEL)
                .visibility(AnalysisVisibility.PUBLIC)
                .input(input)
                .createDate(Instant.now())
                .updateDate(Instant.now());

            this.analysisRepository.save(a);
        }
    }

    @Test
    public void findByOwner() {
        List<Analysis> analyses = this.analysisRepository.findByOwnerUid("user-1");

        assertNotNull(analyses);
        assertEquals(3, analyses.size());

        analyses = this.analysisRepository.findByOwnerUid("user-non-esistente");

        assertNotNull(analyses);
        assertEquals(0, analyses.size());
    }

    @Test
    public void findByOwnerPaged() {
        Pageable page = PageRequest.of(1, 2);
        Page<Analysis> analyses = this.analysisRepository.findByOwnerUid("user-1", page);

        assertNotNull(analyses);
        assertEquals(2, analyses.getTotalPages());
        assertEquals(3, analyses.getTotalElements());
        assertEquals(1, analyses.getNumberOfElements());
    }
}
