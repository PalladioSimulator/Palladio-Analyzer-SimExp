package org.palladiosimulator.simexp.pcm.prism.generator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Set;

import org.palladiosimulator.simexp.pcm.prism.entity.PrismContext;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simexp.pcm.state.PcmSelfAdaptiveSystemState;

public class PrismFileUpdateGenerator implements PrismGenerator {

	public static abstract class PrismFileUpdater {

		protected final PrismSimulatedMeasurementSpec prismSpec;
		
		public PrismFileUpdater(PrismSimulatedMeasurementSpec prismSpec) {
			this.prismSpec = prismSpec;
		}
		
		public boolean isApplicable(PrismSimulatedMeasurementSpec prismSpec) {
			return this.prismSpec.getId().equals(prismSpec.getId());
		}
		
		protected String stringify(File prismFile) {
			try {
				return new String(Files.readAllBytes(prismFile.toPath()), Charset.defaultCharset());
			} catch (IOException e) {
				// TODO exception handling
				throw new RuntimeException("Something went wrong while converting prism files to strings.", e);
			}
		}
		
		public abstract PrismContext apply(PcmSelfAdaptiveSystemState sasState);
	}

	private final Set<PrismFileUpdater> prismFileUpdater;

	public PrismFileUpdateGenerator(Set<PrismFileUpdater> prismFileUpdater) {
		this.prismFileUpdater = prismFileUpdater;
	}

	@Override
	public PrismContext generate(PcmSelfAdaptiveSystemState sasState, PrismSimulatedMeasurementSpec prismSpec) {
		for (PrismFileUpdater each : prismFileUpdater) {
			if (each.isApplicable(prismSpec)) {
				return each.apply(sasState);
			}
		}
		//TODO Exception handling
		throw new RuntimeException("There is no applicable prism file updator.");
	}

}
