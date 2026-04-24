from pathlib import Path

from .util import validate_resource_folder
from .pareto_io import extract_pareto_fronts


class ParetoFrontComparator:
    def compare_fronts(self, resource_folder: Path, target: Path):
        print("comparing pareto fronts")
        target_folder = target / resource_folder.name
        print("baseline: %s" % resource_folder)
        print("target:   %s" % target_folder)
        validate_resource_folder(resource_folder)
        base_fronts = extract_pareto_fronts(resource_folder)
        calculated_fronts = extract_pareto_fronts(target_folder)

        if len(base_fronts) != len(calculated_fronts):
            print("Count mismatch: base has %d fronts, target has %d fronts" % (len(base_fronts), len(calculated_fronts)))

        for i, base_front in enumerate(base_fronts):
            target_front = calculated_fronts[i]
            if base_front == target_front:
                print("front of generation %-2d: match" % base_front.generation)
            else:
                print("front of generation %-2d: mismatch" % base_front.generation)
