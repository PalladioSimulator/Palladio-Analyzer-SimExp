package org.palladiosimulator.simexp.pcm.state.failure;

import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.failuremodel.failurescenario.FailureScenario;
import org.palladiosimulator.failuremodel.failurescenario.FailureScenarioRepository;
import org.palladiosimulator.failuremodel.failurescenario.FailurescenarioFactory;
import org.palladiosimulator.failuremodel.failurescenario.Occurrence;
import org.palladiosimulator.failuremodel.failurescenario.ProcessingResourceReference;
import org.palladiosimulator.failuremodel.failuretype.HWCrashFailure;
import org.palladiosimulator.pcm.core.CoreFactory;
import org.palladiosimulator.pcm.core.PCMRandomVariable;
import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;

public class NodeFailureStateCreator {

    private static FailurescenarioFactory factory = FailurescenarioFactory.eINSTANCE;
    private static CoreFactory coreFactory = CoreFactory.eINSTANCE;
    
    public FailureScenarioRepository createRepo() {
        FailureScenarioRepository repo = factory.createFailureScenarioRepository();
        return repo;
    }
    
    
    public void addScenario(FailureScenarioRepository repo, List<ResourceContainer> containers, HWCrashFailure failureType) {
        if (!containers.isEmpty()) {
            for (ResourceContainer resourceContainer : containers) {
                FailureScenario failureScenario = factory.createFailureScenario();
                failureScenario.setEntityName(failureType.getEntityName() + "at 0s point in time of " + resourceContainer.getEntityName());
                EList<ProcessingResourceSpecification> processingResources = resourceContainer.getActiveResourceSpecifications_ResourceContainer();
                EList<Occurrence> occurences = failureScenario.getOccurrences();
                for (ProcessingResourceSpecification processingResource : processingResources) {
                    Occurrence occurence = createFailureOccurenceForProcessingResource(processingResource);
                    occurence.setFailure(failureType);
                    occurences.add(occurence);
                }
                repo.getFailurescenarios().add(failureScenario);
            }
        }
    }

    
    private Occurrence createFailureOccurenceForProcessingResource(ProcessingResourceSpecification processingResource) {
        Occurrence failureOccurence = factory.createOccurrence();
        PCMRandomVariable simulatedAtPointInTime = coreFactory.createPCMRandomVariable();
        String simulatedAtPointInTimeValue = "0.0";
        simulatedAtPointInTime.setSpecification(simulatedAtPointInTimeValue);
        failureOccurence.setPointInTime(simulatedAtPointInTime);

        ProcessingResourceReference reference = factory.createProcessingResourceReference();  
        reference.setProcessingResource(processingResource);
        failureOccurence.setOrigin(reference);

        return failureOccurence;
    }
}
