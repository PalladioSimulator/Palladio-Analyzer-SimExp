from dataclasses import dataclass


@dataclass(frozen=True)
class Point:
    energy: float
    packet_loss: float
