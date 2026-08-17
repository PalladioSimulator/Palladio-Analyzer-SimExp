from abc import ABC, abstractmethod

from paretoset import paretoset
import pandas as pd

from .individual import Individual
from .generation import Generation
from .task_entry import TaskAverages


class IndividualAverageResolver(ABC):
    @abstractmethod
    def get_averages(self, individual: Individual) -> TaskAverages:
        pass


class ParetoFrontBuilder:
    def build_pareto_front(self, generation: Generation, resolver: IndividualAverageResolver) -> list[Individual]:
        df = pd.DataFrame(columns=['energy_consumption', 'packet_loss'])
        complete_individuals = []
        for individual in generation.individuals:
            averages = resolver.get_averages(individual)
            if averages:
                complete_individuals.append(individual)

        for individual in complete_individuals:
            averages = resolver.get_averages(individual)
            df.loc[len(df)] = [averages.energy_consumption, averages.packet_loss]

        mask = paretoset(df, sense=["min", "min"], use_numba=False)
        pareto_front: list[Individual] = []
        for i, entry in enumerate(mask):
            if entry:
                individual = complete_individuals[i]
                pareto_front.append(individual)
        return pareto_front
