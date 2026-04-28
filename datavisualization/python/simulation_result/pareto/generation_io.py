from pathlib import Path

from .generation import Generation
from .generation_reader import GenerationReader


def extract_generations(folder: Path, include_final: bool = False) -> list[Generation]:
    front_files = _collect_generation_files(folder, include_final)
    # print("found generation files:\n%s" % front_files)
    reader = GenerationReader()
    generations = []
    for generation, front_file in front_files:
        individuals = reader.read_generation(front_file)
        front = Generation(
            generation=generation,
            individuals=individuals,
        )
        generations.append(front)
    sorted_fronts = sorted(generations, key=lambda front: front.generation)
    return sorted_fronts


def _collect_generation_files(folder: Path, include_final: bool) -> list[(int, Path)]:
    generation_files = []
    generations_folder = folder / "generations"
    for entry in generations_folder.glob('generation_*.json'):
        generation = int(entry.stem.split("_")[-1])
        generation_files.append((generation, entry))
    if include_final:
        max_gen = max(entry[0] for entry in generation_files)
        generation_files.append((max_gen + 1, folder / "final_population.json"))
    return generation_files
