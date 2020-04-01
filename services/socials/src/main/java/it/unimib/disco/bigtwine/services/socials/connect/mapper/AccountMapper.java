package it.unimib.disco.bigtwine.services.socials.connect.mapper;

import it.unimib.disco.bigtwine.services.socials.connect.dto.AccountDTO;
import it.unimib.disco.bigtwine.services.socials.domain.Account;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AccountMapper {

    AccountMapper INSTANCE = Mappers.getMapper( AccountMapper.class );

    AccountDTO accountToAccountDTO(Account account);
}
