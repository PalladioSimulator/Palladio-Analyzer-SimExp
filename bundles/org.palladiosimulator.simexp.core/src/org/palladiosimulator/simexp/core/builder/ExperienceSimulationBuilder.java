package org.palladiosimulator.simexp.core.builder;

import static org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants.constructSampleSpaceId;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.palladiosimulator.simexp.core.action.Reconfiguration;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationConfiguration;
import org.palladiosimulator.simexp.core.process.ExperienceSimulationRunner;
import org.palladiosimulator.simexp.core.process.ExperienceSimulator;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.core.reward.RewardEvaluator;
import org.palladiosimulator.simexp.core.reward.SimulatedRewardReceiver;
import org.palladiosimulator.simexp.core.state.SimulationRunnerHolder;
import org.palladiosimulator.simexp.core.statespace.EnvironmentDrivenStateSpaceNavigator;
import org.palladiosimulator.simexp.core.statespace.SelfAdaptiveSystemStateSpaceNavigator;
import org.palladiosimulator.simexp.core.statespace.SelfAdaptiveSystemStateSpaceNavigator.InitialSelfAdaptiveSystemStateCreator;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.environmentaldynamics.process.UnobservableEnvironmentProcess;
import org.palladiosimulator.simexp.markovian.activity.ObservationProducer;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.builder.MarkovianBuilder;
import org.palladiosimulator.simexp.markovian.builder.StateSpaceNavigatorBuilder;
import org.palladiosimulator.simexp.markovian.config.MarkovianConfig;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovEntityFactory;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.sampling.MarkovSampling;
import org.palladiosimulator.simexp.markovian.sampling.SampleDumper;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;
import org.palladiosimulator.simexp.markovian.type.Markovian;

import tools.mdsd.probdist.api.random.ISeedProvider;

public abstract class ExperienceSimulationBuilder<C, A, Aa extends Reconfiguration<A>, R, V> {

    private String simulationID = "";
    private int numberOfRuns = 0;
    private int numberOfSamplesPerRun = 0;
    private Set<Aa> reconfigurationSpace = null;
    private RewardEvaluator<R> rewardEvaluator = null;
    private Policy<A, Aa> policy = null;
    private EnvironmentProcess<A, R, V> envProcess = null;
    private SimulatedExperienceStore<A, R> simulatedExperienceStore;
    private SimulationRunnerHolder simulationRunnerHolder;
    private boolean isHiddenProcess = false;
    private Optional<MarkovModel<A, R>> markovModel = Optional.empty();
    private SelfAdaptiveSystemStateSpaceNavigator<C, A, R, V> navigator = null;
    private Optional<ProbabilityMassFunction<State>> initialDistribution = Optional.empty();
    private List<Initializable> beforeExecutionInitializables = null;
    private SampleDumper sampleDumper = null;
    private ISeedProvider seedProvider = null;

    protected abstract List<ExperienceSimulationRunner> getSimulationRunner();

    protected abstract InitialSelfAdaptiveSystemStateCreator<C, A, V> createInitialSassCreator();

    public SimulationConfigurationBuilder createSimulationConfiguration() {
        return new SimulationConfigurationBuilder();
    }

    public SelfAdaptiveSystemBuilder specifySelfAdaptiveSystemState() {
        return new SelfAdaptiveSystemBuilder();
    }

    public ReconfigurationSpaceBuilder createReconfigurationSpace() {
        return new ReconfigurationSpaceBuilder();
    }

    public RewardReceiverBuilder specifyRewardHandling() {
        return new RewardReceiverBuilder();
    }

    public ExperienceSimulator<C, A, R> build() {
        checkValidity();

        ExperienceSimulationConfiguration<C, A, R> config = ExperienceSimulationConfiguration.<C, A, R> newBuilder()
            .withSimulationID(simulationID)
            .withSampleSpaceID(constructSampleSpaceId(simulationID, policy.getId()))
            .withNumberOfRuns(numberOfRuns)
            .executeBeforeEachRun(beforeExecutionInitializables)
            .addSimulationRunner(getSimulationRunner())
            .sampleWith(buildMarkovSampler(seedProvider, sampleDumper))
            .build();
        return ExperienceSimulator.createSimulator(config, simulatedExperienceStore, simulationRunnerHolder);
    }

    private void checkValidity() {
        // TODO exception handling
        Objects.requireNonNull(rewardEvaluator, "");
        Objects.requireNonNull(reconfigurationSpace, "");
        Objects.requireNonNull(policy, "");
        if (envProcess == null && navigator == null) {
            throw new IllegalArgumentException(
                    "Neither an environmental process nor an state space navigator is specified.");
        }
    }

    private MarkovSampling<A, R> buildMarkovSampler(ISeedProvider seedProvider, SampleDumper sampleDumper) {
        MarkovianConfig<A, R> markovianConfig = buildMarkovianConfig(seedProvider);
        return new MarkovSampling<>(markovianConfig, sampleDumper);
    }

    private MarkovianConfig<A, R> buildMarkovianConfig(ISeedProvider seedProvider) {
        Markovian<A, R> markovian = buildMarkovian(seedProvider);
        return new MarkovianConfig<>(numberOfSamplesPerRun, markovian, null); // TODO bring
                                                                              // in
                                                                              // accordance
                                                                              // with
                                                                              // ReconfigurationFilter...
    }

    private Markovian<A, R> buildMarkovian(ISeedProvider seedProvider) {
        StateSpaceNavigator<A> navigator = buildStateSpaceNavigator();
        ProbabilityMassFunction<State> initial = initialDistribution.orElse(getInitialDistribution(navigator));
        initial.init(seedProvider);
        if (isHiddenProcess) {
            return buildPOMDP(initial, navigator);
        }
        return buildMDP(initial, navigator);
    }

    private ProbabilityMassFunction<State> getInitialDistribution(StateSpaceNavigator<A> navigator) {
        if ((navigator instanceof SelfAdaptiveSystemStateSpaceNavigator) == false) {
            // TODO Exception handling
            throw new RuntimeException("");
        }

        return ((SelfAdaptiveSystemStateSpaceNavigator<C, A, R, V>) navigator)
            .createInitialDistribution(createInitialSassCreator());
    }

    private Markovian<A, R> buildPOMDP(ProbabilityMassFunction<State> initialDist, StateSpaceNavigator<A> navigator) {
        if (UnobservableEnvironmentProcess.class.isInstance(envProcess) == false) {
            throw new RuntimeException("The environment must be unobservable to declare the process as POMDP.");
        }

        SimulatedRewardReceiver<C, A, R, V> rewardReceiver = SimulatedRewardReceiver.<C, A, R, V> with(rewardEvaluator);
        return MarkovianBuilder.<A, Aa, R> createPartiallyObservableMDP()
            .createStateSpaceNavigator(navigator)
            .calculateRewardWith(rewardReceiver)
            .selectActionsAccordingTo(policy)
            .withActionSpace(getReconfigurationSpace())
            .withInitialStateDistribution(initialDist)
            .handleObservationsWith(PASS_THROUGH_OBS_PRODUCER)
            .build();
    }

    private Markovian<A, R> buildMDP(ProbabilityMassFunction<State> initialDist, StateSpaceNavigator<A> navigator) {
        return MarkovianBuilder.<A, Aa, R> createMarkovDecisionProcess()
            .createStateSpaceNavigator(navigator)
            .calculateRewardWith(SimulatedRewardReceiver.<C, A, R, V> with(rewardEvaluator))
            .selectActionsAccordingTo(policy)
            .withActionSpace(getReconfigurationSpace())
            .withInitialStateDistribution(initialDist)
            .build();
    }

    private Set<Aa> getReconfigurationSpace() {
        return reconfigurationSpace.stream()
            .map(each -> each)
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private StateSpaceNavigator<A> buildStateSpaceNavigator() {
        if (markovModel.isPresent()) {
            return StateSpaceNavigatorBuilder.createStateSpaceNavigator(markovModel.get())
                .build();
        } else {
            return createInductiveStateSpaceNavigator();
        }
    }

    private StateSpaceNavigator<A> createInductiveStateSpaceNavigator() {
        if (envProcess != null) {
            return EnvironmentDrivenStateSpaceNavigator.with(envProcess, simulatedExperienceStore,
                    simulationRunnerHolder);
        }
        return navigator;
    }

    public class SimulationConfigurationBuilder {

        public SimulationConfigurationBuilder withSimulationID(String simulationID) {
            ExperienceSimulationBuilder.this.simulationID = simulationID;
            return this;
        }

        public SimulationConfigurationBuilder withNumberOfRuns(int numberOfRuns) {
            ExperienceSimulationBuilder.this.numberOfRuns = numberOfRuns;
            return this;
        }

        public SimulationConfigurationBuilder withSeedProvider(ISeedProvider seedProvider) {
            ExperienceSimulationBuilder.this.seedProvider = seedProvider;
            return this;
        }

        public SimulationConfigurationBuilder andNumberOfSimulationsPerRun(int numberOfSamplesPerRun) {
            ExperienceSimulationBuilder.this.numberOfSamplesPerRun = numberOfSamplesPerRun;
            return this;
        }

        public SimulationConfigurationBuilder andOptionalExecutionBeforeEachRun(
                List<Initializable> beforeExecutionInitializables) {
            ExperienceSimulationBuilder.this.beforeExecutionInitializables = beforeExecutionInitializables;
            return this;
        }

        public SimulationConfigurationBuilder usingSampleDumper(SampleDumper sampleDumper) {
            ExperienceSimulationBuilder.this.sampleDumper = sampleDumper;
            return this;
        }

        public ExperienceSimulationBuilder<C, A, Aa, R, V> done() {
            return ExperienceSimulationBuilder.this;
        }
    }

    public class SelfAdaptiveSystemBuilder {

        public SelfAdaptiveSystemBuilder asEnvironmentalDrivenProcess(EnvironmentProcess<A, R, V> envProcess,
                SimulatedExperienceStore<A, R> simulatedExperienceStore,
                SimulationRunnerHolder simulationRunnerHolder) {
            ExperienceSimulationBuilder.this.envProcess = envProcess;
            ExperienceSimulationBuilder.this.simulatedExperienceStore = simulatedExperienceStore;
            ExperienceSimulationBuilder.this.simulationRunnerHolder = simulationRunnerHolder;
            return this;
        }

        public SelfAdaptiveSystemBuilder asPartiallyEnvironmentalDrivenProcess(
                SelfAdaptiveSystemStateSpaceNavigator<C, A, R, V> navigator) {
            ExperienceSimulationBuilder.this.navigator = navigator;
            return this;
        }

        public SelfAdaptiveSystemBuilder isHiddenProcess() {
            ExperienceSimulationBuilder.this.isHiddenProcess = true;
            return this;
        }

        public SelfAdaptiveSystemBuilder asHiddenProcess(boolean hidden) {
            ExperienceSimulationBuilder.this.isHiddenProcess = hidden;
            return this;
        }

        public SelfAdaptiveSystemBuilder andInitialDistributionOptionally(ProbabilityMassFunction<State> initialDist) {
            ExperienceSimulationBuilder.this.initialDistribution = Optional.ofNullable(initialDist);
            return this;
        }

        public ExperienceSimulationBuilder<C, A, Aa, R, V> done() {
            return ExperienceSimulationBuilder.this;
        }
    }

    public class ReconfigurationSpaceBuilder {

        private class ReconfigurationStrategyAdapter implements Policy<A, Aa> {

            private final Policy<A, Aa> adaptedStrategy;

            public ReconfigurationStrategyAdapter(Policy<A, Aa> adaptedStrategy) {
                this.adaptedStrategy = adaptedStrategy;
            }

            @Override
            public String getId() {
                return adaptedStrategy.getId();
            }

            @Override
            public Aa select(State source, Set<Aa> options) {
                Set<Aa> reconfigurationOptions = options.stream()
                    .map(each -> each)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
                // FIXME: simplify: execution of action should be part of MAPE-K -> no return type
                // of Action required
                return adaptedStrategy.select(source, reconfigurationOptions);
            }

        }

        public ReconfigurationSpaceBuilder addReconfiguration(Aa reconf) {
            if (ExperienceSimulationBuilder.this.reconfigurationSpace == null) {
                ExperienceSimulationBuilder.this.reconfigurationSpace = new LinkedHashSet<>();
            }
            ExperienceSimulationBuilder.this.reconfigurationSpace.add(reconf);
            return this;
        }

        public ReconfigurationSpaceBuilder addReconfigurations(Set<Aa> reconfs) {
            if (ExperienceSimulationBuilder.this.reconfigurationSpace == null) {
                ExperienceSimulationBuilder.this.reconfigurationSpace = new LinkedHashSet<>();
            }
            ExperienceSimulationBuilder.this.reconfigurationSpace.addAll(reconfs);
            return this;
        }

        public ReconfigurationSpaceBuilder andReconfigurationStrategy(Policy<A, Aa> policy) {
            ExperienceSimulationBuilder.this.policy = policy;
            return this;
        }

        public ReconfigurationSpaceBuilder andReconfigurationStrategy(ReconfigurationStrategy<A, Aa> strategy) {
            // todo: setup mape-k executor here
            ExperienceSimulationBuilder.this.policy = new ReconfigurationStrategyAdapter(strategy);
            return this;
        }

        public ExperienceSimulationBuilder<C, A, Aa, R, V> done() {
            return ExperienceSimulationBuilder.this;
        }
    }

    public class RewardReceiverBuilder {

        public RewardReceiverBuilder withRewardEvaluator(RewardEvaluator<R> evaluator) {
            ExperienceSimulationBuilder.this.rewardEvaluator = evaluator;
            return this;
        }

        public ExperienceSimulationBuilder<C, A, Aa, R, V> done() {
            return ExperienceSimulationBuilder.this;
        }
    }

//  private final static ObservationProducer PASS_THROUGH_OBS_PRODUCER = new ObservationProducer() {
//      
//      @Override
//      public Observation<?> produceObservationGiven(State emittingState) {
//          return new ObservationImpl<SelfAdaptiveSystemState<?>>() {
//              
//              @Override
//              public SelfAdaptiveSystemState<?> getValue() {
//                  return (SelfAdaptiveSystemState<?>) emittingState;
//              };
//          };
//      }
//  };

    // Note that in POMDPs only the environment is unobservable captured by the handler of the
    // nested HMM.
    // Therefore, only a pass through obs handler is required.
    private static class OP implements ObservationProducer {

        @Override
        public Observation produceObservationGiven(State emittingState) {
            Observation observation = MarkovEntityFactory.eINSTANCE.createObservation();
            observation.setObserved(emittingState);
            return observation;
        }
    };

    private final ObservationProducer PASS_THROUGH_OBS_PRODUCER = new OP();

}
