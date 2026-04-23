from dataclasses import dataclass


@dataclass(frozen=True)
class Individual:
    id: str
    reward: float
    optimizables: dict[str, str]
