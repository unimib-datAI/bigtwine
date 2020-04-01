package it.unimib.disco.bigtwine.commons.messaging;

import java.time.Instant;

public class CronTaskCompletionEvent implements Event {
    private String cronScheduleId;
    private int task;
    private boolean failed;
    private String message;
    private Instant finishedDate;

    public CronTaskCompletionEvent() {
    }

    public String getCronScheduleId() {
        return cronScheduleId;
    }

    public void setCronScheduleId(String cronScheduleId) {
        this.cronScheduleId = cronScheduleId;
    }

    public int getTask() {
        return task;
    }

    public void setTask(int task) {
        this.task = task;
    }

    public boolean isFailed() {
        return failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getFinishedDate() {
        return finishedDate;
    }

    public void setFinishedDate(Instant finishedDate) {
        this.finishedDate = finishedDate;
    }
}
