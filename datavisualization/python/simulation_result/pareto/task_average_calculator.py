import statistics

from .task_entry import TaskResult, TaskAverages


def calc_averages(task_result: TaskResult) -> TaskAverages:
    energy_values = []
    packet_loss_values = []
    for run in task_result.runs:
        energy_values.extend(run.energy_consumptions)
        packet_loss_values.extend(run.packet_losses)
    averages = TaskAverages(
        energy_consumption=statistics.mean(energy_values),
        packet_loss=statistics.mean(packet_loss_values),
    )
    return averages
