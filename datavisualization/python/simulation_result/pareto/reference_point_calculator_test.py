from pytest import fixture

from .pareto_front import ParetoFront, ParetoEntry
from .reference_point_calculator import ReferencePointCalculator


@fixture()
def calculator():
    return ReferencePointCalculator()


def test_calc_ref_point_simple(calculator):
    front1 = ParetoFront(
        generation=1,
        entries=[
            ParetoEntry(id="1", optimizables="", average_energy_consumption=1.0, average_packet_loss=1.0),
        ])
    fronts = [front1]

    actual_ref_point = calculator.calc_reference_point(fronts)

    assert actual_ref_point == (1.0, 1.0)
