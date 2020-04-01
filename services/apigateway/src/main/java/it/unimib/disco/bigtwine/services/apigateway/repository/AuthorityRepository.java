package it.unimib.disco.bigtwine.services.apigateway.repository;

import it.unimib.disco.bigtwine.services.apigateway.domain.Authority;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the Authority entity.
 */
public interface AuthorityRepository extends MongoRepository<Authority, String> {
}
