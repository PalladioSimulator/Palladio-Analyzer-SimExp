package org.palladiosimulator.simexp.pcm.examples.performability;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundRandomVariable;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.strategy.ReconfigurationStrategy;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.utils.EnvironmentalDynamicsUtils;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;

import com.google.common.collect.Maps;

import tools.mdsd.probdist.api.entity.CategoricalValue;

/**
 * 
 * This policy aims to provide a strategy to compensate server node failures
 * 
 */
public class PerformabilityStrategy<C> extends ReconfigurationStrategy<QVTOReconfigurator, QVToReconfiguration> {

    private static final Logger LOGGER = Logger.getLogger(PerformabilityStrategy.class.getName());

    private static final String SCALE_IN_QVTO_NAME = "scaleIn";
    private static final String SCALE_OUT_SOURCE_QVTO_NAME = "scaleOut";
    private static final String NODE_RECOVERY_QVTO_NAME = "nodeRecovery";
    private static final String AVAILABLE_STATE = "available";
    private static final Threshold UPPER_THRESHOLD = Threshold.lessThanOrEqualTo(2.0);
    private static final Threshold LOWER_THRESHOLD = Threshold.greaterThanOrEqualTo(1.0);

    private final PcmMeasurementSpecification responseTimeSpec;
    private final PerformabilityStrategyConfiguration strategyConfiguration;
//    private final NodeRecoveryStrategy recoveryStrategy;
    private final ReconfigurationPlanningStrategy reconfigurationPlanningStrategy;

    public PerformabilityStrategy(PcmMeasurementSpecification responseTimeSpec,
            PerformabilityStrategyConfiguration strategyConfiguration,
            ReconfigurationPlanningStrategy reconfigurationPlanningStrategy) {
        this.responseTimeSpec = responseTimeSpec;
        this.strategyConfiguration = strategyConfiguration;
//        this.recoveryStrategy = recoveryStrategy;
        this.reconfigurationPlanningStrategy = reconfigurationPlanningStrategy;
    }

    @Override
    public String getId() {
        return PerformabilityStrategy.class.getName();
    }

    @Override
    protected void monitor(State source, SharedKnowledge knowledge) {
        /**
         * transfer status of server nodes to knowledge base
         */
        SelfAdaptiveSystemState<C, QVTOReconfigurator, List<InputValue<CategoricalValue>>> sasState = (SelfAdaptiveSystemState<C, QVTOReconfigurator, List<InputValue<CategoricalValue>>>) source;
        Map<ResourceContainer, CategoricalValue> serverNodeStates = retrieveServerNodeStates(
                sasState.getPerceivedEnvironmentalState());

        for (Entry<ResourceContainer, CategoricalValue> entry : serverNodeStates.entrySet()) {
            String key = entry.getKey()
                .getId();
            Object value = entry.getValue();
            knowledge.store(key, value);
        }
    }

    @Override
    protected boolean analyse(State source, SharedKnowledge knowledge) {
        boolean hasConstraintViolations = false;
        /**
         * check for any constraint violations that require a reconfiguration of the system, e.g.
         * threshold violations, presence of failed nodes, ...
         * 
         */
        SelfAdaptiveSystemState<C, QVTOReconfigurator, List<InputValue<CategoricalValue>>> sasState = (SelfAdaptiveSystemState<C, QVTOReconfigurator, List<InputValue<CategoricalValue>>>) source;
        Double responseTime = retrieveResponseTime(sasState);
        Map<ResourceContainer, CategoricalValue> serverNodeStates = retrieveServerNodeStates(
                sasState.getPerceivedEnvironmentalState());

        /** check threshold constraint violations for response time */
        if (isExceeded(responseTime) || isSubceeded(responseTime)) {
            hasConstraintViolations = true;
        }
        /** check for failed nodes */
        if (!allNodesAreAvailable(serverNodeStates)) {
            hasConstraintViolations = true;
        }
        return hasConstraintViolations;
    }

    @Override
    protected QVToReconfiguration plan(State source, Set<QVToReconfiguration> options, SharedKnowledge knowledge) {
        /**
         * The role of the planner function is to select the best adaptation option and generate a
         * plan for adapting the managed system from its current configuration to the new
         * configuration defined by the best adaptation option.
         */

        /**
         * This method must be implemented for each system model under study, i.e. the actual
         * reconfiguration files (qvto transformations) are defined as part of the system model
         * 
         * In order to compensate for server node failures, dedicated triggers and selected actions
         * (from the Set options) to execute for each case must be implemented here
         * 
         * Overall strategy consists of the following actions and triggers:
         * 
         * if (node.isUnavailable) => node recovery && redistribute work
         * 
         * if (responseTime < threshold) => scale-out if (responseTime > threshold) => scale-in
         * 
         * required adaptation actions: * infrastructure-level 1) node recovery: - this action must
         * ensure that a defined minimal set of nodes is always available - if less nodes are
         * available than the defined minimum, then new nodes must be started - this action does not
         * (re)distribute work (!) 2) work redistribution: - this action must check if the node of
         * current work is available; if not, the work must be redistributed, to an available node
         * application-level 3) scale-in 4) scale-out
         * 
         * As result after applying the adaptation strategy a new SAS (self-adaptive system) state
         * is returned with an adapted/updated environment and architectural model
         * 
         * Notes: if several trigger conditions occur, a priorization within the strategy is
         * required, i.e. node repair will take some time, therefore work must be re-distributed on
         * still-working nodes before actual scaling; remember that this topic is connected to
         * availability of nodes and the minimum set of available nodes
         */

        /**
         * 
         * aktuelles ARchtiekturmodel + Fehlemodell (mit bereits ausgefallem Knoten) inkl Simulation
         * -> Simulationsergebnis/respons time mit kaputten Knoten Rückggabe ist 1 Aktion: options =
         * 2 Aktionen (1. scale/scale out -> trigger: response time < threshold , 2. Knotenausfall
         * recovery -> trigger: env model: failed node(unavailable)) -> über SASState(aktuelles arch
         * + akt env modell + quantified state)) abrufbar, -> QVTO transformation (env,
         * architekturmodel (inkl Fehlermodel))
         * 
         * archtekturmodel (pcm + failurescenariomodel(2)) -> Zeitpunkt, wann Fehler gescheduled
         * werden failurescenarimodel liegt im Blackboard -> input Variable für die QVTO
         * transformation upgedatedte failuresceario wieder zurück ins blackboard (QVTO Ergebnis)
         * 
         * 
         */

        // FIXME: later replace by combined QVTo transformation (Ch.Strier approach) to support
        // execution of multiple actions
        QVToReconfiguration plannedAction = emptyReconfiguration();
        /** could also be a list of actions to execute */

        try {
//            SelfAdaptiveSystemState<?> sasState = (SelfAdaptiveSystemState<?>) source;
//            Double responseTime = retrieveResponseTime(sasState);
//            Map<ResourceContainer, CategoricalValue> serverNodeStates = retrieveServerNodeStates(
//                    sasState.getPerceivedEnvironmentalState());
//            

//            if (allNodesAreAvailable(serverNodeStates)) {
//                if (isExceeded(responseTime)) {
//                    return outSource(options);
//                } else if (isSubceeded(responseTime)) {
//                    return scaleIn(options);
//                } else {
//                    return emptyReconfiguration();
//                }
//            }

            /**
             * workarournd to implement node recovery behavior until we are able to realize this as
             * QVTO transformation
             * 
             */
//            recoveryStrategy.execute(sasState, knowledge);
//            return nodeRecovery(options);

            plannedAction = reconfigurationPlanningStrategy.planReconfigurationSteps(source, options, knowledge);
            return plannedAction;

        } catch (PolicySelectionException e) {
            LOGGER.error("Failed to select an adaptation strategy", e);
            return emptyReconfiguration();
        }

    }

    @Override
    protected QVToReconfiguration emptyReconfiguration() {
        return QVToReconfiguration.empty();
    }

    private Double retrieveResponseTime(
            SelfAdaptiveSystemState<C, QVTOReconfigurator, List<InputValue<CategoricalValue>>> sasState) {
        SimulatedMeasurement simMeasurement = sasState.getQuantifiedState()
            .findMeasurementWith(responseTimeSpec)
            .orElseThrow();
        return simMeasurement.getValue();
    }

    private Map<ResourceContainer, CategoricalValue> retrieveServerNodeStates(
            PerceivableEnvironmentalState<List<InputValue<CategoricalValue>>> state) {
        Map<ResourceContainer, CategoricalValue> serverNodeStates = Maps.newHashMap();
        List<InputValue<CategoricalValue>> inputs = EnvironmentalDynamicsUtils.toInputs(state.getValue()
            .getValue());
        for (InputValue<CategoricalValue> each : inputs) {
            ResourceContainer container = findAppliedObjectsReferencedResourceContainer(each);
            if (container != null) {
                CategoricalValue nodeState = each.getValue();
                serverNodeStates.put(container, nodeState);
            }
        }

        if (serverNodeStates.isEmpty()) {
            throw new RuntimeException(
                    "Environment model holds no specification of node failure random variables. Unabled to run performability strategy.");
        }

        return serverNodeStates;
    }

    private ResourceContainer findAppliedObjectsReferencedResourceContainer(InputValue<CategoricalValue> inputValue) {
        GroundRandomVariable grVariable = inputValue.getVariable();
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

    private boolean allNodesAreAvailable(Map<ResourceContainer, CategoricalValue> serverNodeStates) {
        return serverNodeStates.values()
            .stream()
            .allMatch(v -> v.get()
                .equals(AVAILABLE_STATE));
    }

    private boolean isExceeded(Double responseTime) {
        return UPPER_THRESHOLD.isNotSatisfied(responseTime);
    }

    private boolean isSubceeded(Double responseTime) {
        return LOWER_THRESHOLD.isNotSatisfied(responseTime);
    }

    private QVToReconfiguration scaleIn(Set<QVToReconfiguration> options) throws PolicySelectionException {
        return findReconfiguration(SCALE_IN_QVTO_NAME, options)
            .orElseThrow(() -> new PolicySelectionException(missingQvtoTransformationMessage(SCALE_IN_QVTO_NAME)));
    }

    private QVToReconfiguration outSource(Set<QVToReconfiguration> options) throws PolicySelectionException {
        return findReconfiguration(SCALE_OUT_SOURCE_QVTO_NAME, options).orElseThrow(
                () -> new PolicySelectionException(missingQvtoTransformationMessage(SCALE_OUT_SOURCE_QVTO_NAME)));
    }

    private QVToReconfiguration nodeRecovery(Set<QVToReconfiguration> options) throws PolicySelectionException {
        return findReconfiguration(NODE_RECOVERY_QVTO_NAME, options)
            .orElseThrow(() -> new PolicySelectionException(missingQvtoTransformationMessage(NODE_RECOVERY_QVTO_NAME)));
    }

    private boolean isServerNodeVariable(GroundRandomVariable variable) {
        return variable.getInstantiatedTemplate()
            .getId()
            .equals(strategyConfiguration.getNodeFailureTemplateId());
    }

    private Optional<QVToReconfiguration> findReconfiguration(String name, Set<QVToReconfiguration> options2) {
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

    private String missingQvtoTransformationMessage(String qvtoTransformationName) {
        return String.format(
                "No QVT transformation named '%s' available. Ensure your model defines a corresponding transformation.",
                qvtoTransformationName);
    }

}
