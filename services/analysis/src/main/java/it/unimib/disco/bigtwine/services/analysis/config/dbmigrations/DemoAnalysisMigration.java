package it.unimib.disco.bigtwine.services.analysis.config.dbmigrations;

import com.github.mongobee.changeset.ChangeLog;
import com.github.mongobee.changeset.ChangeSet;
import it.unimib.disco.bigtwine.services.analysis.domain.Analysis;
import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisInput;
import it.unimib.disco.bigtwine.services.analysis.domain.QueryAnalysisInput;
import it.unimib.disco.bigtwine.services.analysis.domain.User;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisStatus;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisType;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisVisibility;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.time.Instant;
import java.util.Arrays;

@ChangeLog(order = "002")
public class DemoAnalysisMigration {
    @ChangeSet(order = "01", id = "addDemoAnalyses", author = "fausto")
    public void addDemoAnalyses(MongoTemplate mongoTemplate) {
        AnalysisInput analysisInput = new QueryAnalysisInput()
            .joinOperator(QueryAnalysisInput.JoinOperator.ANY)
            .tokens(Arrays.asList("italy", "milan", "venice", "rome"));
        User owner = new User()
            .uid("demo")
            .username("demo");

        Analysis analysis = new Analysis()
            .type(AnalysisType.TWITTER_NEEL)
            .input(analysisInput)
            .status(AnalysisStatus.STOPPED)
            .visibility(AnalysisVisibility.PRIVATE)
            .owner(owner)
            .createDate(Instant.now())
            .updateDate(Instant.now());

        mongoTemplate.save(analysis);
    }
}
