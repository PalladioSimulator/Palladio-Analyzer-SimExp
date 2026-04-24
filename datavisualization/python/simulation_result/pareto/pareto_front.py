from dataclasses import dataclass


@dataclass(frozen=True)
class ParetoEntry:
    id: str
    score: float
    fitness: float
    optimizables: dict[str, str]
    average_energy_consumption: float
    average_packet_loss: float


@dataclass(frozen=True)
class ParetoFront:
    generation: int
    entries: list[ParetoEntry]
