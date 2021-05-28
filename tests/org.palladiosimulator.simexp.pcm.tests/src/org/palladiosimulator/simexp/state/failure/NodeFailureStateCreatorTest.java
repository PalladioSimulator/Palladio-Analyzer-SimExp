package org.palladiosimulator.simexp.state.failure;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.Before;
import org.junit.Test;
import org.palladiosimulator.commons.emfutils.EMFCopyHelper;
import org.palladiosimulator.failuremodel.failurescenario.FailureScenario;
import org.palladiosimulator.failuremodel.failurescenario.FailureScenarioRepository;
import org.palladiosimulator.failuremodel.failurescenario.Occurrence;
import org.palladiosimulator.failuremodel.failurescenario.ProcessingResourceReference;
import org.palladiosimulator.failuremodel.failuretype.HWCrashFailure;
import org.palladiosimulator.pcm.resourceenvironment.ProcessingResourceSpecification;
import org.palladiosimulator.pcm.resourceenvironment.ResourceContainer;
import org.palladiosimulator.pcm.resourceenvironment.ResourceEnvironment;
import org.palladiosimulator.pcm.resourceenvironment.ResourceenvironmentFactory;

public class NodeFailureStateCreatorTest {

    private static ResourceenvironmentFactory resourceEnvFactory = ResourceenvironmentFactory.eINSTANCE;

    private NodeFailureStateCreator creator;
    private NodeFailureTypeCreator failureTypeCreator;

    @Before
    public void setUp() throws Exception {
        creator = new NodeFailureStateCreator();
        failureTypeCreator = new NodeFailureTypeCreator();
    }

    @Test
    public void testCreateNoFailureScenarioIfResourceContainersNotPresent() {
        FailureScenarioRepository repo = creator.create(ECollections.emptyEList(), null);
        assertEquals(0, repo.getFailurescenarios()
            .size());
    }

    @Test
    public void testCreateSingleNodeSingleCpuFailureScenario() {
        ResourceEnvironment resourceEnvironment = resourceEnvFactory.createResourceEnvironment();
        EList<ResourceContainer> containers = resourceEnvironment.getResourceContainer_ResourceEnvironment();

        ResourceContainer resoureContainer = createResourceContainer(1);
        resourceEnvironment.getResourceContainer_ResourceEnvironment()
            .add(resoureContainer);
        containers.add(resoureContainer);

        HWCrashFailure failureType = (HWCrashFailure) failureTypeCreator.create()
            .getFailuretypes()
            .get(0);
        FailureScenarioRepository repo = creator.create(containers, failureType);

        EList<FailureScenario> resultedFailureScenario = repo.getFailurescenarios();
        assertEquals("Failed to create single failure scenario for HW crash failure with single cpu resource container",
                1, resultedFailureScenario.size());
        assertEquals("Failed to create one HW crash failure for single cpu resource container", 1,
                resultedFailureScenario.get(0)
                    .getOccurrences()
                    .size());

        Occurrence resultedFailure = resultedFailureScenario.get(0)
            .getOccurrences()
            .get(0);
        ProcessingResourceSpecification expectedProcessingResourceSpecification = resoureContainer
            .getActiveResourceSpecifications_ResourceContainer()
            .get(0);

        testHelperAssertFailureOccurenceForCPU(resultedFailure, expectedProcessingResourceSpecification);
    }

    @Test
    public void testCreateSingleNodeMultipleCpuFailureScenario() throws Exception {
        ResourceEnvironment resourceEnvironment = resourceEnvFactory.createResourceEnvironment();
        EList<ResourceContainer> containers = resourceEnvironment.getResourceContainer_ResourceEnvironment();

        ResourceContainer resoureContainer = createResourceContainer(2);
        resourceEnvironment.getResourceContainer_ResourceEnvironment()
            .add(resoureContainer);
        containers.add(resoureContainer);

        HWCrashFailure failureType = (HWCrashFailure) failureTypeCreator.create()
            .getFailuretypes()
            .get(0);
        FailureScenarioRepository repo = creator.create(containers, failureType);

        EList<FailureScenario> resultedFailureScenario = repo.getFailurescenarios();

        // expected 1 failure scenario with 2 occurrences (one per CPU)
        EList<ProcessingResourceSpecification> processingResourceSpecs = resoureContainer
            .getActiveResourceSpecifications_ResourceContainer();
        Occurrence resultedFailure1 = resultedFailureScenario.get(0)
            .getOccurrences()
            .get(0);
        testHelperAssertFailureOccurenceForCPU(resultedFailure1, processingResourceSpecs.get(0));

        Occurrence resultedFailure2 = resultedFailureScenario.get(0)
            .getOccurrences()
            .get(1);
        testHelperAssertFailureOccurenceForCPU(resultedFailure2, processingResourceSpecs.get(1));
    }

    @Test
    public void testCreateMultipleNodeSingleCpuFailureScenario() throws Exception {
        ResourceEnvironment resourceEnvironment = resourceEnvFactory.createResourceEnvironment();
        EList<ResourceContainer> containers = resourceEnvironment.getResourceContainer_ResourceEnvironment();

        ResourceContainer resoureContainer = createResourceContainer(1);
        resourceEnvironment.getResourceContainer_ResourceEnvironment()
            .add(resoureContainer);
        containers.add(resoureContainer);
        ResourceContainer resoureContainer2 = createResourceContainer(1);
        resourceEnvironment.getResourceContainer_ResourceEnvironment()
            .add(resoureContainer2);
        containers.add(resoureContainer2);

        HWCrashFailure failureType = (HWCrashFailure) failureTypeCreator.create()
            .getFailuretypes()
            .get(0);
        FailureScenarioRepository repo = creator.create(containers, failureType);

        EList<FailureScenario> resultedFailureScenarios = repo.getFailurescenarios();

        // expected 2 failure scenarios with a single occurrence per cpu
        // server node 1
        EList<ProcessingResourceSpecification> processingResourceSpecs = resoureContainer
            .getActiveResourceSpecifications_ResourceContainer();
        Occurrence resultedFailureServer1 = resultedFailureScenarios.get(0)
            .getOccurrences()
            .get(0);
        testHelperAssertFailureOccurenceForCPU(resultedFailureServer1, processingResourceSpecs.get(0));

        // server node 2
        EList<ProcessingResourceSpecification> processingResourceSpecs2 = resoureContainer2
            .getActiveResourceSpecifications_ResourceContainer();
        Occurrence resultedFailureServer2 = resultedFailureScenarios.get(1)
            .getOccurrences()
            .get(0);
        testHelperAssertFailureOccurenceForCPU(resultedFailureServer2, processingResourceSpecs2.get(0));
    }

    @Test
    public void testCopier() throws IOException {
        ResourceEnvironment resourceEnvironment = resourceEnvFactory.createResourceEnvironment();
        EList<ResourceContainer> containers = resourceEnvironment.getResourceContainer_ResourceEnvironment();
        ResourceContainer resoureContainer = createResourceContainer(1);
        resourceEnvironment.getResourceContainer_ResourceEnvironment()
            .add(resoureContainer);
        containers.add(resoureContainer);
        HWCrashFailure failureType = (HWCrashFailure) failureTypeCreator.create()
            .getFailuretypes()
            .get(0);
        FailureScenarioRepository failureScenarioRepo = creator.create(containers, failureType);

        ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry()
            .getExtensionToFactoryMap()
            .put("failurescenario", new XMIResourceFactoryImpl());

        File tmpFile = File.createTempFile("serverNodeFailures", ".failurescenario");
        URI modelURI = URI.createFileURI(tmpFile.getAbsolutePath());

        // check if resSet contains resource
        Resource resource = resourceSet.getResource(modelURI, false);
        if (resource != null) {
            resourceSet.getResources()
                .remove(resource);
        }
        Resource initalResource = resourceSet.createResource(modelURI);
        initalResource.getContents()
            .clear();
        initalResource.getContents()
            .add(failureScenarioRepo);
        initalResource.save(Collections.EMPTY_MAP);
        resourceSet.getResources()
            .add(initalResource);

        List<EObject> modelCopy = EMFCopyHelper.deepCopyToEObjectList(resourceSet);
        
        assertEquals("Failed to copy resource set", 1, modelCopy.get(0).eContents().size());
    }

    private ResourceContainer createResourceContainer(int numberOfCpuProcessingResources) {
        ResourceContainer resoureContainer = resourceEnvFactory.createResourceContainer();
        for (int i = 0; i < numberOfCpuProcessingResources; i++) {
            ProcessingResourceSpecification cpu = resourceEnvFactory.createProcessingResourceSpecification();
            cpu.setResourceContainer_ProcessingResourceSpecification(resoureContainer);
        }
        return resoureContainer;
    }

    private void testHelperAssertFailureOccurenceForCPU(Occurrence resultedFailure,
            ProcessingResourceSpecification expectedProcessingResourceSpecification) {
        assertTrue("Failed to create occurence of failure type " + HWCrashFailure.class.getName(),
                resultedFailure.getFailure() instanceof HWCrashFailure);
        ProcessingResourceReference ref = (ProcessingResourceReference) resultedFailure.getOrigin();
        assertEquals("Failed to create failure for processingResource 'CPU'", expectedProcessingResourceSpecification,
                ref.getProcessingResource());
    }
}
