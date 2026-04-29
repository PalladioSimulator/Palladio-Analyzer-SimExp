from pathlib import Path
import csv

import tabulate

from .pareto_front import ParetoFront
from .pareto_io import ParetoIO


class ParetoFrontExtractor:
    def __init__(self, pareto_io: ParetoIO):
        self._pareto_io = pareto_io

    def extract(self, resource_folder: Path, result_folder: Path):
        pareto_fronts = self._extract_fronts(resource_folder)
        headers = ['generation', "entry", "id", "reward", "energy_consumption_average", "packet_loss_average"]
        table_entries = []
        for fi, front in enumerate(pareto_fronts):
            for i, front_entry in enumerate(front.entries):
                table_entries.append([
                    front.generation,
                    i, front_entry.id,
                    front_entry.fitness,
                    front_entry.average_energy_consumption,
                    front_entry.average_packet_loss,
                ])
            if fi != len(pareto_fronts) - 1:
                table_entries.append(tabulate.SEPARATING_LINE)

        result_folder.mkdir(exist_ok=True)

        extra = ""
        if self._pareto_io.name != "":
            extra = f"{self._pareto_io.name}_"
        result_file = result_folder / f"{resource_folder.stem}_{extra}pareto_fronts.csv"
        print("Generate: %s" % result_file)
        with result_file.open("w", encoding="utf-8") as f:
            writer = csv.DictWriter(f, fieldnames=headers)
            writer.writeheader()
            for front in pareto_fronts:
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

    def _extract_fronts(self, resource_folder: Path) -> list[ParetoFront]:
        return self._pareto_io.extract_pareto_fronts(resource_folder)
