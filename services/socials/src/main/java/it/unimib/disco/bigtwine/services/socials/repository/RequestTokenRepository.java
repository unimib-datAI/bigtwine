package it.unimib.disco.bigtwine.services.socials.repository;

import it.unimib.disco.bigtwine.services.socials.domain.RequestToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RequestTokenRepository extends MongoRepository<RequestToken, String> {
    Optional<RequestToken> findByTokenValue(String value);
}
