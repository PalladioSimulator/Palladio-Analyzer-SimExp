package org.palladiosimulator.simexp.pcm.examples.udacitychallenge.reliability;

import org.eclipse.emf.common.util.URI;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicBehaviourExtension;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicBehaviourRepository;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicmodelPackage;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundProbabilisticNetwork;
import org.palladiosimulator.envdyn.environment.staticmodel.ProbabilisticModelRepository;
import org.palladiosimulator.envdyn.environment.staticmodel.StaticmodelPackage;

import de.uka.ipd.sdq.workflow.mdsd.blackboard.ResourceSetPartition;

public class UdacityEnvironmentLoader {
	
	private final static String ROBOT_COGNITION_PATH = "/org.palladiosimulator.simexp.pcm.examples.udacitychallenge";
	private final static String DYNAMIC_MODEL_EXTENSION = "dynamicmodel";
	private final static String DBN_FILE = String.format("%1s/%2s.%3s", ROBOT_COGNITION_PATH,
			"AutonomousSystemEnvironmentalDynamics", DYNAMIC_MODEL_EXTENSION);
	private final static URI DBN_URI = URI.createPlatformResourceURI(DBN_FILE, true);

	public static DynamicBayesianNetwork load() {
		var partition = new ResourceSetPartition();
		partition.loadModel(DBN_URI);
		partition.resolveAllProxies();

		BayesianNetwork bn = new BayesianNetwork(null, loadGroundProbabilisticNetwork(partition));
		return new DynamicBayesianNetwork(null, bn, loadDynamicBehaviourExtension(partition));
	}

	private static GroundProbabilisticNetwork loadGroundProbabilisticNetwork(ResourceSetPartition partition) {
		var repo = (ProbabilisticModelRepository) partition
				.getElement(StaticmodelPackage.eINSTANCE.getProbabilisticModelRepository()).get(0);
		return repo.getModels().get(0);
	}

	private static DynamicBehaviourExtension loadDynamicBehaviourExtension(ResourceSetPartition partition) {
		var repo = (DynamicBehaviourRepository) partition
				.getElement(DynamicmodelPackage.eINSTANCE.getDynamicBehaviourRepository()).get(0);
		return repo.getExtensions().get(0);
	}
}
