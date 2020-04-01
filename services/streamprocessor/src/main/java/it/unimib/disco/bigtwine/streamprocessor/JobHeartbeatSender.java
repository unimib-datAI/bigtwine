package it.unimib.disco.bigtwine.streamprocessor;

import it.unimib.disco.bigtwine.commons.messaging.JobHeartbeatEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

public class JobHeartbeatSender implements Serializable {
    private static final Logger LOG = LoggerFactory.getLogger(JobHeartbeatSender.class);

    private String kafkaBoostrapServer;
    private String topic;
    private String jobId;
    private int interval;

    private transient KafkaTemplate<String, JobHeartbeatEvent> kafkaTemplate;
    private transient long lastSentHeartbeatTs = 0;

    public JobHeartbeatSender() {
    }

    public JobHeartbeatSender(String kafkaBoostrapServer, String topic, String jobId, int interval) {
        this.kafkaBoostrapServer = kafkaBoostrapServer;
        this.topic = topic;
        this.jobId = jobId;
        this.interval = interval;
    }

    private ProducerFactory<String, JobHeartbeatEvent> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBoostrapServer);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ExtendedJsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    private KafkaTemplate<String, JobHeartbeatEvent> getKafkaTemplate() {
        if (this.kafkaTemplate == null) {
            this.kafkaTemplate = new KafkaTemplate<>(producerFactory());
        }

        return this.kafkaTemplate;
    }

    private boolean shouldSendHeartbeat() {
        if (this.interval < 0) {
            return true;
        }

        return System.currentTimeMillis() > (this.lastSentHeartbeatTs + (this.interval * 1000));
    }

    private void send(double progress, boolean isLast, boolean isFailed, String message, boolean force) {
        JobHeartbeatEvent event = new JobHeartbeatEvent();
        event.setJobId(this.jobId);
        event.setProgress(progress);
        event.setLast(isLast);
        event.setFailed(isFailed);
        event.setMessage(message);
        event.setTimestamp(Instant.now());

        if (force || this.shouldSendHeartbeat()) {
            LOG.info("Sending heartbeat for job {} ({}, {}, {})", getJobId(), isLast, isFailed, message);

            try {
                this.getKafkaTemplate().send(this.topic, event);
                this.lastSentHeartbeatTs = System.currentTimeMillis();
            } catch (Exception e) {
                // Ritardo il prossimo invio
                this.lastSentHeartbeatTs = this.lastSentHeartbeatTs + 1000;
                LOG.info("Failed to send heartbeat", e);
            }

        }
    }

    public void send(double progress, boolean force) {
        this.send(progress, false, false, null, force);
    }

    public void send(double progress) {
        this.send(progress, false);
    }

    public void sendLast(double progress) {
        this.send(progress, true, false, null, true);
    }

    public void sendLast() {
        this.sendLast(-1);
    }

    public void sendError(String message) {
        this.send(-1, false, true, message, true);
    }

    public String getKafkaBoostrapServer() {
        return kafkaBoostrapServer;
    }

    public void setKafkaBoostrapServer(String kafkaBoostrapServer) {
        this.kafkaBoostrapServer = kafkaBoostrapServer;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
