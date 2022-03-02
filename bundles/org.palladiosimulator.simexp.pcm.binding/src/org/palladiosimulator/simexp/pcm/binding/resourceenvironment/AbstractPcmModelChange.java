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
        LOGGER.debug(String.format("Apply perceived environmental value '%s' to PCM model element '%s'", change.getValue().toString(), pcmAttrbuteName));
        // fixme: replace ? with a concrete type
        Optional<?> newValue = change.getElement(pcmAttrbuteName);
        
        if (newValue.isPresent()) {
            CategoricalValue changedValue = (CategoricalValue) change.getElement(pcmAttrbuteName).get();
            applyChange(changedValue);
        } else {
            LOGGER.error(String.format("Failed binding: could not apply perceived environmental value '%s' to PCM model element '%s'", change.getValue().toString(), pcmAttrbuteName));
        }
    }
    
}
