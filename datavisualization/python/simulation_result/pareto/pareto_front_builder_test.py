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
    resolver = Resolver(average_map={
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
    })
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


def test_regression_1c_gen21():
    builder = ParetoFrontBuilder()
    task645 = _create_individual("Task 645")
    task785 = _create_individual("Task 785")
    task794 = _create_individual("Task 794")
    task788 = _create_individual("Task 788")
    task798 = _create_individual("Task 798")
    resolver = Resolver(average_map={
        "Task 645": TaskAverages(energy_consumption=9.329275267205833, packet_loss=0.07460441924724677),
        "Task 785": TaskAverages(energy_consumption=9.158890543298957, packet_loss=0.07965621109221145),
        "Task 794": TaskAverages(energy_consumption=9.167328071855417, packet_loss=0.07830180799961763),
        "Task 788": TaskAverages(energy_consumption=9.310202792564791, packet_loss=0.07620039901667755),
        "Task 798": TaskAverages(energy_consumption=9.331774274217292, packet_loss=0.07460431700913854),
    })
    generation = Generation(
        generation=1,
        individuals=[task645, task785, task794, task788, task798],
    )

    actual_front = builder.build_pareto_front(generation, resolver)

    assert actual_front == unordered([task645, task785, task794, task788, task798])


class Resolver(IndividualAverageResolver):
    def __init__(self, average_map: dict[str, TaskAverages]):
        self._map = average_map

    def get_averages(self, individual: Individual) -> TaskAverages:
        return self._map[individual.id]


def _create_individual(name: str) -> Individual:
    return Individual(id=name, reward=0.0, optimizables={})
