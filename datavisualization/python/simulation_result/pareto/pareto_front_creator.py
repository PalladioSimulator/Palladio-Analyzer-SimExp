from pathlib import Path

from .util import validate_resource_folder
from .generation_io import extract_generations
from .pareto_front_builder import ParetoFrontBuilder, IndividualAverageResolver
from .task_entry import TaskAverages
from .task_io import TaskReader
from .task_average_calculator import calc_averages
from .individual import Individual
from .pareto_front import ParetoFront, ParetoEntry
from .pareto_writer import ParetoWriter


class ParetoFrontCreator:
    def create_fronts(self, resource_folder: Path, target: Path):
        validate_resource_folder(resource_folder)
        target_folder = target / resource_folder.name
        target_folder.mkdir(parents=True, exist_ok=True)

        generations = extract_generations(resource_folder)
        front_builder = ParetoFrontBuilder()
        task_reader = TaskReader(resource_folder)

        pareto_writer = ParetoWriter(target_folder)
        for i, generation in enumerate(generations):
            print("calculate pareto front for generation: %d" % generation.generation)
            averages_map: dict[str, TaskAverages | None] = {}
            for individual in generation.individuals:
                if individual.id in averages_map:
                    continue
                task_entry = task_reader.read_task(individual.id)
                if task_entry.result.complete:
                    averages = calc_averages(task_entry.result)
                    averages_map[individual.id] = averages
                else:
                    averages_map[individual.id] = None

            class Resolver(IndividualAverageResolver):
                def get_averages(self, individual: Individual) -> TaskAverages:
                    return averages_map[individual.id]

            resolver = Resolver()
            front = front_builder.build_pareto_front(generation, resolver)
            pareto_front = self._create_pareto_front(generation.generation, front, resolver)
            final = i == len(generations) - 1
            pareto_writer.write_pareto_front(pareto_front, final)

    def _create_pareto_front(self, generation: int, front_individuals: list[Individual],
                             resolver: IndividualAverageResolver) -> ParetoFront:
        entries: list[ParetoEntry] = []
        for individual in front_individuals:
            averages = resolver.get_averages(individual)
            score = self._calculate_score(averages)
            pareto_entry = ParetoEntry(
                id=individual.id,
                score=score,
                fitness=individual.reward,
                optimizables=individual.optimizables,
                average_energy_consumption=averages.energy_consumption,
                average_packet_loss=averages.packet_loss,
            )
            entries.append(pareto_entry)

        pareto_front = ParetoFront(
            generation=generation,
            entries=entries,
        )
        return pareto_front

    def _calculate_score(self, averages: TaskAverages) -> float:
        sum = averages.energy_consumption + averages.packet_loss
        score = sum / 2
        return score
