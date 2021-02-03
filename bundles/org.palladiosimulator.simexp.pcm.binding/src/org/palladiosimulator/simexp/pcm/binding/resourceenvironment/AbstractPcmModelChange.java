package org.palladiosimulator.simexp.pcm.binding.resourceenvironment;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;
import org.palladiosimulator.simexp.pcm.perceiption.PcmModelChange;
import org.palladiosimulator.simexp.pcm.util.ExperimentRunner;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public abstract class AbstractPcmModelChange implements PcmModelChange {
    
    private static final Logger LOGGER = Logger.getLogger(AbstractPcmModelChange.class);
    
    private final String pcmAttrbuteName;
    private final ExperimentRunner experimentRunner;
    
    public AbstractPcmModelChange(String pcmAttributeName, ExperimentRunner experimentRunner){
        this.pcmAttrbuteName = pcmAttributeName;
        this.experimentRunner = experimentRunner;
    }
    
    abstract void applyChange(CategoricalValue value);
    
    protected String getPcmAttributeName() {
        return pcmAttrbuteName;
    }
    
    protected PCMResourceSetPartition lookupPcmWorkingModel() {
        return experimentRunner.getWorkingPartition(); 
    }
    
    @Override
    public void apply(PerceivedValue<?> change) {
        LOGGER.debug(String.format("Apply pcmModelChanges: pcmAttributeName:'%s' ; perceived value:'%s'", pcmAttrbuteName, change.getValue().toString()));
        // fixme: replace ? with a concrete type
        Optional<?> newValue = change.getElement(pcmAttrbuteName);
        
        if (newValue.isPresent()) {
            CategoricalValue changedValue = (CategoricalValue) change.getElement(pcmAttrbuteName).get();
            applyChange(changedValue);
        } else {
            LOGGER.error("Failed binding: could not apply changed perceived value to PCM.");
        }
    }
    
}
