package it.unimib.disco.bigtwine.services.analysis.domain;

import org.springframework.data.mongodb.core.mapping.Field;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;

import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisStatus;

import it.unimib.disco.bigtwine.services.analysis.domain.enumeration.AnalysisErrorCode;

/**
 * A AnalysisStatusHistory.
 */
public class AnalysisStatusHistory implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    @Field("new_status")
    private AnalysisStatus newStatus;

    @Field("old_status")
    private AnalysisStatus oldStatus;

    @Field("user")
    private User user;

    @Field("error_code")
    private AnalysisErrorCode errorCode;

    @Field("message")
    private String message;

    @NotNull
    @Field("date")
    private Instant date;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public AnalysisStatus getNewStatus() {
        return newStatus;
    }

    public AnalysisStatusHistory newStatus(AnalysisStatus newStatus) {
        this.newStatus = newStatus;
        return this;
    }

    public void setNewStatus(AnalysisStatus newStatus) {
        this.newStatus = newStatus;
    }

    public AnalysisStatus getOldStatus() {
        return oldStatus;
    }

    public AnalysisStatusHistory oldStatus(AnalysisStatus oldStatus) {
        this.oldStatus = oldStatus;
        return this;
    }

    public void setOldStatus(AnalysisStatus oldStatus) {
        this.oldStatus = oldStatus;
    }

    public User getUser() {
        return user;
    }

    public AnalysisStatusHistory user(User user) {
        this.user = user;
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AnalysisErrorCode getErrorCode() {
        return errorCode;
    }

    public AnalysisStatusHistory errorCode(AnalysisErrorCode errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public void setErrorCode(AnalysisErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public AnalysisStatusHistory message(String message) {
        this.message = message;
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getDate() {
        return date;
    }

    public AnalysisStatusHistory date(Instant date) {
        this.date = date;
        return this;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public String toString() {
        return "AnalysisStatusHistory{" +
            ", newStatus='" + getNewStatus() + "'" +
            ", oldStatus='" + getOldStatus() + "'" +
            ", user='" + getUser() + "'" +
            ", errorCode='" + getErrorCode() + "'" +
            ", message='" + getMessage() + "'" +
            ", date='" + getDate() + "'" +
            "}";
    }
}
