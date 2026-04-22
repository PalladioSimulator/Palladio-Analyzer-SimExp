import re
from pathlib import Path
import csv
import json

import tabulate


class ParetoFrontVisualize:
    def visualize(self, front_files: list, result_file: Path):
        entries = []
        for pareto_front_file in front_files:
            entry = self._extract_pareto_front(pareto_front_file)
            entries.append(entry)

        max_gen = max([entry["generation"] for entry in entries])
        sorted_entries = sorted(entries, key=lambda e: e["generation"] if e["generation"] >= 0 else max_gen + 1)
        if sorted_entries[-1]["generation"] == -1:
            sorted_entries[-1]["generation"] = max_gen + 1

        headers = ['generation', 'file', "entry", "id", "reward", "energy_consumption_average", "packet_loss_average"]
        table_entries = []
        for entry in sorted_entries:
            for i, front_member in enumerate(entry["members"]):
                table_entries.append([
                    entry["generation"], entry["file"],
                    i, front_member["id"],
                    front_member["reward"], front_member["energy_consumption_average"],
                    front_member["packet_loss_average"],
                ])

        with result_file.open("w", encoding="utf-8") as f:
            writer = csv.DictWriter(f, fieldnames=headers)
            writer.writeheader()
            for entry in sorted_entries:
                for i, front_member in enumerate(entry["members"]):
                    writer.writerow({'generation': entry["generation"],
                                     'file': entry["file"],
                                     'entry': i,
                                     'id': front_member["id"],
                                     'reward': front_member["reward"],
                                     'energy_consumption_average': front_member["energy_consumption_average"],
                                     'packet_loss_average': front_member["packet_loss_average"],
                                     })

        table_str = tabulate.tabulate(table_entries,
                                      headers=headers,
                                      tablefmt="simple"
                                      )
        print(table_str)

    def _extract_pareto_front(self, pareto_front_file: Path) -> dict:
        front = self._read_json_file(pareto_front_file)
        r = re.compile(r"pareto_front_(\d+)")
        match = r.match(pareto_front_file.stem)
        if match:
            generation = int(match.group(1)) - 1
        else:
            generation = -1

        front_members = []
        for front_entry in front:
            optimizables = ["%s=%s" % (key, value) for key, value in front_entry["optimizables"].items()]
            values = ",".join(optimizables)

            front_member = {
                "id": front_entry["id"],
                "reward": front_entry["fitness"],
                "values": values,
                "energy_consumption_average": front_entry["averages"]["EnergyConsumption.props"],
                "packet_loss_average": front_entry["averages"]["PacketLoss.props"],
            }
            front_members.append(front_member)

        entry = {
            "generation": generation,
            "file": pareto_front_file.stem,
            "members": front_members,
        }

        return entry

    def _read_json_file(self, json_file):
        with json_file.open("r", encoding="utf-8") as f:
            result = json.load(f)
            return result
