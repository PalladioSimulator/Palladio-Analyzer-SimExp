package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.dispatcher;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.palladiosimulator.simexp.dsl.ea.api.dispatcher.IDisposeableEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.preferences.KubernetesPreferenceConstants;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.pcm.config.IModelledWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader.Factory;
import org.palladiosimulator.simexp.workflow.api.LaunchDescriptionProvider;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import tools.mdsd.probdist.api.random.ISeedProvider;

public class KubernetesFitnessEvaluator implements IDisposeableEAFitnessEvaluator {
    private static final Logger LOGGER = Logger.getLogger(KubernetesFitnessEvaluator.class);
    private final IPreferencesService preferencesService;

    public KubernetesFitnessEvaluator(IModelledWorkflowConfiguration config,
            LaunchDescriptionProvider launchDescriptionProvider, Optional<ISeedProvider> seedProvider,
            Factory modelLoaderFactory, Path resourcePath, IPreferencesService preferencesService) {
        this.preferencesService = preferencesService;
    }

    @Override
    public void init() {
        String clusterURL = getPreference(KubernetesPreferenceConstants.CLUSTER_URL);
        String apiToken = getPreference(KubernetesPreferenceConstants.API_TOKEN);
        Config config = new ConfigBuilder().withMasterUrl(clusterURL)
            .withOauthToken(apiToken)
            .withTrustCerts(true)
            .build();
        try (KubernetesClient client = new KubernetesClientBuilder().withConfig(config)
            .build()) {
            LOGGER.info("connected");

            LOGGER.info("nodes:");
            client.nodes()
                .list()
                .getItems()
                .stream()
                .map(Node::getMetadata)
                .map(ObjectMeta::getName)
                .forEach(LOGGER::info);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            LOGGER.info("done");
        }
    }

    private String getPreference(String key) {
        String value = preferencesService.getString(KubernetesPreferenceConstants.ID, key, "", null);
        return value;
    }

    @Override
    public void dispose() {
    }

    @Override
    public Future<Optional<Double>> calcFitness(List<OptimizableValue<?>> optimizableValues) {
        LOGGER.info("dispatch fitness calculation");

        // TODO Auto-generated method stub
        return null;
    }

}
