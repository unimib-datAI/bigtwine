package it.unimib.disco.bigtwine.services.analysis.validation.analysis.input;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unimib.disco.bigtwine.services.analysis.domain.AnalysisInput;
import it.unimib.disco.bigtwine.services.analysis.domain.QueryAnalysisInput;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisInputType;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

public class AnalysisInputSerializationTest {

    private static final List<String> VALID_TOKENS = Arrays.asList("query", "di", "prova");

    private ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS);

        return mapper;
    }

    @Test
    public void testValidInputSerialization() throws Exception {
        AnalysisInput input = new QueryAnalysisInput()
            .tokens(VALID_TOKENS)
            .joinOperator(QueryAnalysisInput.JoinOperator.ANY);

        String json = getObjectMapper().writeValueAsString(input);

        assertNotNull(json);

        QueryAnalysisInput deserializedInput = getObjectMapper().readValue(json, QueryAnalysisInput.class);

        assertEquals(input, deserializedInput);
        assertEquals(deserializedInput.getType(), AnalysisInputType.QUERY);
        assertEquals(deserializedInput.getTokens(), VALID_TOKENS);
        assertEquals(deserializedInput.getJoinOperator(), QueryAnalysisInput.JoinOperator.ANY);
    }

    @Test(expected = Exception.class)
    public void testInvalidDeserialization() throws Exception {
        String json = "{\"type\": \"DATASET\", \"tokens\": [\"query\", \"di\", \"prova\"], \"joinOperator\": \"ANY\"}";

        QueryAnalysisInput deserializedInput = getObjectMapper().readValue(json, QueryAnalysisInput.class);

        assertEquals(deserializedInput.getType(), AnalysisInputType.QUERY); // proprietà type deve essere ignorata
        assertEquals(deserializedInput.getTokens(), VALID_TOKENS);
        assertEquals(deserializedInput.getJoinOperator(), QueryAnalysisInput.JoinOperator.ANY);
    }

    @Test
    public void testGenericDeserialization() throws Exception {
        String json = "{\"type\": \"QUERY\", \"tokens\": [\"query\", \"di\", \"prova\"], \"joinOperator\": \"ANY\"}";

        AnalysisInput deserializedInput = getObjectMapper().readValue(json, AnalysisInput.class);

        assertEquals(deserializedInput.getType(), AnalysisInputType.QUERY);
        assertTrue(deserializedInput instanceof QueryAnalysisInput);
        assertEquals(((QueryAnalysisInput)deserializedInput).getTokens(), VALID_TOKENS);
        assertEquals(((QueryAnalysisInput)deserializedInput).getJoinOperator(), QueryAnalysisInput.JoinOperator.ANY);
    }

    @Test
    public void testCaseInsensitiveDeserialization() throws Exception {
        String json = "{\"type\": \"QUERY\", \"tokens\": [\"query\", \"di\", \"prova\"], \"joinOperator\": \"any\"}";

        AnalysisInput deserializedInput = getObjectMapper().readValue(json, AnalysisInput.class);

        assertEquals(deserializedInput.getType(), AnalysisInputType.QUERY);
        assertTrue(deserializedInput instanceof QueryAnalysisInput);
        assertEquals(((QueryAnalysisInput)deserializedInput).getTokens(), VALID_TOKENS);
        assertEquals(((QueryAnalysisInput)deserializedInput).getJoinOperator(), QueryAnalysisInput.JoinOperator.ANY);
    }
}
