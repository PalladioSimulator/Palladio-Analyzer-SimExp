from pathlib import Path

from .pareto_front import ParetoFront
from .pareto_reader import ParetoReader


def extract_pareto_fronts(folder) -> list[ParetoFront]:
    front_files = _collect_front_files(folder)
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
    sorted_fronts = sorted(pareto_fronts, key=lambda front: front.generation)
    return sorted_fronts


def _collect_front_files(folder: Path) -> list[(int, Path)]:
    front_files = []
    generations_folder = folder / "generations"
    for entry in generations_folder.glob('pareto_front_*.json'):
        generation = int(entry.stem.split("_")[-1])
        # print("found %d : %s" % (generation, entry))
        front_files.append((generation, entry))
    # print("found %d front files" % len(front_files))
    max_gen = max(entry[0] for entry in front_files)
    # print("max generation: %d" % max_gen)
    front_files.append((max_gen + 1, folder / "pareto_front.json"))
    return front_files
