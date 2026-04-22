import numpy as np
from pygmo import hypervolume

from .pareto_front import ParetoFront
from .point import Point


class HypervolumeCalculator:
    def calc_hypervolume(self, ref_point: Point, front: ParetoFront) -> float:
        preprocessed_front = self._preprocess_front(front)
        hv = hypervolume(preprocessed_front)
        hv_value = hv.compute([ref_point.energy, ref_point.packet_loss])
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
        for entry in front.entries:
            entries.append([entry.average_energy_consumption, entry.average_packet_loss])
        return np.array(entries)
