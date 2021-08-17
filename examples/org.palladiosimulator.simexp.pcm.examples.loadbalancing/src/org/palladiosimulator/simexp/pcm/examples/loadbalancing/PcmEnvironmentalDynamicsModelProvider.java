package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import org.eclipse.emf.ecore.resource.Resource;
import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.simexp.pcm.examples.ISimExpPcmConfiguration;

public class PcmEnvironmentalDynamicsModelProvider implements IEnvironmentalDynamicsModelProvider {
    
    private IDynamicBayesianNetworkLoader dbnLoader;
    private IDynamicBayesianNetworkPersistence dbnPersistence;
    private IDynamicBayesianNetworkGenerator dbnModelGenerator;
    
    public PcmEnvironmentalDynamicsModelProvider() {
        this.dbnPersistence = new DynamicBayesiannetworkPersistence(DynamicBayesianNetworkResourceSetManager.INSTANCE);
    }
    

    @Override
    public DynamicBayesianNetwork getEnvironmentalDynamicsModel(ISimExpPcmConfiguration simExpPcmConfiguration, Resource usageModel) {
        // FIXME: decide if injected as constructor dependencies
        this.dbnModelGenerator = new DynamicBayesianNetworkModelGenerator(dbnPersistence, dbnLoader, simExpPcmConfiguration.getEnvironmentalStaticsModelFile()
                , simExpPcmConfiguration.getEnvironmentalDynamicsModelFile());
        this.dbnLoader = new DynamicBayesianNetworkLoader(DynamicBayesianNetworkResourceSetManager.INSTANCE, simExpPcmConfiguration.getEnvironmentalStaticsModelFile()
                , simExpPcmConfiguration.getEnvironmentalDynamicsModelFile(), dbnModelGenerator);
        DynamicBayesianNetwork dbn = dbnLoader.loadOrGenerateDBN(usageModel);
        return dbn;
    }

}
