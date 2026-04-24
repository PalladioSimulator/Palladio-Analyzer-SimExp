from pathlib import Path

import tabulate

from .reference_point_calculator import ReferencePointCalculator
from .hypervolume_calculator import HypervolumeCalculator
from .util import validate_resource_folder
from .pareto_io import extract_pareto_fronts
from .normalization_boundary_calculator import NormalizationBoundaryCalculator


class ParetoFrontRanking:
    def rank_pareto_fronts(self, resource_folder: Path):
        validate_resource_folder(resource_folder)
        pareto_fronts = extract_pareto_fronts(resource_folder)
        delta = 0.1

        reference_point_calculator = ReferencePointCalculator(delta)
        ref_point = reference_point_calculator.calc_reference_point(pareto_fronts)

        boundary_calculator = NormalizationBoundaryCalculator(delta)
        boundary = boundary_calculator.calculate_boundaries(pareto_fronts)

        hypervolume_calculator = HypervolumeCalculator(boundary)
        hv_list = []
        hv_dict = {}
        for front in pareto_fronts:
            hv = hypervolume_calculator.calc_hypervolume(ref_point, front)
            hv_dict[front.generation] = hv
            hv_list.append((hv, front))
        sorted_hv_list = sorted(hv_list,
                                key=lambda entry: entry[0],
                                reverse=False)
        sorted_hv_front = [entry[1] for entry in sorted_hv_list]

        table_entries = []
        for front in pareto_fronts:
            hv_rank = sorted_hv_front.index(front) + 1
            hv = hv_dict[front.generation]
            table_entries.append((front.generation, len(front.entries), front.generation, hv, hv_rank))

        headers = ["generation", "# entries", "front", "hv", "hv rank"]
        table_str = tabulate.tabulate(table_entries,
                                      headers=headers,
                                      tablefmt="simple"
                                      )
        print(table_str)

        print("sorted HV list:")
        headers = ["rank", "# entries", "front", "hv", "generation rank"]
        table_entries = []
        for i, entry in enumerate(sorted_hv_list):
            hv, front = entry
            generation_rank = pareto_fronts.index(front) + 1
            table_entries.append((i + 1, len(front.entries), front.generation, hv, generation_rank))
        table_str = tabulate.tabulate(table_entries,
                                      headers=headers,
                                      tablefmt="simple"
                                      )
        print(table_str)
