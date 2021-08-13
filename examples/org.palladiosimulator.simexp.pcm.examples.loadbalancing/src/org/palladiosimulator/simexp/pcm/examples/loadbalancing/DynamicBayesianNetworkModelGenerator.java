package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.util.List;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.api.generator.BayesianNetworkGenerator;
import org.palladiosimulator.envdyn.api.generator.DynamicBayesianNetworkGenerator;
import org.palladiosimulator.envdyn.environment.templatevariable.TemplateVariableDefinitions;
import org.palladiosimulator.envdyn.environment.templatevariable.TemplatevariablePackage;
import org.palladiosimulator.experimentautomation.experiments.Experiment;
import org.palladiosimulator.simexp.pcm.util.ExperimentProvider;

public class DynamicBayesianNetworkModelGenerator implements IDynamicBayesianNetworkGenerator {
    
    private IDynamicBayesianNetworkLoader dbnLoader;
    private IDynamicBayesianNetworkPersistence dbnPersistence;
    
    private String environmentalStaticsModel;
    private String environmentalDynamicsModel;

    public DynamicBayesianNetworkModelGenerator(IDynamicBayesianNetworkPersistence dbnPersistence
            , IDynamicBayesianNetworkLoader dbnLoader
            , String environmentalStaticsModel, String environmentalDynamicsModel) {
        this.dbnLoader = dbnLoader;
        this.dbnPersistence = dbnPersistence;
        this.environmentalStaticsModel = environmentalStaticsModel;
        this.environmentalDynamicsModel = environmentalDynamicsModel;
    }

    @Override
    public DynamicBayesianNetwork generateDBN(Experiment exp) {
        TemplateVariableDefinitions templates = loadTemplates();

        BayesianNetwork bn = null;
        try {
            bn = new BayesianNetwork(null, dbnLoader.loadGroundProbabilisticNetwork());
        } catch (Exception e) {
            bn = generateBN(templates, exp);
            dbnPersistence.persist(bn.get(), environmentalStaticsModel);
        }

        DynamicBayesianNetwork dbn = new DynamicBayesianNetworkGenerator(templates)
                .createProbabilisticNetwork(bn.get());

        dbnPersistence.persist(dbn.getDynamics(), environmentalDynamicsModel);

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

}
