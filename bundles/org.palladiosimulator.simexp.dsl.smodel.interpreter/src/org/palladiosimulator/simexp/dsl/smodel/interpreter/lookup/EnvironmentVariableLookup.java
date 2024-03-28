package org.palladiosimulator.simexp.dsl.smodel.interpreter.lookup;

import org.eclipse.emf.common.util.EList;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundProbabilisticNetwork;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundRandomVariable;
import org.palladiosimulator.envdyn.environment.staticmodel.LocalProbabilisticNetwork;
import org.palladiosimulator.envdyn.environment.staticmodel.ProbabilisticModelRepository;
import org.palladiosimulator.simexp.dsl.smodel.smodel.EnvVariable;

public class EnvironmentVariableLookup implements IEnvVariableLookup {

    private final ProbabilisticModelRepository staticEnvDynModel;

    public EnvironmentVariableLookup(ProbabilisticModelRepository staticEnvDynModel) {
        this.staticEnvDynModel = staticEnvDynModel;
    }

    @Override
    public GroundRandomVariable findEnvironmentVariable(EnvVariable envVar) {
        EList<GroundProbabilisticNetwork> gpns = staticEnvDynModel.getModels();
        for (GroundProbabilisticNetwork gpn : gpns) {
            EList<LocalProbabilisticNetwork> localProbabilisticNeworks = gpn.getLocalProbabilisticModels();
            for (LocalProbabilisticNetwork localProbNetwork : localProbabilisticNeworks) {
                EList<GroundRandomVariable> groundRandomVariables = localProbNetwork.getGroundRandomVariables();
                for (GroundRandomVariable gvr : groundRandomVariables) {
                    String gvrId = gvr.getId();
                    if (gvrId.equals(envVar.getVariableId())) {
                        return gvr;
                    }
                }
            }
        }
        return null;
    }

}
