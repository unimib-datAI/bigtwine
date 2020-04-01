package it.unimib.disco.bigtwine.streamprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.functions.MapFunction;

import java.util.HashMap;
import java.util.Map;

public class MapToJsonSerializer implements MapFunction<Map<String, String>, String> {

    private transient ObjectMapper mapper;

    private ObjectMapper getMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper();
        }

        return mapper;
    }

    private Map<String, Object> convertToNestedMap(Map<String, String> value) {
        Map<String, Object> nestedMap = new HashMap<>();

        for (String key : value.keySet()) {
            Map<String, Object> parent = nestedMap;
            String[] keyPath = key.split("__");

            for (int i = 0; i < keyPath.length; ++i) {
                String innerKey = keyPath[i];
                boolean isLast = i == keyPath.length - 1;
                Map<String, Object> child;

                if (isLast) {
                    parent.put(innerKey, value.get(key));
                } else {
                    if (parent.containsKey(innerKey)) {
                        child = (Map<String, Object>) parent.get(innerKey);
                    } else {
                        child = new HashMap<>();
                        parent.put(innerKey, child);
                    }

                    parent = child;
                }
            }
        }

        return nestedMap;
    }

    @Override
    public String map(Map<String, String> value) throws Exception {
        Map<String, Object> nestedMap = this.convertToNestedMap(value);
        return this.getMapper().writeValueAsString(nestedMap);
    }
}
