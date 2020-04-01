package it.unimib.disco.bigtwine.services.linkresolver.processors;

import it.unimib.disco.bigtwine.services.linkresolver.executors.SparqlSyncExecutor;
import it.unimib.disco.bigtwine.services.linkresolver.parsers.SparqlQueryResultParser;
import it.unimib.disco.bigtwine.services.linkresolver.producers.SparqlQueryProducer;

public class DbpediaSparqlSyncProcessor extends SparqlSyncProcessor {
    public DbpediaSparqlSyncProcessor(SparqlQueryProducer queryProducer, SparqlQueryResultParser resultParser, SparqlSyncExecutor executor) {
        super(queryProducer, resultParser, executor);
    }

    @Override
    public String getProcessorId() {
        return "dbpedia";
    }
}
