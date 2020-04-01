package it.unimib.disco.bigtwine.services.analysis.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.unimib.disco.bigtwine.services.analysis.config.Constants;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;

@Document(collection = Constants.ANALYSIS_RESULTS_DB_COLLECTION)
@CompoundIndexes({
    @CompoundIndex(def = "{'analysis': 1}", name = "analysis"),
    @CompoundIndex(def = "{'payload.entities.category': 1}", name = "entity_category"),
    @CompoundIndex(def = "{'payload.status.text': 'text'}", name = "payload.status.text_text")
})
public class AnalysisResult<P extends AnalysisResultPayload> implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("process_date")
    private Instant processDate;

    @NotNull
    @Field("save_date")
    private Instant saveDate;

    @DBRef
    @Indexed
    @Field("analysis")
    @JsonIgnoreProperties("")
    private Analysis analysis;

    @NotNull
    @Field("payload")
    private P payload;

    public String getId() {
        return id;
    }

    public AnalysisResult<P> id(String id) {
        this.id = id;
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getProcessDate() {
        return processDate;
    }

    public AnalysisResult<P> processDate(Instant processDate) {
        this.processDate = processDate;
        return this;
    }

    public void setProcessDate(Instant processDate) {
        this.processDate = processDate;
    }

    public Instant getSaveDate() {
        return saveDate;
    }

    public AnalysisResult<P> saveDate(Instant saveDate) {
        this.saveDate = saveDate;
        return this;
    }

    public void setSaveDate(Instant saveDate) {
        this.saveDate = saveDate;
    }

    public Analysis getAnalysis() {
        return analysis;
    }

    public AnalysisResult<P> analysis(Analysis analysis) {
        this.analysis = analysis;
        return this;
    }

    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }

    public P getPayload() {
        return payload;
    }

    public AnalysisResult<P> payload(P payload) {
        this.payload = payload;
        return this;
    }

    public void setPayload(P payload) {
        this.payload = payload;
    }
}
