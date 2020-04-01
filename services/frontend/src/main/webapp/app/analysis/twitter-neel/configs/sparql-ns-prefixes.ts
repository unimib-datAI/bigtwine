export const SPARQL_NS_PREFIXES = {
    owl: 'http://www.w3.org/2002/07/owl#',
    dbo: 'http://dbpedia.org/ontology/',
    dbc: 'http://dbpedia.org/resource/Category:',
    dbr: 'http://dbpedia.org/resource/',
    schema: 'http://schema.org/',
    yago: 'http://dbpedia.org/class/yago/',
    foaf: 'http://xmlns.com/foaf/0.1/',
    dul: 'http://www.ontologydesignpatterns.org/ont/dul/DUL.owl#',
    wikidata: 'http://www.wikidata.org/entity/',
    'umbel-rc': 'http://umbel.org/umbel/rc/',
};

export const SPARQL_NS_PREFIXES_REV = Object.keys(SPARQL_NS_PREFIXES).reduce((ret, key) => {
    ret[SPARQL_NS_PREFIXES[key]] = key;
    return ret;
}, {});
