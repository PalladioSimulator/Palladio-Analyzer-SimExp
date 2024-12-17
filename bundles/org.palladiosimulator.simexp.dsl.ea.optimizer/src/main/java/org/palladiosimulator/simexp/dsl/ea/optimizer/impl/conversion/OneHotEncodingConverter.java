package org.palladiosimulator.simexp.dsl.ea.optimizer.impl.conversion;

public class OneHotEncodingConverter extends AbstractConverter {

    public OneHotEncodingConverter() {
        super(new OneHotCodecCreator());
    }

}
