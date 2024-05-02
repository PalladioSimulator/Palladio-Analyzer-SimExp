package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import java.util.List;
import java.util.Set;

import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.QVToReconfigurationManager;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.DeltaIoTNetworkReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.DeltaIoTDefaultReconfigurationStrategy;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTModelAccess;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.SystemConfigurationTracker;
import org.palladiosimulator.simexp.pcm.util.SimulationParameters;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import com.google.common.collect.Sets;

public class DefaultDeltaIoTStrategyContext
        implements ReconfigurationStrategyContext<QVTOReconfigurator, QVToReconfiguration> {

    private final DeltaIoTDefaultReconfigurationStrategy strategy;
    private final DeltaIoTReconfigurationParamRepository reconfParamsRepo;
    private final QVToReconfigurationManager qvtoReconfigurationManager;
    private final DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess;

    public DefaultDeltaIoTStrategyContext(DeltaIoTReconfigurationParamRepository reconfParamsRepo,
            QVToReconfigurationManager qvtoReconfigurationManager,
            DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess, SimulationParameters simulationParameters,
            SystemConfigurationTracker systemConfigurationTracker) {
        this.strategy = new DeltaIoTDefaultReconfigurationStrategy(reconfParamsRepo, modelAccess, simulationParameters,
                systemConfigurationTracker);
        this.reconfParamsRepo = reconfParamsRepo;
        this.qvtoReconfigurationManager = qvtoReconfigurationManager;
        this.modelAccess = modelAccess;
    }

    @Override
    public Set<QVToReconfiguration> getReconfigurationSpace() {
        List<QVToReconfiguration> qvts = qvtoReconfigurationManager.loadReconfigurations();
        if (qvts.size() != 1) {
            throw new RuntimeException("No DeltaIoT network reconfigutation could be found.");
        }

        Set<QVToReconfiguration> reconfs = Sets.newHashSet();
        if (DeltaIoTNetworkReconfiguration.isCorrectQvtReconfguration(qvts.get(0))) {
            reconfs.add(new DeltaIoTNetworkReconfiguration(qvts.get(0), reconfParamsRepo, modelAccess));
        }
        return reconfs;
    }

    @Override
    public boolean isSelectionPolicy() {
        return false;
    }

    @Override
    public ReconfigurationStrategy<QVTOReconfigurator, QVToReconfiguration> getStrategy() {
        return strategy;
    }

    @Override
    public Policy<QVTOReconfigurator, QVToReconfiguration> getSelectionPolicy() {
        return null;
    }

}
