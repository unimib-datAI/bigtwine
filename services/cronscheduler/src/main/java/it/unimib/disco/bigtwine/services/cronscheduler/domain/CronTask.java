package it.unimib.disco.bigtwine.services.cronscheduler.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

import it.unimib.disco.bigtwine.services.cronscheduler.domain.enumeration.CronTaskStatus;

/**
 * A CronTask.
 */
@Document(collection = "cron_task")
@CompoundIndexes({
    @CompoundIndex(name = "schedule_task", def = "{'schedule.id' : 1, 'task': 1}")
})
public class CronTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("task")
    private Integer task;

    @NotNull
    @Field("status")
    private CronTaskStatus status;

    @Field("message")
    private String message;

    @Field("executed_date")
    private Instant executedDate;

    @Field("finished_date")
    private Instant finishedDate;

    @DBRef
    @Field("schedule")
    private CronSchedule schedule;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getTask() {
        return task;
    }

    public CronTask task(Integer task) {
        this.task = task;
        return this;
    }

    public void setTask(Integer task) {
        this.task = task;
    }

    public CronTaskStatus getStatus() {
        return status;
    }

    public CronTask status(CronTaskStatus status) {
        this.status = status;
        return this;
    }

    public void setStatus(CronTaskStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public CronTask message(String message) {
        this.message = message;
        return this;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getExecutedDate() {
        return executedDate;
    }

    public CronTask executedDate(Instant executedDate) {
        this.executedDate = executedDate;
        return this;
    }

    public void setExecutedDate(Instant executedDate) {
        this.executedDate = executedDate;
    }

    public Instant getFinishedDate() {
        return finishedDate;
    }

    public CronTask finishedDate(Instant finishedDate) {
        this.finishedDate = finishedDate;
        return this;
    }

    public void setFinishedDate(Instant finishedDate) {
        this.finishedDate = finishedDate;
    }

    public CronSchedule getSchedule() {
        return schedule;
    }

    public CronTask schedule(CronSchedule cronSchedule) {
        this.schedule = cronSchedule;
        return this;
    }

    public void setSchedule(CronSchedule cronSchedule) {
        this.schedule = cronSchedule;
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
        CronTask cronTask = (CronTask) o;
        if (cronTask.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), cronTask.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CronTask{" +
            "id=" + getId() +
            ", task=" + getTask() +
            ", status='" + getStatus() + "'" +
            ", message='" + getMessage() + "'" +
            ", executedDate='" + getExecutedDate() + "'" +
            ", finishedDate='" + getFinishedDate() + "'" +
            "}";
    }
}
