package org.palladiosimulator.simexp.pcm.binding.resourceenvironment;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;
import org.palladiosimulator.simexp.pcm.perceiption.PcmModelChange;

public abstract class AbstractPcmModelChange implements PcmModelChange {
    
    private static final Logger LOGGER = Logger.getLogger(AbstractPcmModelChange.class);
    
    private final String pcmAttrbuteName;
    protected final PCMResourceSetPartition pcm;
    
    public AbstractPcmModelChange(String pcmAttributeName, PCMResourceSetPartition pcm){
        this.pcmAttrbuteName = pcmAttributeName;
        this.pcm = pcm;
    }
    
    abstract void applyChange(Object object);
    
    @Override
    public void apply(PerceivedValue<?> change) {
        LOGGER.debug(String.format("Apply pcmModelChanges: pcmAttributeName:'%s' ; changed value:'%s'", pcmAttrbuteName, change.getValue().toString()));
        
        // fixme: replace ? with a concrete type
        Optional<?> newValue = change.getElement(pcmAttrbuteName);
        
        if (newValue.isPresent()) {
            applyChange(newValue.get());
        } else {
            LOGGER.error("Failed binding: could not apply changed value to PCM.");
        }
    }
    
}
