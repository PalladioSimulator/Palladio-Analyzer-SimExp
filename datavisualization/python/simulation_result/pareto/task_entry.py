from dataclasses import dataclass


@dataclass(frozen=True)
class TaskRun:
    energy_consumptions: list[float]
    packet_losses: list[float]


@dataclass(frozen=True)
class TaskResult:
    id: str
    complete: bool
    reward: float
    runs: list[TaskRun]


@dataclass(frozen=True)
class TaskAverages:
    energy_consumption: float
    packet_loss: float


@dataclass(frozen=True)
class TaskEntry:
    id: str
    optimizables: str
    result: TaskResult
