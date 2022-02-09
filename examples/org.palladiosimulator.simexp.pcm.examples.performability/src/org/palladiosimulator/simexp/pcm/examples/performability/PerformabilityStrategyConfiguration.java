package org.palladiosimulator.simexp.pcm.examples.performability;

public class PerformabilityStrategyConfiguration {
    
    
    private final String nodeFailureTemplateId;   // defines the node failue template id *.templatevariable model
    private final String nodeBalancerId;            // id of component 'LoadBalancer' in *.repository model
    
    public PerformabilityStrategyConfiguration(String nodeFailureTemplateId, String loadBalancerId) {
        this.nodeFailureTemplateId = nodeFailureTemplateId;
        this.nodeBalancerId = loadBalancerId;
    }

    public String getNodeBalancerId() {
        return nodeBalancerId;
    }
    
    public String getNodeFailureTemplateId() {
        return nodeFailureTemplateId;
    }
    
}
