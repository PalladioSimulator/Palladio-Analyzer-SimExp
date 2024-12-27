package org.palladiosimulator.simexp.dsl.ea.optimizer.research;

public class ParameterConfig {

    private int populationSize;
    private int selector;
    private int offspringSelector;
    private double mutator;
    private double crossoverOperator;
    private double bestValue;
    private long fitnessEvals;

    public ParameterConfig(int populationSize, int selectorSize, int offspringSelectorSize, double mutationRate,
            double crossoverRate, double bestValue, long fitnessEvals) {
        this.populationSize = populationSize;
        this.selector = selectorSize;
        this.offspringSelector = offspringSelectorSize;
        this.mutator = mutationRate;
        this.crossoverOperator = crossoverRate;
        this.bestValue = bestValue;
        this.fitnessEvals = fitnessEvals;

    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Avg. Fitness: " + getBestValue() + " ||  ");
        strBuilder.append("PopSize: " + populationSize + "  ");
        strBuilder.append("Selector: " + selector + "  ");
        strBuilder.append("OffspringSel: " + offspringSelector + "  ");
        strBuilder.append("Mutation rate: " + mutator + "  ");
        strBuilder.append("Crossover rate: " + crossoverOperator + "  ");
        strBuilder.append("Num of fitness Evaluations: " + fitnessEvals);

        return strBuilder.toString();

    }

    public double getBestValue() {
        return bestValue;
    }
}
