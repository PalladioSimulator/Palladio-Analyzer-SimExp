from pytest import fixture, approx

from .pareto_front import ParetoEntry, ParetoFront
from .hypervolume_calculator import HypervolumeCalculator
from .point import Point
from .normalization_boundaries import NormalizationBoundary


@fixture()
def calculator():
    boundary = NormalizationBoundary(
        energy_min=0,
        energy_max=7.0,
        packet_loss_min=0,
        packet_loss_max=7.0,
    )
    return HypervolumeCalculator(boundary)


def test_calc_hypervolume(calculator):
    front = ParetoFront(
        generation=1,
        entries=[
            ParetoEntry(id="1", score=0.0, fitness=0.0, optimizables={}, average_energy_consumption=1.0, average_packet_loss=5.0),
            ParetoEntry(id="2", score=0.0, fitness=0.0, optimizables={}, average_energy_consumption=2.5, average_packet_loss=3.0),
            ParetoEntry(id="3", score=0.0, fitness=0.0, optimizables={}, average_energy_consumption=4.0, average_packet_loss=1.5),
        ])
    ref_point = Point(energy=7.0, packet_loss=7.0)

    actual_volume = calculator.calc_hypervolume(ref_point, front)

    assert actual_volume == approx(46.37755102040816)


def test_calc_hypervolume_with_dominated(calculator):
    front = ParetoFront(
        generation=1,
        entries=[
            ParetoEntry(id="1", score=0.0, fitness=0.0, optimizables={}, average_energy_consumption=1.0, average_packet_loss=0.0),
            ParetoEntry(id="2", score=0.0, fitness=0.0, optimizables={}, average_energy_consumption=0.5, average_packet_loss=0.5),
            ParetoEntry(id="3", score=0.0, fitness=0.0, optimizables={}, average_energy_consumption=0.0, average_packet_loss=1.0),
            ParetoEntry(id="4", score=0.0, fitness=0.0, optimizables={}, average_energy_consumption=1.5, average_packet_loss=0.75),
        ])
    ref_point = Point(energy=2.0, packet_loss=2.0)

    actual_volume = calculator.calc_hypervolume(ref_point, front)

    assert actual_volume == approx(3.9846938775510203)
