package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import java.util.List;
import java.util.Set;

import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.action.SingleQVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.IDeltaIoToReconfCustomizerFactory;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.IDeltaIoToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.LocalQualityBasedReconfigurationStrategy;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTModelAccess;
import org.palladiosimulator.simexp.pcm.prism.entity.PrismSimulatedMeasurementSpec;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import com.google.common.collect.Sets;

public class LocalQualityBasedStrategyContext
        implements ReconfigurationStrategyContext<QVTOReconfigurator, QVToReconfiguration> {

    private final Policy<QVTOReconfigurator, QVToReconfiguration> reconfSelectionPolicy;
    private final QVToReconfigurationManager qvtoReconfigurationManager;
    private final IDeltaIoToReconfCustomizerFactory customizerFactory;

    public LocalQualityBasedStrategyContext(PrismSimulatedMeasurementSpec packetLossSpec,
            PrismSimulatedMeasurementSpec energyConsumptionSpec,
            DeltaIoTReconfigurationParamRepository reconfParamsRepo,
            QVToReconfigurationManager qvtoReconfigurationManager, IDeltaIoToReconfCustomizerFactory customizerFactory,
            DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess) {
        this.reconfSelectionPolicy = LocalQualityBasedReconfigurationStrategy.newBuilder(modelAccess)
            .withReconfigurationParams(reconfParamsRepo)
            .andPacketLossSpec(packetLossSpec)
            .andEnergyConsumptionSpec(energyConsumptionSpec)
            .build();
        this.qvtoReconfigurationManager = qvtoReconfigurationManager;
        this.customizerFactory = customizerFactory;
    }

    @Override
    public Set<QVToReconfiguration> getReconfigurationSpace() {
        Set<QVToReconfiguration> reconfs = Sets.newHashSet();

        List<QVToReconfiguration> qvts = qvtoReconfigurationManager.loadReconfigurations();
        for (QVToReconfiguration each : qvts) {
            SingleQVToReconfiguration singleQVToReconfiguration = (SingleQVToReconfiguration) each;
            IDeltaIoToReconfiguration customizer = customizerFactory.create(singleQVToReconfiguration);
            if (customizer != null) {
                reconfs.add(customizer);
            }
        }

        if (reconfs.isEmpty()) {
            // TODO exception handling
            throw new RuntimeException("No DeltaIoT reconfigutations could be found or generated");
        }
        return reconfs;
    }

    @Override
    public boolean isSelectionPolicy() {
        return true;
    }

    @Override
    public ReconfigurationStrategy<QVTOReconfigurator, QVToReconfiguration> getStrategy() {
        return null;
    }

    @Override
    public Policy<QVTOReconfigurator, QVToReconfiguration> getSelectionPolicy() {
        return reconfSelectionPolicy;
    }

}
