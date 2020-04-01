package it.unimib.disco.bigtwine.services.cronscheduler.repository;

import it.unimib.disco.bigtwine.services.cronscheduler.domain.CronTask;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data MongoDB repository for the CronTask entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CronTaskRepository extends MongoRepository<CronTask, String> {
    List<CronTask> findAllByScheduleId(String id);
    Optional<CronTask> findByScheduleIdAndTask(String id, int task);
    void deleteAllByScheduleId(String id);
}
