package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.conversion;

import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.conversion.codecs.GrayCodecCreator;

public class GrayRepresentationConverter extends AbstractConverter {

    public GrayRepresentationConverter() {
        super(new GrayCodecCreator());
    }

}
