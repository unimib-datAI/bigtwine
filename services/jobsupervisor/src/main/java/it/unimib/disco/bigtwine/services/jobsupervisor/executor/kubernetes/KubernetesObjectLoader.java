package it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes;

import java.util.Map;

public interface KubernetesObjectLoader {
    <T> T getKubernetesObjectSpec(Map<String, Object> variables, Class<T> clazz);

    default <T> T getKubernetesObjectSpec(Class<T> clazz) {
        return this.getKubernetesObjectSpec(null, clazz);
    }

    default Object getKubernetesObjectSpec(Map<String, Object> variables) {
        return this.getKubernetesObjectSpec(variables, Object.class);
    }

    default Object getKubernetesObjectSpec() {
        return this.getKubernetesObjectSpec(null, Object.class);
    }
}
