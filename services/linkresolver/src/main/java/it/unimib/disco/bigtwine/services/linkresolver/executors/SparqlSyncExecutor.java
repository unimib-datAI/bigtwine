package it.unimib.disco.bigtwine.services.linkresolver.executors;

import it.unimib.disco.bigtwine.commons.executors.SyncExecutor;
import org.apache.jena.query.*;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

public interface SparqlSyncExecutor extends SyncExecutor {
    default ResultSet queryEndpoint(String szQuery, String szEndpoint)
        throws Exception
    {
        // Create a Query with the given String
        Query query = QueryFactory.create(szQuery);

        // Create the Execution Factory using the given Endpoint
        QueryExecution qexec = QueryExecutionFactory.sparqlService(
            szEndpoint, query);

        // Set Timeout
        ((QueryEngineHTTP)qexec).addParam("timeout", "10000");


        // Execute Query
        return qexec.execSelect();
    }

    String getEndpoint();
    default ResultSet query(String query) throws Exception {
        return this.queryEndpoint(query, this.getEndpoint());
    }
}
