from pathlib import Path

from .util import validate_resource_folder
from .pareto_io import extract_pareto_fronts


class ParetoFittest:
    def pareto_fittest_check(self, resource_folder: Path):
        validate_resource_folder(resource_folder)
        pareto_fronts = extract_pareto_fronts(resource_folder)
