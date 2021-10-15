package org.palladiosimulator.simexp.pcm.examples.performability;

public class PerformabilityStrategyConfiguration {
    
    
    private String nodeFailureTemplateId;   // defines the node failue template id *.templatevariable model
    
    public String getNodeFailureTemplateId() {
        return nodeFailureTemplateId;
    }

    public PerformabilityStrategyConfiguration(String nodeFailureTemplateId) {
        this.nodeFailureTemplateId = nodeFailureTemplateId;
    }

}
