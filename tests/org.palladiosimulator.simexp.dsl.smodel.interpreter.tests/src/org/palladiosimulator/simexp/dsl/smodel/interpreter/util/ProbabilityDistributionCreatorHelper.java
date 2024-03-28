package org.palladiosimulator.simexp.dsl.smodel.interpreter.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import tools.mdsd.probdist.distributionfunction.DistributionfunctionFactory;
import tools.mdsd.probdist.distributionfunction.Domain;
import tools.mdsd.probdist.distributionfunction.ParamRepresentation;
import tools.mdsd.probdist.distributionfunction.Parameter;
import tools.mdsd.probdist.distributionfunction.ParameterType;
import tools.mdsd.probdist.distributionfunction.ProbabilityDistribution;
import tools.mdsd.probdist.distributionfunction.ProbabilityDistributionFunctionRepository;
import tools.mdsd.probdist.distributionfunction.RandomVariable;
import tools.mdsd.probdist.distributionfunction.SimpleParameter;
import tools.mdsd.probdist.distributionfunction.TabularCPD;
import tools.mdsd.probdist.distributionfunction.TabularCPDEntry;
import tools.mdsd.probdist.distributiontype.DistributiontypeFactory;
import tools.mdsd.probdist.distributiontype.ParameterSignature;
import tools.mdsd.probdist.distributiontype.ProbabilityDistributionRepository;
import tools.mdsd.probdist.distributiontype.ProbabilityDistributionSkeleton;

public class ProbabilityDistributionCreatorHelper {

    private final DistributiontypeFactory distributTypeFactory = DistributiontypeFactory.eINSTANCE;
    private final DistributionfunctionFactory distributionFunctionFactory = DistributionfunctionFactory.eINSTANCE;

    public ProbabilityDistributionCreatorHelper() {
    }

    public ProbabilityDistributionRepository createProbabilityDistRepo(
            List<ProbabilityDistributionSkeleton> probDistSkeletons) {
        ProbabilityDistributionRepository repository = distributTypeFactory.createProbabilityDistributionRepository();
        repository.getDistributionFamilies()
            .addAll(probDistSkeletons);
        return repository;
    }

    public ProbabilityDistributionSkeleton createMultinomialDistributionType() {
        ProbabilityDistributionSkeleton skeleton = distributTypeFactory.createProbabilityDistributionSkeleton();
        skeleton.setEntityName("MultinomialDistribution");
        ParameterSignature parameterSignature = distributTypeFactory.createParameterSignature();
        parameterSignature.setId("EventProbability");
        skeleton.getParamStructures()
            .add(parameterSignature);
        return skeleton;
    }

    public ProbabilityDistributionFunctionRepository createBasicProbabilityDistributionFunctions() {
        ProbabilityDistributionSkeleton multinomialSkel = createMultinomialDistributionType();
        ProbabilityDistribution staticProbDist = createDistributionFunction("testRandomVariable", multinomialSkel);
        RandomVariable staticRandomVar = createRandomVariable("testStaticRandomVar");
        staticProbDist.getRandomVariables()
            .add(staticRandomVar);
        ProbabilityDistribution dynamicProbDist = createDistributionFunction("testRandomVariable", multinomialSkel);
        RandomVariable dynamicRandomVar = createRandomVariable("testDynamicRandomVar");
        dynamicProbDist.getRandomVariables()
            .add(dynamicRandomVar);
        List<ProbabilityDistribution> probDists = Arrays.asList(staticProbDist, dynamicProbDist);

        ParameterSignature signature = multinomialSkel.getParamStructures()
            .get(0);
        String value = "{0.2,0.1};{0.225,0.1};{0.25,0.1};{0.275,0.1};{0.3,0.1};{0.325,0.1};{0.35,0.1};{0.375,0.1};{0.4,0.1};{0.425,0.1}";
        ParamRepresentation simpleRepresentation = createSimpleParamRepresentation(value);
        Parameter staticParam = createDistributionParameter("testRandomVariable", signature, simpleRepresentation);

        List<String> conditionals = Arrays.asList(value);
        TabularCPDEntry entry = createCPDEntry(conditionals);
        List<TabularCPDEntry> tabularCPD = Arrays.asList(entry, entry);
        ParamRepresentation complexRepresentation = createTabularCPDParamRepresentation(tabularCPD);
        Parameter dynamicParam = createDistributionParameter("testRandomVariable", signature, complexRepresentation);
        List<Parameter> params = Arrays.asList(staticParam, dynamicParam);
        ProbabilityDistributionFunctionRepository repo = createProbabilistyDistributionFunctionsRepo(probDists, params);
        return repo;
    }

    public ProbabilityDistributionFunctionRepository createProbabilistyDistributionFunctionsRepo(
            List<ProbabilityDistribution> probDists, List<Parameter> params) {
        ProbabilityDistributionFunctionRepository probDistFuncRepo = distributionFunctionFactory
            .createProbabilityDistributionFunctionRepository();
        probDistFuncRepo.setEntityName("testEnvironmentDistributions");
        probDistFuncRepo.getDistributions()
            .addAll(probDists);
        probDistFuncRepo.getParams()
            .addAll(params);
        return probDistFuncRepo;
    }

    public ProbabilityDistribution createDistributionFunction(String entityName,
            ProbabilityDistributionSkeleton skeleton) {
        ProbabilityDistribution probabilityDistribution = distributionFunctionFactory.createProbabilityDistribution();
        probabilityDistribution.setEntityName(entityName);
        probabilityDistribution.setInstantiated(skeleton);
        return probabilityDistribution;
    }

    public Parameter createDistributionParameter(String entityName, ParameterSignature signature,
            ParamRepresentation paramRepresentation) {
        Parameter parameter = distributionFunctionFactory.createParameter();
        parameter.setEntityName(entityName);
        parameter.setInstantiated(signature);
        parameter.setRepresentation(paramRepresentation);
        return parameter;
    }

    public RandomVariable createRandomVariable(String entityName) {
        RandomVariable randomVariable = distributionFunctionFactory.createRandomVariable();
        randomVariable.setEntityName(entityName);
        randomVariable.setValueSpace(Domain.CATEGORY);
        return randomVariable;
    }

    public ParamRepresentation createSimpleParamRepresentation(String value) {
        SimpleParameter simpleParam = distributionFunctionFactory.createSimpleParameter();
        simpleParam.setType(ParameterType.SAMPLESPACE);
        simpleParam.setValue(value);
        return simpleParam;
    }

    public ParamRepresentation createTabularCPDParamRepresentation(Collection<? extends TabularCPDEntry> entries) {
        TabularCPD tabularCPD = distributionFunctionFactory.createTabularCPD();
        tabularCPD.getCpdEntries()
            .addAll(entries);
        return tabularCPD;
    }

    public TabularCPDEntry createCPDEntry(Collection<? extends String> conditionals) {
        TabularCPDEntry CPDEntry = distributionFunctionFactory.createTabularCPDEntry();
        CPDEntry.getConditonals()
            .addAll(conditionals);
        return CPDEntry;
    }

}
