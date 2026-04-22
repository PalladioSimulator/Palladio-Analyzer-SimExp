from .pareto_front import ParetoFront


class ReferencePointCalculator:
    def __init__(self, delta: float):
        self._delta = delta

    def calc_reference_point(self, fronts: list[ParetoFront]) -> (float, float):
        max_energy = 0.0
        max_packet_loss = 0.0
        for front in fronts:
            for entry in front.entries:
                max_energy = max(max_energy, entry.average_energy_consumption)
                max_packet_loss = max(max_packet_loss, entry.average_packet_loss)
        return max_energy + self._delta, max_packet_loss + self._delta
