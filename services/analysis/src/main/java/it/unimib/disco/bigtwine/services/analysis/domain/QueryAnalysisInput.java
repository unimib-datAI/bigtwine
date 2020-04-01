package it.unimib.disco.bigtwine.services.analysis.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisInputType;

import java.util.List;
import java.util.Objects;

public class QueryAnalysisInput implements AnalysisInput {
    private List<String> tokens;
    private JoinOperator joinOperator;

    public QueryAnalysisInput() {
    }

    @Override
    @JsonProperty("type")
    public AnalysisInputType getType() {
        return AnalysisInputType.QUERY;
    }

    @Override
    @JsonProperty("isBounded")
    public boolean isBounded() {
        return false;
    }

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public QueryAnalysisInput tokens(List<String> tokens) {
        this.setTokens(tokens);
        return this;
    }

    public JoinOperator getJoinOperator() {
        return joinOperator;
    }

    public void setJoinOperator(JoinOperator joinOperator) {
        this.joinOperator = joinOperator;
    }

    public QueryAnalysisInput joinOperator(JoinOperator joinOperator) {
        this.setJoinOperator(joinOperator);
        return this;
    }

    public enum JoinOperator {
        ALL, ANY
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getTokens(), this.getJoinOperator());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        QueryAnalysisInput input = (QueryAnalysisInput) o;
        if (input.getTokens() == null || getTokens() == null ||
            input.getJoinOperator() == null || getJoinOperator() == null) {
            return false;
        }
        return Objects.equals(getTokens(), input.getTokens()) &&
            Objects.equals(getJoinOperator(), input.getJoinOperator());
    }
}
