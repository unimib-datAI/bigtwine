package it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes;

import io.kubernetes.client.util.Yaml;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Map;

public class YamlTemplateKubernetesObjectLoader implements KubernetesObjectLoader {

    private static final Logger log = LoggerFactory.getLogger(YamlTemplateKubernetesObjectLoader.class);

    private File templatePath;

    public YamlTemplateKubernetesObjectLoader() {
    }

    public YamlTemplateKubernetesObjectLoader(File templatePath) {
        this.templatePath = templatePath;
    }


    public File getTemplatePath() {
        return templatePath;
    }

    public void setTemplatePath(File templatePath) {
        this.templatePath = templatePath;
    }

    private String loadTemplate() throws IOException {
        Reader templateReader = new FileReader(templatePath);
        return IOUtils.toString(templateReader);
    }

    private void applyVariables(Object object, Map<String, Object> variables) throws ReflectiveOperationException {
        for (Map.Entry<String, Object> entry: variables.entrySet()) {
            PropertyUtils.setNestedProperty(object, entry.getKey(), entry.getValue());
        }
    }

    public <T> T getKubernetesObjectSpec(Map<String, Object> variables, Class<T> clazz) {
        try {
            String template = this.loadTemplate();
            T object = Yaml.loadAs(template, clazz);
            if (variables != null) {
                this.applyVariables(object, variables);
            }
            return object;
        } catch (IOException e) {
            log.error("Cannot load kubernetes template located at: " + this.getTemplatePath().getAbsolutePath());
            return null;
        } catch(ReflectiveOperationException e) {
            log.error("Cannot substitute variables", e);
            return null;
        }
    }

}
