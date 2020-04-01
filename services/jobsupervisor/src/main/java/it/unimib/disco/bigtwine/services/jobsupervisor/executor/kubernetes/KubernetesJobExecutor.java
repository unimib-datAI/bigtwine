package it.unimib.disco.bigtwine.services.jobsupervisor.executor.kubernetes;

import com.google.gson.JsonSyntaxException;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.apis.BatchV1Api;
import io.kubernetes.client.models.V1DeleteOptions;
import io.kubernetes.client.models.V1Job;
import io.kubernetes.client.models.V1Status;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutable;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobExecutor;
import it.unimib.disco.bigtwine.services.jobsupervisor.executor.JobProcess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KubernetesJobExecutor implements JobExecutor<KubernetesJobProcess, KubernetesJobExecutable> {
    private static final Logger log = LoggerFactory.getLogger(KubernetesJobExecutor.class);

    final private ApiClient apiClient;
    private String namespace;
    private BatchV1Api batchApi = null;

    public KubernetesJobExecutor(ApiClient apiClient, String namespace) {
        this.apiClient = apiClient;
        this.namespace = namespace;
    }

    public KubernetesJobExecutor(ApiClient apiClient) {
        this(apiClient, "default");
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public boolean isRunning(KubernetesJobProcess process) throws JobExecutorException {
        if (process.getKind() != null && "job".equals(process.getKind().toLowerCase())) {
            return this.getJobIsRunning(process);
        }else {
            throw new JobExecutorException(String.format("Unsupported kubernetes object type: %s", process.getKind()));
        }
    }

    @Override
    public boolean stop(KubernetesJobProcess process) throws JobExecutorException {
        if (process.getKind() != null && "job".equals(process.getKind().toLowerCase())) {
            return this.stopJob(process);
        }else {
            throw new JobExecutorException(String.format("Unsupported kubernetes object type: %s", process.getKind()));
        }
    }

    @Override
    public KubernetesJobProcess execute(KubernetesJobExecutable executable) throws JobExecutorException {
        Object k8sObjectSpec = executable.getKubernetesObjectSpec();

        if (k8sObjectSpec instanceof V1Job) {
            return this.executeJob((V1Job) k8sObjectSpec);
        }else {
            throw new JobExecutorException(String.format("Unsupported kubernetes object: %s", k8sObjectSpec.getClass()));
        }
    }

    @Override
    public boolean test(JobProcess process) {
        return process instanceof KubernetesJobProcess;
    }

    @Override
    public boolean test(JobExecutable executable) {
        return executable instanceof KubernetesJobExecutable;
    }

    private BatchV1Api getBatchApi() {
        if (this.batchApi == null) {
            this.batchApi = new BatchV1Api(apiClient);
        }

        return this.batchApi;
    }

    private boolean getJobIsRunning(KubernetesJobProcess process) throws JobExecutorException {
        try {
            V1Job job = this.getBatchApi().readNamespacedJob(process.getName(), this.getNamespace(), null, false, false);
            return job.getStatus().getCompletionTime() == null;
        } catch (Exception e) {
            if (e instanceof ApiException && ((ApiException)e).getCode() == 404) {
                return false;
            }else {
                log.error("Cannot read kubernetes job", e);
                throw new JobExecutorException(String.format("Cannot read kubernetes job: %s", e.getLocalizedMessage()));
            }
        }
    }

    private boolean stopJob(KubernetesJobProcess process) throws JobExecutorException {
        V1DeleteOptions options = new V1DeleteOptions()
            .apiVersion("v1")
            .kind("DeleteOptions")
            .propagationPolicy("Foreground");

        try {
            V1Status status = this.getBatchApi().deleteNamespacedJob(process.getName(), this.getNamespace(), null, options, null, null, null, null);
            return status.getCode() == 200;
        } catch (JsonSyntaxException e) {
            // Workaround bug https://github.com/kubernetes-client/java/issues/86#issuecomment-334981383
            return true;
        } catch (Exception e) {
            log.error("Cannot stop kubernetes job", e);
            throw new JobExecutorException(String.format("Cannot stop kubernetes job: %s", e.getLocalizedMessage()));
        }
    }

    private KubernetesJobProcess executeJob(V1Job jobSpec) throws JobExecutorException {
        V1Job job;
        try {
            job = this.getBatchApi().createNamespacedJob(this.getNamespace(), jobSpec, null, null, null);
        } catch (Exception e) {
            log.error("Cannot execute kubernetes job", e);
            throw new JobExecutorException(String.format("Cannot execute kubernetes job: %s", e.getLocalizedMessage()));
        }

        return new KubernetesJobProcess(job.getApiVersion(), job.getKind(), job.getMetadata().getName());
    }
}
