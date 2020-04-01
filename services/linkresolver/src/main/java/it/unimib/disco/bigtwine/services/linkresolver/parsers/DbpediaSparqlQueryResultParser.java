package it.unimib.disco.bigtwine.services.linkresolver.parsers;

import it.unimib.disco.bigtwine.services.linkresolver.domain.ExtraField;
import it.unimib.disco.bigtwine.services.linkresolver.domain.Resource;
import it.unimib.disco.bigtwine.services.linkresolver.domain.Coordinates;
import it.unimib.disco.bigtwine.services.linkresolver.QueryType;
import org.apache.commons.lang.StringUtils;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.RDFNode;

import java.util.*;

public final class DbpediaSparqlQueryResultParser implements SparqlQueryResultParser {

    private ResultSet resultSet;
    private ExtraField[] extraFields;
    private Map<String, ExtraField> extraFieldsByName;

    @Override
    public QueryType getQueryType() {
        return QueryType.sparql;
    }

    @Override
    public void setResultSet(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public Resource parse(ResultSet resultSet) {
        this.resultSet = resultSet;
        return this.parse();
    }

    @Override
    public Resource parse(ResultSet resultSet, ExtraField[] extraFields) {
        this.resultSet = resultSet;
        this.extraFields = extraFields;
        return this.parse();
    }

    @Override
    public Resource parse() {
        if (this.resultSet == null) throw new IllegalStateException("resultSet is null");

        Resource res = new Resource();
        Double lat = null;
        Double lng = null;
        Set<String> extraFieldNames = this.getExtraFieldNames();

        if (this.resultSet.hasNext()) {
            // Get Result
            QuerySolution qs = this.resultSet.next();

            // Get Variable Names
            Iterator<String> itVars = qs.varNames();

            // Display Result
            while (itVars.hasNext()) {
                String szVar = itVars.next().toString();
                RDFNode node = qs.get(szVar);
                String szVal;

                if (node.isResource()) {
                    szVal = node.asResource().getURI();
                }else if (node.isLiteral()) {
                    szVal = node.asLiteral().getLexicalForm();
                }else {
                    continue;
                }

                switch (szVar) {
                    case "uri":
                        res.setUrl(szVal);
                        break;
                    case "name":
                        res.setName(szVal);
                        break;
                    case "name_w":
                    case "name_f":
                        if (res.getName() == null)
                            res.setName(szVal);
                        break;
                    case "abstract":
                        res.setShortDesc(szVal);
                        break;
                    case "tag":
                        if (!szVal.isEmpty())
                            res.setTag(szVal);
                        break;
                    case "thumb":
                        res.setThumbLarge(szVal);
                        res.setThumb(szVal.replace("width=300", "width=88"));
                        break;
                    case "lat":
                        try {
                            lat = Double.parseDouble(szVal);
                        }catch (Exception e) {
                            lat = null;
                        }
                        break;
                    case "lng":
                        try {
                            lng = Double.parseDouble(szVal);
                        }catch (Exception e) {
                            lng = null;
                        }
                        break;
                }

                if (extraFieldNames.contains(szVar) && StringUtils.isNotBlank(szVal)) {
                    if (res.getExtra() == null) {
                        res.setExtra(new HashMap<>());
                    }

                    Object extraFieldValue = szVal;
                    if (this.extraFieldsByName.get(szVar).isList()) {
                        extraFieldValue = ((String)extraFieldValue).split("\\|");
                    }

                    res.getExtra().put(szVar, extraFieldValue);
                }
            }
        }

        if (lat != null && lng != null) {
            res.setCoordinates(new Coordinates(lat, lng));
        }

        if (res.getUrl() != null) {
            return res;
        }else {
            return null;
        }
    }

    private Set<String> getExtraFieldNames() {
        Set<String> names = new HashSet<>();
        if (extraFields == null || extraFields.length == 0) {
            return names;
        }

        if (this.extraFieldsByName == null) {
            this.extraFieldsByName = new HashMap<>();
            for (ExtraField extraField: extraFields) {
                this.extraFieldsByName.put(extraField.getSaveAs(), extraField);
            }
        }

        return this.extraFieldsByName.keySet();
    }
}
