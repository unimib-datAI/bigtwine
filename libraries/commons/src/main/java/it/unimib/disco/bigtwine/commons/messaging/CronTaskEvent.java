package it.unimib.disco.bigtwine.commons.messaging;

import java.time.Instant;

public class CronTaskEvent implements Event {
    private String cronScheduleId;
    private String service;
    private String group;
    private String name;
    private int task;
    private int tasksCount;
    private Instant executedDate;

    public CronTaskEvent() {
    }

    public String getCronScheduleId() {
        return cronScheduleId;
    }

    public void setCronScheduleId(String cronScheduleId) {
        this.cronScheduleId = cronScheduleId;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTask() {
        return task;
    }

    public void setTask(int task) {
        this.task = task;
    }

    public int getTasksCount() {
        return tasksCount;
    }

    public void setTasksCount(int tasksCount) {
        this.tasksCount = tasksCount;
    }

    public Instant getExecutedDate() {
        return executedDate;
    }

    public void setExecutedDate(Instant executedDate) {
        this.executedDate = executedDate;
    }
}
