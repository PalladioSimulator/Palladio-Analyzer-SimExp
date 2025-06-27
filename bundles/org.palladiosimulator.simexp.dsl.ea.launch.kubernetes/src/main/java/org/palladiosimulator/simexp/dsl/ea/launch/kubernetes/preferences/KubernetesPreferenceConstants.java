package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.preferences;

public interface KubernetesPreferenceConstants {
    static final String ID = "org.palladiosimulator.simexp.dsl.ea.launch.kubernetes";

    static final String CLUSTER_URL = "Cluster URL";
    static final String API_TOKEN = "API token";

    static final String RABBIT_MQ_URL = "RabbitMQ URL";
    static final String INTERNAL_RABBIT_MQ_URL = "Internal RabbitMQ URL";
    static final String INTERNAL_IMAGE_REGISTRY_URL = "Internal image registry URL";

    static final String RABBIT_QUEUE_OUT = "RabbitMQ queue out";
    static final String RABBIT_QUEUE_IN = "RabbitMQ queue in";
    static final String RABBIT_CONSUMER_TIMEOUT = "RabbitMQ consumer timeout";
    static final String RABBIT_MAX_DELIVERY = "RabbitMQ max delivery";
}
