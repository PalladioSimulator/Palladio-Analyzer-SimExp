from pathlib import Path

import tabulate

from .pareto_front import ParetoFront
from .pareto_reader import ParetoReader
from .reference_point_calculator import ReferencePointCalculator
from .hypervolume_calculator import HypervolumeCalculator


class ParetoFrontRanking:
    def rank_pareto_fronts(self, resource_folder: Path):
        self._validate_resource_folder(resource_folder)
        pareto_fronts = self._extract_pareto_fronts(resource_folder)
        sorted_fronts = sorted(pareto_fronts, key=lambda front: front.generation)

        reference_point_calculator = ReferencePointCalculator(0.1)
        ref_point = reference_point_calculator.calc_reference_point(sorted_fronts)

        hypervolume_calculator = HypervolumeCalculator()
        hv_list = []
        hv_dict = {}
        for front in sorted_fronts:
            hv = hypervolume_calculator.calc_hypervolume(ref_point, front)
            hv_dict[front.generation] = hv
            hv_list.append((hv, front))
        sorted_hv_list = sorted(hv_list,
                                key=lambda entry: entry[0],
                                reverse=False)
        sorted_hv_front = [entry[1] for entry in sorted_hv_list]

        table_entries = []
        for front in sorted_fronts:
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
            generation_rank = sorted_fronts.index(front) + 1
            table_entries.append((i + 1, len(front.entries), front.generation, hv, generation_rank))
        table_str = tabulate.tabulate(table_entries,
                                      headers=headers,
                                      tablefmt="simple"
                                      )
        print(table_str)

    def _validate_resource_folder(self, folder: Path):
        if not folder.is_dir():
            raise ValueError("not a directory: %s", folder)
        if not (folder / "pareto_front.json").is_file():
            raise ValueError("missing final pareto_front.json file in: %s" % folder)
        if not (folder / "generations").is_dir():
            raise ValueError("missing generations folder in: %s" % folder)

    def _extract_pareto_fronts(self, folder) -> list[ParetoFront]:
        front_files = self._collect_front_files(folder)
        #print("found front files:\n%s" % front_files)
        reader = ParetoReader()
        pareto_fronts = []
        for generation, front_file in front_files:
            entries = reader.read_pareto_front(front_file)
            front = ParetoFront(
                generation=generation,
                entries=entries,
            )
            pareto_fronts.append(front)
        return pareto_fronts

    def _collect_front_files(self, folder: Path) -> list[(int, Path)]:
        front_files = []
        generations_folder = folder / "generations"
        for entry in generations_folder.glob('pareto_front_*.json'):
            generation = int(entry.stem.split("_")[-1])
            #print("found %d : %s" % (generation, entry))
            front_files.append((generation, entry))
        #print("found %d front files" % len(front_files))
        max_gen = max(entry[0] for entry in front_files)
        #print("max generation: %d" % max_gen)
        front_files.append((max_gen + 1, folder / "pareto_front.json"))
        return front_files
