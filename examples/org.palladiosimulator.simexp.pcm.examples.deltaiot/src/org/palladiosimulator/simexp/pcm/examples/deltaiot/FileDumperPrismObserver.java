package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import org.apache.log4j.Logger;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismContext;
import org.palladiosimulator.simexp.pcm.prism.process.IPrismObserver;

public class FileDumperPrismObserver implements IPrismObserver {
    private static final Logger LOGGER = Logger.getLogger(FileDumperPrismObserver.class);

    public FileDumperPrismObserver() {
    }

    @Override
    public void onContext(PrismContext context) {
        LOGGER.info("Prism module:");
        LOGGER.info("Prism property: " + context.propertyFileContent);
        LOGGER.info(context.moduleFileContent);
    }
}
