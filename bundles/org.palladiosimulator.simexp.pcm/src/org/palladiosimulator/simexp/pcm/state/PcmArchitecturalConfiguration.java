package org.palladiosimulator.simexp.pcm.state;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.resource.Resource;
import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.state.ArchitecturalConfiguration;
import org.palladiosimulator.simexp.pcm.compare.PcmModelComparison;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.solver.models.PCMInstance;

import de.uka.ipd.sdq.scheduler.resources.active.ResourceTableManager;

public class PcmArchitecturalConfiguration<A> extends ArchitecturalConfiguration<PCMInstance, A> {

    private static final Logger LOGGER = Logger.getLogger(PcmArchitecturalConfiguration.class.getName());
    private final IExperimentProvider experimentProvider;

    private PcmArchitecturalConfiguration(PCMInstance configuration, IExperimentProvider experimentProvider) {
        super(configuration);
        this.experimentProvider = experimentProvider;
    }

    public static <A> PcmArchitecturalConfiguration<A> of(PCMInstance configuration,
            IExperimentProvider experimentProvider) {
        return new PcmArchitecturalConfiguration<>(configuration, experimentProvider);
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
                    "'EXECUTE' failed to apply reconfiguration: Found invalid reconfiguration; expected an instance of IPCMReconfigurationExecutor");
        }
        IPCMReconfigurationExecutor qvtoReconf = (IPCMReconfigurationExecutor) reconf;
        qvtoReconf.apply(experimentProvider, new ResourceTableManager());

        LOGGER.info("'EXECUTE' step done");
        PcmArchitecturalConfiguration<A> updatedArchitecturalConfiguration = new PcmArchitecturalConfiguration<>(
                makeSnapshot(experimentProvider), experimentProvider);
        return updatedArchitecturalConfiguration;
    }

    private PCMInstance makeSnapshot(IExperimentProvider experimentProvider) {
        return experimentProvider.getExperimentRunner()
            .makeSnapshotOfPCM();
    }

    private boolean isNotValid(Reconfiguration<A> action) {
        return !isValid(action);
    }

    private boolean isValid(Reconfiguration<A> action) {
        return action instanceof IPCMReconfigurationExecutor;
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
