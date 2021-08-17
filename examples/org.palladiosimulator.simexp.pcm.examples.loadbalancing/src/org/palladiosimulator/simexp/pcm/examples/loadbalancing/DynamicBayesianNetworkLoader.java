package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.palladiosimulator.envdyn.api.entity.bn.BayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.environment.dynamicmodel.DynamicBehaviourExtension;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundProbabilisticNetwork;

public class DynamicBayesianNetworkLoader implements IDynamicBayesianNetworkLoader {
    
    public final static String STATIC_MODEL_EXTENSION = "staticmodel";
    public final static String DYNAMIC_MODEL_EXTENSION = "dynamicmodel";
    
    private final IDynamicBayesianNetworkResourceSetManager resourceSetManager;
    private final IDynamicBayesianNetworkGenerator dbnModelGenerator;

    private String environmentalStaticsModel;
    private String environmentalDynamicsModel;

    public DynamicBayesianNetworkLoader(IDynamicBayesianNetworkResourceSetManager resourceSetManager
            , String environmentalStaticsModel, String environmentalDynamicsModel, IDynamicBayesianNetworkGenerator dbnModelGenerator) {
        this.resourceSetManager = resourceSetManager;
        this.environmentalStaticsModel = environmentalStaticsModel;
        this.environmentalDynamicsModel = environmentalDynamicsModel;
        this.dbnModelGenerator = dbnModelGenerator;
    }

    @Override
    public DynamicBayesianNetwork loadDBN() {
        BayesianNetwork bn = new BayesianNetwork(null, loadGroundProbabilisticNetwork());
        return new DynamicBayesianNetwork(null, bn, loadDynamicBehaviourExtension());
    }
    
    private DynamicBehaviourExtension loadDynamicBehaviourExtension() {
        ResourceSet resourceSet = resourceSetManager.getResourceSet();
        URI dbnURI = createDBNPlatformResourceURI(environmentalDynamicsModel);
        return DynamicBehaviourExtension.class.cast(resourceSet.getResource(dbnURI, true).getContents().get(0));
    }
    
    private URI createDBNPlatformResourceURI(String pathName) {
        return  URI.createPlatformResourceURI(pathName, true);
    }


    @Override
    public GroundProbabilisticNetwork loadGroundProbabilisticNetwork() {
        ResourceSet resourceSet = resourceSetManager.getResourceSet();
        URI bnURI = createDBNPlatformResourceURI(environmentalStaticsModel);
        return GroundProbabilisticNetwork.class.cast(resourceSet.getResource(bnURI, true).getContents().get(0));
    }

    
    @Override
    public DynamicBayesianNetwork loadOrGenerateDBN(Resource usageModel) {
            try {
                return loadDBN();
            } catch (Exception e) {
                // FIXME: refactor: in case of exception, generate DBN; when does this situation occur?
                return dbnModelGenerator.generateDBN(usageModel);
            }
    }

}
