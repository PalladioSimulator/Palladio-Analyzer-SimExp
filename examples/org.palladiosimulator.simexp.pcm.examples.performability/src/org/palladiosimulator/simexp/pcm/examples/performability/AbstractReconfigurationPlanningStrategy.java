package org.palladiosimulator.simexp.pcm.examples.performability;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork.InputValue;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundRandomVariable;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.utils.EnvironmentalDynamicsUtils;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;

import com.google.common.collect.Maps;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public abstract class AbstractReconfigurationPlanningStrategy<S, A> implements ReconfigurationPlanningStrategy<S> {

    protected static final Logger LOGGER = Logger.getLogger(ReconfigurationPlanningStrategy.class.getName());

    private final PcmMeasurementSpecification responseTimeSpec;
    private final PerformabilityStrategyConfiguration strategyConfiguration;
    protected final NodeRecoveryStrategy<S, A> recoveryStrategy;

    public AbstractReconfigurationPlanningStrategy(PcmMeasurementSpecification responseTimeSpec,
            PerformabilityStrategyConfiguration strategyConfiguration, NodeRecoveryStrategy<S, A> recoveryStrategy) {
        this.responseTimeSpec = responseTimeSpec;
        this.strategyConfiguration = strategyConfiguration;
        this.recoveryStrategy = recoveryStrategy;
    }

    @Override
    public abstract QVToReconfiguration planReconfigurationSteps(State<S> source, Set<QVToReconfiguration> options,
            SharedKnowledge knowledge) throws PolicySelectionException;

    protected Double retrieveResponseTime(SelfAdaptiveSystemState<S, A> sasState) {
        SimulatedMeasurement simMeasurement = sasState.getQuantifiedState()
            .findMeasurementWith(responseTimeSpec)
            .orElseThrow();
        return simMeasurement.getValue();
    }

    protected Map<ResourceContainer, CategoricalValue> retrieveServerNodeStates(PerceivableEnvironmentalState state) {
        Map<ResourceContainer, CategoricalValue> serverNodeStates = Maps.newHashMap();
        List<InputValue> inputs = EnvironmentalDynamicsUtils.toInputs(state.getValue()
            .getValue());
        for (InputValue each : inputs) {
            ResourceContainer container = findAppliedObjectsReferencedResourceContainer(each);
            if (container != null) {
                CategoricalValue nodeState = (CategoricalValue) each.value;
                serverNodeStates.put(container, nodeState);
            }
        }

        if (serverNodeStates.isEmpty()) {
            throw new RuntimeException(
                    "Environment model holds no specification of node failure random variables. Unabled to run performability strategy.");
        }

        return serverNodeStates;
    }

    private ResourceContainer findAppliedObjectsReferencedResourceContainer(InputValue inputValue) {
        GroundRandomVariable grVariable = inputValue.variable;
        if (isServerNodeVariable(grVariable)) {
            // NOTE: The ground random variable definition in *.staticmodel defines the attributge
            // appliedObjects;
            // retrieving of the referenced objects requires the consideration of their specified
            // order in the model
            EList<EObject> appliedObjects = grVariable.getAppliedObjects();
            for (EObject appliedObject : appliedObjects) {
                // find referenced appliedObjecs 'ResourceContainer'
                if (appliedObject instanceof ResourceContainer) {
                    return (ResourceContainer) appliedObject;
                }
            }
        }
        return null;
    }

    private boolean isServerNodeVariable(GroundRandomVariable variable) {
        return variable.getInstantiatedTemplate()
            .getId()
            .equals(strategyConfiguration.getNodeFailureTemplateId());
    }

    protected Optional<QVToReconfiguration> findReconfiguration(String name, Set<QVToReconfiguration> options2) {
        List<QVToReconfiguration> options = options2.stream()
            .filter(QVToReconfiguration.class::isInstance)
            .map(QVToReconfiguration.class::cast)
            .collect(toList());

        for (QVToReconfiguration each : options) {
            String reconfName = each.getStringRepresentation();
            if (reconfName.equals(name)) {
                return Optional.of(each);
            }
        }
        return Optional.empty();
    }

    protected String missingQvtoTransformationMessage(String qvtoTransformationName) {
        return String.format(
                "No QVT transformation named '%s' available. Ensure your model defines a corresponding transformation.",
                qvtoTransformationName);
    }

    protected QVToReconfiguration emptyReconfiguration() {
        return QVToReconfiguration.empty();
    }
}
