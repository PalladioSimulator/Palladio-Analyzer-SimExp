from pytest import fixture, approx

from .pareto_front import ParetoFront, ParetoEntry
from .reference_point_calculator import ReferencePointCalculator


@fixture()
def calculator():
    return ReferencePointCalculator(0.1)


def test_calc_ref_point_one_front_one(calculator):
    front1 = ParetoFront(
        generation=1,
        entries=[
            ParetoEntry(id="1", optimizables="", average_energy_consumption=1.0, average_packet_loss=1.0),
        ])
    fronts = [front1]

    actual_ref_point = calculator.calc_reference_point(fronts)

    assert actual_ref_point == approx((1.1, 1.1))


def test_calc_ref_point_one_front_two(calculator):
    front1 = ParetoFront(
        generation=1,
        entries=[
            ParetoEntry(id="1", optimizables="", average_energy_consumption=2.0, average_packet_loss=1.0),
            ParetoEntry(id="2", optimizables="", average_energy_consumption=1.0, average_packet_loss=2.0),
        ])
    fronts = [front1]

    actual_ref_point = calculator.calc_reference_point(fronts)

    assert actual_ref_point == approx((2.1, 2.1))


def test_calc_ref_point_two_front_one(calculator):
    front1 = ParetoFront(
        generation=1,
        entries=[
            ParetoEntry(id="1", optimizables="", average_energy_consumption=2.0, average_packet_loss=1.0),
        ])
    front2 = ParetoFront(
        generation=2,
        entries=[
            ParetoEntry(id="2", optimizables="", average_energy_consumption=1.0, average_packet_loss=2.0),
        ])
    fronts = [front1, front2]

    actual_ref_point = calculator.calc_reference_point(fronts)

    assert actual_ref_point == approx((2.1, 2.1))
