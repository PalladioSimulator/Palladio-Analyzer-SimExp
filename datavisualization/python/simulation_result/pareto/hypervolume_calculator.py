import numpy as np
from pymoo.indicators.hv import HV

from .pareto_front import ParetoFront
from .point import Point
from .normalization_boundaries import NormalizationBoundary


class HypervolumeCalculator:
    def __init__(self, boundaries: NormalizationBoundary):
        self._boundaries = boundaries

    def calc_hypervolume(self, ref_point: Point, front: ParetoFront) -> float:
        preprocessed_front = self._preprocess_front(front)
        hv = HV(ref_point=np.array([ref_point.energy, ref_point.packet_loss]))
        hv_value = hv(preprocessed_front)
        return hv_value

    def _preprocess_front(self, front: ParetoFront):
        """
        - Ensure minimization
        - Remove dominated points
        - Sort by first objective
        """
        # Sort by energy ascending
        np_front = self._to_np_front(front)
        front = np_front[np.argsort(np_front[:, 0])]
        #print("sorted front:\n%s" % front)

        ## Remove dominated points
        #non_dominated = []
        #best_packet_loss = float("inf")
        #for energy, packet_loss in front:
        #    if packet_loss < best_packet_loss:
        #        non_dominated.append([energy, packet_loss])
        #        best_packet_loss = packet_loss
        #print("non dominated front:\n%s" % "\n".join([str(e) for e in non_dominated]))

        #return np.array(non_dominated)
        return front

    def _to_np_front(self, front: ParetoFront):
        entries = []
        energy_min = self._boundaries.energy_min
        energy_max = self._boundaries.energy_max
        packet_loss_min = self._boundaries.packet_loss_min
        packet_loss_max = self._boundaries.packet_loss_max
        for entry in front.entries:
            energy = self._normalize(entry.average_energy_consumption, energy_min, energy_max)
            packet_loss = self._normalize(entry.average_packet_loss, packet_loss_min, packet_loss_max)
            entries.append([energy, packet_loss])
        return np.array(entries)

    def _normalize(self, x, xmin, xmax):
        return (x - xmin) / (xmax - xmin)
