from prism_property import PrismKind, identify_prism


def test_packet_loss():
    actual_prism = identify_prism('P=? [ F "Packetloss" ]')

    assert actual_prism == PrismKind.PACKET_LOSS


def test_energy_consumption():
    actual_prism = identify_prism('Rmax=? [ F "EnergyConsumption" ]')

    assert actual_prism == PrismKind.ENERGY_CONSUMPTION