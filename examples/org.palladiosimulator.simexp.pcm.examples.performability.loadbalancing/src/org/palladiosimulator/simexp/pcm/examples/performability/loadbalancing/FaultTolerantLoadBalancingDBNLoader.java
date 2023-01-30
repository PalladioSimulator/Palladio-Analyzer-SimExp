package org.palladiosimulator.simexp.pcm.examples.performability.loadbalancing;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.api.generator.BayesianNetworkGenerator;
import org.palladiosimulator.envdyn.api.generator.DynamicBayesianNetworkGenerator;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicBehaviourExtension;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundProbabilisticNetwork;
import org.palladiosimulator.envdyn.environment.templatevariable.TemplateVariableDefinitions;
import org.palladiosimulator.envdyn.environment.templatevariable.TemplatevariablePackage;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;

import tools.mdsd.probdist.api.factory.IProbabilityDistributionFactory;

public class FaultTolerantLoadBalancingDBNLoader {

	private final static String LOAD_BALANCER_PATH = "/org.palladiosimulator.simexp.pcm.examples.loadbalancer.faulttolerant";
	private final static String LOAD_BALANCER_ENVIRONMENT_PATH = LOAD_BALANCER_PATH + "/environment";
	private final static String STATIC_MODEL_EXTENSION = "staticmodel";
	private final static String DYNAMIC_MODEL_EXTENSION = "dynamicmodel";
	private final static String BN_FILE = String.format("%1s/%2s.%3s", LOAD_BALANCER_ENVIRONMENT_PATH,
			"environmentalStatics", STATIC_MODEL_EXTENSION);
	private final static String DBN_FILE = String.format("%1s/%2s.%3s", LOAD_BALANCER_ENVIRONMENT_PATH,
			"environmentalDynamics", DYNAMIC_MODEL_EXTENSION);
	private final static URI DBN_URI = URI.createPlatformResourceURI(DBN_FILE, true);
	private final static URI BN_URI = URI.createPlatformResourceURI(BN_FILE, true);
	private final static ResourceSet RESOURCE_SET = new ResourceSetImpl();
	static {
		RESOURCE_SET.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		RESOURCE_SET.getPackageRegistry().put(TemplatevariablePackage.eNS_URI, TemplatevariablePackage.eINSTANCE);
	}

	private static void persist(EObject eObj, String path) {
		Resource resource = RESOURCE_SET.createResource(URI.createFileURI(path));
		resource.getContents().add(eObj);
		try {
			resource.save(Collections.EMPTY_MAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static DynamicBayesianNetwork loadDBN(IProbabilityDistributionFactory probabilityDistributionFactory) {
		BayesianNetwork bn = new BayesianNetwork(null, loadGroundProbabilisticNetwork(), probabilityDistributionFactory);
		return new DynamicBayesianNetwork(null, bn, loadDynamicBehaviourExtension(), probabilityDistributionFactory);
	}

	private static DynamicBehaviourExtension loadDynamicBehaviourExtension() {
		return DynamicBehaviourExtension.class.cast(RESOURCE_SET.getResource(DBN_URI, true).getContents().get(0));
	}

	public static GroundProbabilisticNetwork loadGroundProbabilisticNetwork() {
		return GroundProbabilisticNetwork.class.cast(RESOURCE_SET.getResource(BN_URI, true).getContents().get(0));
	}

	public static DynamicBayesianNetwork loadOrGenerateDBN(Experiment exp, IProbabilityDistributionFactory probabilityDistributionFactory) {
		try {
			return loadDBN(probabilityDistributionFactory);
		} catch (Exception e) {
			return generateDBN(exp, probabilityDistributionFactory);
		}
	}

	private static DynamicBayesianNetwork generateDBN(Experiment exp, IProbabilityDistributionFactory probabilityDistributionFactory) {
		TemplateVariableDefinitions templates = loadTemplates();

		BayesianNetwork bn = null;
		try {
			bn = new BayesianNetwork(null, loadGroundProbabilisticNetwork(), probabilityDistributionFactory);
		} catch (Exception e) {
			bn = generateBN(templates, exp, probabilityDistributionFactory);
			persist(bn.get(), BN_FILE);
		}

		DynamicBayesianNetwork dbn = new DynamicBayesianNetworkGenerator(templates)
				.createProbabilisticNetwork(bn.get(), probabilityDistributionFactory);

		persist(dbn.getDynamics(), DBN_FILE);

		return dbn;
	}

	private static BayesianNetwork generateBN(TemplateVariableDefinitions templates, Experiment exp, IProbabilityDistributionFactory probabilityDistributionFactory) {
		ResourceSet appliedModels = new ResourceSetImpl();
		appliedModels.getResources().add(exp.getInitialModel().getUsageModel().eResource());
		return new BayesianNetworkGenerator(templates).generate(appliedModels, probabilityDistributionFactory);
	}

	private static TemplateVariableDefinitions loadTemplates() {
		List<TemplateVariableDefinitions> result = ExperimentProvider.get().getExperimentRunner()
				.getWorkingPartition()
				.getElement(TemplatevariablePackage.eINSTANCE.getTemplateVariableDefinitions());
		if (result.isEmpty()) {
			// TODO exception handling
			throw new RuntimeException("There are no templates.");
		}
		return result.get(0);
	}
}
