package org.palladiosimulator.simexp.pcm.modelled;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.simexp.dsl.smodel.smodel.Smodel;
import org.palladiosimulator.simexp.pcm.examples.executor.ModelLoader;

public interface ModelledModelLoader extends ModelLoader {
    interface Factory extends ModelLoader.Factory {
        @Override
        ModelledModelLoader create();
    }

    Smodel loadSModel(URI smodelURI);
}
