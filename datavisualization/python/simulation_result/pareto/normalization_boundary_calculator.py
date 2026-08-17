from .normalization_boundaries import NormalizationBoundary
from .pareto_front import ParetoFront


class NormalizationBoundaryCalculator:
    def __init__(self, delta: float):
        self._delta = delta

    def calculate_boundaries(self, fronts: list[ParetoFront]) -> NormalizationBoundary:
        energies_list = []
        packet_loss_list = []
        for front in fronts:
            energies_list.extend([entry.average_energy_consumption for entry in front.entries])
            packet_loss_list.extend([entry.average_packet_loss for entry in front.entries])

        return NormalizationBoundary(
            energy_min=min(energies_list) + self._delta,
            energy_max=max(energies_list) + self._delta,
            packet_loss_min=min(packet_loss_list) + self._delta,
            packet_loss_max=max(packet_loss_list) + self._delta,
        )
