package it.unimib.disco.bigtwine.streamprocessor.source;

import it.unimib.disco.bigtwine.commons.messaging.JobHeartbeatEvent;
import org.apache.flink.streaming.api.functions.source.SourceFunction;

import java.time.Instant;

class JobHeartbeatSource implements SourceFunction<JobHeartbeatEvent> {
    private final String jobId;
    private final int heartbeatInterval;
    private boolean shouldStop;
    private int counter;

    public JobHeartbeatSource(String jobId, int heartbeatInterval) {
        this.jobId = jobId;
        this.heartbeatInterval = heartbeatInterval;
        shouldStop = false;
        counter = 0;
    }

    @Override
    public void run(SourceContext<JobHeartbeatEvent> ctx) throws Exception {
        while (!shouldStop) {
            if (counter == heartbeatInterval) {
                JobHeartbeatEvent event = new JobHeartbeatEvent();
                event.setTimestamp(Instant.now());
                event.setJobId(this.jobId);

                ctx.collect(event);
                counter = 0;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) { }

            counter++;
        }
    }

    @Override
    public void cancel() {
        shouldStop = true;
    }
}
