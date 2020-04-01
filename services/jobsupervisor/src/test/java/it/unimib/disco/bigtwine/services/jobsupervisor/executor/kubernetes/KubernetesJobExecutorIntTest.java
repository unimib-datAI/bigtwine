package it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.apis.BatchV1Api;
import io.kubernetes.client.models.V1Job;
import io.kubernetes.client.models.V1JobBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.*;

public class KubernetesJobExecutorIntTest {

    private static final String K8S_NAMESPACE = "bigtwine-test";

    private ApiClient apiClient;
    private KubernetesJobExecutor executor;

    @Before
    public void setUp() {
        this.apiClient = new ApiClient()
        .setBasePath("https://localhost:6443")
        .setVerifyingSsl(false)
        .setDebugging(true);

        this.apiClient.setApiKey("eyJhbGciOiJSUzI1NiIsImtpZCI6IiJ9.eyJpc3MiOiJrdWJlcm5ldGVzL3NlcnZpY2VhY2NvdW50Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9uYW1lc3BhY2UiOiJkZWZhdWx0Iiwia3ViZXJuZXRlcy5pby9zZXJ2aWNlYWNjb3VudC9zZWNyZXQubmFtZSI6ImRlZmF1bHQtdG9rZW4tenI2emsiLCJrdWJlcm5ldGVzLmlvL3NlcnZpY2VhY2NvdW50L3NlcnZpY2UtYWNjb3VudC5uYW1lIjoiZGVmYXVsdCIsImt1YmVybmV0ZXMuaW8vc2VydmljZWFjY291bnQvc2VydmljZS1hY2NvdW50LnVpZCI6IjcwZDhhNDBjLTU4NjktMTFlOS05NWMwLTAyNTAwMDAwMDAwMSIsInN1YiI6InN5c3RlbTpzZXJ2aWNlYWNjb3VudDpkZWZhdWx0OmRlZmF1bHQifQ.g9h9VkEIK0eI2swI-fr0YImtB8DMGOmFSLS5Dm7jkz7WviPtCyRSgA7jORRhB_TotumXCcpIZDSu38b0_-gdZ47St5kXHCbZ9GG72BACdlkDNi3mUE7TNCJY8A3ywB5_2prJXkwcbBa7_J5_Cqe6GtdsqnCBH8U_7V-SkBAnyNJWnl_pDMdI0Wbv-nnTwGIa_E-0H6qS2RBtYKXejffTM-zflvdBYfC2FKHjsI5IIxFamn_t1Wobj125e4fevL6X-VafK_pXa-_xqqVJCPEUGnQsgJQm0KFt0ud_NH-jMt_UdM6TJDdFWqZQZnC47Rse8LWFdH6rwS1DSaSXYnCzvg");
        this.apiClient.setApiKeyPrefix("Bearer");

        this.executor = new KubernetesJobExecutor(apiClient, K8S_NAMESPACE);
    }

    @After
    public void tearDown() throws Exception {
        BatchV1Api api = new BatchV1Api(apiClient);
        api.deleteCollectionNamespacedJob(K8S_NAMESPACE, null, null, null, null, null, null, null, null);
    }

    @Test
    public void testRunJob() throws Exception {

        KubernetesJobProcess process = this.executor.execute(this.createJobExecutable());

        assertNotNull(process);
        assertNotNull(process.getName());
        assertNotNull(process.getPID());
        assertEquals("batch/v1", process.getApiVersion());
        assertEquals("Job", process.getKind());
    }

    @Test
    public void testStopJob() throws Exception {
        KubernetesJobProcess process = this.executor.execute(this.createJobExecutable());

        assertTrue(executor.isRunning(process));
        this.executor.stop(process);
        Thread.sleep(3000);
        assertFalse(executor.isRunning(process));
    }

    @Test
    public void testJobIsRunning() throws Exception {
        KubernetesJobProcess process = this.executor.execute(this.createJobExecutable());

        assertTrue(executor.isRunning(process));
        Thread.sleep(10000);
        assertFalse(executor.isRunning(process));
    }

    private KubernetesJobExecutable createJobExecutable() {
        V1Job job = new V1JobBuilder()
            .withKind("Job")
            .withApiVersion("batch/v1")
            .withNewMetadata()
            .withName(String.format("analysisjob-%s", UUID.randomUUID().toString()))
            .endMetadata()
            .withNewSpec()
            .withNewTemplate()
            .withNewSpec()
            .addNewContainer()
            .withName("timer")
            .withImage("alpine")
            .withCommand(Arrays.asList("sleep", "3"))
            .endContainer()
            .withRestartPolicy("Never")
            .endSpec()
            .endTemplate()
            .endSpec()
            .build();

        KubernetesJobExecutable executable = new KubernetesJobExecutable();
        executable.setKubernetesObjectSpec(job);

        return executable;
    }
}
