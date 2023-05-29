package org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing;

import org.palladiosimulator.pcm.query.RepositoryModelLookup;
import org.palladiosimulator.pcm.query.ResourceEnvironmentModelLookup;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.simexp.core.state.ArchitecturalConfiguration;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.pcm.examples.performability.AbstractNodeRecoveryStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityStrategyConfiguration;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityStrategyConstants;
import org.palladiosimulator.simexp.pcm.examples.performability.RepositoryModelUpdater;
import org.palladiosimulator.solver.models.PCMInstance;

public class FaultTolerantScalingNodeFailureRecoveryStrategy extends AbstractNodeRecoveryStrategy {
    
    private static final String STRATEGY_NAME = FaultTolerantScalingNodeFailureRecoveryStrategy.class.getName();
    
    private final String loadBalancerId = "_xISeMAEpEeS7FKokKTKFow";
    private final String loadBalancerSeffBranchActionId = "_m2PcEAEoEeS7FKokKTKFow";
    private final String serverNodeOneId = "_yaTfsAEpEeS7FKokKTKFow";
    private final String serverNodeTwoId = "_3uVlIAEpEeS7FKokKTKFow";
    
    
    public FaultTolerantScalingNodeFailureRecoveryStrategy(PerformabilityStrategyConfiguration strategyConfiguration,
            RepositoryModelLookup repositoryLookup, ResourceEnvironmentModelLookup resourceEnvLookup,
            RepositoryModelUpdater repositoryUpdater) {
        super(strategyConfiguration, repositoryLookup, resourceEnvLookup, repositoryUpdater);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(SelfAdaptiveSystemState<?> sasState, SharedKnowledge knowledge) {
        LOGGER.info(String.format("'EXECUTE' apply reconfiguration 'nodeRecovery' workaround %s ", STRATEGY_NAME));
        
        String serverNode1State = knowledge.getValue(serverNodeOneId).get().toString();
        String serverNode2State = knowledge.getValue(serverNodeTwoId).get().toString();
        
        LOGGER.info(String.format("Knowledge: [%s,%s], [%s,%s]", serverNodeOneId, serverNode1State, serverNodeTwoId, serverNode2State));
        
        ArchitecturalConfiguration<PCMInstance> architecturalConfig = (ArchitecturalConfiguration<PCMInstance>) sasState.getArchitecturalConfiguration();
        PCMInstance pcmInstance = architecturalConfig.getConfiguration();
        Repository defaultRepository = repositoryLookup.findRepositoryByEntityName(pcmInstance.getRepositories(), "defaultRepository");
        
        ProbabilisticBranchTransition probBranchTransitionToServerNode1 = findProbabilisticBranchTransitionToServerNode(
                defaultRepository
                , loadBalancerId, "processRequest", "delegateToServer1", "AC_Server1", loadBalancerSeffBranchActionId);
        ProbabilisticBranchTransition probBranchTransitionToServerNode2 = findProbabilisticBranchTransitionToServerNode(
                defaultRepository
                , loadBalancerId, "processRequest", "delegateToServer2", "AC_Server2", loadBalancerSeffBranchActionId);
        
        if ( !handleStates(serverNode1State, serverNode2State, probBranchTransitionToServerNode1, probBranchTransitionToServerNode2)) {
            LOGGER.debug(String.format("Failed to perform %s failure recovery strategy. Unable to identify connected nodes.", STRATEGY_NAME));
        }
        
        ResourceEnvironment resourceEnv = pcmInstance.getResourceEnvironment();
        ResourceContainer node1 = resourceEnvLookup.findResourceContainerById(resourceEnv, serverNodeOneId);
        ResourceContainer node2 = resourceEnvLookup.findResourceContainerById(resourceEnv, serverNodeTwoId);
        logMsg(node1.getEntityName(), node2.getEntityName(), serverNodeOneId, serverNodeTwoId, serverNode1State, serverNode2State
                , probBranchTransitionToServerNode1.getBranchProbability(), probBranchTransitionToServerNode2.getBranchProbability());

        LOGGER.info(String.format("'EXECUTE' applied reconfiguration 'nodeRecovery' workaround %s ", STRATEGY_NAME));
    }

    
    private boolean handleStates(String serverNode1State, String serverNode2State, ProbabilisticBranchTransition probBranchTransitionToServerNode1, ProbabilisticBranchTransition probBranchTransitionToServerNode2) {
        if (serverNode1State.equals(PerformabilityStrategyConstants.NODE_STATE_AVAILABLE)) {
            if (serverNode2State.equals(PerformabilityStrategyConstants.NODE_STATE_AVAILABLE)) {
                /** how to restore the branch transition probabilities if a node becomes available again is use case specific*/
                if ( ZERO_BRANCH_TRANSITION_PROBABILITY == probBranchTransitionToServerNode1.getBranchProbability()
                     || ZERO_BRANCH_TRANSITION_PROBABILITY == probBranchTransitionToServerNode2.getBranchProbability()) {
                    repositoryUpdater.updateBranchProbability(probBranchTransitionToServerNode1, ONE_BRANCH_TRANSITION_PROBABILITY);
                    repositoryUpdater.updateBranchProbability(probBranchTransitionToServerNode2, ZERO_BRANCH_TRANSITION_PROBABILITY);
                }
                return true;
            }
            if (serverNode2State.equals(PerformabilityStrategyConstants.NODE_STATE_UNAVAILABLE)) {
                repositoryUpdater.updateBranchProbability(probBranchTransitionToServerNode1, ONE_BRANCH_TRANSITION_PROBABILITY);
                repositoryUpdater.updateBranchProbability(probBranchTransitionToServerNode2, ZERO_BRANCH_TRANSITION_PROBABILITY);
                return true;
            }
        }
        if (serverNode1State.equals(PerformabilityStrategyConstants.NODE_STATE_UNAVAILABLE)) {
            if (serverNode2State.equals(PerformabilityStrategyConstants.NODE_STATE_AVAILABLE)) {
                repositoryUpdater.updateBranchProbability(probBranchTransitionToServerNode1, ZERO_BRANCH_TRANSITION_PROBABILITY);
                repositoryUpdater.updateBranchProbability(probBranchTransitionToServerNode2, ONE_BRANCH_TRANSITION_PROBABILITY);
                return true;
            }
            if (serverNode2State.equals(PerformabilityStrategyConstants.NODE_STATE_UNAVAILABLE)) {
                repositoryUpdater.updateBranchProbability(probBranchTransitionToServerNode1, ZERO_BRANCH_TRANSITION_PROBABILITY);
                repositoryUpdater.updateBranchProbability(probBranchTransitionToServerNode2, ZERO_BRANCH_TRANSITION_PROBABILITY);
                return true;
            }
        }
        return false;
    }
    
}
