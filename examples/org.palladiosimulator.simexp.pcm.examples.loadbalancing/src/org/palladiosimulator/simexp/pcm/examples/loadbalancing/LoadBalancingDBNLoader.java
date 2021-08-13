package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

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

public enum LoadBalancingDBNLoader implements IManager, IDynamicBayesianNetworkLoader {
    INSTANCE;

	private final static String LOAD_BALANCER_PATH = "/org.palladiosimulator.simexp.pcm.examples.loadbalancer";
	private final static String STATIC_MODEL_EXTENSION = "staticmodel";
	private final static String DYNAMIC_MODEL_EXTENSION = "dynamicmodel";
	private final static String BN_FILE = String.format("%1s/%2s.%3s", LOAD_BALANCER_PATH,
			"LoadBalancerNonTemporalEnvironment", STATIC_MODEL_EXTENSION);
	private final static String DBN_FILE = String.format("%1s/%2s.%3s", LOAD_BALANCER_PATH,
			"LoadBalancerEnvironmentalDynamics", DYNAMIC_MODEL_EXTENSION);
	private final static URI DBN_URI = URI.createPlatformResourceURI(DBN_FILE, true);
	private final static URI BN_URI = URI.createPlatformResourceURI(BN_FILE, true);
	
	private final ResourceSet resourceSet = new ResourceSetImpl();
	
	private LoadBalancingDBNLoader() {
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		resourceSet.getPackageRegistry().put(TemplatevariablePackage.eNS_URI, TemplatevariablePackage.eINSTANCE);
	}

	public void persist(EObject eObj, String path) {
		Resource resource = resourceSet.createResource(URI.createFileURI(path));
		resource.getContents().add(eObj);
		try {
			resource.save(Collections.EMPTY_MAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public DynamicBayesianNetwork loadDBN() {
		BayesianNetwork bn = new BayesianNetwork(null, loadGroundProbabilisticNetwork());
		return new DynamicBayesianNetwork(null, bn, loadDynamicBehaviourExtension());
	}

	private DynamicBehaviourExtension loadDynamicBehaviourExtension() {
		return DynamicBehaviourExtension.class.cast(resourceSet.getResource(DBN_URI, true).getContents().get(0));
	}

	public GroundProbabilisticNetwork loadGroundProbabilisticNetwork() {
		return GroundProbabilisticNetwork.class.cast(resourceSet.getResource(BN_URI, true).getContents().get(0));
	}

	public DynamicBayesianNetwork loadOrGenerateDBN(Experiment exp) {
		try {
			return loadDBN();
		} catch (Exception e) {
			return generateDBN(exp);
		}
	}

	private DynamicBayesianNetwork generateDBN(Experiment exp) {
		TemplateVariableDefinitions templates = loadTemplates();

		BayesianNetwork bn = null;
		try {
			bn = new BayesianNetwork(null, loadGroundProbabilisticNetwork());
		} catch (Exception e) {
			bn = generateBN(templates, exp);
			persist(bn.get(), BN_FILE);
		}

		DynamicBayesianNetwork dbn = new DynamicBayesianNetworkGenerator(templates)
				.createProbabilisticNetwork(bn.get());

		persist(dbn.getDynamics(), DBN_FILE);

		return dbn;
	}

	private BayesianNetwork generateBN(TemplateVariableDefinitions templates, Experiment exp) {
		ResourceSet appliedModels = new ResourceSetImpl();
		appliedModels.getResources().add(exp.getInitialModel().getUsageModel().eResource());
		return new BayesianNetworkGenerator(templates).generate(appliedModels);
	}

	private TemplateVariableDefinitions loadTemplates() {
		List<TemplateVariableDefinitions> result = ExperimentProvider.get().getExperimentRunner()
				.getWorkingPartition()
				.getElement(TemplatevariablePackage.eINSTANCE.getTemplateVariableDefinitions());
		if (result.isEmpty()) {
			// TODO exception handling
			throw new RuntimeException("There are no templates.");
		}
		return result.get(0);
	}

    @Override
    public ResourceSet getResourceSet() {
        return resourceSet;
    }
}
