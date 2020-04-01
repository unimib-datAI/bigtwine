package it.unimib.disco.bigtwine.services.linkresolver.processors;

import it.unimib.disco.bigtwine.services.linkresolver.LinkType;
import it.unimib.disco.bigtwine.services.linkresolver.QueryType;
import it.unimib.disco.bigtwine.services.linkresolver.executors.ExecutorFactory;
import it.unimib.disco.bigtwine.services.linkresolver.executors.SparqlSyncExecutor;
import it.unimib.disco.bigtwine.services.linkresolver.parsers.QueryResultParserFactory;
import it.unimib.disco.bigtwine.services.linkresolver.parsers.SparqlQueryResultParser;
import it.unimib.disco.bigtwine.services.linkresolver.producers.QueryProducerFactory;
import it.unimib.disco.bigtwine.services.linkresolver.producers.SparqlQueryProducer;
import org.springframework.beans.factory.FactoryBean;

public class ProcessorFactory implements FactoryBean<Processor> {

    private ExecutorFactory executorFactory;
    private QueryProducerFactory queryProducerFactory;
    private QueryResultParserFactory queryResultParserFactory;
    private LinkType linkType;

    public LinkType getLinkType() {
        return linkType;
    }

    public void setLinkType(LinkType linkType) {
        this.linkType = linkType;
    }

    public ProcessorFactory(QueryProducerFactory queryProducerFactory, ExecutorFactory executorFactory, QueryResultParserFactory queryResultParserFactory) {
        this.queryProducerFactory = queryProducerFactory;
        this.executorFactory = executorFactory;
        this.queryResultParserFactory = queryResultParserFactory;
    }

    protected Processor getDbpediaSparqlSyncProcessor() throws Exception {
        QueryType qt = QueryType.sparql;
        return new DbpediaSparqlSyncProcessor(
            (SparqlQueryProducer) this.queryProducerFactory.getProducer(this.linkType, qt),
            (SparqlQueryResultParser) this.queryResultParserFactory.getResultParser(this.linkType, qt),
            (SparqlSyncExecutor) this.executorFactory.getExecutor(this.linkType, qt));
    }

    public Processor getProcessor(LinkType linkType) throws Exception {
        this.setLinkType(linkType);
        return this.getProcessor();
    }

    public Processor getProcessor() throws Exception {
        if (this.linkType == null) {
            throw new IllegalStateException("linkType not set");
        }

        switch (linkType) {
            case dbpediaResource:
                return this.getDbpediaSparqlSyncProcessor();
            default:
                return null;
        }
    }

    @Override
    public Processor getObject() throws Exception {
        return this.getProcessor();
    }

    @Override
    public Class<?> getObjectType() {
        if (this.linkType == null) {
            return null;
        }

        switch (linkType) {
            case dbpediaResource:
                return DbpediaSparqlSyncProcessor.class;
            default:
                return null;
        }
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
