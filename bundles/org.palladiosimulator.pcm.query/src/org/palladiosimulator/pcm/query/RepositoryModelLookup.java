package org.palladiosimulator.pcm.query;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.repository.OperationRequiredRole;
import org.palladiosimulator.pcm.repository.Repository;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.repository.Signature;
import org.palladiosimulator.pcm.seff.AbstractAction;
import org.palladiosimulator.pcm.seff.AbstractBranchTransition;
import org.palladiosimulator.pcm.seff.BranchAction;
import org.palladiosimulator.pcm.seff.ExternalCallAction;
import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.pcm.seff.ResourceDemandingSEFF;
import org.palladiosimulator.pcm.seff.ServiceEffectSpecification;
import org.palladiosimulator.pcm.seff.StartAction;
import org.palladiosimulator.pcm.seff.StopAction;

public class RepositoryModelLookup {
    
    
    public Repository findRepositoryById(List<Repository> repositories, String repositoryId) {
        for (Repository repository : repositories) {
            if (repository.getId().equals(repositoryId)) {
                return repository;
            }
        }
        return null;
    }

    
    public Repository findRepositoryByEntityName(List<Repository> repositories, String repositoryEntityName) {
        for (Repository repository : repositories) {
            if (repository.getEntityName().equals(repositoryEntityName)) {
                return repository;
            }
        }
        return null;
    }
    
    
    public RepositoryComponent findBasicComponentById(Repository repository, String basicComponentId) {
        EList<RepositoryComponent> components__Repository = repository.getComponents__Repository();
        for (RepositoryComponent repositoryComponent : components__Repository) {
            repositoryComponent.getId().equals(basicComponentId);{
                return repositoryComponent;
            }
        }
        return null;
    }

    
    public RepositoryComponent findBasicComponentByEntityName(Repository repository, String basicComponentEntityName) {
        EList<RepositoryComponent> components__Repository = repository.getComponents__Repository();
        for (RepositoryComponent repositoryComponent : components__Repository) {
            repositoryComponent.getEntityName().equals(basicComponentEntityName);{
                return repositoryComponent;
            }
        }
        return null;
    }
    
    
    public ServiceEffectSpecification findSeffOfComponentByOperationSignature(RepositoryComponent component, String seffOperationSignatureEntityName) {
        BasicComponent basicComponent = (BasicComponent) component;
        EList<ServiceEffectSpecification> serviceEffectSpecificationsOfBasicComponent = basicComponent.getServiceEffectSpecifications__BasicComponent();
        
        for (ServiceEffectSpecification serviceEffectSpecification : serviceEffectSpecificationsOfBasicComponent) {
            Signature signature = serviceEffectSpecification.getDescribedService__SEFF();
            if(signature.getEntityName().equals(seffOperationSignatureEntityName)) {
                return serviceEffectSpecification;
            }
        }
        return null;
    }
    
    public BranchAction findSeffBranchActionById(ResourceDemandingSEFF seff, String branchActionId) {
        EList<AbstractAction> actions = seff.getSteps_Behaviour();
        for (AbstractAction action : actions) {
            if (action instanceof BranchAction) {
                BranchAction branchAction = (BranchAction) action;
                if (branchAction.getId().equals(branchActionId)) {
                    return branchAction;
                }
            }
        }
        return null;
    }
    
    
    public BranchAction findSeffBranchActionByEntityName(ResourceDemandingSEFF seff, String seffEntityName) {
        EList<AbstractAction> actions = seff.getSteps_Behaviour();
        for (AbstractAction action : actions) {
            if (action instanceof BranchAction) {
                BranchAction branchAction = (BranchAction) action;
                if (branchAction.getEntityName().equals(seffEntityName)) {
                    return branchAction;
                }
            }
        }
        return null;
    }
    
    public ProbabilisticBranchTransition findSeffProbabilisticBranchTransitionById(BranchAction branchAction, String transitionId) {
        AbstractAction nextAction = branchAction;
        while (nextAction != null) {
            if (nextAction instanceof StopAction) {
                break;
            }
            if(nextAction instanceof ProbabilisticBranchTransition) {
                ProbabilisticBranchTransition probBranchTransition = (ProbabilisticBranchTransition) nextAction;
                if (probBranchTransition.getId().equals(transitionId)) {
                    return probBranchTransition;
                }
            }
            nextAction = nextAction.getSuccessor_AbstractAction();
        }
        return null;
    }

    public ProbabilisticBranchTransition findSeffProbabilisticBranchTransitionByEntityName(BranchAction branchAction, String transitionEntityName) {
        BranchAction nextAction = branchAction;
        EList<AbstractBranchTransition> branches_Branch = nextAction.getBranches_Branch();
        for (AbstractBranchTransition abstractBranchTransition : branches_Branch) {
            if (abstractBranchTransition instanceof ProbabilisticBranchTransition) {
                ProbabilisticBranchTransition probBranchTransition = (ProbabilisticBranchTransition) abstractBranchTransition;
                if (probBranchTransition.getEntityName().equals(transitionEntityName)) {
                    return probBranchTransition;
                }
            }
        }
        return null;
    }
    
    
    
    public boolean isProbabilisticBranchTransitionExternalCallActionTo(ProbabilisticBranchTransition branchTransition, String operationRequiredRoleName) {
        AbstractAction nextAction = null;
        /**
         * NOTE: branchTransition.getBranchBehaviour_BranchTransition().getSteps_Behaviour() returns an unsorted list
         * i.e. we need to identify the StartAction from this list in order to traverse the remaining actions
         * */
        EList<AbstractAction> branchTransitionStepsBehaviour = branchTransition.getBranchBehaviour_BranchTransition().getSteps_Behaviour();
        for (AbstractAction abstractAction : branchTransitionStepsBehaviour) {
            if (abstractAction instanceof StartAction) {
                nextAction = abstractAction;
                break;
            }
        }
        
        while (nextAction != null) {
            if (nextAction instanceof StopAction) {
                break;
            }
            if (nextAction instanceof ExternalCallAction) {
                ExternalCallAction externalCallAction = (ExternalCallAction) nextAction;
                OperationRequiredRole roleExternalService = externalCallAction.getRole_ExternalService();
                if (roleExternalService.getEntityName().equals(operationRequiredRoleName)) {
                    return true;
                }
            }
            nextAction = nextAction.getSuccessor_AbstractAction();
        }
        return false;
    }
    

}
