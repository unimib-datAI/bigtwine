package it.unimib.disco.bigtwine.services.linkresolver.producers;

import it.unimib.disco.bigtwine.services.linkresolver.domain.ExtraField;
import it.unimib.disco.bigtwine.services.linkresolver.domain.Link;
import it.unimib.disco.bigtwine.services.linkresolver.QueryType;

public final class DbpediaSparqlQueryProducer implements SparqlQueryProducer {

    @Override
    public QueryType getQueryType() {
        return QueryType.sparql;
    }

    @Override
    public String buildQuery(Link link, ExtraField[] extraFields) {
        String whereExtra = "";
        String selectExtra = "";
        String groupBy = "";
        if (extraFields != null && extraFields.length > 0) {
            boolean needsGroupBy = false;
            StringBuilder selectBuilder = new StringBuilder();
            StringBuilder whereBuilder = new StringBuilder();

            for (ExtraField extraField : extraFields) {
                String fieldName = extraField.getSaveAs();

                if (!extraField.isList()) {
                    selectBuilder.append(String.format("?%s ", fieldName));
                } else {
                    selectBuilder.append(String.format(
                        "(group_concat(?_%s;separator=\"|\") as ?%s) ", fieldName, fieldName
                    ));
                    fieldName = "_" + fieldName;
                    needsGroupBy = true;
                }

                whereBuilder.append(
                    String.format("OPTIONAL {?uri %s ?%s.}\n", extraField.getValuePath(), fieldName)
                );
            }

            selectExtra = selectBuilder.toString();
            whereExtra = whereBuilder.toString();

            if (needsGroupBy) {
                groupBy = "GROUP BY ?uri ?name ?name_f ?name_w ?thumb ?abstract ?wiki_id ?lat ?lng ?tag";
                for (ExtraField extraField : extraFields) {
                    if (extraField.isList()) {
                        groupBy += " ?" + extraField.getSaveAs();
                    }
                }
                groupBy += "\n";
            }
        }

        String szQuery =
            "PREFIX dbo: <http://dbpedia.org/ontology/>\n" +
                "PREFIX dbp: <http://dbpedia.org/property/> \n" +
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX wgs: <http://www.w3.org/2003/01/geo/wgs84_pos#>\n" +
                "SELECT ?uri ?name ?name_f ?name_w ?thumb ?abstract ?wiki_id ?lat ?lng ?tag "+selectExtra+"\n" +
                "WHERE {\n" +
                "    VALUES ?tag {'{RESOURCE_TAG}'}\n" +
                "    OPTIONAL {?uri dbo:wikiPageID ?wiki_id.}\n" +
                "    OPTIONAL {?uri rdfs:label ?name . FILTER(LANG(?name)='en')}\n" +
                "    OPTIONAL {?uri dbp:name ?name_w . FILTER(LANG(?name_w)='en')}\n" +
                "    OPTIONAL {?uri foaf:name ?name_f . FILTER(LANG(?name_f)='en')}\n" +
                "    OPTIONAL {?uri dbo:abstract ?abstract . FILTER(LANG(?abstract)='en') }\n" +
                "    OPTIONAL {?uri dbo:thumbnail ?thumb.}\n" +
                "    OPTIONAL {?uri wgs:lat ?lat.}\n" +
                "    OPTIONAL {?uri wgs:long ?lng.}\n" +
                whereExtra +
                "    FILTER (?uri = <{RESOURCE_URI}>)\n" +
                "}\n" +
                groupBy +
                "LIMIT 1";

        return szQuery
            .replace("{RESOURCE_URI}", link.getUrl())
            .replace("{RESOURCE_TAG}", link.getTag() != null ? link.getTag() : "");
    }

    @Override
    public String buildQuery(Link link) {
        return buildQuery(link, null);
    }
}
