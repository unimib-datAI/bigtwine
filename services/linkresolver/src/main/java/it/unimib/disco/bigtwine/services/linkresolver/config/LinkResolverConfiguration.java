package it.unimib.disco.bigtwine.services.linkresolver.config;

import it.unimib.disco.bigtwine.commons.messaging.KafkaUtils;
import it.unimib.disco.bigtwine.services.linkresolver.executors.ExecutorFactory;
import it.unimib.disco.bigtwine.services.linkresolver.parsers.QueryResultParserFactory;
import it.unimib.disco.bigtwine.services.linkresolver.processors.ProcessorFactory;
import it.unimib.disco.bigtwine.services.linkresolver.producers.QueryProducerFactory;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class LinkResolverConfiguration {

    @Value("${spring.cloud.stream.kafka.binder.brokers}")
    private String kafkaBrokers;

    @Value("${spring.cloud.stream.kafka.binder.defaultBrokerPort:9092}")
    private Integer kafkaDefaultBrokerPort;

    @Bean
    public ExecutorFactory getExecutorFactory() {
        return new ExecutorFactory();
    }

    @Bean
    public QueryProducerFactory getQueryProducerFactory() {
        return new QueryProducerFactory();
    }

    @Bean
    public QueryResultParserFactory getQueryResultParserFactory() {
        return new QueryResultParserFactory();
    }

    @Bean
    public ProcessorFactory getProcessorFactory() {
        return new ProcessorFactory(
            this.getQueryProducerFactory(),
            this.getExecutorFactory(),
            this.getQueryResultParserFactory());
    }

    @Bean
    public ProducerFactory<Integer, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    public Map<String, Object> producerConfigs() {
        String bootstrapServers = KafkaUtils.buildBootstrapServersConfig(this.kafkaBrokers, this.kafkaDefaultBrokerPort);

        // See https://kafka.apache.org/documentation/#producerconfigs for more properties
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return props;
    }

    @Bean
    public KafkaTemplate<Integer, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
