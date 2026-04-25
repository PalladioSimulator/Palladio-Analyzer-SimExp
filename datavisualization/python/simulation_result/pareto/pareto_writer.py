from pathlib import Path
import json

from .pareto_front import ParetoFront


class ParetoWriter:
    def write_pareto_front(self, front_file: Path, front: ParetoFront):
        front_entries = []
        for front_entry in front.entries:
            entry = {
                "score": front_entry.score,
                "averages": {
                    "EnergyConsumption.props": front_entry.average_energy_consumption,
                    "PacketLoss.props": front_entry.average_packet_loss,
                },
                "fitness": front_entry.fitness,
                "optimizables": front_entry.optimizables,
                "id": front_entry.id,
            }
            front_entries.append(entry)

        front_file.parent.mkdir(exist_ok=True)
        self._write_json_file(front_file, front_entries)

    def _write_json_file(self, json_file, content):
        with json_file.open("w", encoding="utf-8") as f:
            json.dump(content, f, indent=2)
