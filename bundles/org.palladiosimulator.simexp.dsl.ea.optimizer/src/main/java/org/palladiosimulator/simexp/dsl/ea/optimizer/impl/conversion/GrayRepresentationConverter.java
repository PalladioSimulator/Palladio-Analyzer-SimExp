package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.conversion;

public class GrayRepresentationConverter extends AbstractConverter {

    public GrayRepresentationConverter() {
        super(new GrayCodecCreator());
    }

}
