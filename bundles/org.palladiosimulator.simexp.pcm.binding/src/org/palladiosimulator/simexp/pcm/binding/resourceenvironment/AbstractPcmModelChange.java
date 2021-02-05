package org.palladiosimulator.simexp.pcm.binding.resourceenvironment;

import java.util.Optional;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivedValue;
import org.palladiosimulator.simexp.pcm.perceiption.PcmModelChange;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public abstract class AbstractPcmModelChange implements PcmModelChange {
    
    private static final Logger LOGGER = Logger.getLogger(AbstractPcmModelChange.class);
    
    private final String pcmAttrbuteName;
    
    public AbstractPcmModelChange(String pcmAttributeName){
        this.pcmAttrbuteName = pcmAttributeName;
    }
    
    abstract void applyChange(CategoricalValue value);
    
    protected String getPcmAttributeName() {
        return pcmAttrbuteName;
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
