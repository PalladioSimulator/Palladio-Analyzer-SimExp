package org.palladiosimulator.simexp.pcm.examples.performability;

import java.util.List;

import org.apache.log4j.Logger;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.pcm.query.RepositoryModelLookup;
import org.palladiosimulator.pcm.query.ResourceEnvironmentModelLookup;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public abstract class AbstractNodeRecoveryStrategy<PCMInstance, A> implements NodeRecoveryStrategy<PCMInstance, A> {

    protected static final Logger LOGGER = Logger.getLogger(NodeRecoveryStrategy.class.getName());

    protected static final double ZERO_BRANCH_TRANSITION_PROBABILITY = 0.0;
    protected static final double ONE_BRANCH_TRANSITION_PROBABILITY = 1.0;
    protected static final double DEFAULT_BRANCH_TRANSITION_PROBABILITY = 0.5;

    protected final PerformabilityStrategyConfiguration strategyConfiguration;
    protected final RepositoryModelLookup repositoryLookup;
    protected final ResourceEnvironmentModelLookup resourceEnvLookup;
    protected final RepositoryModelUpdater repositoryUpdater;

    public AbstractNodeRecoveryStrategy(PerformabilityStrategyConfiguration strategyConfiguration,
            RepositoryModelLookup repositoryLookup, ResourceEnvironmentModelLookup resourceEnvLookup,
            RepositoryModelUpdater repositoryUpdater) {
        this.strategyConfiguration = strategyConfiguration;
        this.repositoryLookup = repositoryLookup;
        this.resourceEnvLookup = resourceEnvLookup;
        this.repositoryUpdater = repositoryUpdater;
    }

    @Override
    public abstract void execute(SelfAdaptiveSystemState<PCMInstance, A, List<InputValue<CategoricalValue>>> sasState,
            SharedKnowledge knowledge);

    protected ProbabilisticBranchTransition findProbabilisticBranchTransitionToServerNode(Repository repository,
            String loadBalancerBasicComponentId, String loadBalancerSeffOperationSignatureEntityName,
            String branchTransitionEntityName, String operationRequiredRoleName,
            String loadBalancerSeffBranchActionId) {
        RepositoryComponent loadBalancer = repositoryLookup.findBasicComponentById(repository,
                loadBalancerBasicComponentId);
        if (loadBalancer == null) {
            return null;
        }
        ResourceDemandingSEFF rdSeff = (ResourceDemandingSEFF) repositoryLookup
            .findSeffOfComponentByOperationSignature(loadBalancer, loadBalancerSeffOperationSignatureEntityName);
        BranchAction rdSeffBranchAction = repositoryLookup.findSeffBranchActionById(rdSeff,
                loadBalancerSeffBranchActionId);
        ProbabilisticBranchTransition branchTransition = repositoryLookup
            .findSeffProbabilisticBranchTransitionByEntityName(rdSeffBranchAction, branchTransitionEntityName);
        boolean isReference = repositoryLookup.isProbabilisticBranchTransitionExternalCallActionTo(branchTransition,
                operationRequiredRoleName);
        if (isReference) {
            return branchTransition;
        }
        return null;
    }

    protected void logMsg(String node1EntiyName, String node2EntiyName, String node1Id, String node2Id,
            String node1State, String node2State, double probBranchTransitionToServerNode1,
            double probBranchTransitionToServerNode2) {
        LOGGER.debug(String.format(
                "Performed node recovery - (enityName,id,state,branch probability): [%s,%s,%s,%s], [%s,%s,%s, %s]",
                node1EntiyName, node1Id, node1State, probBranchTransitionToServerNode1, node2EntiyName, node2Id,
                node2State, probBranchTransitionToServerNode2));
    }

}
