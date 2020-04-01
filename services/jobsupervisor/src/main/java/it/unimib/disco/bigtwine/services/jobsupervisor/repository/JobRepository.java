package it.unimib.disco.bigtwine.services.jobsupervisor.repository;

import it.unimib.disco.bigtwine.services.jobsupervisor.domain.Job;
import it.unimib.disco.bigtwine.services.jobsupervisor.domain.enumeration.JobType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface JobRepository extends MongoRepository<Job, String> {
    @Query("{ $and: [{ 'analysis.id': ?0 }, { 'job_type': ?1 }, { 'running': true }] }")
    Stream<Job> findRunningJobForAnalysisAndJobType(String analysisId, JobType jobType);

    @Query("{ $and: [{ 'analysis.id': ?0 }, { 'running': true }] }")
    Stream<Job> findRunningJobsForAnalysis(String analysisId);
}
