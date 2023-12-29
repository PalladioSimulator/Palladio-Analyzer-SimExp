package org.palladiosimulator.simexp.core.builder;

import static org.palladiosimulator.simexp.core.util.SimulatedExperienceConstants.constructSampleSpaceId;

import java.util.HashSet;
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
import org.palladiosimulator.simexp.core.statespace.EnvironmentDrivenStateSpaceNavigator;
import org.palladiosimulator.simexp.core.statespace.SelfAdaptiveSystemStateSpaceNavigator;
import org.palladiosimulator.simexp.core.statespace.SelfAdaptiveSystemStateSpaceNavigator.InitialSelfAdaptiveSystemStateCreator;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.distribution.function.ProbabilityMassFunction;
import org.palladiosimulator.simexp.environmentaldynamics.process.EnvironmentProcess;
import org.palladiosimulator.simexp.environmentaldynamics.process.UnobservableEnvironmentProcess;
import org.palladiosimulator.simexp.markovian.activity.ObservationProducer;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.builder.MarkovianBuilder;
import org.palladiosimulator.simexp.markovian.builder.StateSpaceNavigatorBuilder;
import org.palladiosimulator.simexp.markovian.config.MarkovianConfig;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.MarkovModel;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.Observation;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.sampling.MarkovSampling;
import org.palladiosimulator.simexp.markovian.statespace.StateSpaceNavigator;
import org.palladiosimulator.simexp.markovian.type.Markovian;

public abstract class ExperienceSimulationBuilder<S, A, Aa extends Reconfiguration<A>, R> {

    private String simulationID = "";
    private int numberOfRuns = 0;
    private int numberOfSamplesPerRun = 0;
    private Set<Reconfiguration<A>> reconfigurationSpace = null;
    private RewardEvaluator<R> rewardEvaluator = null;
    private Policy<S, A, Aa> policy = null;
    private EnvironmentProcess envProcess = null;
    private boolean isHiddenProcess = false;
    private Optional<MarkovModel<S, A, R>> markovModel = Optional.empty();
    private SelfAdaptiveSystemStateSpaceNavigator<S, A> navigator = null;
    private Optional<ProbabilityMassFunction<State<S>>> initialDistribution = Optional.empty();
    private Initializable beforeExecutionInitialization = null;

    protected abstract List<ExperienceSimulationRunner> getSimulationRunner();

    protected abstract InitialSelfAdaptiveSystemStateCreator<S> createInitialSassCreator();

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

    public ExperienceSimulator<S, A, R> build() {
        checkValidity();

        ExperienceSimulationConfiguration<S, A, R> config = ExperienceSimulationConfiguration.<S, A, R> newBuilder()
            .withSimulationID(simulationID)
            .withSampleSpaceID(constructSampleSpaceId(simulationID, policy.getId()))
            .withNumberOfRuns(numberOfRuns)
            .executeBeforeEachRun(beforeExecutionInitialization)
            .addSimulationRunner(getSimulationRunner())
            .sampleWith(buildMarkovSampler())
            .build();
        return ExperienceSimulator.createSimulator(config);
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

    private MarkovSampling<S, A, R> buildMarkovSampler() {
        return new MarkovSampling<>(buildMarkovianConfig());
    }

    private MarkovianConfig<S, A, R> buildMarkovianConfig() {
        return new MarkovianConfig<>(numberOfSamplesPerRun, buildMarkovian(), null); // TODO bring
                                                                                     // in
                                                                                     // accordance
                                                                                     // with
                                                                                     // ReconfigurationFilter...
    }

    private Markovian<S, A, R> buildMarkovian() {
        StateSpaceNavigator<S, A> navigator = buildStateSpaceNavigator();
        ProbabilityMassFunction<State<S>> initial = initialDistribution.orElse(getInitialDistribution(navigator));
        if (isHiddenProcess) {
            return buildPOMDP(initial, navigator);
        }
        return buildMDP(initial, navigator);
    }

    private ProbabilityMassFunction<State<S>> getInitialDistribution(StateSpaceNavigator<S, A> navigator) {
        if ((navigator instanceof SelfAdaptiveSystemStateSpaceNavigator) == false) {
            // TODO Exception handling
            throw new RuntimeException("");
        }

        return ((SelfAdaptiveSystemStateSpaceNavigator<S, A>) navigator)
            .createInitialDistribution(createInitialSassCreator());
    }

    private Markovian<S, A, R> buildPOMDP(ProbabilityMassFunction<State<S>> initialDist,
            StateSpaceNavigator<S, A> navigator) {
        if (UnobservableEnvironmentProcess.class.isInstance(envProcess) == false) {
            throw new RuntimeException("The environment must be unobservable to declare the process as POMDP.");
        }

        SimulatedRewardReceiver<S, A, R> rewardReceiver = SimulatedRewardReceiver.<S, A, R> with(rewardEvaluator);
        return MarkovianBuilder.<S, A, Aa, R> createPartiallyObservableMDP()
            .createStateSpaceNavigator(navigator)
            .calculateRewardWith(rewardReceiver)
            .selectActionsAccordingTo(policy)
            .withActionSpace(getReconfigurationSpace())
            .withInitialStateDistribution(initialDist)
            .handleObservationsWith(PASS_THROUGH_OBS_PRODUCER)
            .build();
    }

    private Markovian<S, A, R> buildMDP(ProbabilityMassFunction<State<S>> initialDist,
            StateSpaceNavigator<S, A> navigator) {
        return MarkovianBuilder.<S, A, Aa, R> createMarkovDecisionProcess()
            .createStateSpaceNavigator(navigator)
            .calculateRewardWith(SimulatedRewardReceiver.<S, A, R> with(rewardEvaluator))
            .selectActionsAccordingTo(policy)
            .withActionSpace(getReconfigurationSpace())
            .withInitialStateDistribution(initialDist)
            .build();
    }

    private Set<Aa> getReconfigurationSpace() {
        return reconfigurationSpace.stream()
            .map(each -> (Aa) each)
            .collect(Collectors.toSet());
    }

    private StateSpaceNavigator<S, A> buildStateSpaceNavigator() {
        if (markovModel.isPresent()) {
            return StateSpaceNavigatorBuilder.createStateSpaceNavigator(markovModel.get())
                .build();
        } else {
            return createInductiveStateSpaceNavigator();
        }
    }

    private StateSpaceNavigator<S, A> createInductiveStateSpaceNavigator() {
        if (envProcess != null) {
            return EnvironmentDrivenStateSpaceNavigator.with(envProcess);
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

        public SimulationConfigurationBuilder andNumberOfSimulationsPerRun(int numberOfSamplesPerRun) {
            ExperienceSimulationBuilder.this.numberOfSamplesPerRun = numberOfSamplesPerRun;
            return this;
        }

        public SimulationConfigurationBuilder andOptionalExecutionBeforeEachRun(
                Initializable beforeExecutionInitialization) {
            ExperienceSimulationBuilder.this.beforeExecutionInitialization = beforeExecutionInitialization;
            return this;
        }

        public ExperienceSimulationBuilder<S, A, Aa, R> done() {
            return ExperienceSimulationBuilder.this;
        }
    }

    public class SelfAdaptiveSystemBuilder {

        public SelfAdaptiveSystemBuilder asEnvironmentalDrivenProcess(EnvironmentProcess envProcess) {
            ExperienceSimulationBuilder.this.envProcess = envProcess;
            return this;
        }

        public SelfAdaptiveSystemBuilder asPartiallyEnvironmentalDrivenProcess(
                SelfAdaptiveSystemStateSpaceNavigator<S, A> navigator) {
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

        public SelfAdaptiveSystemBuilder andInitialDistributionOptionally(
                ProbabilityMassFunction<State<S>> initialDist) {
            ExperienceSimulationBuilder.this.initialDistribution = Optional.ofNullable(initialDist);
            return this;
        }

        public ExperienceSimulationBuilder<S, A, Aa, R> done() {
            return ExperienceSimulationBuilder.this;
        }
    }

    public class ReconfigurationSpaceBuilder {

        private class ReconfigurationStrategyAdapter implements Policy<S, A, Aa> {

            private final Policy<S, A, Aa> adaptedStrategy;

            public ReconfigurationStrategyAdapter(Policy<S, A, Aa> adaptedStrategy) {
                this.adaptedStrategy = adaptedStrategy;
            }

            @Override
            public String getId() {
                return adaptedStrategy.getId();
            }

            @Override
            public Aa select(State<S> source, Set<Aa> options) {
                Set<Aa> reconfigurationOptions = options.stream()
                    .map(each -> each)
                    .collect(Collectors.toSet());
                // FIXME: simplify: execution of action should be part of MAPE-K -> no return type
                // of Action required
                return adaptedStrategy.select(source, reconfigurationOptions);
            }

        }

        public ReconfigurationSpaceBuilder addReconfiguration(Reconfiguration<A> reconf) {
            if (ExperienceSimulationBuilder.this.reconfigurationSpace == null) {
                ExperienceSimulationBuilder.this.reconfigurationSpace = new HashSet<>();
            }
            ExperienceSimulationBuilder.this.reconfigurationSpace.add(reconf);
            return this;
        }

        public ReconfigurationSpaceBuilder addReconfigurations(Set<Reconfiguration<A>> reconfs) {
            if (ExperienceSimulationBuilder.this.reconfigurationSpace == null) {
                ExperienceSimulationBuilder.this.reconfigurationSpace = new HashSet<>();
            }
            ExperienceSimulationBuilder.this.reconfigurationSpace.addAll(reconfs);
            return this;
        }

        public ReconfigurationSpaceBuilder andReconfigurationStrategy(Policy<S, A, Aa> policy) {
            ExperienceSimulationBuilder.this.policy = policy;
            return this;
        }

        public ReconfigurationSpaceBuilder andReconfigurationStrategy(ReconfigurationStrategy<S, A, Aa> strategy) {
            // todo: setup mape-k executor here
            ExperienceSimulationBuilder.this.policy = new ReconfigurationStrategyAdapter(strategy);
            return this;
        }

        public ExperienceSimulationBuilder<S, A, Aa, R> done() {
            return ExperienceSimulationBuilder.this;
        }
    }

    public class RewardReceiverBuilder {

        public RewardReceiverBuilder withRewardEvaluator(RewardEvaluator<R> evaluator) {
            ExperienceSimulationBuilder.this.rewardEvaluator = evaluator;
            return this;
        }

        public ExperienceSimulationBuilder<S, A, Aa, R> done() {
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
    private static class OP<T> implements ObservationProducer<T> {

        @Override
        public Observation<T> produceObservationGiven(State<T> emittingState) {
            // return new ObservationImpl<T>() {
            //
            // @Override
            // public T getValue() {
            // return emittingState;
            // };
            // };
            return emittingState.getProduces()
                .get(0);
        }
    };

    private final ObservationProducer<S> PASS_THROUGH_OBS_PRODUCER = new OP<>();

}
