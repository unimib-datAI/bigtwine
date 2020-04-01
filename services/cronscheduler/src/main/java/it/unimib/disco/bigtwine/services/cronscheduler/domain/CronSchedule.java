package it.unimib.disco.bigtwine.services.cronscheduler.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import it.unimib.disco.bigtwine.services.cronscheduler.domain.enumeration.CronStatus;

/**
 * A CronSchedule.
 */
@Document(collection = "cron_schedule")
public class CronSchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("status")
    private CronStatus status;

    @Min(value = 1)
    @Field("tasks_count")
    private Integer tasksCount;

    @Field("created_date")
    private Instant createdDate;

    @Field("updated_date")
    private Instant updatedDate;

    @Field("scheduled_date")
    private Instant scheduledDate;

    @Field("executed_date")
    private Instant executedDate;

    @Field("finished_date")
    private Instant finishedDate;

    @DBRef
    @Field("entry")
    private CronEntry entry;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public CronStatus getStatus() {
        return status;
    }

    public CronSchedule status(CronStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(CronStatus status) {
        this.status = status;
    }

    public Integer getTasksCount() {
        return tasksCount;
    }

    public CronSchedule tasksCount(Integer tasksCount) {
        this.tasksCount = tasksCount;
        return this;
    }

    public void setTasksCount(Integer tasksCount) {
        this.tasksCount = tasksCount;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public CronSchedule createdDate(Instant createdDate) {
        this.createdDate = createdDate;
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public Instant getUpdatedDate() {
        return updatedDate;
    }

    public CronSchedule updatedDate(Instant updatedDate) {
        this.updatedDate = updatedDate;
        return this;
    }

    public void setUpdatedDate(Instant updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Instant getScheduledDate() {
        return scheduledDate;
    }

    public CronSchedule scheduledDate(Instant scheduledDate) {
        this.scheduledDate = scheduledDate;
        return this;
    }

    public void setScheduledDate(Instant scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public Instant getExecutedDate() {
        return executedDate;
    }

    public CronSchedule executedDate(Instant executedDate) {
        this.executedDate = executedDate;
        return this;
    }

    public void setExecutedDate(Instant executedDate) {
        this.executedDate = executedDate;
    }

    public Instant getFinishedDate() {
        return finishedDate;
    }

    public CronSchedule finishedDate(Instant finishedDate) {
        this.finishedDate = finishedDate;
        return this;
    }

    public void setFinishedDate(Instant finishedDate) {
        this.finishedDate = finishedDate;
    }

    public CronEntry getEntry() {
        return entry;
    }

    public CronSchedule entry(CronEntry cronEntry) {
        this.entry = cronEntry;
        return this;
    }

    public void setEntry(CronEntry cronEntry) {
        this.entry = cronEntry;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CronSchedule cronSchedule = (CronSchedule) o;
        if (cronSchedule.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), cronSchedule.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CronSchedule{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", tasksCount=" + getTasksCount() +
            ", createdDate='" + getCreatedDate() + "'" +
            ", updatedDate='" + getUpdatedDate() + "'" +
            ", scheduledDate='" + getScheduledDate() + "'" +
            ", executedDate='" + getExecutedDate() + "'" +
            ", finishedDate='" + getFinishedDate() + "'" +
            "}";
    }
}
