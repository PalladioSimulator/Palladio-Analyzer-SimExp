package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.deployment;

import org.apache.log4j.Logger;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.RollableScalableResource;

public class DeploymentScaler implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(DeploymentScaler.class);

    private final ClassLoader classloader;
    private final KubernetesClient client;
    private final String namespace;

    public DeploymentScaler(ClassLoader classloader, KubernetesClient client, String namespace) {
        this.classloader = classloader;
        this.client = client;
        this.namespace = namespace;
    }

    @Override
    public void run() {
        ClassLoader oldContextClassLoader = Thread.currentThread()
            .getContextClassLoader();
        Thread.currentThread()
            .setContextClassLoader(classloader);
        try {
            adjustReplicaCount();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            Thread.currentThread()
                .setContextClassLoader(oldContextClassLoader);
        }
    }

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
            // deploymentResource.scale(availableCPUCores);
        }
    }

    private int getWorkerCPUCores() {
        NodeInfo nodeInfo = new NodeInfo();
        Integer sum = client.nodes()
            .list()
            .getItems()
            .stream()
            .filter(n -> nodeInfo.isWorker(n))
            .filter(n -> nodeInfo.isReady(n))
            .filter(n -> nodeInfo.isSchedulable(n))
            .mapToInt(n -> nodeInfo.nodeCPUCores(n))
            .sum();
        return sum;
    }

    private int getAvailableCPUCores() {
        return getWorkerCPUCores() - 4;
    }

}
