package org.palladiosimulator.simexp.dsl.smodel.interpreter.lookup;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;

public class SModelLookup implements IModelNameLookup {

    private final Smodel smodel;

    public SModelLookup(Smodel smodel) {
        this.smodel = smodel;
    }

    @Override
    public String findModelName() {
        String modelName = smodel.getModelName();
        return modelName;
    }

}
