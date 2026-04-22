from .pareto_front import ParetoFront


class ReferencePointCalculator:
    def calc_reference_point(self, fronts: list[ParetoFront]) -> (float, float):
        max_energy = 0.0
        max_packet_loss = 0.0
        for front in fronts:
            for entry in front.entries:
                max_energy = max(max_energy, entry.average_energy_consumption)
                max_packet_loss = max(max_packet_loss, entry.average_packet_loss)
        return max_energy, max_packet_loss
