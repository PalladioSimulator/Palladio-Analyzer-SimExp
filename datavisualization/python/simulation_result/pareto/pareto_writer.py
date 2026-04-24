from pathlib import Path
import json

from .pareto_front import ParetoFront


class ParetoWriter:
    def __init__(self, resource_path: Path):
        self._resource_path = resource_path

    def write_pareto_front(self, front: ParetoFront, final: bool = False):
        front_file = self._get_front_file(front, final)

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

    def _get_front_file(self, front: ParetoFront, final: bool) -> Path:
        if final:
            return self._resource_path / "pareto_front.json"
        return self._resource_path / "generations" / ("pareto_front_%03d.json" % front.generation)

    def _write_json_file(self, json_file, content):
        with json_file.open("w", encoding="utf-8") as f:
            json.dump(content, f)
