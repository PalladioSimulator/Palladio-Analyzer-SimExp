from pathlib import Path
import json

from .task_entry import TaskRun, TaskResult, TaskEntry


class TaskReader:
    def __init__(self, resource_path: Path):
        self._resource_path = resource_path / "kubernetes" / "tasks"

    def read_task(self, task_id: str) -> TaskEntry:
        task_file = self._get_task_file(task_id)
        task_content = self._read_json_file(task_file)

        result_content = task_content["result"]
        complete = result_content["status"] == "COMPLETE"

        task_runs = []
        if complete:
            runs = result_content["quality_measurements"]["runs"]
            for run in runs:
                qas = run["quality_attributes"]
                task_run = TaskRun(
                    energy_consumptions=qas["EnergyConsumption.props"],
                    packet_losses=qas["PacketLoss.props"],
                )
                task_runs.append(task_run)

        task_result = TaskResult(
            id=result_content["id"],
            complete=complete,
            reward=result_content["reward"],
            runs=task_runs,
        )

        task_entry = TaskEntry(
            id=result_content["id"],
            optimizables=task_content["optimizables"],
            result=task_result,
        )

        return task_entry

    def _get_task_file(self, task_id: str) -> Path:
        task_number = int(task_id.split(" ")[1])
        task_file = self._resource_path / ("Result_Task_%d.json" % task_number)
        return task_file

    def _read_json_file(self, json_file):
        with json_file.open("r", encoding="utf-8") as f:
            result = json.load(f)
            return result
