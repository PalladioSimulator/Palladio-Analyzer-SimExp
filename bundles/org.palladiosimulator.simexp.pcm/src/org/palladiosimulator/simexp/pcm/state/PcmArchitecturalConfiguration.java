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
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.compare.PcmModelComparison;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import de.uka.ipd.sdq.scheduler.resources.active.IResourceTableManager;
import de.uka.ipd.sdq.scheduler.resources.active.ResourceTableManager;

public class PcmArchitecturalConfiguration<A> extends ArchitecturalConfiguration<PCMInstance, A> {

    private static final Logger LOGGER = Logger.getLogger(PcmArchitecturalConfiguration.class.getName());
    private final IExperimentProvider experimentProvider;
    private final IQVToReconfigurationManager qvtoReconfigurationManager;

    private PcmArchitecturalConfiguration(PCMInstance configuration, IExperimentProvider experimentProvider,
            IQVToReconfigurationManager qvtoReconfigurationManager) {
        super(configuration);
        this.experimentProvider = experimentProvider;
        this.qvtoReconfigurationManager = qvtoReconfigurationManager;
    }

    public static <A> PcmArchitecturalConfiguration<A> of(PCMInstance configuration,
            IExperimentProvider experimentProvider, IQVToReconfigurationManager qvtoReconfigurationManager) {
        return new PcmArchitecturalConfiguration<>(configuration, experimentProvider, qvtoReconfigurationManager);
    }

    @Override
    public String getStringRepresentation() {
        return Integer.toString(deriveUniqueID());
    }

    private Integer deriveUniqueID() {
        OutputStream output = new ByteArrayOutputStream();
        getConfiguration().getRepositories()
            .forEach(r -> appendSerializationString(r.eResource(), output));
        appendSerializationString(getConfiguration().getSystem()
            .eResource(), output);
        appendSerializationString(getConfiguration().getAllocation()
            .eResource(), output);
        return output.toString()
            .hashCode();
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
    public String difference(ArchitecturalConfiguration<PCMInstance, A> other) {
        if (isNotValid(other)) {
            return "";
        }
        return difference((PcmArchitecturalConfiguration<A>) other);
    }

    @Override
    public ArchitecturalConfiguration<PCMInstance, A> apply(Reconfiguration<A> reconf) {
        if (isNotValid(reconf)) {
            throw new RuntimeException(
                    "'EXECUTE' failed to apply reconfiguration: Found invalid reconfiguration; expected an instance of QVToReconfiguration");
        }
        QVToReconfiguration qvtoReconf = (QVToReconfiguration) reconf;
        if (qvtoReconf.isEmptyReconfiguration() == false) {
            String transformationName = qvtoReconf.getTransformation()
                .getTransformationName();
            LOGGER.info(String.format("'EXECUTE' apply reconfiguration '%s'", transformationName));
            doApply(qvtoReconf, new ResourceTableManager(), qvtoReconfigurationManager);
        }

        LOGGER.info("'EXECUTE' step done");
        PcmArchitecturalConfiguration<A> updatedArchitecturalConfiguration = new PcmArchitecturalConfiguration<>(
                makeSnapshot(experimentProvider), experimentProvider, qvtoReconfigurationManager);
        return updatedArchitecturalConfiguration;
    }

    private PCMInstance makeSnapshot(IExperimentProvider experimentProvider) {
        return experimentProvider.getExperimentRunner()
            .makeSnapshotOfPCM();
    }

    private void doApply(QVToReconfiguration reconf, IResourceTableManager resourceTableManager,
            IQVToReconfigurationManager qvtoReconfigurationManager) {
        boolean succeded = false;
        QVTOReconfigurator qvtoReconf = qvtoReconfigurationManager.getReconfigurator(experimentProvider);
        succeded = qvtoReconf.runExecute(ECollections.asEList(reconf.getTransformation()), null, resourceTableManager);
        String transformationName = reconf.getTransformation()
            .getTransformationName();
        if (succeded) {
            LOGGER.info(String.format("'EXECUTE' applied reconfiguration '%s'", transformationName));
        } else {
            LOGGER.error(String.format(
                    "'EXECUTE' failed to apply reconfiguration: reconfiguration engine could not execute reconfiguration '%s'",
                    transformationName));
        }
    }

    private boolean isNotValid(Reconfiguration<A> action) {
        return !isValid(action);
    }

    private boolean isValid(Reconfiguration<A> action) {
        return action instanceof QVToReconfiguration;
    }

    private boolean isNotValid(ArchitecturalConfiguration<PCMInstance, A> other) {
        return other == null || !(other instanceof PcmArchitecturalConfiguration);
    }

    private String difference(PcmArchitecturalConfiguration<A> other) {
        PCMInstance first = getConfiguration();
        PCMInstance second = other.getConfiguration();
        return PcmModelComparison.of(first)
            .compareTo(second)
            .toString();
    }

}
