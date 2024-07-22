package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.config.SimulationParameters;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.DeltaIoTNetworkReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.IDeltaIoToReconfCustomizerFactory;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.reconfiguration.IDeltaIoToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy.DeltaIoTDefaultReconfigurationStrategy;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTModelAccess;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.SystemConfigurationTracker;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import com.google.common.collect.Sets;

public class DefaultDeltaIoTStrategyContext
        implements ReconfigurationStrategyContext<QVTOReconfigurator, QVToReconfiguration> {

    private final DeltaIoTDefaultReconfigurationStrategy strategy;
    private final List<QVToReconfiguration> reconfigurations;
    private final IDeltaIoToReconfCustomizerFactory reconfCustomizerFactory;

    public DefaultDeltaIoTStrategyContext(DeltaIoTReconfigurationParamRepository reconfParamsRepo,
            List<QVToReconfiguration> reconfigurations, IDeltaIoToReconfCustomizerFactory reconfCustomizerFactory,
            DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess, SimulationParameters simulationParameters,
            SystemConfigurationTracker systemConfigurationTracker) {
        DeltaIoToReconfCustomizerResolver reconfCustomizerResolver = new DeltaIoToReconfCustomizerResolver();
        this.strategy = new DeltaIoTDefaultReconfigurationStrategy(reconfParamsRepo, modelAccess, simulationParameters,
                systemConfigurationTracker, reconfCustomizerResolver);
        this.reconfigurations = Collections.unmodifiableList(reconfigurations);
        this.reconfCustomizerFactory = reconfCustomizerFactory;
    }

    @Override
    public Set<QVToReconfiguration> getReconfigurationSpace() {
        if (reconfigurations.size() != 1) {
            throw new RuntimeException("No DeltaIoT network reconfigutation could be found.");
        }

        Set<QVToReconfiguration> reconfs = Sets.newHashSet();
        QVToReconfiguration qvt = reconfigurations.get(0);
        if (DeltaIoTNetworkReconfiguration.isCorrectQvtReconfguration(qvt)) {
            IDeltaIoToReconfiguration customizer = reconfCustomizerFactory.create(qvt);
            if (customizer != null) {
                reconfs.add(customizer);
            }
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
