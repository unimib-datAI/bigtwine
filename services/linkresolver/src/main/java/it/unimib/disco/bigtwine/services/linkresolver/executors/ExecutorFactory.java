package it.unimib.disco.bigtwine.services.linkresolver.executors;

import it.unimib.disco.bigtwine.commons.executors.Executor;
import it.unimib.disco.bigtwine.services.linkresolver.LinkType;
import it.unimib.disco.bigtwine.services.linkresolver.QueryType;
import org.springframework.beans.factory.FactoryBean;

public class ExecutorFactory implements FactoryBean<Executor> {

    protected LinkType linkType;
    protected QueryType queryType;

    public LinkType getLinkType() {
        return linkType;
    }

    public void setLinkType(LinkType linkType) {
        this.linkType = linkType;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryType queryType) {
        this.queryType = queryType;
    }

    public Executor getExecutor() throws Exception {
        if (this.linkType == null) {
            throw new IllegalStateException("linkType not set");
        }

        if (this.queryType == null) {
            throw new IllegalStateException("queryType not set");
        }

        if (linkType.equals(LinkType.dbpediaResource) && queryType.equals(QueryType.sparql)) {
            return new DbpediaSparqlSyncExecutor();
        }else {
            return null;
        }
    }

    public Executor getExecutor(LinkType linkType, QueryType queryType) throws Exception {
        this.setLinkType(linkType);
        this.setQueryType(queryType);
        return this.getExecutor();
    }

    @Override
    public Executor getObject() throws Exception {
        return this.getExecutor();
    }

    @Override
    public Class<?> getObjectType() {
        if (this.linkType == null) {
            return null;
        }

        if (this.queryType == null) {
            return null;
        }

        if (linkType.equals(LinkType.dbpediaResource) && queryType.equals(QueryType.sparql)) {
            return DbpediaSparqlSyncExecutor.class;
        }else {
            return null;
        }
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
