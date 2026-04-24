from dataclasses import dataclass


@dataclass(frozen=True)
class NormalizationBoundary:
    energy_min: float
    energy_max: float
    packet_loss_min: float
    packet_loss_max: float
