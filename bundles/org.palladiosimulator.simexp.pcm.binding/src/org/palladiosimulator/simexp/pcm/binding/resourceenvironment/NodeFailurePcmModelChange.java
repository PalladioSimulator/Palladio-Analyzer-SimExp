package org.palladiosimulator.simexp.pcm.binding.resourceenvironment;

import org.apache.commons.lang.StringUtils;
import org.palladiosimulator.analyzer.workflow.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.failuremodel.failurescenario.FailureScenario;
import org.palladiosimulator.failuremodel.failurescenario.FailurescenarioFactory;
import org.palladiosimulator.failuremodel.failuretype.Failure;
import org.palladiosimulator.failuremodel.failuretype.FailureTypeRepository;
import org.palladiosimulator.failuremodel.failuretype.FailuretypeFactory;
import org.palladiosimulator.failuremodel.failuretype.HWCrashFailure;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class NodeFailurePcmModelChange extends AbstractPcmModelChange {
    
    
    public NodeFailurePcmModelChange(String pcmAttributeName) {
        super(pcmAttributeName);
        // TODO Auto-generated constructor stub
    }

    @Override
    void applyChange(CategoricalValue value) {
        double serverNodeFailureRate = (StringUtils.equals("unavailable", value.get())) ? 1.0 : 0.0;
        
        /**
         * note: The current implementation assumes that, for each trajectory, a new experiment runner is created,
         * i.e. if a new experiment is started, the pcm model must be resetted to the original state; thus
         * we can not pass the experiment runner as constructor param, because it will be continuously updated
         * during the various experiment runs; if we would pass is as constructor param, we would not be able
         * to get the latest updated state of the pcm model: Intead we would always work on a stale state of
         * the pcm model
         * */
        PCMResourceSetPartition pcm = ExperimentProvider.get().getExperimentRunner().getWorkingPartition();

    }
    
    
    
    private void createServerNodeFailureScenario() {
        /**
         * 
         * if a node fails, we must create a failure scenario containing CrashHW node failures for all CPUs located
         * on the respective resource container.
         * The simulation must be initialized with the erroneous crashed resoure containers. This means
         * that the crash failures must be schedueld at simulation time t=0
         * 
         * */
    }
    
    private void createServerNodeFailureTypes() {
        
        FailuretypeFactory factory = FailuretypeFactory.eINSTANCE;
        
        FailureTypeRepository failureTypeRepo = factory.createFailureTypeRepository();

        HWCrashFailure hwCrashFailureType = factory.createHWCrashFailure();
        
    }

}
