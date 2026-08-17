from pathlib import Path
import json

from .individual import Individual


class GenerationReader:
    def read_generation(self, front_file: Path) -> list[Individual]:
        front = self._read_json_file(front_file)
        entries = []
        for individual_entry in front:
            entry = Individual(
                id=individual_entry["id"],
                reward=individual_entry["fitness"],
                optimizables=individual_entry["optimizables"],
            )
            entries.append(entry)
        return entries

    def _read_json_file(self, json_file):
        with json_file.open("r", encoding="utf-8") as f:
            result = json.load(f)
            return result
