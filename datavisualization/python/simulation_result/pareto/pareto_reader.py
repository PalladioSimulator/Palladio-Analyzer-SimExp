from pathlib import Path
import json

from .pareto_front import ParetoEntry


class ParetoReader:
    def read_pareto_front(self, front_file: Path) -> list[ParetoEntry]:
        front = self._read_json_file(front_file)
        entries = []
        for front_entry in front:
            optimizables = ["%s=%s" % (key, value) for key, value in front_entry["optimizables"].items()]
            values = ",".join(optimizables)

            entry = ParetoEntry(
                id=front_entry["id"],
                optimizables=values,
                average_energy_consumption=front_entry["averages"]["EnergyConsumption.props"],
                average_packet_loss=front_entry["averages"]["PacketLoss.props"],
            )
            entries.append(entry)
        return entries


    def _read_json_file(self, json_file):
        with json_file.open("r", encoding="utf-8") as f:
            result = json.load(f)
            return result
