from pathlib import Path

from .util import validate_resource_folder
from .generation_io import extract_generations
from .pareto_front_builder import ParetoFrontBuilder, IndividualAverageResolver
from .task_entry import TaskAverages
from .task_io import TaskReader
from .task_average_calculator import calc_averages
from .individual import Individual
from .pareto_front import ParetoFront, ParetoEntry


class ParetoFrontCreator:
    def create_fronts(self, resource_folder: Path, target: Path):
        validate_resource_folder(resource_folder)
        target_folder = target / resource_folder.name
        target_folder.mkdir(parents=True, exist_ok=True)

        generations = extract_generations(resource_folder)
        front_builder = ParetoFrontBuilder()
        task_reader = TaskReader(resource_folder)
        for generation in generations:
            print("calculate pareto front for generation: %d", generation.generation)
            averages_map: dict[str, TaskAverages] = {}
            for individual in generation.individuals:
                if individual.id in averages_map:
                    continue
                task_entry = task_reader.read_task(individual.id)
                averages = calc_averages(task_entry.result)
                averages_map[individual.id] = averages

            class Resolver(IndividualAverageResolver):
                def get_averages(self, individual: Individual) -> TaskAverages:
                    return averages_map[individual.id]

            resolver = Resolver()
            front = front_builder.build_pareto_front(generation, resolver)

            entries: list[ParetoEntry] = []
            for individual in front:
                averages = resolver.get_averages(individual)
                pareto_entry = ParetoEntry(
                    id=individual.id,
                    optimizables=individual.optimizables,
                    average_energy_consumption=averages.energy_consumption,
                    average_packet_loss=averages.packet_loss,
                )
                entries.append(pareto_entry)

            pareto_front = ParetoFront(
                generation=generation.generation,
                entries=entries,
            )

            # ToDo: store front
