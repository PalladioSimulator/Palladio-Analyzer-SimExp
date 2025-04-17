package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.deployment;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.EmptyDirVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
import io.fabric8.kubernetes.api.model.LocalObjectReferenceBuilder;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeCondition;
import io.fabric8.kubernetes.api.model.NodeSpec;
import io.fabric8.kubernetes.api.model.NodeStatus;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.api.model.ResourceRequirementsBuilder;
import io.fabric8.kubernetes.api.model.Toleration;
import io.fabric8.kubernetes.api.model.TolerationBuilder;
import io.fabric8.kubernetes.api.model.Volume;
import io.fabric8.kubernetes.api.model.VolumeBuilder;
import io.fabric8.kubernetes.api.model.VolumeMount;
import io.fabric8.kubernetes.api.model.VolumeMountBuilder;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.RollableScalableResource;

public class DeploymentDispatcher /* implements IShutdownReceiver */ {
    private static final Logger LOGGER = Logger.getLogger(DeploymentDispatcher.class);

    private final KubernetesClient client;
    private final String namespace;
    /*
     * private final ReentrantLock lock = new ReentrantLock(); private final Condition
     * terminateCondition = lock.newCondition();
     * 
     * private boolean shutdown = false;
     */

    public DeploymentDispatcher(KubernetesClient client) {
        this(client, "default");
    }

    public DeploymentDispatcher(KubernetesClient client, String namespace) {
        this.client = client;
        this.namespace = namespace;
    }

    public void dispatch(String brokerUrl, String outQueue, String inQueue, Runnable runnable) throws IOException {
        Deployment deployment = createDeployment(brokerUrl, outQueue, inQueue);
        try {
            runnable.run();
            // waitForShutdown();
        } finally {
            removeDeployment(deployment);
        }
    }

    /*
     * @Override public void shutdown() { LOGGER.info("signal shutdown"); lock.lock(); try {
     * shutdown = true; terminateCondition.signalAll(); } finally { lock.unlock(); } }
     * 
     * private void waitForShutdown() throws IOException { LOGGER.info("wait for shutdown");
     * lock.lock(); try { while (!shutdown) { if (!terminateCondition.await(30, TimeUnit.SECONDS)) {
     * // adjustReplicaCount(); } } LOGGER.info("shut down"); } catch (InterruptedException e) {
     * throw new IOException(e); } finally { lock.unlock(); } }
     */

    private void adjustReplicaCount() {
        int availableCPUCores = Math.max(1, getAvailableCPUCores());
        LOGGER.debug(String.format("available cores:  %d", availableCPUCores));

        RollableScalableResource<Deployment> deploymentResource = client.apps()
            .deployments()
            .inNamespace(namespace)
            .withName("simexp");
        Deployment deployment = deploymentResource.get();
        Integer replicas = deployment.getStatus()
            .getReplicas();
        LOGGER.debug(String.format("current replicas: %d", replicas));

        if (availableCPUCores != replicas) {
            LOGGER.info(String.format("adjust replicas from: %d to %d", replicas, availableCPUCores));
            deploymentResource.scale(availableCPUCores);
        }
    }

    private String getRole(Node node) {
        ObjectMeta metadata = node.getMetadata();
        Map<String, String> labels = metadata.getLabels();
        for (Map.Entry<String, String> entry : labels.entrySet()) {
            if (entry.getKey()
                .startsWith("node-role.kubernetes.io/")) {
                String role = entry.getKey()
                    .substring("node-role.kubernetes.io/".length());
                return role;
            }
        }
        return "unknown";
    }

    private boolean isWorker(Node node) {
        String role = getRole(node);
        if (role.equalsIgnoreCase("worker")) {
            return true;
        }
        return false;
    }

    private int nodeCPUCores(Node node) {
        NodeStatus status = node.getStatus();
        Quantity cpuCount = status.getCapacity()
            .get("cpu");
        return Integer.valueOf(cpuCount.getAmount());
    }

    private boolean isReady(Node node) {
        NodeStatus status = node.getStatus();
        List<NodeCondition> conditions = status.getConditions();
        for (NodeCondition condition : conditions) {
            if (condition.getType()
                .equals("Ready")) {
                if (condition.getStatus()
                    .equals("True")) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isSchedulable(Node node) {
        NodeSpec spec = node.getSpec();
        boolean isCordoned = Boolean.TRUE.equals(spec.getUnschedulable());
        return !isCordoned;
    }

    private int getWorkerCPUCores() {
        Integer sum = client.nodes()
            .list()
            .getItems()
            .stream()
            .filter(this::isWorker)
            .filter(this::isReady)
            .filter(this::isSchedulable)
            .mapToInt(this::nodeCPUCores)
            .sum();
        return sum;
    }

    private int getAvailableCPUCores() {
        return getWorkerCPUCores() - 4;
    }

    private Deployment createDeployment(String brokerUrl, String outQueue, String inQueue) {
        LOGGER.info("create deployment");

        VolumeMount volumeMount = new VolumeMountBuilder().withMountPath("/workspace")
            .withName("workspace")
            .build();
        Volume volume = new VolumeBuilder().withName(volumeMount.getName())
            .withEmptyDir(new EmptyDirVolumeSourceBuilder().withNewSizeLimit("1Gi")
                .build())
            .build();

        Container container = createContainer(brokerUrl, outQueue, inQueue, volumeMount);

        Toleration toleration = new TolerationBuilder().withKey("remote")
            .withOperator("Exists")
            .withEffect("NoExecute")
            .build();

        // int availableCPUCores = getAvailableCPUCores();
        int availableCPUCores = 1;
        LOGGER.info(String.format("available cores: %d", availableCPUCores));

        Map<String, String> labels = Collections.singletonMap("app", "simexp");
        Deployment deployment = new DeploymentBuilder().withNewMetadata()
            .withName("simexp")
            .withLabels(labels)
            .endMetadata()
            .withNewSpec()
            .withReplicas(availableCPUCores)
            .withNewSelector()
            .withMatchLabels(labels)
            .endSelector()
            .withNewTemplate()
            .withNewMetadata()
            .addToLabels(labels)
            .endMetadata()
            .withNewSpec()
            .withServiceAccount("node-query-sa")
            .withContainers(container)
            .withVolumes(volume)
            .withImagePullSecrets(new LocalObjectReferenceBuilder().withName("cred-simexp-registry")
                .build())
            .withTolerations(toleration)
            .endSpec()
            .endTemplate()
            .endSpec()
            .build();

        Deployment deploymentResource = client.apps()
            .deployments()
            .inNamespace(namespace)
            .resource(deployment)
            .create();
        return deploymentResource;
    }

    private Container createContainer(String brokerUrl, String outQueue, String inQueue, VolumeMount volumeMount) {
        Map<String, Quantity> reqMap = new HashMap<>();
        reqMap.put("cpu", new Quantity("0.9"));
        // reqMap.put("memory", new Quantity("1500Mi"))
        ResourceRequirements reqs = new ResourceRequirementsBuilder().withRequests(reqMap)
            .build();

        Container container = new ContainerBuilder().withName("simexp")
            .withImage("10.0.0.10:30500/simexp_console")
            .withImagePullPolicy("Always")
            .withEnv(
                    // new EnvVarBuilder().withName("DEBUG_FLAG").withValue("-v").build(),
                    new EnvVarBuilder().withName("BROKER_URL")
                        .withValue(brokerUrl)
                        .build(),
                    new EnvVarBuilder().withName("QUEUE_IN")
                        .withValue(outQueue)
                        .build(),
                    new EnvVarBuilder().withName("QUEUE_OUT")
                        .withValue(inQueue)
                        .build(),
                    new EnvVarBuilder().withName("NODE_NAME")
                        .withNewValueFrom()
                        .withNewFieldRef()
                        .withFieldPath("spec.nodeName")
                        .endFieldRef()
                        .endValueFrom()
                        .build(),
                    new EnvVarBuilder().withName("POD_NAMESPACE")
                        .withNewValueFrom()
                        .withNewFieldRef()
                        .withFieldPath("metadata.namespace")
                        .endFieldRef()
                        .endValueFrom()
                        .build(),
                    new EnvVarBuilder().withName("POD_NAME")
                        .withNewValueFrom()
                        .withNewFieldRef()
                        .withFieldPath("metadata.name")
                        .endFieldRef()
                        .endValueFrom()
                        .build())
            .withResources(reqs)
            .withVolumeMounts(volumeMount)
            .build();
        return container;
    }

    private void removeDeployment(Deployment deployment) {
        LOGGER.info(String.format("delete deployment: %s", deployment));
        // client.resource(deployment).delete();
    }

}
