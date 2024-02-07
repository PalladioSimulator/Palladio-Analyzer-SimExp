package org.palladiosimulator.simexp.core.process;

import java.util.List;
import java.util.Objects;

import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.markovian.sampling.MarkovSampling;

import com.google.common.collect.Lists;

public class ExperienceSimulationConfiguration<S, A, R> {

    public class ExperienceSimulationConfigBuilder {

        public ExperienceSimulationConfigBuilder withSimulationID(String simulationID) {
            ExperienceSimulationConfiguration.this.simulationID = simulationID;
            return this;
        }

        public ExperienceSimulationConfigBuilder withSampleSpaceID(String sampleSpaceID) {
            ExperienceSimulationConfiguration.this.sampleSpaceID = sampleSpaceID;
            return this;
        }

        public ExperienceSimulationConfigBuilder withNumberOfRuns(int numOfRuns) {
            ExperienceSimulationConfiguration.this.numberOfRuns = numOfRuns;
            return this;
        }

        public ExperienceSimulationConfigBuilder executeBeforeEachRun(Initializable beforeExecutionInitialization) {
            ExperienceSimulationConfiguration.this.beforeExecutionInitialization = beforeExecutionInitialization;
            return this;
        }

        public ExperienceSimulationConfigBuilder addSimulationRunner(List<ExperienceSimulationRunner<S, A>> runner) {
            ExperienceSimulationConfiguration.this.runner.addAll(runner);
            return this;
        }

        public ExperienceSimulationConfigBuilder addSimulationRunner(ExperienceSimulationRunner<S, A> runner) {
            ExperienceSimulationConfiguration.this.runner.add(runner);
            return this;
        }

        public ExperienceSimulationConfigBuilder sampleWith(MarkovSampling<S, A, R, State<S>> markovSampler) {
            ExperienceSimulationConfiguration.this.markovSampler = markovSampler;
            return this;
        }

        public ExperienceSimulationConfiguration<S, A, R> build() {
            checkValidity();

            return ExperienceSimulationConfiguration.this;
        }

        private void checkValidity() {
            // TODO exception handling
            Objects.requireNonNull(runner, "");
            Objects.requireNonNull(markovSampler, "");
            if (isNegative(numberOfRuns) || isNegative(horizon)) {
                throw new RuntimeException("");
            }
            if (isEmptyString(simulationID) || isEmptyString(sampleSpaceID)) {
                throw new RuntimeException("");
            }
        }

        private boolean isNegative(int value) {
            return value < 0;
        }

        private boolean isEmptyString(String value) {
            return value == "";
        }
    }

    private int horizon = 0;
    private int numberOfRuns = 0;
    private String simulationID = "";
    private String sampleSpaceID = "";
    private List<ExperienceSimulationRunner<S, A>> runner = Lists.newArrayList();
    private MarkovSampling<S, A, R, State<S>> markovSampler = null;
    private Initializable beforeExecutionInitialization = null;

    private ExperienceSimulationConfiguration() {

    }

    public int getHorizon() {
        return horizon;
    }

    public int getNumberOfRuns() {
        return numberOfRuns;
    }

    public String getSimulationID() {
        return simulationID;
    }

    public List<ExperienceSimulationRunner<S, A>> getSimulationRunner() {
        return runner;
    }

    public MarkovSampling<S, A, R, State<S>> getMarkovSampler() {
        return markovSampler;
    }

    public String getSampleSpaceID() {
        return sampleSpaceID;
    }

    public static <S, A, R> ExperienceSimulationConfiguration<S, A, R>.ExperienceSimulationConfigBuilder newBuilder() {
        ExperienceSimulationConfiguration<S, A, R> experienceSimulationConfiguration = new ExperienceSimulationConfiguration<>();
        return experienceSimulationConfiguration.new ExperienceSimulationConfigBuilder();
    }

    public Initializable getBeforeExecutionInitialization() {
        return beforeExecutionInitialization;
    }

}
