package it.unimib.disco.bigtwine.services.linkresolver.executors;


public class DbpediaSparqlSyncExecutor implements SparqlSyncExecutor {
    @Override
    public String getEndpoint() {
        return "http://dbpedia.org/sparql";
    }

    @Override
    public String getExecutorId() {
        return "dbpedia-sparql";
    }
}
