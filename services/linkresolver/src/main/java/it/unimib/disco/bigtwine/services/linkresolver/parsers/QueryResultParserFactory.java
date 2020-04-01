package it.unimib.disco.bigtwine.services.linkresolver.parsers;

import it.unimib.disco.bigtwine.services.linkresolver.LinkType;
import it.unimib.disco.bigtwine.services.linkresolver.QueryType;
import org.springframework.beans.factory.FactoryBean;

public class QueryResultParserFactory implements FactoryBean<QueryResultParser> {

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

    public QueryResultParser getResultParser() throws Exception {
        if (this.linkType == null) {
            throw new IllegalStateException("linkType not set");
        }

        if (this.queryType == null) {
            throw new IllegalStateException("queryType not set");
        }

        if (linkType.equals(LinkType.dbpediaResource) && queryType.equals(QueryType.sparql)) {
            return new DbpediaSparqlQueryResultParser();
        }else {
            return null;
        }
    }

    public QueryResultParser getResultParser(LinkType linkType, QueryType queryType) throws Exception {
        this.setLinkType(linkType);
        this.setQueryType(queryType);
        return this.getResultParser();
    }

    @Override
    public QueryResultParser getObject() throws Exception {
        return this.getResultParser();
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
            return DbpediaSparqlQueryResultParser.class;
        }else {
            return null;
        }
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
