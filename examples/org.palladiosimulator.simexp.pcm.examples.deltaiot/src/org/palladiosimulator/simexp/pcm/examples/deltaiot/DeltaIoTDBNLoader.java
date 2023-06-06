package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import java.io.IOException;
import java.util.Collections;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicBehaviourExtension;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundProbabilisticNetwork;
import org.palladiosimulator.envdyn.environment.templatevariable.TemplatevariablePackage;

public class DeltaIoTDBNLoader {

	private final static String DELTAIOT_PATH = "/org.palladiosimulator.envdyn.examples.deltaiot";
	private final static String STATIC_MODEL_EXTENSION = "staticmodel";
	private final static String DYNAMIC_MODEL_EXTENSION = "dynamicmodel";
	private final static String BN_FILE = String.format("%1s/model/%2s.%3s", DELTAIOT_PATH,
			"DeltaIoTNonTemporalEnvironment", STATIC_MODEL_EXTENSION);
	private final static String DBN_FILE = String.format("%1s/model/%2s.%3s", DELTAIOT_PATH,
			"DeltaIoTEnvironmentalDynamics", DYNAMIC_MODEL_EXTENSION);
	private final static URI DBN_URI = URI.createPlatformResourceURI(DBN_FILE, true);
	private final static URI BN_URI = URI.createPlatformResourceURI(BN_FILE, true);
	private final static ResourceSet RESOURCE_SET = new ResourceSetImpl();
	static {
		RESOURCE_SET.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new XMIResourceFactoryImpl());
		RESOURCE_SET.getPackageRegistry().put(TemplatevariablePackage.eNS_URI, TemplatevariablePackage.eINSTANCE);
	}

	public static void persist(EObject eObj, String path) {
		Resource resource = RESOURCE_SET.createResource(URI.createFileURI(path));
		resource.getContents().add(eObj);
		try {
			resource.save(Collections.EMPTY_MAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static DynamicBayesianNetwork loadDBN() {
//		BayesianNetwork bn = new BayesianNetwork(null, loadGroundProbabilisticNetwork());
//		return new DynamicBayesianNetwork(null, bn, loadDynamicBehaviourExtension());
		return null;
	}

//	private static DynamicBehaviourExtension loadDynamicBehaviourExtension() {
//		return loadDynamicBehaviourRepo().getExtensions().get(0);
//	}

	private static DynamicBehaviourExtension loadDynamicBehaviourExtension() {
		return DynamicBehaviourExtension.class.cast(RESOURCE_SET.getResource(DBN_URI, true).getContents().get(0));
	}

	public static GroundProbabilisticNetwork loadGroundProbabilisticNetwork() {
		return GroundProbabilisticNetwork.class.cast(RESOURCE_SET.getResource(BN_URI, true).getContents().get(0));
	}

}
