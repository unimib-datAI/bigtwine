package it.unimib.disco.bigtwine.services.linkresolver.parsers;

import it.unimib.disco.bigtwine.services.linkresolver.domain.ExtraField;
import it.unimib.disco.bigtwine.services.linkresolver.domain.Resource;
import org.apache.jena.query.ResultSet;

public interface SparqlQueryResultParser extends QueryResultParser {
    void setResultSet(ResultSet resultSet);
    Resource parse(ResultSet resultSet);
    Resource parse(ResultSet resultSet, ExtraField[] extraFields);
}
