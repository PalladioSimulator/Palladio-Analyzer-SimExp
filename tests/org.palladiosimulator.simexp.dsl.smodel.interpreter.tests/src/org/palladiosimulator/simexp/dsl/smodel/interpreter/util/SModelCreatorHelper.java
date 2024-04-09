package org.palladiosimulator.simexp.dsl.smodel.interpreter.util;

import org.palladiosimulator.simexp.dsl.smodel.smodel.Probe;
import org.palladiosimulator.simexp.dsl.smodel.smodel.ProbeAdressingKind;
import org.palladiosimulator.simexp.dsl.smodel.smodel.SmodelFactory;

public class SModelCreatorHelper {

    private static final SmodelFactory smodelFactory = SmodelFactory.eINSTANCE;

    public SModelCreatorHelper() {
        // TODO Auto-generated constructor stub
    }

    public Probe createProbe(String monitorId, ProbeAdressingKind value) {
        Probe probe = smodelFactory.createProbe();
        probe.setName("testProbe");
        probe.setIdentifier(monitorId);
        probe.setKind(value);
        return probe;
    }

}
