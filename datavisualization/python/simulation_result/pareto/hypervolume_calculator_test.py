from pytest import fixture, approx

from .pareto_front import ParetoEntry, ParetoFront
from .hypervolume_calculator import HypervolumeCalculator
from .point import Point


@fixture()
def calculator():
    return HypervolumeCalculator()


def test_calc_hypervolume(calculator):
    front = ParetoFront(
        generation=1,
        entries=[
            ParetoEntry(id="1", optimizables="", average_energy_consumption=1.0, average_packet_loss=5.0),
            ParetoEntry(id="2", optimizables="", average_energy_consumption=2.5, average_packet_loss=3.0),
            ParetoEntry(id="3", optimizables="", average_energy_consumption=4.0, average_packet_loss=1.5),
        ])
    ref_point = Point(energy=7.0, packet_loss=7.0)

    actual_volume = calculator.calc_hypervolume(ref_point, front)

    assert actual_volume == approx(25.5)


def test_calc_hypervolume_with_dominated(calculator):
    front = ParetoFront(
        generation=1,
        entries=[
            ParetoEntry(id="1", optimizables="", average_energy_consumption=1.0, average_packet_loss=0.0),
            ParetoEntry(id="2", optimizables="", average_energy_consumption=0.5, average_packet_loss=0.5),
            ParetoEntry(id="3", optimizables="", average_energy_consumption=0.0, average_packet_loss=1.0),
            ParetoEntry(id="4", optimizables="", average_energy_consumption=1.5, average_packet_loss=0.75),
        ])
    ref_point = Point(energy=2.0, packet_loss=2.0)

    actual_volume = calculator.calc_hypervolume(ref_point, front)

    assert actual_volume == approx(3.25)
