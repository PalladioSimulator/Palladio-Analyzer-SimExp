from pathlib import Path

from .util import validate_resource_folder
from .pareto_front import ParetoFront
from .pareto_io import extract_pareto_fronts
from .generation_io import extract_generations
from .individual import Individual
from .generation import Generation


class ParetoFittest:
    def pareto_fittest_check(self, resource_folder: Path):
        validate_resource_folder(resource_folder)
        pareto_fronts = extract_pareto_fronts(resource_folder)
        generations = extract_generations(resource_folder)
        for i, generation in enumerate(generations):
            fittest = self._find_fittest(generation)
            pareto_front = pareto_fronts[i]
            if generation.generation != pareto_front.generation:
                raise RuntimeError("invalid pareto front for generation: %d", generation.generation)

            in_front = self._fittest_in_front(fittest, pareto_front)
            print("fittest in generation %d has ID: %s" % (generation.generation, fittest.id))
            print("In generation %d the fittest is %sin the front" % (generation.generation, "" if in_front else "not "))

    def _find_fittest(self, generation: Generation) -> Individual:
        fittest = max(generation.individuals, key=lambda i: i.reward)
        return fittest

    def _fittest_in_front(self, fittest: Individual, pareto_front: ParetoFront) -> bool:
        for pareto_entry in pareto_front.entries:
            if fittest.id == pareto_entry.id:
                return True
        return False
