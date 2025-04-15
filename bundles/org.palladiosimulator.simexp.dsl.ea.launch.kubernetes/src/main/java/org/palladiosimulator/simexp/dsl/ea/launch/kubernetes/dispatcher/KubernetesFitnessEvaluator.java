package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.dispatcher;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.palladiosimulator.simexp.dsl.ea.api.dispatcher.IDisposeableEAFitnessEvaluator;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.deployment.DeploymentDispatcher;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.preferences.KubernetesPreferenceConstants;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.TaskManager;
import org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.task.TaskReceiver;
import org.palladiosimulator.simexp.dsl.smodel.api.OptimizableValue;
import org.palladiosimulator.simexp.pcm.config.IModelledWorkflowConfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader.Factory;
import org.palladiosimulator.simexp.workflow.api.LaunchDescriptionProvider;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import tools.mdsd.probdist.api.random.ISeedProvider;

public class KubernetesFitnessEvaluator implements IDisposeableEAFitnessEvaluator {
    private static final Logger LOGGER = Logger.getLogger(KubernetesFitnessEvaluator.class);

    private final IPreferencesService preferencesService;
    private final ClassLoader classloader;

    private EAFitnessEvaluator fitnessEvaluator;

    public KubernetesFitnessEvaluator(IModelledWorkflowConfiguration config,
            LaunchDescriptionProvider launchDescriptionProvider, Optional<ISeedProvider> seedProvider,
            Factory modelLoaderFactory, Path resourcePath, IPreferencesService preferencesService) {
        this.preferencesService = preferencesService;
        this.classloader = Thread.currentThread()
            .getContextClassLoader();
    }

    @Override
    public void evaluate(EvaluatorClient evaluatorClient) {
        String clusterURL = getPreference(KubernetesPreferenceConstants.CLUSTER_URL);
        String apiToken = getPreference(KubernetesPreferenceConstants.API_TOKEN);
        Config config = new ConfigBuilder().withMasterUrl(clusterURL)
            .withOauthToken(apiToken)
            .withTrustCerts(true)
            .build();
        LOGGER.info(String.format("Connecting to kubernetes at: %s", clusterURL));
        try (KubernetesClient client = new KubernetesClientBuilder().withConfig(config)
            .build()) {
            LOGGER.info("Connected to kubernetes");
            evaluateWithRabbitMQ(client, evaluatorClient);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            LOGGER.info("done");
        }
    }

    private void evaluateWithRabbitMQ(KubernetesClient client, EvaluatorClient evaluatorClient)
            throws IOException, TimeoutException {
        String rabbitMQString = getPreference(KubernetesPreferenceConstants.RABBIT_MQ_URL);
        URL rabbitMQURL = new URL(rabbitMQString);

        LOGGER.info(String.format("Connecting to RabbitMQ at: %s", rabbitMQURL));
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMQURL.getHost());
        factory.setPort(rabbitMQURL.getPort());
        try (Connection conn = factory.newConnection()) {
            LOGGER.info("Connected to RabbitMQ");
            try (Channel channel = conn.createChannel()) {
                setupQueues(channel);
                evaluateWithMessageChannel(client, channel, evaluatorClient);
            }
        }
    }

    private void evaluateWithMessageChannel(KubernetesClient client, Channel channel, EvaluatorClient evaluatorClient)
            throws IOException {
        TaskReceiver taskReceiver = new TaskReceiver(channel, classloader);
        String inQueueName = getPreference(KubernetesPreferenceConstants.RABBIT_QUEUE_IN);
        boolean autoAck = false;
        channel.basicConsume(inQueueName, autoAck, "answerConsumer", taskReceiver);
        try {
            TaskManager taskManager = new TaskManager();
            taskReceiver.registerTaskConsumer(taskManager);
            DeploymentDispatcher dispatcher = new DeploymentDispatcher(client);
            String brokerUrl = buildBrokerURL();
            String outQueueName = getPreference(KubernetesPreferenceConstants.RABBIT_QUEUE_OUT);
            fitnessEvaluator = new EAFitnessEvaluator(taskManager, channel, outQueueName, classloader);
            dispatcher.dispatch(brokerUrl, outQueueName, inQueueName, new Runnable() {

                @Override
                public void run() {
                    evaluatorClient.process(KubernetesFitnessEvaluator.this);
                }
            });
        } finally {
            channel.basicCancel("answerConsumer");
        }
    }

    private String buildBrokerURL() throws MalformedURLException {
        String internalRabbitMQ = getPreference(KubernetesPreferenceConstants.INTERNAL_RABBIT_MQ_URL);
        URL internalRabbitMQURL = new URL(internalRabbitMQ);
        String jobRabbitHost = internalRabbitMQURL.getHost();
        int jobRabbitPort = internalRabbitMQURL.getPort();
        List<String> urlArguments = new ArrayList<>();
        urlArguments.add("blocked_connection_timeout=30");
        urlArguments.add("connection_attempts=3");
        urlArguments.add("retry_delay=5");
        urlArguments.add("heartbeat=60");
        String arguments = StringUtils.join(urlArguments, "&");
        String brokerUrl = String.format("amqp://guest:guest@%s:%d/?%s", jobRabbitHost, jobRabbitPort, arguments);
        return brokerUrl;
    }

    private void setupQueues(Channel channel) throws IOException {
        String outQueueName = getPreference(KubernetesPreferenceConstants.RABBIT_QUEUE_OUT);
        String inQueueName = getPreference(KubernetesPreferenceConstants.RABBIT_QUEUE_IN);
        LOGGER.info("Deleting queues ...");
        channel.queueDelete(inQueueName);
        channel.queueDelete(outQueueName);
        LOGGER.info("Queues deleted");

        boolean durable = true;
        boolean exclusive = false;
        boolean autoDelete = false;
        Map<String, Object> outArguments = new HashMap<>();
        outArguments.put("x-consumer-timeout", TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
        channel.queueDeclare(outQueueName, durable, exclusive, autoDelete, outArguments);
        channel.queueDeclare(inQueueName, durable, exclusive, autoDelete, null);
    }

    private String getPreference(String key) {
        String value = preferencesService.getString(KubernetesPreferenceConstants.ID, key, "", null);
        return value;
    }

    @Override
    public Future<Optional<Double>> calcFitness(List<OptimizableValue<?>> optimizableValues) throws IOException {
        return fitnessEvaluator.calcFitness(optimizableValues);
    }
}
