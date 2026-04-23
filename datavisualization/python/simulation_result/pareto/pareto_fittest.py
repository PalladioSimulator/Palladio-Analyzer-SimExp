from pathlib import Path

from .util import validate_resource_folder


class ParetoFittest:
    def pareto_fittest_check(self, resource_folder: Path):
        validate_resource_folder(resource_folder)
