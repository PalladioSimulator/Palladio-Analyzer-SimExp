package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.dispatcher;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.palladiosimulator.simexp.dsl.ea.api.dispatcher.IDisposeableEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.preferences.KubernetesPreferenceConstants;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.pcm.config.IModelledWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader.Factory;
import org.palladiosimulator.simexp.workflow.api.LaunchDescriptionProvider;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

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
    public void evaluate(EvaluatorClient evaluatorClient) {
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

            evaluateWithRabbitMQ(client, evaluatorClient);

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            LOGGER.info("done");
        }
    }

    private void evaluateWithRabbitMQ(KubernetesClient client, EvaluatorClient evaluatorClient)
            throws IOException, TimeoutException {
        String rabbiteMQString = getPreference(KubernetesPreferenceConstants.RABBIT_MQ_URL);
        URL rabbiteMQURL = new URL(rabbiteMQString);
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbiteMQURL.getHost());
        factory.setPort(rabbiteMQURL.getPort());

        LOGGER.info("Connecting to RabbitMQ...");
        try (Connection conn = factory.newConnection()) {
            LOGGER.info("Connected to RabbitMQ");
            try (Channel channel = conn.createChannel()) {
                evaluatorClient.process(this);
            }
        }
    }

    private String getPreference(String key) {
        String value = preferencesService.getString(KubernetesPreferenceConstants.ID, key, "", null);
        return value;
    }

    @Override
    public Future<Optional<Double>> calcFitness(List<OptimizableValue<?>> optimizableValues) {
        LOGGER.info("dispatch fitness calculation");

        // TODO Auto-generated method stub
        return null;
    }

}
