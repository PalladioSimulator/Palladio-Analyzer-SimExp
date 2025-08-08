package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.deployment;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ContainerBuilder;
import io.fabric8.kubernetes.api.model.EmptyDirVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.EnvVar;
import io.fabric8.kubernetes.api.model.EnvVarBuilder;
import io.fabric8.kubernetes.api.model.HostPathVolumeSourceBuilder;
import io.fabric8.kubernetes.api.model.LocalObjectReferenceBuilder;
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

public class DeploymentDispatcher /* implements IShutdownReceiver */ {
    private static final Logger LOGGER = Logger.getLogger(DeploymentDispatcher.class);

    private final KubernetesClient client;
    private final String namespace;
    private final URL imageRegistryUrl;
    private final String timeZone;
    private final ClassLoader classloader;

    public DeploymentDispatcher(ClassLoader classloader, KubernetesClient client, URL imageRegistryUrl,
            String timeZone) {
        this(classloader, client, imageRegistryUrl, timeZone, "default");
    }

    public DeploymentDispatcher(ClassLoader classloader, KubernetesClient client, URL imageRegistryUrl, String timeZone,
            String namespace) {
        this.classloader = classloader;
        this.client = client;
        this.imageRegistryUrl = imageRegistryUrl;
        this.timeZone = timeZone;
        this.namespace = namespace;
    }

    public void dispatch(int memoryUsage, String brokerUrl, String outQueue, String inQueue, int maxDelivery,
            Runnable runnable) throws IOException {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        try {
            Deployment deployment = createDeployment(memoryUsage, brokerUrl, outQueue, inQueue, maxDelivery);
            try {
                DeploymentScaler scaler = new DeploymentScaler(classloader, client, namespace);
                ScheduledFuture<?> scalerFuture = executor.scheduleAtFixedRate(scaler, 60, 60, TimeUnit.SECONDS);
                try {
                    runnable.run();
                } finally {
                    scalerFuture.cancel(false);
                }
            } finally {
                removeDeployment(deployment);
            }
        } finally {
            LOGGER.info("shutdown deployment monitor");
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                throw new IOException(e.getMessage(), e);
            }
            LOGGER.info("shutt down deployment monitor");
        }
    }

    private Deployment createDeployment(int memoryUsage, String brokerUrl, String outQueue, String inQueue,
            int maxDelivery) {
        LOGGER.info("create deployment");

        List<VolumeMount> volumeMounts = new ArrayList<>();
        List<Volume> volumes = new ArrayList<>();
        VolumeMount volumeMountTimeZone = new VolumeMountBuilder().withMountPath("/etc/localtime")
            .withName("timezone")
            .build();
        volumeMounts.add(volumeMountTimeZone);
        VolumeMount volumeMountWorkspace = new VolumeMountBuilder().withMountPath("/workspace")
            .withName("workspace")
            .build();
        volumeMounts.add(volumeMountWorkspace);
        Volume volumeTimeZone = new VolumeBuilder().withName(volumeMountTimeZone.getName())
            .withHostPath(new HostPathVolumeSourceBuilder().withPath(String.format("/usr/share/zoneinfo/%s", timeZone))
                .build())
            .build();
        volumes.add(volumeTimeZone);
        Volume volumeWorkspace = new VolumeBuilder().withName(volumeMountWorkspace.getName())
            .withEmptyDir(new EmptyDirVolumeSourceBuilder().withNewSizeLimit("1Gi")
                .build())
            .build();
        volumes.add(volumeWorkspace);

        List<EnvVar> environment = createEnvironment(brokerUrl, outQueue, inQueue, maxDelivery);
        Container container = createContainer(memoryUsage, environment, volumeMounts);

        Toleration toleration = new TolerationBuilder().withKey("remote")
            .withOperator("Exists")
            .withEffect("NoExecute")
            .build();

        Map<String, String> labels = Collections.singletonMap("app", "simexp");
        Deployment deployment = new DeploymentBuilder().withNewMetadata()
            .withName("simexp")
            .withLabels(labels)
            .endMetadata()
            .withNewSpec()
            .withReplicas(1)
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
            .withVolumes(volumes)
            .withImagePullSecrets(new LocalObjectReferenceBuilder().withName("cred-simexp-registry")
                .build())
            .withTolerations(toleration)
            // .withRestartPolicy("Never")
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

    private Container createContainer(int memoryUsage, List<EnvVar> environment, List<VolumeMount> volumeMounts) {
        Map<String, Quantity> reqMap = new HashMap<>();
        reqMap.put("cpu", new Quantity("0.9"));
        reqMap.put("memory", new Quantity(String.format("%dGi", memoryUsage)));
        ResourceRequirements reqs = new ResourceRequirementsBuilder().withRequests(reqMap)
            .build();

        Container container = new ContainerBuilder().withName("simexp")
            .withImage(String.format("%s:%s/simexp_console", imageRegistryUrl.getHost(), imageRegistryUrl.getPort()))
            .withImagePullPolicy("Always")
            .withEnv(environment)
            .withResources(reqs)
            .withVolumeMounts(volumeMounts)
            .build();
        return container;
    }

    private List<EnvVar> createEnvironment(String brokerUrl, String outQueue, String inQueue, int maxDelivery) {
        return Arrays.asList( //
                // new EnvVarBuilder().withName("DEBUG_FLAG")
                // .withValue("-v")
                // .build(),
                new EnvVarBuilder().withName("BROKER_URL")
                    .withValue(brokerUrl)
                    .build(),
                new EnvVarBuilder().withName("QUEUE_IN")
                    .withValue(outQueue)
                    .build(),
                new EnvVarBuilder().withName("QUEUE_OUT")
                    .withValue(inQueue)
                    .build(),
                new EnvVarBuilder().withName("MAX_DELIVERY")
                    .withValue(String.format("--max-delivery %d", maxDelivery))
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
                    .build());
    }

    private void removeDeployment(Deployment deployment) {
        LOGGER.info(String.format("delete deployment: %s", deployment));
        // client.resource(deployment).delete();
    }

}
