package it.unimib.disco.bigtwine.services.cronscheduler.repository;

import it.unimib.disco.bigtwine.services.cronscheduler.domain.CronEntry;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data MongoDB repository for the CronEntry entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CronEntryRepository extends MongoRepository<CronEntry, String> {
    List<CronEntry> findAllByActive(boolean active);
}
