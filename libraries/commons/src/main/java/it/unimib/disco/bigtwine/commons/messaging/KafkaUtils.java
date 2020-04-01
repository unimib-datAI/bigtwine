package it.unimib.disco.bigtwine.commons.messaging;

import java.util.Arrays;
import java.util.stream.Collectors;

public class KafkaUtils {
    public static String buildBootstrapServersConfig(String kafkaBrokers, Integer kafkaDefaultBrokerPort) {
        return Arrays.stream(kafkaBrokers.split(","))
                .map((broker) -> {
                    if (broker.contains(":")) {
                        return broker;
                    } else {
                        return broker + ":" + kafkaDefaultBrokerPort;
                    }
                })
                .collect(Collectors.joining("," ));
    }
}
