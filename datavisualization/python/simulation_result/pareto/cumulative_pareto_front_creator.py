from pathlib import Path

from .pareto_front_creator import ParetoFrontCreator
from .pareto_front_builder import ParetoFrontBuilder, IndividualAverageResolver
from .task_io import TaskReader
from .task_entry import TaskAverages
from .task_average_calculator import calc_averages
from .pareto_writer import ParetoWriter
from .generation import Generation
from .individual import Individual
from .pareto_front import ParetoFront
from .generation_io import extract_generations


class CumulativeParetoFrontCreator(ParetoFrontCreator):
    def _collect_generations(self, resource_folder: Path) -> list[Generation]:
        return extract_generations(resource_folder, False)

    def _process_generations(self, resource_folder: Path, target_folder: Path, generations: list[Generation]):
        task_reader = TaskReader(resource_folder)
        averages_map: dict[str, TaskAverages | None] = {}

        cumulated_individuals = []
        for generation in generations:
            cumulated_individuals.extend(generation.individuals)
            print("Collected %d individuals from all %d generations" % (len(cumulated_individuals),
                                                                        generation.generation))
            for individual in generation.individuals:
                if individual.id in averages_map:
                    continue
                task_entry = task_reader.read_task(individual.id)
                if task_entry.result.complete:
                    averages = calc_averages(task_entry.result)
                    averages_map[individual.id] = averages
                else:
                    averages_map[individual.id] = None
            unique_count = sum(1 for v in averages_map.values() if v is not None)
            print("Found %d unique and valid individuals" % unique_count)

            class Resolver(IndividualAverageResolver):
                def get_averages(self, individual: Individual) -> TaskAverages:
                    return averages_map[individual.id]

            resolver = Resolver()
            front_builder = ParetoFrontBuilder()
            overall_generation = Generation(
                generation=generation.generation,
                individuals=cumulated_individuals,
            )
            front = front_builder.build_pareto_front(overall_generation, resolver)
            pareto_front = self._create_pareto_front(overall_generation.generation, front, resolver)
            print("Cumulative front consists of %d individuals" % len(pareto_front.entries))
            front_file = self._get_front_file(target_folder, pareto_front, False)
            pareto_writer = ParetoWriter()
            print("Generate: %s" % front_file)
            pareto_writer.write_pareto_front(front_file, pareto_front)

    def _get_front_file(self, target_folder: Path, front: ParetoFront, final: bool) -> Path:
        return target_folder / "generations" / ("cumulative_pareto_front_%03d.json" % front.generation)
