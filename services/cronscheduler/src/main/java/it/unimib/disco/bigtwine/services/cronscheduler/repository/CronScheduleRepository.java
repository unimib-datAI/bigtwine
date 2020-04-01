package it.unimib.disco.bigtwine.services.cronscheduler.repository;

import it.unimib.disco.bigtwine.services.cronscheduler.domain.CronSchedule;
import it.unimib.disco.bigtwine.services.cronscheduler.domain.enumeration.CronStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;


/**
 * Spring Data MongoDB repository for the CronSchedule entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CronScheduleRepository extends MongoRepository<CronSchedule, String> {
    List<CronSchedule> findAllByEntryIdAndStatus(String id, CronStatus status, Pageable page);
    long countAllByEntryIdAndStatus(String id, CronStatus status);
    Stream<CronSchedule> findAllByUpdatedDateBefore(Instant date);
}
