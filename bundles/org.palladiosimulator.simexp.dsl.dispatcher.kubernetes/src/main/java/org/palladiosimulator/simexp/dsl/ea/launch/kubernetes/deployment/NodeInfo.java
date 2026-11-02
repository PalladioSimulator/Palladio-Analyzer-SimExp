package org.palladiosimulator.simexp.dsl.ea.launch.kubernetes.deployment;

import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeCondition;
import io.fabric8.kubernetes.api.model.NodeSpec;
import io.fabric8.kubernetes.api.model.NodeStatus;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Quantity;

public class NodeInfo {

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

    public boolean isWorker(Node node) {
        String role = getRole(node);
        if (role.equalsIgnoreCase("worker")) {
            return true;
        }
        return false;
    }

    public int nodeCPUCores(Node node) {
        NodeStatus status = node.getStatus();
        Quantity cpuCount = status.getCapacity()
            .get("cpu");
        return Integer.valueOf(cpuCount.getAmount());
    }

    public boolean isReady(Node node) {
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

    public boolean isSchedulable(Node node) {
        NodeSpec spec = node.getSpec();
        boolean isCordoned = Boolean.TRUE.equals(spec.getUnschedulable());
        return !isCordoned;
    }

}
