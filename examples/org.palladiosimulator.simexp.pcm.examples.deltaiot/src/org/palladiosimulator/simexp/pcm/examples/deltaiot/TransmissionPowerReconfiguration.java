package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPower;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPowerValue;

import com.google.common.collect.Sets;

import de.uka.ipd.sdq.stoex.VariableReference;

public class TransmissionPowerReconfiguration extends QVToReconfiguration {

    private final static String QVT_FILE_SUFFIX = "TransmissionPower";
    private final static int MAX_POWER = 15;
    private final static int MIN_POWER = 0;

    private final Set<TransmissionPower> powerSettings;

    public TransmissionPowerReconfiguration(QVToReconfiguration reconfiguration, Set<TransmissionPower> powerSettings) {
        super(reconfiguration);

        this.powerSettings = powerSettings;
    }

    public TransmissionPowerReconfiguration(QVToReconfiguration reconfiguration,
            List<TransmissionPower> powerSettings) {
        this(reconfiguration, Sets.newHashSet(powerSettings));
    }

    public static boolean isCorrectQvtReconfguration(QVToReconfiguration qvt) {
        return qvt.getStringRepresentation()
            .endsWith(QVT_FILE_SUFFIX);
    }

    public void adjustPowerSetting(Map<VariableReference, Integer> powerAdjustements) {
        for (VariableReference each : powerAdjustements.keySet()) {
            findTransmissionPowerValueWith(each).ifPresent(v -> adjust(v, powerAdjustements.get(each)));
        }
    }

    private void adjust(TransmissionPowerValue value, int adjustement) {
        int newPowerVal = value.getPowerSetting() + adjustement;
        value.setPowerSetting(newPowerVal);
    }

    public boolean canBeAdjusted(Map<VariableReference, Integer> powerValues) {
        for (VariableReference each : powerValues.keySet()) {
            Optional<TransmissionPowerValue> powerVal = findTransmissionPowerValueWith(each);
            if (powerVal.isEmpty()) {
                // TODO logging
                return false;
            }

            int adjustedPowerSetting = powerVal.get()
                .getPowerSetting() + powerValues.get(each);
            if (Boolean.logicalOr(adjustedPowerSetting < MIN_POWER, adjustedPowerSetting > MAX_POWER)) {
                return false;
            }
        }
        return true;
    }

    private Optional<TransmissionPowerValue> findTransmissionPowerValueWith(VariableReference varRef) {
        return powerSettings.stream()
            .flatMap(each -> each.getTransmissionPowerValues()
                .stream())
            .filter(transmissionPowerValuesWith(varRef))
            .findFirst();
    }

    private Predicate<TransmissionPowerValue> transmissionPowerValuesWith(VariableReference varRef) {
        return v -> v.getVariableRef()
            .getReferenceName()
            .equals(varRef.getReferenceName());
    }

}
