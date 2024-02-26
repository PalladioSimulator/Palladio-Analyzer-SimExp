package org.palladiosimulator.simexp.model.strategy;

import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.executor.PcmExperienceSimulationExecutor;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simexp.pcm.util.SimulationParameters;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

public abstract class ModelledSimulationExecutor<R>
        extends PcmExperienceSimulationExecutor<PCMInstance, QVTOReconfigurator, QVToReconfiguration, R> {
    private final Kmodel kmodel;

    protected ModelledSimulationExecutor(ExperienceSimulator<PCMInstance, QVTOReconfigurator, R> experienceSimulator,
            Experiment experiment, SimulationParameters simulationParameters,
            Policy<QVTOReconfigurator, QVToReconfiguration> reconfSelectionPolicy,
            TotalRewardCalculation rewardCalculation, IExperimentProvider experimentProvider,
            IQVToReconfigurationManager qvtoReconfigurationManager, Kmodel kmodel) {
        super(experienceSimulator, experiment, simulationParameters, reconfSelectionPolicy, rewardCalculation,
                experimentProvider, qvtoReconfigurationManager);
        this.kmodel = kmodel;
    }

    protected Kmodel getKmodel() {
        return kmodel;
    }
}
