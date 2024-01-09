package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import static java.util.stream.Collectors.toList;
import static org.palladiosimulator.simexp.pcm.examples.deltaiot.DeltaIoTEnvironemtalDynamics.toInputs;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundRandomVariable;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.core.composition.AssemblyConnector;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.parameter.VariableUsage;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;
import org.palladiosimulator.pcm.system.System;
import org.palladiosimulator.simexp.core.state.ArchitecturalConfiguration;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.pcm.state.PcmArchitecturalConfiguration;
import org.palladiosimulator.solver.models.PCMInstance;

import com.google.common.collect.Lists;

public class DeltaIoTModelAccess<S, A> {

    // TODO: singleton
    private final static DeltaIoTModelAccess INSTANCE = new DeltaIoTModelAccess();

    private DeltaIoTModelAccess() {

    }

    public static <S, A> DeltaIoTModelAccess<S, A> get() {
        return INSTANCE;
    }

    public double retrieveWirelessInterference(GroundRandomVariable maVariable,
            PerceivableEnvironmentalState nextEnvironment) {
        return toInputs(nextEnvironment.getValue()
            .getValue()).stream()
                .filter(v -> v.variable.getId()
                    .equals(maVariable.getId()))
                .map(InputValue::asCategorical)
                .map(v -> Double.valueOf(v.get()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("The mote activation template is missing."));
    }

    public int retrieveTransmissionPower(AssemblyContext mote, LinkingResource link) {
        List<VariableUsage> varUsages = mote.getConfigParameterUsages__AssemblyContext();
        if (varUsages.isEmpty()) {
            // TODO exception handling
            throw new RuntimeException(String.format("There is no specified variable usage for instantiated mote: %s",
                    mote.getEntityName()));
        }

        PCMRandomVariable pcmVar = null;
        if (varUsages.size() == 1) {
            pcmVar = varUsages.get(0)
                .getVariableCharacterisation_VariableUsage()
                .get(0)
                .getSpecification_VariableCharacterisation();
        } else {
            pcmVar = retrieveTransmissionPower(mote.getConfigParameterUsages__AssemblyContext(), link);
        }
        return Integer.valueOf(pcmVar.getSpecification());
    }

    public AssemblyContext findSourceMote(LinkingResource link, ArchitecturalConfiguration<S, A> archConfig) {
        PcmArchitecturalConfiguration<A> configuration = PcmArchitecturalConfiguration.class.cast(archConfig);
        PCMInstance pcm = configuration.getConfiguration();
        List<AllocationContext> allocContexts = resolveDeployments(
                link.getConnectedResourceContainers_LinkingResource(), pcm.getAllocation());
        return resolveSourceMote(allocContexts, pcm.getSystem());
    }

    public boolean isTransmissionPowerOfLink(VariableUsage varUsage, LinkingResource link) {
        String refNameSuffix = varUsage.getNamedReference__VariableUsage()
            .getReferenceName()
            .replaceFirst("TransmissionPower", "");
        String linkNameSuffix = link.getEntityName()
            .replaceFirst("Unicast", "");
        return refNameSuffix.equals(linkNameSuffix);
    }

    private PCMRandomVariable retrieveTransmissionPower(List<VariableUsage> varUsages, LinkingResource link) {
        for (VariableUsage each : varUsages) {
            if (isTransmissionPowerOfLink(each, link)) {
                return each.getVariableCharacterisation_VariableUsage()
                    .get(0)
                    .getSpecification_VariableCharacterisation();
            }
        }

        throw new RuntimeException(
                String.format("There is no specified variable usage for link: %s", link.getEntityName()));
    }

    private AssemblyContext resolveSourceMote(List<AllocationContext> allocContexts, System system) {
        if (allocContexts.size() != 2) {
            throw new RuntimeException(
                    "There should be an one-to-one mapping between deployed sensor motes and sensor hardware.");
        }

        AssemblyContext first = allocContexts.get(0)
            .getAssemblyContext_AllocationContext();
        AssemblyContext second = allocContexts.get(1)
            .getAssemblyContext_AllocationContext();
        return areIndirectlyConnected(first, second, system) ? first : second;
    }

    private boolean areIndirectlyConnected(AssemblyContext source, AssemblyContext target, System system) {
        List<AssemblyContext> result = filterAssemblyContextsWithSource(source, system);
        if (result.isEmpty()) {
            return false;
        }

        AssemblyContext transmitter = result.get(0);
        for (AssemblyContext each : filterAssemblyContextsWithSource(transmitter, system)) {
            if (each.getId()
                .equals(target.getId())) {
                return true;
            }
        }
        return false;
    }

    private List<AssemblyContext> filterAssemblyContextsWithSource(AssemblyContext source, System system) {
        return system.getConnectors__ComposedStructure()
            .stream()
            .filter(AssemblyConnector.class::isInstance)
            .map(AssemblyConnector.class::cast)
            .filter(c -> c.getRequiringAssemblyContext_AssemblyConnector()
                .getId()
                .equals(source.getId()))
            .map(c -> c.getProvidingAssemblyContext_AssemblyConnector())
            .collect(toList());
    }

    private List<AllocationContext> resolveDeployments(List<ResourceContainer> container, Allocation allocation) {
        List<AllocationContext> deployments = Lists.newArrayList();
        for (AllocationContext each : allocation.getAllocationContexts_Allocation()) {
            if (isMoteOrGatewayAllocation(each)) {
                findContainerFor(each, container).ifPresent(c -> deployments.add(each));
            }
        }
        return deployments;
    }

    private boolean isMoteOrGatewayAllocation(AllocationContext allocContext) {
        String entity = allocContext.getAssemblyContext_AllocationContext()
            .getEncapsulatedComponent__AssemblyContext()
            .getEntityName();
        return Boolean.logicalOr(entity.equals("SensorNode"), entity.equals("Gateway"));
    }

    private Optional<ResourceContainer> findContainerFor(AllocationContext allocContext,
            List<ResourceContainer> container) {
        return container.stream()
            .filter(c -> c.getId()
                .equals(allocContext.getResourceContainer_AllocationContext()
                    .getId()))
            .findFirst();
    }

    public List<ProbabilisticBranchTransition> retrieveCommunicatingBranches(AssemblyContext sourceMote) {
        System system = (System) sourceMote.eContainer();
        AssemblyContext sourceTransmitter = filterAssemblyContextsWithSource(sourceMote, system).get(0);
        BasicComponent comp = (BasicComponent) sourceTransmitter.getEncapsulatedComponent__AssemblyContext();
        return comp.getServiceEffectSpecifications__BasicComponent()
            .stream()
            .flatMap(filterProbabilistBranches())
            .collect(toList());
    }

    private Function<ServiceEffectSpecification, Stream<ProbabilisticBranchTransition>> filterProbabilistBranches() {
        return seff -> {
            if (ResourceDemandingSEFF.class.isInstance(seff) == false) {
                return Stream.empty();
            }

            List<ProbabilisticBranchTransition> result = Lists.newArrayList();

            TreeIterator<EObject> iterator = ResourceDemandingSEFF.class.cast(seff)
                .eAllContents();
            while (iterator.hasNext()) {
                EObject next = iterator.next();
                if (ProbabilisticBranchTransition.class.isInstance(next)) {
                    result.add((ProbabilisticBranchTransition) next);
                }
            }

            return result.stream();
        };
    }

}
