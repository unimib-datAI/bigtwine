package it.unimib.disco.bigtwine.services.linkresolver.parsers;

import it.unimib.disco.bigtwine.services.linkresolver.domain.Resource;
import it.unimib.disco.bigtwine.services.linkresolver.QueryType;

public interface QueryResultParser {
    QueryType getQueryType();
    Resource parse();
}
