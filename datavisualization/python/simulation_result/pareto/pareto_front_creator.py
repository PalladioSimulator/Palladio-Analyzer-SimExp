from pathlib import Path

from .util import validate_resource_folder


class ParetoFrontCreator:
    def create_fronts(self, resources: Path, target: Path):
        validate_resource_folder(resources)
        target_folder = target / resources.name
        target_folder.mkdir(parents=True, exist_ok=True)
