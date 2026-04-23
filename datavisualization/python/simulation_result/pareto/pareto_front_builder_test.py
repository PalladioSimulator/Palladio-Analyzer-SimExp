from pytest_unordered import unordered

from .individual import Individual
from .generation import Generation
from .pareto_front_builder import ParetoFrontBuilder, IndividualAverageResolver
from .task_entry import TaskAverages


def test_build_pareto_front():
    builder = ParetoFrontBuilder()
    # All points:
    # A(1.0, 7.0)
    # B(2.0, 6.0)
    # C(3.0, 5.0)
    # D(4.0, 4.0)
    # E(5.0, 3.0)
    # F(6.0, 2.0)
    # G(7.0, 1.0)
    # H(2.0, 2.0)
    # I(5.0, 5.0)
    # J(0.0, 8.0)
    a = Individual(id="a", reward=0.0, optimizables={})
    b = Individual(id="b", reward=0.0, optimizables={})
    c = Individual(id="c", reward=0.0, optimizables={})
    d = Individual(id="d", reward=0.0, optimizables={})
    e = Individual(id="e", reward=0.0, optimizables={})
    f = Individual(id="f", reward=0.0, optimizables={})
    g = Individual(id="g", reward=0.0, optimizables={})
    h = Individual(id="h", reward=0.0, optimizables={})
    i = Individual(id="i", reward=0.0, optimizables={})
    j = Individual(id="j", reward=0.0, optimizables={})

    class Resolver(IndividualAverageResolver):
        def __init__(self):
            self._map = {
                "a": TaskAverages(energy_consumption=1.0, packet_loss=7.0),
                "b": TaskAverages(energy_consumption=2.0, packet_loss=6.0),
                "c": TaskAverages(energy_consumption=3.0, packet_loss=5.0),
                "d": TaskAverages(energy_consumption=4.0, packet_loss=4.0),
                "e": TaskAverages(energy_consumption=5.0, packet_loss=3.0),
                "f": TaskAverages(energy_consumption=6.0, packet_loss=2.0),
                "g": TaskAverages(energy_consumption=7.0, packet_loss=1.0),
                "h": TaskAverages(energy_consumption=2.0, packet_loss=2.0),
                "i": TaskAverages(energy_consumption=5.0, packet_loss=5.0),
                "j": TaskAverages(energy_consumption=0.0, packet_loss=8.0),
            }

        def get_averages(self, individual: Individual) -> TaskAverages:
            return self._map[individual.id]

    resolver = Resolver()
    generation = Generation(
        generation=1,
        individuals=[a, b, c, d, e, f, g, h, i, j],
    )

    actual_front = builder.build_pareto_front(generation, resolver)

    # y\x - 0 - -1 - -2 - -3 - -4 - -5 - -6 - -7 -
    # -----------------------------------
    # 8 | [J] - -- --- --- --- --- --- ---
    # 7 | --- [A] - -- --- --- --- --- ---
    # 6 | --- --- -B - --- --- --- --- ---
    # 5 | --- --- --- -C - --- -I - --- ---
    # 4 | --- --- --- --- -D - --- --- ---
    # 3 | --- --- --- --- --- -E - --- ---
    # 2 | --- --- [H] - -- --- --- -F - ---
    # 1 | --- --- --- --- --- --- --- [G]
    # -----------------------------------
    # ___ - 0 - -1 - -2 - -3 - -4 - -5 - -6 - -7 -
    #
    # Pareto front(non - dominated points, minimization):
    # A(1.0, 7.0)
    # J(0.0, 8.0)
    # H(2.0, 2.0)
    # G(7.0, 1.0)
    assert actual_front == unordered([a, h, j, g])
