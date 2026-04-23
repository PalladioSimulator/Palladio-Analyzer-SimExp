from pytest import approx
from dataclasses import astuple

from .task_entry import TaskRun, TaskResult
from .task_average_calculator import calc_averages


def test_calc_averages_1_run_1_entry():
    run1 = TaskRun(
        energy_consumptions=[1.0],
        packet_losses=[2.0],
    )
    task_result = TaskResult(
        id="",
        reward=0.0,
        runs=[run1],
    )

    actual_averages = calc_averages(task_result)

    assert astuple(actual_averages) == approx((1.0, 2.0))


def test_calc_averages_1_run_2_entry():
    run1 = TaskRun(
        energy_consumptions=[1.0, 2.0],
        packet_losses=[2.0, 3.0],
    )
    task_result = TaskResult(
        id="",
        reward=0.0,
        runs=[run1],
    )

    actual_averages = calc_averages(task_result)

    assert astuple(actual_averages) == approx((1.5, 2.5))


def test_calc_averages_2_run_2_entry():
    run1 = TaskRun(
        energy_consumptions=[1.0, 2.0],
        packet_losses=[2.0, 3.0],
    )
    run2 = TaskRun(
        energy_consumptions=[2.0, 5.0],
        packet_losses=[2.0, 1.0],
    )
    task_result = TaskResult(
        id="",
        reward=0.0,
        runs=[run1, run2],
    )

    actual_averages = calc_averages(task_result)

    assert astuple(actual_averages) == approx((2.5, 2.0))
