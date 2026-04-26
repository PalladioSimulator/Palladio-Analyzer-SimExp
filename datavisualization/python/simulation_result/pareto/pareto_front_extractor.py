import re
from pathlib import Path
import csv

import tabulate

from .pareto_front import ParetoFront
from .pareto_reader import ParetoReader


class ParetoFrontExtractor:
    def extract(self, front_files: list[Path], result_file: Path):
        entries: list[ParetoFront] = []
        reader = ParetoReader()
        r = re.compile(r"pareto_front_(\d+)")
        for pareto_front_file in front_files:
            pareto_entries = reader.read_pareto_front(pareto_front_file)
            match = r.match(pareto_front_file.stem)
            if match:
                generation = int(match.group(1)) - 1
            else:
                generation = -1
            front = ParetoFront(
                generation=generation,
                entries=pareto_entries,
            )
            entries.append(front)

        max_gen = max([entry.generation for entry in entries])
        sorted_entries = sorted(entries, key=lambda e: e.generation if e.generation >= 0 else max_gen + 1)

        headers = ['generation', "entry", "id", "reward", "energy_consumption_average", "packet_loss_average"]
        table_entries = []
        for front in sorted_entries:
            for i, front_entry in enumerate(front.entries):
                table_entries.append([
                    front.generation,
                    i, front_entry.id,
                    front_entry.fitness,
                    front_entry.average_energy_consumption,
                    front_entry.average_packet_loss,
                ])

        with result_file.open("w", encoding="utf-8") as f:
            writer = csv.DictWriter(f, fieldnames=headers)
            writer.writeheader()
            for front in sorted_entries:
                for i, front_entry in enumerate(front.entries):
                    writer.writerow({'generation': front.generation,
                                     'entry': i,
                                     'id': front_entry.id,
                                     'reward': front_entry.fitness,
                                     'energy_consumption_average': front_entry.average_energy_consumption,
                                     'packet_loss_average': front_entry.average_packet_loss,
                                     })

        table_str = tabulate.tabulate(table_entries,
                                      headers=headers,
                                      tablefmt="simple"
                                      )
        print(table_str)
