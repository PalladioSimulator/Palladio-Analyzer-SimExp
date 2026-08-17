from dataclasses import dataclass

from .individual import Individual


@dataclass(frozen=True)
class Generation:
    generation: int
    individuals: list[Individual]
