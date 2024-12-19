package org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.SingleQVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPower;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPowerValue;

import com.google.common.collect.Sets;

import de.uka.ipd.sdq.stoex.VariableReference;

public class TransmissionPowerReconfiguration extends DeltaIoTBaseReconfiguration
        implements ITransmissionPowerReconfiguration // extends
// ReconfigurationImpl<QVTOReconfigurator>
{

    private final static String QVT_FILE_SUFFIX = "TransmissionPower";
    final static int MAX_POWER = 15;
    final static int MIN_POWER = 0;

//    private final QVToReconfiguration reconfiguration;
    private final Set<TransmissionPower> powerSettings;

    public TransmissionPowerReconfiguration(SingleQVToReconfiguration reconfiguration,
            Set<TransmissionPower> powerSettings) {
        super(reconfiguration);
//        this.reconfiguration = reconfiguration;
        this.powerSettings = powerSettings;
    }

//    @Override
//    public String getStringRepresentation() {
//        return reconfiguration.getStringRepresentation();
//    }
//
//    @Override
//    public void execute(IExperimentProvider experimentProvider, IResourceTableManager resourceTableManager) {
//        reconfiguration.execute(experimentProvider, resourceTableManager);
//    }

    public TransmissionPowerReconfiguration(SingleQVToReconfiguration reconfiguration,
            List<TransmissionPower> powerSettings) {
        this(reconfiguration, Sets.newLinkedHashSet(powerSettings));
    }

    public static boolean isCorrectQvtReconfguration(QVToReconfiguration qvt) {
        return qvt.getReconfigurationName()
            .endsWith(QVT_FILE_SUFFIX);
    }

    @Override
    public void adjustTransmissionPower(Map<VariableReference, Integer> powerSetting) {
        for (Map.Entry<VariableReference, Integer> entry : powerSetting.entrySet()) {
            VariableReference each = entry.getKey();
            Integer adjustment = entry.getValue();
            Optional<TransmissionPowerValue> transmissionPowerValue = findTransmissionPowerValueWith(each);
            transmissionPowerValue.ifPresent(v -> adjust(v, adjustment));
        }
    }

    private void adjust(TransmissionPowerValue value, int adjustement) {
        int newPowerVal = value.getPowerSetting() + adjustement;
        value.setPowerSetting(newPowerVal);
    }

    @Override
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
