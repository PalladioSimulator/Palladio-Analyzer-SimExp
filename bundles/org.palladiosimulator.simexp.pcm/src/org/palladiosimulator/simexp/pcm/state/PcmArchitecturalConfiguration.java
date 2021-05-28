package org.palladiosimulator.simexp.pcm.state;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.ecore.resource.Resource;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.state.ArchitecturalConfiguration;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.compare.PcmModelComparison;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import de.uka.ipd.sdq.scheduler.resources.active.IResourceTableManager;
import de.uka.ipd.sdq.scheduler.resources.active.ResourceTableManager;

public class PcmArchitecturalConfiguration extends ArchitecturalConfiguration<PCMInstance> {
    
    private static final Logger LOGGER = Logger.getLogger(PcmArchitecturalConfiguration.class.getName());

	private PcmArchitecturalConfiguration(PCMInstance configuration) {
		super(configuration);
	}

	public static PcmArchitecturalConfiguration of(PCMInstance configuration) {
		return new PcmArchitecturalConfiguration(configuration);
	}

	@Override
	public String getStringRepresentation() {
		return Integer.toString(deriveUniqueID());
	}

	private Integer deriveUniqueID() {
		OutputStream output = new ByteArrayOutputStream();
		getConfiguration().getRepositories().forEach(r -> appendSerializationString(r.eResource(), output));
		appendSerializationString(getConfiguration().getSystem().eResource(), output);
		appendSerializationString(getConfiguration().getAllocation().eResource(), output);
		return output.toString().hashCode();
	}

	private void appendSerializationString(Resource resource, OutputStream output) {
		try {
			resource.save(output, Collections.EMPTY_MAP);
		} catch (IOException e) {
			// TODO Exception handling
			throw new RuntimeException(e);
		}
	}

	@Override
	public String difference(ArchitecturalConfiguration<?> other) {
		if (isNotValid(other)) {
			return "";
		}
		return difference((PcmArchitecturalConfiguration) other);
	}

	@Override
	public ArchitecturalConfiguration<?> apply(Reconfiguration<?> reconf) {
		if (isNotValid(reconf)) {
			throw new RuntimeException("Failed to apply reconfiguration: reconfiguration was invalid. Expected a QVTO transformation.");
		}

		QVToReconfiguration qvtoReconf = (QVToReconfiguration) reconf;
		if (qvtoReconf.isEmptyReconfiguration() == false) {
			apply(qvtoReconf, new ResourceTableManager());
		}

		return new PcmArchitecturalConfiguration(makeSnapshot());
	}

	private PCMInstance makeSnapshot() {
		return ExperimentProvider.get().getExperimentRunner().makeSnapshotOfPCM();
	}

	private void apply(QVToReconfiguration reconf, IResourceTableManager resourceTableManager) {
		QVTOReconfigurator qvtoReconf = QVToReconfigurationManager.get().getReconfigurator();
		boolean succeded = qvtoReconf.runExecute(ECollections.asEList(reconf.getTransformation()), null, resourceTableManager);
		if (!succeded) {
			LOGGER.error("Failed to apply reconfiguration: reconfiguration engine could not execute reconfiguration.");
		}
		LOGGER.info(String.format("Applied reconfiguration: '%s'", reconf.getTransformation().getTransformationName()));
	}

	private boolean isNotValid(Reconfiguration<?> action) {
		return (action instanceof QVToReconfiguration) == false;
	}

	private boolean isNotValid(ArchitecturalConfiguration<?> other) {
		return other == null || !(other instanceof PcmArchitecturalConfiguration);
	}

	private String difference(PcmArchitecturalConfiguration other) {
		PCMInstance first = getConfiguration();
		PCMInstance second = other.getConfiguration();
		return PcmModelComparison.of(first).compareTo(second).toString();
	}

}
