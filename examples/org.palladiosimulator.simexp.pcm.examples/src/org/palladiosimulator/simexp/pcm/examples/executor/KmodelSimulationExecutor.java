package org.palladiosimulator.simexp.pcm.examples.executor;

import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.pcm.action.IQVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;
import org.palladiosimulator.simexp.pcm.util.SimulationParameters;

public abstract class KmodelSimulationExecutor<C, A, Aa extends Action<A>, R>
        extends PcmExperienceSimulationExecutor<C, A, Aa, R> {
    private final Kmodel kmodel;

    protected KmodelSimulationExecutor(ExperienceSimulator<C, A, R> experienceSimulator, Experiment experiment,
            SimulationParameters simulationParameters, Policy<A, Aa> reconfSelectionPolicy,
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
