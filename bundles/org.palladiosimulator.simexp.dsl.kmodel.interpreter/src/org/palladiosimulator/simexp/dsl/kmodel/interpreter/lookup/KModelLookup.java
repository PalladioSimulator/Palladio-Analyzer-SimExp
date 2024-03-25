package org.palladiosimulator.simexp.dsl.kmodel.interpreter.lookup;

import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;

public class KModelLookup implements IModelNameLookup {

    private final Kmodel kmodel;

    public KModelLookup(Kmodel kmodel) {
        this.kmodel = kmodel;
    }

    @Override
    public String findModelName() {
        String modelName = kmodel.getModelName();
        return modelName;
    }

}
