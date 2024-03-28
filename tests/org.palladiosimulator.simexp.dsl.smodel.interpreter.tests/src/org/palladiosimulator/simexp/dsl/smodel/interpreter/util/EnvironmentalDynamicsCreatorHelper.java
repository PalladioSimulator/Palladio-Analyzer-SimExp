package org.palladiosimulator.simexp.dsl.smodel.interpreter.util;

import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundProbabilisticModel;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundProbabilisticNetwork;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundRandomVariable;
import org.palladiosimulator.envdyn.environment.staticmodel.LocalProbabilisticNetwork;
import org.palladiosimulator.envdyn.environment.staticmodel.ProbabilisticModelRepository;
import org.palladiosimulator.envdyn.environment.staticmodel.StaticmodelFactory;
import org.palladiosimulator.envdyn.environment.templatevariable.Argument;
import org.palladiosimulator.envdyn.environment.templatevariable.LogicalVariable;
import org.palladiosimulator.envdyn.environment.templatevariable.ProbabilisticTemplateFactor;
import org.palladiosimulator.envdyn.environment.templatevariable.TemplateFactor;
import org.palladiosimulator.envdyn.environment.templatevariable.TemplateVariable;
import org.palladiosimulator.envdyn.environment.templatevariable.TemplateVariableDefinitions;
import org.palladiosimulator.envdyn.environment.templatevariable.TemplatevariableFactory;
import org.palladiosimulator.pcm.usagemodel.UsageModel;

import tools.mdsd.probdist.distributionfunction.ProbabilityDistribution;
import tools.mdsd.probdist.distributionfunction.ProbabilityDistributionFunctionRepository;
import tools.mdsd.probdist.distributiontype.ProbabilityDistributionRepository;
import tools.mdsd.probdist.distributiontype.ProbabilityDistributionSkeleton;

public class EnvironmentalDynamicsCreatorHelper {

    private final StaticmodelFactory staticEnvModelFactory = StaticmodelFactory.eINSTANCE;
    private static TemplatevariableFactory templateVarFactory = TemplatevariableFactory.eINSTANCE;

    private final PcmModelsCreatorHelper pcmModelsCreator;
    private final ProbabilityDistributionCreatorHelper probDistCreator;

    public EnvironmentalDynamicsCreatorHelper() {
        this.pcmModelsCreator = new PcmModelsCreatorHelper();
        this.probDistCreator = new ProbabilityDistributionCreatorHelper();
    }

    public EnvironmentalDynamicsTestModels createSimpleEnvDynamcisModels() {

        // model *.distributiontypes
        ProbabilityDistributionSkeleton probDistributionSkel = probDistCreator.createMultinomialDistributionType();
        ProbabilityDistributionRepository probDistRepo = probDistCreator
            .createProbabilityDistRepo(Arrays.asList(probDistributionSkel));

        // model *.distributionfunction
        ProbabilityDistributionFunctionRepository distributions = probDistCreator
            .createBasicProbabilityDistributionFunctions();
        ProbabilityDistribution multinomialDistribution = (ProbabilityDistribution) distributions.getDistributions()
            .get(0);

        // model *.templatevariable
        LogicalVariable logicalVar = createLogicalVariable("testLogicalVar");
        TemplateVariable templateVar = createTemplateVariable("testTemplateVar", false, logicalVar);
        List<TemplateVariable> templateVars = Arrays.asList(templateVar);

        ProbabilisticTemplateFactor staticFactor = createProbabilsiticTemplateFactor("testFactorStatic", templateVar,
                probDistRepo.getDistributionFamilies()
                    .get(0),
                false);
        ProbabilisticTemplateFactor factorDynamic = createProbabilsiticTemplateFactor("testFactorDynamic", templateVar,
                probDistRepo.getDistributionFamilies()
                    .get(0),
                true);
        List<ProbabilisticTemplateFactor> probabilisticTemplateFactors = Arrays.asList(staticFactor, factorDynamic);
        // persistence relation ???
        // dependence relation ???
        // timeSliceRelation ???
        Argument argument = createArgument("testArg");
        List<Argument> arguments = Arrays.asList(argument);

        TemplateVariableDefinitions templateVarDefinitions = createTemplateVarsDefinitionModel(templateVars,
                probabilisticTemplateFactors, arguments);

        // model *.staticmodel
        GroundProbabilisticModel gpModel = createGroundProbModel("testGPModel", multinomialDistribution, staticFactor);
        List<GroundProbabilisticModel> gpModels = Arrays.asList(gpModel);

        UsageModel usageModel = pcmModelsCreator.createBasicUsageScenario();
        GroundRandomVariable groundRandomVar = createGroundRandomVariable("testGRV", gpModel, templateVar, usageModel);
        List<GroundRandomVariable> grvs = Arrays.asList(groundRandomVar);
        GroundProbabilisticNetwork groundProbabilisticNetwork = createGroundProbNetwork(gpModels, grvs);
        ProbabilisticModelRepository staticEnvDynamcisModel = createStaticEnvDynamcisModel(groundProbabilisticNetwork);

        // model *.dynamicmodel
        EnvironmentalDynamicsTestModels testEnvDynModels = new EnvironmentalDynamicsTestModels("testSimulation",
                templateVarDefinitions, staticEnvDynamcisModel);
        return testEnvDynModels;
    }

//    public void createPerformanceAnalysisEnvDynamicModels() { }

    private void createDistributionFunctionModel() {

    }

    private TemplateVariableDefinitions createTemplateVarsDefinitionModel(List<TemplateVariable> templateVars,
            List<ProbabilisticTemplateFactor> probabilisticTemplateFactors, List<Argument> arguments) {
        TemplateVariableDefinitions templateVarDefinitions = templateVarFactory.createTemplateVariableDefinitions();
        templateVarDefinitions.getVariables()
            .addAll(templateVars);
        templateVarDefinitions.getArguments()
            .addAll(arguments);
        templateVarDefinitions.getFactors()
            .addAll(probabilisticTemplateFactors);
        return templateVarDefinitions;
    }

    private Argument createArgument(String entityName) {
        Argument argument = templateVarFactory.createArgument();
        argument.setEntityName(entityName);
        return argument;
    }

    private LogicalVariable createLogicalVariable(String argumentEntityName) {
        LogicalVariable logicalVarVariable = templateVarFactory.createLogicalVariable();
        logicalVarVariable.setArgument(createArgument(argumentEntityName));
        return logicalVarVariable;
    }

    private TemplateVariable createTemplateVariable(String entityName, boolean isShared, LogicalVariable logicalVar) {
        TemplateVariable templateVariable = templateVarFactory.createTemplateVariable();
        templateVariable.setEntityName(entityName);
        templateVariable.setShared(isShared);
        templateVariable.getSignature()
            .add(logicalVar);
        return templateVariable;
    }

    private ProbabilisticTemplateFactor createProbabilsiticTemplateFactor(String entityName,
            TemplateVariable templateVar, ProbabilityDistributionSkeleton probabilityDistribution, boolean isTemporal) {
        ProbabilisticTemplateFactor factor = templateVarFactory.createProbabilisticTemplateFactor();
        factor.setEntityName(entityName);
        factor.setDistributionFamily(probabilityDistribution);
        factor.getScope()
            .add(templateVar);
        factor.setTemporal(isTemporal);
        return factor;
    }

    private ProbabilisticModelRepository createStaticEnvDynamcisModel(
            GroundProbabilisticNetwork groundProbabilistiNetwork) {
        ProbabilisticModelRepository repo = staticEnvModelFactory.createProbabilisticModelRepository();
        repo.getModels()
            .add(groundProbabilistiNetwork);
        return repo;
    }

    public GroundProbabilisticNetwork createGroundProbNetwork(List<GroundProbabilisticModel> groundGPModels,
            List<GroundRandomVariable> grvs) {
        GroundProbabilisticNetwork gpn = staticEnvModelFactory.createGroundProbabilisticNetwork();
        gpn.setEntityName("testGPN");
        gpn.getLocalModels()
            .addAll(groundGPModels);

        LocalProbabilisticNetwork localProbailisticNetwork = createLocalProbabilisticNetwork(grvs);
        gpn.getLocalProbabilisticModels()
            .add(localProbailisticNetwork);
        return gpn;
    }

    private LocalProbabilisticNetwork createLocalProbabilisticNetwork(List<GroundRandomVariable> groundRandomVars) {
        LocalProbabilisticNetwork localProbabilisticNetwork = staticEnvModelFactory.createLocalProbabilisticNetwork();
        localProbabilisticNetwork.getGroundRandomVariables()
            .addAll(groundRandomVars);
        return localProbabilisticNetwork;
    }

    public GroundProbabilisticModel createGroundProbModel(String entityName, ProbabilityDistribution distribution,
            TemplateFactor factor) {
        GroundProbabilisticModel gpModel = staticEnvModelFactory.createGroundProbabilisticModel();
        gpModel.setEntityName(entityName);
        gpModel.setInstantiatedFactor(factor);
        gpModel.setDistribution(distribution);
        return gpModel;
    }

    public GroundRandomVariable createGroundRandomVariable(String entityName, GroundProbabilisticModel gpModel,
            TemplateVariable templateVar, EObject appliedObject) {
        GroundRandomVariable groundRandomVar = staticEnvModelFactory.createGroundRandomVariable();
        groundRandomVar.setEntityName(entityName);
        groundRandomVar.setDescriptiveModel(gpModel);
        groundRandomVar.setInstantiatedTemplate(templateVar);
        groundRandomVar.getAppliedObjects()
            .add(appliedObject); // refers to an PCM model e.g. usageScenario
        return groundRandomVar;
    }

}
