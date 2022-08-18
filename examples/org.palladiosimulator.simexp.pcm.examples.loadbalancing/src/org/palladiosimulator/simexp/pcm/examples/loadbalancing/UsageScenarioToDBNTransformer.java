package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicBehaviourExtension;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicBehaviourRepository;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicmodelFactory;
import org.palladiosimulator.envdyn.environment.staticmodel.ProbabilisticModelRepository;
import org.palladiosimulator.envdyn.environment.staticmodel.StaticmodelFactory;
import org.palladiosimulator.envdyn.environment.templatevariable.TemplateFactor;
import org.palladiosimulator.envdyn.environment.templatevariable.TemplateVariableDefinitions;
import org.palladiosimulator.envdyn.environment.templatevariable.TemplatevariableFactory;
import org.palladiosimulator.envdyn.environment.templatevariable.TemporalRelation;
import org.palladiosimulator.pcm.usagemodel.UsageScenario;
import org.scaledl.usageevolution.Usage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import tools.descartes.dlim.Sequence;
import tools.descartes.dlim.generator.ModelEvaluator;
import tools.mdsd.probdist.distributionfunction.ComplexParameter;
import tools.mdsd.probdist.distributionfunction.DistributionfunctionFactory;
import tools.mdsd.probdist.distributionfunction.ParameterType;
import tools.mdsd.probdist.distributionfunction.ProbabilityDistribution;
import tools.mdsd.probdist.distributionfunction.ProbabilityDistributionFunctionRepository;
import tools.mdsd.probdist.distributionfunction.SimpleParameter;
import tools.mdsd.probdist.distributiontype.ProbabilityDistributionSkeleton;
import tools.mdsd.probdist.model.basic.loader.BasicDistributionTypesLoader;

public class UsageScenarioToDBNTransformer {
	
	private final static String LOAD_BALANCER_PATH = "/org.palladiosimulator.simexp.pcm.examples.loadbalancer";
	private final static String BN_FILE = String.format("%1s/%2s.%3s", LOAD_BALANCER_PATH,
			"LoadBalancingTOBEREPLACEDStativEnv", "staticmodel");
	private final static String DBN_FILE = String.format("%1s/%2s.%3s", LOAD_BALANCER_PATH,
			"LoadBalancingTOBEREPLACEDEnvDyn", "dynamicmodel");
	private final static String TEMPLATE_FILE = String.format("%1s/%2s.%3s", LOAD_BALANCER_PATH,
			"LoadBalancerTOBEREPLACEDTemplates", "templatevariable");
	private final static String DIST_FILE = String.format("%1s/%2s.%3s", LOAD_BALANCER_PATH,
			"LoadBalancerTOBEREPLACEDDist", "distributionfunction");
	private final static double SAMPLE_RATE = 1.0;
	
	private final TemplatevariableFactory templateFactory;
	private final StaticmodelFactory staticModelFactory;
	private final DynamicmodelFactory dynamicModelFactory;
	private final DistributionfunctionFactory distributionFactory; 
	private final ProbabilityDistributionSkeleton skeleton;
	
	private TemplateVariableDefinitions templateDefinitions;
	private ProbabilisticModelRepository bnRepository;
	private DynamicBehaviourRepository dbnExtension;
	private ProbabilityDistributionFunctionRepository distributionRepository;
	
	public UsageScenarioToDBNTransformer() {
		this.templateFactory = TemplatevariableFactory.eINSTANCE;
		this.staticModelFactory = StaticmodelFactory.eINSTANCE;
		this.dynamicModelFactory = DynamicmodelFactory.eINSTANCE;
		this.distributionFactory = DistributionfunctionFactory.eINSTANCE;
		this.skeleton = BasicDistributionTypesLoader.loadRepository().getDistributionFamilies().stream()
				.filter(each -> each.getEntityName().equals("MultinomialDistribution"))
				.findFirst()
				.get();
	}
	
	public DynamicBehaviourExtension transformAndPersist(Usage usage) {
		var result = transform(usage);
		
		persistAll(usage);
		
		return result;
	}
	
	public DynamicBehaviourExtension transform(Usage usage) {
		var samples = samplingUsageEvolution(usage.getLoadEvolution());
		return generateDBNFrom(samples, usage.getScenario());
	}

	private List<Double> samplingUsageEvolution(Sequence loadEvolution) {
		var evaluator = new ModelEvaluator(loadEvolution);
		var samples = Lists.<Double>newArrayList();
		for (double time = 0.0; time < loadEvolution.getFinalDuration(); time += SAMPLE_RATE) {
			var arrivalRate = evaluator.getArrivalRateAtTime(time);
			var interArrivalTime = 1 / arrivalRate;
			samples.add(interArrivalTime);
		}
		return samples;
	}
	
	private DynamicBehaviourExtension generateDBNFrom(List<Double> samples, UsageScenario scenario) {
		buildInitialStructure(scenario);
		buildDistributionsFrom(samples);
		complementInitialStructureWithDistributions();
		
		return dbnExtension.getExtensions().get(0);
	}

	private void buildInitialStructure(UsageScenario scenario) {
		buildTemplateDefinition();
		buildBayesianNetwork(scenario);
		buildDynamicsBayesianNetwork();
	}

	private void buildTemplateDefinition() {
		templateDefinitions = templateFactory.createTemplateVariableDefinitions();
		templateDefinitions.setEntityName("LoadBalancerTemplateDefs");
		
		var interArrivalTimeArg = templateFactory.createArgument();
		interArrivalTimeArg.setEntityName("OpenWorkload");
		
		var interArrivalTimeVar = templateFactory.createLogicalVariable();
		interArrivalTimeVar.setArgument(interArrivalTimeArg);
		
		var interArrivalTimeTemplate = templateFactory.createTemplateVariable();
		interArrivalTimeTemplate.setEntityName("InterArrivalTimeTemplate");
		interArrivalTimeTemplate.getSignature().add(interArrivalTimeVar);
				
		var staticInterArrivalTimeFactor = templateFactory.createProbabilisticTemplateFactor();
		staticInterArrivalTimeFactor.setEntityName("StaticInterArrivalTimeFactor");
		staticInterArrivalTimeFactor.setDistributionFamily(skeleton);
		staticInterArrivalTimeFactor.getScope().add(interArrivalTimeTemplate);
		
		var dynamicInterArrivalTimeFactor = templateFactory.createProbabilisticTemplateFactor();
		dynamicInterArrivalTimeFactor.setEntityName("DynamicInterArrivalTimeFactor");
		dynamicInterArrivalTimeFactor.setDistributionFamily(skeleton);
		dynamicInterArrivalTimeFactor.setTemporal(true);
		dynamicInterArrivalTimeFactor.getScope().add(interArrivalTimeTemplate);
		
		var dynamicInterArrivalTimeRelation = templateFactory.createPersistenceRelation();
		dynamicInterArrivalTimeRelation.setEntityName("InterArrivalTimeDynamics");
		dynamicInterArrivalTimeRelation.setInterfaceVariable(interArrivalTimeTemplate);
		
		templateDefinitions.getVariables().add(interArrivalTimeTemplate);
		templateDefinitions.getArguments().add(interArrivalTimeArg);
		templateDefinitions.getFactors().add(staticInterArrivalTimeFactor);
		templateDefinitions.getFactors().add(dynamicInterArrivalTimeFactor);
		templateDefinitions.getRelation().add(dynamicInterArrivalTimeRelation);
	}

	private void buildBayesianNetwork(UsageScenario scenario) {
		bnRepository = staticModelFactory.createProbabilisticModelRepository();
		bnRepository.setEntityName("InterArrivalTimeDistributionRepo");
		
		var groundModel = staticModelFactory.createGroundProbabilisticModel();
		groundModel.setEntityName("StaticInterArrivalTimeModel");
		groundModel.setInstantiatedFactor(retrieveNonTemporalFactor());
		
		var groundVariable = staticModelFactory.createGroundRandomVariable();
		groundVariable.setEntityName(VaryingInterarrivelRateProcess.WORKLOAD_VARIABLE);
		groundVariable.setDescriptiveModel(groundModel);
		groundVariable.setInstantiatedTemplate(templateDefinitions.getVariables().get(0));
		groundVariable.getAppliedObjects().add(scenario.getWorkload_UsageScenario());
		
		var localNetwork = staticModelFactory.createLocalProbabilisticNetwork();
		localNetwork.getGroundRandomVariables().add(groundVariable);
		
		var groundNetwork = staticModelFactory.createGroundProbabilisticNetwork();
		groundNetwork.setEntityName("StaticInterArrivalTimeNetwork");
		groundNetwork.getLocalModels().add(groundModel);
		groundNetwork.getLocalProbabilisticModels().add(localNetwork);
		
		bnRepository.getModels().add(groundNetwork);
	}

	private void buildDynamicsBayesianNetwork() {
		dbnExtension = dynamicModelFactory.createDynamicBehaviourRepository();
		dbnExtension.setEntityName("InterArrivalTimeDistributionDynamicsRepo");
	
		var temporalDynamic = dynamicModelFactory.createTemporalDynamic();
		temporalDynamic.setEntityName("DynamicInterArrivalTime");
		temporalDynamic.setInstantiatedFactor(retrieveTemporalFactor());
		
		var timeSlice = dynamicModelFactory.createInterTimeSliceInduction();
		timeSlice.setAppliedGroundVariable(bnRepository.getModels().get(0)
				.getLocalProbabilisticModels().get(0).getGroundRandomVariables().get(0));
		timeSlice.setDescriptiveModel(temporalDynamic);
		timeSlice.getTemporalStructure().add((TemporalRelation) templateDefinitions.getRelation().get(0));
		
		var dynamicBehaviour = dynamicModelFactory.createInductiveDynamicBehaviour();
		dynamicBehaviour.getLocalModels().add(temporalDynamic);
		dynamicBehaviour.getTimeSliceInductions().add(timeSlice);
		
		var dynamicBehaviourExt = dynamicModelFactory.createDynamicBehaviourExtension();
		dynamicBehaviourExt.setEntityName("InterArrivalTimeDistributionDynamics");
		dynamicBehaviourExt.setModel(bnRepository.getModels().get(0));
		dynamicBehaviourExt.setBehaviour(dynamicBehaviour);
		
		dbnExtension.getExtensions().add(dynamicBehaviourExt);
	}
	
	private void buildDistributionsFrom(List<Double> samples) {
		distributionRepository = distributionFactory.createProbabilityDistributionFunctionRepository();
		distributionRepository.setEntityName("DistributionRepo");
		
		var adjustedSamples = adjustSamples(samples); 
		
		var initialInterArrivalTimeParam = distributionFactory.createParameter();
		initialInterArrivalTimeParam.setEntityName("InitialInterArrivalTimeParam");
		initialInterArrivalTimeParam.setInstantiated(skeleton.getParamStructures().get(0));
		initialInterArrivalTimeParam.setRepresentation(createSimpleRepresentationFrom(adjustedSamples));
		
		var temporalInterArrivalTimeParam = distributionFactory.createParameter();
		temporalInterArrivalTimeParam.setEntityName("TemporalInterArrivalTimeParam");
		temporalInterArrivalTimeParam.setInstantiated(skeleton.getParamStructures().get(0));
		temporalInterArrivalTimeParam.setRepresentation(createComplexRepresentationFrom(adjustedSamples));
		
		var initialInterArrivalTimeDist = distributionFactory.createProbabilityDistribution();
		initialInterArrivalTimeDist.setEntityName("InitialInterArrivalTimeDist");
		initialInterArrivalTimeDist.setInstantiated(skeleton);
		initialInterArrivalTimeDist.getParams().add(initialInterArrivalTimeParam);
		
		var temporalInterArrivalTimeDist = distributionFactory.createProbabilityDistribution();
		temporalInterArrivalTimeDist.setEntityName("TemporalInterArrivalTimeDist");
		temporalInterArrivalTimeDist.setInstantiated(skeleton);
		temporalInterArrivalTimeDist.getParams().add(temporalInterArrivalTimeParam);
		
		distributionRepository.getParams().add(initialInterArrivalTimeParam);
		distributionRepository.getParams().add(temporalInterArrivalTimeParam);
		distributionRepository.getDistributions().add(initialInterArrivalTimeDist);
		distributionRepository.getDistributions().add(temporalInterArrivalTimeDist);
	}
	
	private List<Double> adjustSamples(List<Double> samples) {
		var distinctSamples = samples.stream().distinct().collect(Collectors.toList());
		if (distinctSamples.size() == 1) {
			return distinctSamples;
		}
		
		if (distinctSamples.size() == samples.size()) {
			return samples;
		}
		
		// Assuming a maximum of two distinct values
		var adjustedSamples = Lists.<Double>newArrayList();
		for (Double each : samples) {
			if (adjustedSamples.contains(each)) {
				adjustedSamples.add(each + 0.00001);
			} else {
				adjustedSamples.add(each);
			}
		}
		return adjustedSamples;
	}

	private void complementInitialStructureWithDistributions() {
		bnRepository.getModels().get(0).getLocalModels().get(0).setDistribution(retrieveInitialDist());
		dbnExtension.getExtensions().get(0).getBehaviour().getLocalModels().get(0).setDistributionFunction(retrieveTemporalDist());
	}
	
	private ComplexParameter createComplexRepresentationFrom(List<Double> samples) {
		var tabularCPD = distributionFactory.createTabularCPD();
		for (int i = 0; i < samples.size(); i++) {
			var tabularCPDEntry = distributionFactory.createTabularCPDEntry();
			tabularCPDEntry.getConditonals().add(samples.get(i).toString());

			SimpleParameter param;
			if (i + 1 == samples.size()) {
				param = createSimpleRepresentationFrom(samples, i);
			} else {
				param = createSimpleRepresentationFrom(samples, i + 1);
			}
			
			tabularCPDEntry.setEntry(param);
			tabularCPD.getCpdEntries().add(tabularCPDEntry);
		}
		return tabularCPD;
	}
	
	private SimpleParameter createSimpleRepresentationFrom(List<Double> samples) {
		return createSimpleRepresentationFrom(samples, 0);
	}
	
	private SimpleParameter createSimpleRepresentationFrom(List<Double> samples, int peak) {
		var param = distributionFactory.createSimpleParameter();
		param.setType(ParameterType.SAMPLESPACE);
		param.setValue(parseToDiracDistribution(samples, peak));
		return param;
	}
	
	private String parseToDiracDistribution(List<Double> samples, int peak) {
		Map<String, String> sampleSpace = Maps.newHashMap();
		for (int i = 0; i < samples.size(); i++) {
			sampleSpace.put(samples.get(i).toString(), Double.toString(i == peak ? 1.0 : 0.0));
		}
		return parseToString(sampleSpace);
	}

	private String parseToString(Map<String, String> sampleSpace) {
		var builder = new StringBuilder();
		for (String eachCategory : sampleSpace.keySet()) {
			builder.append(String.format("{%1s,%2s};", eachCategory, sampleSpace.get(eachCategory)));
		}
		return builder.deleteCharAt(builder.length() - 1).toString();
	}
	
	private TemplateFactor retrieveTemporalFactor() {
		return templateDefinitions.getFactors().stream()
				.filter(f -> f.isTemporal())
				.findFirst()
				.get();
	}
	
	private TemplateFactor retrieveNonTemporalFactor() {
		return templateDefinitions.getFactors().stream()
				.filter(f -> f.isTemporal() == false)
				.findFirst()
				.get();
	}
	
	private ProbabilityDistribution retrieveInitialDist() {
		return distributionRepository.getDistributions().stream()
				.map(ProbabilityDistribution.class::cast)
				.filter(d -> d.getParams().get(0).getRepresentation() instanceof SimpleParameter)
				.findFirst()
				.get();
	}
	
	private ProbabilityDistribution retrieveTemporalDist() {
		return distributionRepository.getDistributions().stream()
				.map(ProbabilityDistribution.class::cast)
				.filter(d -> d.getParams().get(0).getRepresentation() instanceof ComplexParameter)
				.findFirst()
				.get();
	}
	
	private void persistAll(Usage usage) {
		var usageEvolutionName = usage.getLoadEvolution().getName();
		
		var resolvedDistFile = DIST_FILE.replaceFirst("TOBEREPLACED", usageEvolutionName);
		URI DIST_URI = URI.createPlatformResourceURI(resolvedDistFile, true);
		
		var resolvedDBNFile = DBN_FILE.replaceFirst("TOBEREPLACED", usageEvolutionName);
		URI DBN_URI = URI.createPlatformResourceURI(resolvedDBNFile, true);
		
		var resolvedBNFile = BN_FILE.replaceFirst("TOBEREPLACED", usageEvolutionName);
		URI BN_URI = URI.createPlatformResourceURI(resolvedBNFile, true);
		
		var resolvedTemplateFile = TEMPLATE_FILE.replaceFirst("TOBEREPLACED", usageEvolutionName);
		URI TEMPLATE_URI = URI.createPlatformResourceURI(resolvedTemplateFile, true);
		
		persist(distributionRepository, DIST_URI);
		persist(templateDefinitions, TEMPLATE_URI);
		persist(bnRepository, BN_URI);
		persist(dbnExtension, DBN_URI);
	}
	
	private void persist(EObject eObj, URI uri) {
		Resource resource = new ResourceSetImpl().createResource(uri);
		resource.getContents().add(eObj);
		try {
			resource.save(Collections.EMPTY_MAP);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
