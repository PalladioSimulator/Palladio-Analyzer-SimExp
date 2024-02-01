package org.palladiosimulator.simexp.core.process;

import java.util.List;
import java.util.Objects;

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

        public ExperienceSimulationConfigBuilder addSimulationRunner(List<ExperienceSimulationRunner<S>> runners) {
            ExperienceSimulationConfiguration.this.runners.addAll(runners);
            return this;
        }

        public ExperienceSimulationConfigBuilder addSimulationRunner(ExperienceSimulationRunner<S> runner) {
            ExperienceSimulationConfiguration.this.runners.add(runner);
            return this;
        }

        public ExperienceSimulationConfigBuilder sampleWith(MarkovSampling<S, A, R> markovSampler) {
            ExperienceSimulationConfiguration.this.markovSampler = markovSampler;
            return this;
        }

        public ExperienceSimulationConfiguration<S, A, R> build() {
            checkValidity();

            return ExperienceSimulationConfiguration.this;
        }

        private void checkValidity() {
            // TODO exception handling
            Objects.requireNonNull(runners, "");
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
    private List<ExperienceSimulationRunner<S>> runners = Lists.newArrayList();
    private MarkovSampling<S, A, R> markovSampler = null;
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

    public List<ExperienceSimulationRunner<S>> getSimulationRunners() {
        return runners;
    }

    public MarkovSampling<S, A, R> getMarkovSampler() {
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
