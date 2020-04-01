package it.unimib.disco.bigtwine.services.socials.client;

import it.unimib.disco.bigtwine.services.socials.config.Constants;
import it.unimib.disco.bigtwine.services.socials.connect.dto.AccountDTO;
import it.unimib.disco.bigtwine.services.socials.domain.Account;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(name = Constants.GATEWAY_SERVICE_ID)
public interface AuthServiceClient {
    @RequestMapping(method = RequestMethod.POST, path = "/api/users", consumes = "application/json")
    Account createAccount(AccountDTO accountInfo);

    @RequestMapping(method = RequestMethod.GET, path = "/api/users/id/{id}", consumes = "application/json")
    Account findAccountById(@PathVariable(name = "id") String id);


    @RequestMapping(method = RequestMethod.GET, path = "/api/users/{login}", consumes = "application/json")
    Account findAccountByLogin(@PathVariable(name = "login") String login);
}
