package it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes;

import io.kubernetes.client.models.V1Job;
import io.kubernetes.client.util.Yaml;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.*;

public class YamlTemplateKubernetesObjectLoaderTest {

    @Test
    public void testLoadTemplate() throws Exception {
        File template = getTemplateFile();
        KubernetesObjectLoader k8sObjLoader = new YamlTemplateKubernetesObjectLoader(template);

        List<String> command = new ArrayList<>();
        Collections.addAll(command, "/bin/cmd");
        List<String> args = new ArrayList<>();
        Collections.addAll(args, "arg1", "100");

        Map<String, Object> vars = new HashMap<>();
        vars.put("spec.template.spec.containers[0].command", command);
        vars.put("spec.template.spec.containers[0].args", args);
        vars.put("metadata.labels.job-id", "1111");
        vars.put("metadata.labels.analysis-id", "2222");
        vars.put("metadata.labels.analysis-type", "twitter-neel");

        V1Job job = k8sObjLoader.getKubernetesObjectSpec(vars, V1Job.class);

        assertNotNull(job);

        String yml = Yaml.dump(job);

        assertNotNull(yml);

        V1Job job2 = Yaml.loadAs(yml, V1Job.class);

        assertNotNull(job2);
        assertEquals("Job", job2.getKind());
        assertEquals("1111", job2.getMetadata().getLabels().get("job-id"));
        assertEquals("2222", job2.getMetadata().getLabels().get("analysis-id"));
        assertEquals("twitter-neel", job2.getMetadata().getLabels().get("analysis-type"));
        assertEquals("/bin/cmd", job2.getSpec().getTemplate().getSpec().getContainers().get(0).getCommand().get(0));
        assertEquals("arg1", job2.getSpec().getTemplate().getSpec().getContainers().get(0).getArgs().get(0));
        assertEquals("100", job2.getSpec().getTemplate().getSpec().getContainers().get(0).getArgs().get(1));
    }

    @Test
    public void testLoadTemplateMultiple() throws Exception {
        KubernetesObjectLoader k8sObjLoader = new YamlTemplateKubernetesObjectLoader(getTemplateFile());

        Map<String, Object> vars = new HashMap<>();
        vars.put("metadata.name", "job1");
        V1Job job1 = k8sObjLoader.getKubernetesObjectSpec(vars, V1Job.class);
        assertNotNull(job1);
        assertEquals("job1", job1.getMetadata().getName());

        V1Job job2 = k8sObjLoader.getKubernetesObjectSpec(null, V1Job.class);
        assertNotNull(job2);
        assertEquals("testjob", job2.getMetadata().getName());
    }

    private File getTemplateFile() throws Exception {
        URI res = IOUtils.resourceToURL(
            "template/kubernetes/job.yaml",
            YamlTemplateKubernetesObjectLoaderTest.class.getClassLoader())
            .toURI();

        return Paths.get(res).toFile();
    }
}
