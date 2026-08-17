from .pareto_front import ParetoFront
from .point import Point


class ReferencePointCalculator:
    def __init__(self, delta: float):
        self._delta = delta

    def calc_reference_point(self, fronts: list[ParetoFront]) -> Point:
        max_energy = 0.0
        max_packet_loss = 0.0
        for front in fronts:
            for entry in front.entries:
                max_energy = max(max_energy, entry.average_energy_consumption)
                max_packet_loss = max(max_packet_loss, entry.average_packet_loss)
        return Point(
            energy=max_energy + self._delta,
            packet_loss=max_packet_loss + self._delta,
        )
