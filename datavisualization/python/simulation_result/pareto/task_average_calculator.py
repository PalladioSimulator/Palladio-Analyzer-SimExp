from .task_entry import TaskResult, TaskAverages


def calc_averages(task_result: TaskResult) -> TaskAverages:
    energy_values = []
    packet_loss_values = []
    for run in task_result.runs:
        energy_values.extend(run.energy_consumptions)
        packet_loss_values.extend(run.packet_losses)
    averages = TaskAverages(
        energy_consumption=sum(energy_values) / len(energy_values),
        packet_loss=sum(packet_loss_values) / len(packet_loss_values),
    )
    return averages
