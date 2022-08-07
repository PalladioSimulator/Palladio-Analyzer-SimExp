package org.palladiosimulator.simexp.workflow.jobs;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.palladiosimulator.core.simulation.SimulationExecution;
import org.palladiosimulator.simexp.dsl.kmodel.KmodelStandaloneSetup;
import org.palladiosimulator.simexp.dsl.kmodel.kmodel.Kmodel;
import org.palladiosimulator.simexp.pcm.examples.loadbalancing.LoadBalancingSimulationExecutor.LoadBalancingSimulationExecutorFactory;
import org.palladiosimulator.simexp.workflow.config.SimExpWorkflowConfiguration;

import com.google.inject.Injector;

import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.IBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;
import de.uka.ipd.sdq.workflow.mdsd.blackboard.MDSDBlackboard;

public class PcmExperienceSimulationJob implements IBlackboardInteractingJob<MDSDBlackboard> {

    private static final Logger LOGGER = Logger.getLogger(PcmExperienceSimulationJob.class.getName());

    private final SimExpWorkflowConfiguration config;
    private final LoadBalancingSimulationExecutorFactory loadBalancingSimulationExecutorFactory;

    private MDSDBlackboard blackboard;

    public PcmExperienceSimulationJob(SimExpWorkflowConfiguration config,
            LoadBalancingSimulationExecutorFactory loadBalancingSimulationExecutorFactory) {
        this.config = config;
        this.loadBalancingSimulationExecutorFactory = loadBalancingSimulationExecutorFactory;
    }

    @Override
    public void execute(IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
        LOGGER.info("**** PcmExperienceSimulationJob.execute ****");

        try {
            URI uri = URI.createURI(config.getKmodelFile());
            Kmodel kmodel = loadModel(uri);
            // TODO: add selection to switch between regular and FT loadBalancing
            SimulationExecution simExecutor = loadBalancingSimulationExecutorFactory.create(kmodel);

            simExecutor.execute();
            simExecutor.evaluate();
        } catch (IOException e) {
            throw new JobFailedException(e.getMessage(), e);
        }

        LOGGER.info("**** PcmExperienceSimulationJob.execute - Done.****");
    }

    private Kmodel loadModel(URI kmodelUri) throws IOException {
        KmodelStandaloneSetup.doSetup();
        ResourceSet resourceSet = new ResourceSetImpl();
        Resource resource = resourceSet.getResource(kmodelUri, true);
        EList<EObject> contents = resource.getContents();
        Kmodel model = (Kmodel) contents.get(0);
        return model;
    }

    @Override
    public void cleanup(IProgressMonitor monitor) throws CleanupFailedException {
        /**
         * 
         * nothing to do here
         * 
         */
    }

    @Override
    public String getName() {
        return this.getClass()
            .getCanonicalName();
    }

    @Override
    public void setBlackboard(MDSDBlackboard blackboard) {
        this.blackboard = blackboard;

    }

}
