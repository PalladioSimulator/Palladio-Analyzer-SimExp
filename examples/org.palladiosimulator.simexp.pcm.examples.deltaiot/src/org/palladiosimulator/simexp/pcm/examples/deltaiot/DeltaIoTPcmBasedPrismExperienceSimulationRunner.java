package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactor;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactorValue;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPower;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismGenerator;
import org.palladiosimulator.simexp.pcm.prism.process.PcmBasedPrismExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;

public class DeltaIoTPcmBasedPrismExperienceSimulationRunner<S, A>
        extends PcmBasedPrismExperienceSimulationRunner<S, A, List<InputValue>> implements Initializable {

    private final static String REPO_NAME = "DeltaIoTRepository";
    private final static int INITIAL_POWER_VALUE = 0;

    private final DeltaIoTReconfigurationParamRepository reconfParamsRepo;
    private final IExperimentProvider experimentProvider;

    public DeltaIoTPcmBasedPrismExperienceSimulationRunner(PrismGenerator<A, List<InputValue>> prismGenerator,
            File prismFolder, DeltaIoTReconfigurationParamRepository reconfParamsRepo,
            IExperimentProvider experimentProvider) {
        super(prismGenerator, prismFolder);

        this.reconfParamsRepo = reconfParamsRepo;
        this.experimentProvider = experimentProvider;
    }

    @Override
    public void initialize() {
        PCMResourceSetPartition pcmPartition = experimentProvider.getExperimentRunner()
            .getWorkingPartition();
        updateModelReferences(pcmPartition);
    }

    private void updateModelReferences(PCMResourceSetPartition pcmPartition) {
        updateDistributionFactorReferences(retrieveRepo(pcmPartition.getRepositories()));
        updateAndResetTransmissionPower(pcmPartition.getSystem());
    }

    private Repository retrieveRepo(List<Repository> repos) {
        return repos.stream()
            .filter(r -> r.getEntityName()
                .equals(REPO_NAME))
            .findFirst()
            .get();
    }

    private void updateDistributionFactorReferences(Repository repository) {
        for (DistributionFactor eachFactor : reconfParamsRepo.getDistributionFactors()) {
            for (RepositoryComponent eachComponent : repository.getComponents__Repository()) {
                if (eachFactor.getAppliedComponent()
                    .getId()
                    .equals(eachComponent.getId())) {
                    updateComponentAndBranch(eachFactor, eachComponent);
                }
            }
        }
    }

    private void updateComponentAndBranch(DistributionFactor factorToAdjust, RepositoryComponent component) {
        factorToAdjust.setAppliedComponent(component);
        for (DistributionFactorValue each : factorToAdjust.getFactorValues()) {
            Optional<EObject> newBranch = findModelElement(Entity.class.cast(each.getAppliedBranch()), component);
            if (newBranch.isPresent()) {
                each.setAppliedBranch(ProbabilisticBranchTransition.class.cast(newBranch.get()));
            } else {
                // TODO logging
            }
        }
    }

    private void updateAndResetTransmissionPower(System system) {
        for (TransmissionPower powerSetting : reconfParamsRepo.getTransmissionPower()) {
            updateTransmissionPowerReferences(powerSetting, system);
            resetPowerSettings(powerSetting);
        }
    }

    private void resetPowerSettings(TransmissionPower powerSetting) {
        powerSetting.getTransmissionPowerValues()
            .forEach(value -> value.setPowerSetting(INITIAL_POWER_VALUE));
    }

    private void updateTransmissionPowerReferences(TransmissionPower powerSetting, System system) {
        for (AssemblyContext eachContext : system.getAssemblyContexts__ComposedStructure()) {
            if (powerSetting.getAppliedAssembly()
                .getId()
                .equals(eachContext.getId())) {
                powerSetting.setAppliedAssembly(eachContext);
            }
        }
    }

    private Optional<EObject> findModelElement(Entity searchedElement, EObject element) {
        TreeIterator<EObject> iterator = element.eAllContents();
        while (iterator.hasNext()) {
            EObject next = iterator.next();
            if (Entity.class.isInstance(next)) {
                if (Entity.class.cast(next)
                    .getId()
                    .equals(searchedElement.getId())) {
                    return Optional.of(next);
                }
            }
        }
        return Optional.empty();
    }

}
