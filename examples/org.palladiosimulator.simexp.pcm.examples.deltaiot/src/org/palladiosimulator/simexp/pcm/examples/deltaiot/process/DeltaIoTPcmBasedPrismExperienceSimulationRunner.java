package org.palladiosimulator.simexp.pcm.examples.deltaiot.process;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.simexp.core.process.Initializable;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DeltaIoTReconfigurationParamRepository;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactor;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.DistributionFactorValue;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPower;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.param.reconfigurationparams.TransmissionPowerValue;
import org.palladiosimulator.simexp.pcm.prism.generator.PrismGenerator;
import org.palladiosimulator.simexp.pcm.prism.process.PcmBasedPrismExperienceSimulationRunner;
import org.palladiosimulator.simexp.pcm.util.IExperimentProvider;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class DeltaIoTPcmBasedPrismExperienceSimulationRunner<A> extends
        PcmBasedPrismExperienceSimulationRunner<A, List<InputValue<CategoricalValue>>> implements Initializable {
    private final static String REPO_NAME = "DeltaIoTRepository";

    private final DeltaIoTReconfigurationParamRepository reconfParamsRepo;
    private final IExperimentProvider experimentProvider;

    public DeltaIoTPcmBasedPrismExperienceSimulationRunner(
            PrismGenerator<A, List<InputValue<CategoricalValue>>> prismGenerator, Path prismLogPath, String strategyId,
            DeltaIoTReconfigurationParamRepository reconfParamsRepo, IExperimentProvider experimentProvider) {
        super(prismGenerator, prismLogPath, strategyId);

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
        }
    }

    private void updateTransmissionPowerReferences(TransmissionPower powerSetting, System system) {
        for (AssemblyContext eachContext : system.getAssemblyContexts__ComposedStructure()) {
            if (powerSetting.getAppliedAssembly()
                .getId()
                .equals(eachContext.getId())) {
                powerSetting.setAppliedAssembly(eachContext);
                updateToDefaultPowerValues(eachContext, powerSetting);
            }
        }
    }

    private void updateToDefaultPowerValues(AssemblyContext context, TransmissionPower powerSetting) {
        for (VariableUsage eachVarUsage : context.getConfigParameterUsages__AssemblyContext()) {
            for (TransmissionPowerValue eachValue : powerSetting.getTransmissionPowerValues()) {
                var refName = eachVarUsage.getNamedReference__VariableUsage()
                    .getReferenceName();
                var valueRefName = eachValue.getVariableRef()
                    .getReferenceName();
                if (refName.equals(valueRefName)) {
                    var strTp = eachVarUsage.getVariableCharacterisation_VariableUsage()
                        .get(0)
                        .getSpecification_VariableCharacterisation()
                        .getSpecification();
                    eachValue.setPowerSetting(Integer.parseInt(strTp));
                }
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
