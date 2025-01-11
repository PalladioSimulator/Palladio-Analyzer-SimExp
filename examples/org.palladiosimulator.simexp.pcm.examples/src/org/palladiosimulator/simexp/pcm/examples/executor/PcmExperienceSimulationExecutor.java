package org.palladiosimulator.simexp.pcm.examples.executor;

import org.apache.log4j.Logger;
import org.palladiosimulator.core.simulation.SimulationExecutor;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.core.evaluation.TotalRewardCalculation;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Action;
import org.palladiosimulator.simexp.pcm.config.SimulationParameters;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;

public class PcmExperienceSimulationExecutor<C, A, Aa extends Action<A>, R> implements SimulationExecutor {

    protected static final Logger LOGGER = Logger.getLogger(PcmExperienceSimulationExecutor.class.getName());

    protected final ExperienceSimulator<C, A, R> experienceSimulator;
    protected final Experiment experiment;
    protected final SimulationParameters simulationParameters;
    protected final IExperimentProvider experimentProvider;
    protected final Policy<A, Aa> reconfSelectionPolicy;
    protected final TotalRewardCalculation rewardCalculation;

    public PcmExperienceSimulationExecutor(ExperienceSimulator<C, A, R> experienceSimulator, Experiment experiment,
            SimulationParameters simulationParameters, Policy<A, Aa> reconfSelectionPolicy,
            TotalRewardCalculation rewardCalculation, IExperimentProvider experimentProvider) {
        this.experienceSimulator = experienceSimulator;
        this.experiment = experiment;
        this.simulationParameters = simulationParameters;
        this.reconfSelectionPolicy = reconfSelectionPolicy;
        this.rewardCalculation = rewardCalculation;
        this.experimentProvider = experimentProvider;
    }

    @Override
    public String getPolicyId() {
        return reconfSelectionPolicy.getId();
    }

    @Override
    public void execute() {
        experienceSimulator.run();
    }

    @Override
    public void evaluate() {
        double totalReward = rewardCalculation.computeTotalReward();
        LOGGER.info("***********************************************************************");
        LOGGER.info(String.format("The total %s reward of policy %1s is %2s", rewardCalculation.getName(),
                reconfSelectionPolicy.getId(), totalReward));
        LOGGER.info("***********************************************************************");
    }
}
