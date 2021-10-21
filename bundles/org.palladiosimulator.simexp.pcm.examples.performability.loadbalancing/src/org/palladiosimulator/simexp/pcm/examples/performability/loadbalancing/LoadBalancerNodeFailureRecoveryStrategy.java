package org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing;

import org.apache.log4j.Logger;
import org.palladiosimulator.pcm.query.RepositoryModelLookup;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.simexp.core.state.ArchitecturalConfiguration;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.pcm.examples.performability.NodeRecoveryStrategy;
import org.palladiosimulator.simexp.pcm.examples.performability.PerformabilityStrategyConfiguration;
import org.palladiosimulator.solver.models.PCMInstance;

public class LoadBalancerNodeFailureRecoveryStrategy implements NodeRecoveryStrategy {
    

    private static final Logger LOGGER = Logger.getLogger(LoadBalancerNodeFailureRecoveryStrategy.class.getName());

    private static final String NODE_STATE_UNAVAILABLE = "unavailable";
    private static final String NODE_STATE_AVAILABLE = "available";

    private static final double ZERO_BRANCH_TRANSITION_PROBABILITY= 0.0;
    private static final double ONE_BRANCH_TRANSITION_PROBABILITY= 1.0;
    private static final double DEFAULT_BRANCH_TRANSITION_PROBABILITY = 0.5;
    
    private final String loadBalancerId = "_xISeMAEpEeS7FKokKTKFow";
    private final String loadBalancerEntityName = "LoadBalancer";
    private final String loadBalancerSeffBranchActionId = "_m2PcEAEoEeS7FKokKTKFow";
    private final String serverNodeOneId = "_yaTfsAEpEeS7FKokKTKFow";
    private final String serverNodeOneEntityName = "ServerNode1";
    private final String serverNodeTwoId = "_3uVlIAEpEeS7FKokKTKFow";
    private final String serverNodeTwoEntityName = "ServerNode2";

    private final PerformabilityStrategyConfiguration strategyConfiguration;
    private final RepositoryModelLookup repositoryLookup;
    private final RepositoryModelUpdater repositoryUpdater;
    
    public LoadBalancerNodeFailureRecoveryStrategy(PerformabilityStrategyConfiguration strategyConfiguration
            , RepositoryModelLookup repositoryLookup, RepositoryModelUpdater repositoryUpdater) {
        this.strategyConfiguration = strategyConfiguration;
        this.repositoryLookup = repositoryLookup;
        this.repositoryUpdater = repositoryUpdater;
    }
    

    @Override
    public void execute(SelfAdaptiveSystemState<?> sasState, SharedKnowledge knowledge) {
        LOGGER.info(String.format("'EXECUTE' apply reconfiguration 'nodeRecovery' workaround %s ", LoadBalancerNodeFailureRecoveryStrategy.class.getName()));
        
        String serverNode1State = knowledge.getValue(serverNodeOneId).get().toString();
        String serverNode2State = knowledge.getValue(serverNodeTwoId).get().toString();
        
        LOGGER.info(String.format("Knowledge: [%s,%s], [%s,&%s]", serverNodeOneId, serverNode1State, serverNodeTwoId, serverNode2State));
        
        ArchitecturalConfiguration<PCMInstance> architecturalConfig = (ArchitecturalConfiguration<PCMInstance>) sasState.getArchitecturalConfiguration();
        PCMInstance pcmInstance = architecturalConfig.getConfiguration();
        Repository defaultRepository = repositoryLookup.findRepositoryByEntityName(pcmInstance.getRepositories(), "defaultRepository");
        
        ProbabilisticBranchTransition probBranchTransitionToServerNode1 = findProbabilisticBranchTransitionToServerNode(
                defaultRepository
                , loadBalancerId, "processRequest", "delegateToServer1", "AC_Server1");
        ProbabilisticBranchTransition probBranchTransitionToServerNode2 = findProbabilisticBranchTransitionToServerNode(
                defaultRepository
                , loadBalancerId, "processRequest", "delegateToServer2", "AC_Server2");

        if (serverNode1State.equals(NODE_STATE_UNAVAILABLE) && serverNode2State.equals(NODE_STATE_UNAVAILABLE)) {
            repositoryUpdater.updateBranchProbability(probBranchTransitionToServerNode1, ZERO_BRANCH_TRANSITION_PROBABILITY);
            repositoryUpdater.updateBranchProbability(probBranchTransitionToServerNode2, ZERO_BRANCH_TRANSITION_PROBABILITY);
            LOGGER.debug(String.format("All nodes are unavailable. Set branch transition to probability %s.", ZERO_BRANCH_TRANSITION_PROBABILITY));
        } else  if (serverNode1State.equals(NODE_STATE_AVAILABLE) && serverNode2State.equals(NODE_STATE_AVAILABLE)) {
            /** how to restore the branch transition probabilities if a node becomes available again is use case specific*/
            if ( ZERO_BRANCH_TRANSITION_PROBABILITY == probBranchTransitionToServerNode1.getBranchProbability()
                 || ZERO_BRANCH_TRANSITION_PROBABILITY == probBranchTransitionToServerNode2.getBranchProbability()) {
                repositoryUpdater.updateBranchProbability(probBranchTransitionToServerNode1, DEFAULT_BRANCH_TRANSITION_PROBABILITY);
                repositoryUpdater.updateBranchProbability(probBranchTransitionToServerNode2, DEFAULT_BRANCH_TRANSITION_PROBABILITY);
                LOGGER.debug(String.format("All nodes are available. Restored branch transition to default probability %s.", DEFAULT_BRANCH_TRANSITION_PROBABILITY));
            }
        } else if (serverNode1State.equals(NODE_STATE_UNAVAILABLE) && serverNode2State.equals(NODE_STATE_AVAILABLE)) {
            repositoryUpdater.updateBranchProbability(probBranchTransitionToServerNode1, ZERO_BRANCH_TRANSITION_PROBABILITY);
            repositoryUpdater.updateBranchProbability(probBranchTransitionToServerNode2, ONE_BRANCH_TRANSITION_PROBABILITY);
            LOGGER.debug(String.format("Detected unavailable node %s. Changed branch transition to node %s to probability %s."
                    , serverNodeOneEntityName, serverNodeTwoEntityName, ONE_BRANCH_TRANSITION_PROBABILITY));
        } else if (serverNode1State.equals(NODE_STATE_AVAILABLE) && serverNode2State.equals(NODE_STATE_UNAVAILABLE)) {
            repositoryUpdater.updateBranchProbability(probBranchTransitionToServerNode1, ONE_BRANCH_TRANSITION_PROBABILITY);
            repositoryUpdater.updateBranchProbability(probBranchTransitionToServerNode2, ZERO_BRANCH_TRANSITION_PROBABILITY);
            LOGGER.debug(String.format("Detected unavailable node %s. Changed branch transition to node %s to probability %s."
                    , serverNodeTwoEntityName, serverNodeOneEntityName, ONE_BRANCH_TRANSITION_PROBABILITY));
        } else {
            LOGGER.debug("Failed to perform load balancer node failure recovery strategy. Unable to identify connected nodes.");
        }
    }
    
    
    private  ProbabilisticBranchTransition findProbabilisticBranchTransitionToServerNode(
            Repository repository, String loadBalancerBasicComponentId
            , String loadBalancerSeffOperationSignatureEntityName
            , String branchTransitionEntityName
            , String operationRequiredRoleName) {
        RepositoryComponent loadBalancer = repositoryLookup.findBasicComponentById(repository, loadBalancerBasicComponentId);
        if (loadBalancer == null) {
            return null;
        }
        ResourceDemandingSEFF rdSeff = (ResourceDemandingSEFF) repositoryLookup.findSeffOfComponentByOperationSignature(loadBalancer
                , loadBalancerSeffOperationSignatureEntityName);
        BranchAction rdSeffBranchAction = repositoryLookup.findSeffBranchActionById(rdSeff, loadBalancerSeffBranchActionId);
        ProbabilisticBranchTransition branchTransition = repositoryLookup.findSeffProbabilisticBranchTransitionByEntityName(rdSeffBranchAction, branchTransitionEntityName);
        boolean isReference = repositoryLookup.isProbabilisticBranchTransitionExternalCallActionTo(branchTransition, operationRequiredRoleName);
        if (isReference) {
            return branchTransition;
        }
        return null;
    }

}
