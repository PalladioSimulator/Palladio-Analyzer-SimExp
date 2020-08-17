package org.palladiosimulator.simexp.pcm.examples.hri;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicBehaviourExtension;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicBehaviourRepository;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundProbabilisticNetwork;
import org.palladiosimulator.envdyn.environment.staticmodel.ProbabilisticModelRepository;

public class RobotCognitionDBNLoader {

	private final static String ROBOT_COGNITION_PATH = "/org.palladiosimulator.dependability.ml.hri";
	private final static ResourceSet RESOURCE_SET = new ResourceSetImpl();
	private final static String STATIC_MODEL_EXTENSION = "staticmodel";
	private final static String DYNAMIC_MODEL_EXTENSION = "dynamicmodel";
	private final static String BN_FILE = String.format("%1s/%2s.%3s", ROBOT_COGNITION_PATH,
			"StaticRobotCognitionEnvironment", STATIC_MODEL_EXTENSION);
	private final static String DBN_FILE = String.format("%1s/%2s.%3s", ROBOT_COGNITION_PATH,
			"DynamicRobotCognitionEnvironment", DYNAMIC_MODEL_EXTENSION);
	private final static URI DBN_URI = URI.createPlatformResourceURI(DBN_FILE, true);
	private final static URI BN_URI = URI.createPlatformResourceURI(BN_FILE, true);

	public static DynamicBayesianNetwork load() {
		BayesianNetwork bn = new BayesianNetwork(null, loadGroundProbabilisticNetwork());
		return new DynamicBayesianNetwork(null, bn, loadDynamicBehaviourExtension());
	}

	public static GroundProbabilisticNetwork loadGroundProbabilisticNetwork() {
		var modelRepo = ProbabilisticModelRepository.class
				.cast(RESOURCE_SET.getResource(BN_URI, true).getContents().get(0));
		return modelRepo.getModels().get(0);
	}

	private static DynamicBehaviourExtension loadDynamicBehaviourExtension() {
		var dynBehaviourRepo = DynamicBehaviourRepository.class
				.cast(RESOURCE_SET.getResource(DBN_URI, true).getContents().get(0));
		return dynBehaviourRepo.getExtensions().get(0);
	}
}
