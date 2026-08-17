import sys
from pathlib import Path

from .pareto_front import ParetoFront
from .pareto_reader import ParetoReader


class ParetoIO:
    @property
    def name(self) -> str:
        return ""

    def extract_pareto_fronts(self, folder: Path) -> list[ParetoFront]:
        front_files = self._collect_front_files(folder)
        if not front_files:
            raise RuntimeError("no front files found in: %s" % folder)
        # print("found front files:\n%s" % front_files)
        reader = ParetoReader()
        pareto_fronts = []
        for generation, front_file in front_files:
            entries = reader.read_pareto_front(front_file)
            front = ParetoFront(
                generation=generation,
                entries=entries,
            )
            pareto_fronts.append(front)
        sorted_fronts = sorted(pareto_fronts, key=lambda front: front.generation if front.generation >= 0 else sys.maxsize)
        return sorted_fronts

    def _collect_front_files(self, folder: Path) -> list[(int, Path)]:
        front_files = []
        generations_folder = folder / "generations"
        for entry in generations_folder.glob('pareto_front_*.json'):
            generation = int(entry.stem.split("_")[-1])
            # print("found %d : %s" % (generation, entry))
            front_files.append((generation, entry))
        # print("found %d front files" % len(front_files))
        if front_files:
            max_gen = max(entry[0] for entry in front_files)
        else:
            max_gen = 0
        # print("max generation: %d" % max_gen)
        front_files.append((max_gen + 1, folder / "pareto_front.json"))
        return front_files


class ApproximatedParetoIO(ParetoIO):
    @property
    def name(self) -> str:
        return "approximated"

    def _collect_front_files(self, folder: Path) -> list[(int, Path)]:
        front_files = super()._collect_front_files(folder)
        front_files.append((-1, folder / "approximated_pareto_front.json"))
        return front_files


class CumulatedParetoIO(ParetoIO):
    @property
    def name(self) -> str:
        return "cumulative"

    def _collect_front_files(self, folder: Path) -> list[(int, Path)]:
        front_files = []
        generations_folder = folder / "generations"
        for entry in generations_folder.glob('cumulative_pareto_front_*.json'):
            generation = int(entry.stem.split("_")[-1])
            # print("found %d : %s" % (generation, entry))
            front_files.append((generation, entry))
        # print("found %d front files" % len(front_files))
        return front_files
