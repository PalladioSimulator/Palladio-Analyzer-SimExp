from enum import Enum


class PrismKind(Enum):
    ENERGY_CONSUMPTION = "EnergyConsumption"
    PACKET_LOSS = "Packetloss"


def identify_prism(content: str) -> PrismKind:
    for kind in PrismKind:
        if f'[ F "{kind.value}" ]' in content:
            return kind
    raise ValueError("unknown PRISM kind: %s" % content)
