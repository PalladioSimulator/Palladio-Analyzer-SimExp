package org.palladiosimulator.pcm.query;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.pcm.allocation.Allocation;
import org.palladiosimulator.pcm.allocation.AllocationContext;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.repository.RepositoryComponent;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

public class AllocationModelLookup {
    
    
    
    public AllocationContext findAllocationContextById(Allocation allocation, String allocationContextId) {
        EList<AllocationContext> allocationContexts_Allocation = allocation.getAllocationContexts_Allocation();
        for (AllocationContext allocationContext : allocationContexts_Allocation) {
            if (allocationContext.getId().equals(allocationContextId)) {
                return allocationContext;
            }
        }
        return null;
    }

    public AllocationContext findAllocationContextByEntityName(Allocation allocation, String allocationEntityName) {
        EList<AllocationContext> allocationContexts_Allocation = allocation.getAllocationContexts_Allocation();
        for (AllocationContext allocationContext : allocationContexts_Allocation) {
            if (allocationContext.getEntityName().equals(allocationEntityName)) {
                return allocationContext;
            }
        }
        return null;
    }
    
    
    public AllocationContext findAllocationContextByAllocatedResourceContainerId(Allocation allocation, String resourceContaineId) {
        EList<AllocationContext> allocationContexts_Allocation = allocation.getAllocationContexts_Allocation();
        for (AllocationContext allocationContext : allocationContexts_Allocation) {
            if (allocationContext.getResourceContainer_AllocationContext().getId().equals(resourceContaineId)) {
                return allocationContext;
            }
        }
        return null;
    }
    
    
    public AssemblyContext findAssemblyContextById(Allocation allocation, String allocationContextId) {
        EList<AllocationContext> allocationContexts_Allocation = allocation.getAllocationContexts_Allocation();
        for (AllocationContext allocationContext : allocationContexts_Allocation) {
            if (allocationContext.getId().equals(allocationContextId)) {
                AssemblyContext assemblyContext = allocationContext.getAssemblyContext_AllocationContext();
                return assemblyContext;
            }
        }
        return null;
    }

    
    public AssemblyContext findAssemblyContextByEntityName(Allocation allocation, String allocationEntityName) {
        EList<AllocationContext> allocationContexts_Allocation = allocation.getAllocationContexts_Allocation();
        for (AllocationContext allocationContext : allocationContexts_Allocation) {
            if (allocationContext.getEntityName().equals(allocationEntityName)) {
                AssemblyContext assemblyContext = allocationContext.getAssemblyContext_AllocationContext();
                return assemblyContext;
            }
        }
        return null;
    }

    
    public AssemblyContext findAssemblyContextByAllocatedResourceContainerId(Allocation allocation, String resourceContaineId) {
        EList<AllocationContext> allocationContexts_Allocation = allocation.getAllocationContexts_Allocation();
        for (AllocationContext allocationContext : allocationContexts_Allocation) {
            if (allocationContext.getResourceContainer_AllocationContext().getId().equals(resourceContaineId)) {
                AssemblyContext assemblyContext = allocationContext.getAssemblyContext_AllocationContext();
                return assemblyContext;
            }
        }
        return null;
    }
    
    /** allocated resource container from resource environment */
    public ResourceContainer findAllocatedResoureContainerByAllocationCtx(AllocationContext allocCtx) {
        return allocCtx.getResourceContainer_AllocationContext();
    }
    
    /** allocated component from repository */
    public RepositoryComponent findRepositoryComonentByAssemblyCtx(AssemblyContext assemblyContxt) {
        RepositoryComponent encapsulatedComponent = assemblyContxt.getEncapsulatedComponent__AssemblyContext();
        return encapsulatedComponent;
    }
}
