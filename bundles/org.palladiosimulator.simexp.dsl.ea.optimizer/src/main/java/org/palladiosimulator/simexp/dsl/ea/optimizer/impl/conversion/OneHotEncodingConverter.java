package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.conversion;

import org.palladiosimulator.simexp.dsl.ea.optimizer.impl.conversion.codecs.OneHotCodecCreator;

public class OneHotEncodingConverter extends AbstractConverter {

    public OneHotEncodingConverter() {
        super(new OneHotCodecCreator());
    }

}
