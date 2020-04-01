package it.unimib.disco.bigtwine.services.cronscheduler.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;
import javax.validation.constraints.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A CronEntry.
 */
@Document(collection = "cron_entry")
public class CronEntry implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    @Field("service")
    private String service;

    @NotNull
    @Field("group")
    private String group;

    @NotNull
    @Field("name")
    private String name;

    @NotNull
    @Min(value = 0)
    @Field("parallelism")
    private Integer parallelism;

    @Field("active")
    private Boolean active;

    @NotNull
    @Field("cron_expr")
    private String cronExpr;

    @Field("last_run")
    private Instant lastRun;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getService() {
        return service;
    }

    public CronEntry service(String service) {
        this.service = service;
        return this;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getGroup() {
        return group;
    }

    public CronEntry group(String group) {
        this.group = group;
        return this;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public CronEntry name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParallelism() {
        return parallelism;
    }

    public CronEntry parallelism(Integer parallelism) {
        this.parallelism = parallelism;
        return this;
    }

    public void setParallelism(Integer parallelism) {
        this.parallelism = parallelism;
    }

    public Boolean isActive() {
        return active;
    }

    public CronEntry active(Boolean active) {
        this.active = active;
        return this;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getCronExpr() {
        return cronExpr;
    }

    public CronEntry cronExpr(String cronExpr) {
        this.cronExpr = cronExpr;
        return this;
    }

    public void setCronExpr(String cronExpr) {
        this.cronExpr = cronExpr;
    }

    public Instant getLastRun() {
        return lastRun;
    }

    public CronEntry lastRun(Instant lastRun) {
        this.lastRun = lastRun;
        return this;
    }

    public void setLastRun(Instant lastRun) {
        this.lastRun = lastRun;
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
        CronEntry cronEntry = (CronEntry) o;
        if (cronEntry.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), cronEntry.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CronEntry{" +
            "id=" + getId() +
            ", service='" + getService() + "'" +
            ", group='" + getGroup() + "'" +
            ", name='" + getName() + "'" +
            ", parallelism=" + getParallelism() +
            ", active='" + isActive() + "'" +
            ", cronExpr='" + getCronExpr() + "'" +
            ", lastRun='" + getLastRun() + "'" +
            "}";
    }
}
