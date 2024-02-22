package org.palladiosimulator.simexp.pcm.prism.generator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Set;

import org.palladiosimulator.simexp.pcm.prism.entity.PrismContext;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;

public class PrismFileUpdateGenerator<A, V> implements PrismGenerator<A, V> {

    public static abstract class PrismFileUpdater<A, V> {

        protected final PrismSimulatedMeasurementSpec prismSpec;

        public PrismFileUpdater(PrismSimulatedMeasurementSpec prismSpec) {
            this.prismSpec = prismSpec;
        }

        public boolean isApplicable(PrismSimulatedMeasurementSpec prismSpec) {
            return this.prismSpec.getId()
                .equals(prismSpec.getId());
        }

        protected String stringify(File prismFile) {
            try {
                return new String(Files.readAllBytes(prismFile.toPath()), Charset.defaultCharset());
            } catch (IOException e) {
                // TODO exception handling
                throw new RuntimeException("Something went wrong while converting prism files to strings.", e);
            }
        }

        public abstract PrismContext apply(PcmSelfAdaptiveSystemState<A, V> sasState);
    }

    private final Set<PrismFileUpdater<A, V>> prismFileUpdater;

    public PrismFileUpdateGenerator(Set<PrismFileUpdater<A, V>> prismFileUpdater) {
        this.prismFileUpdater = prismFileUpdater;
    }

    @Override
    public PrismContext generate(PcmSelfAdaptiveSystemState<A, V> sasState, PrismSimulatedMeasurementSpec prismSpec) {
        for (PrismFileUpdater<A, V> each : prismFileUpdater) {
            if (each.isApplicable(prismSpec)) {
                return each.apply(sasState);
            }
        }
        // TODO Exception handling
        throw new RuntimeException("There is no applicable prism file updator.");
    }

}
