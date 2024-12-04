package org.palladiosimulator.simexp.pcm.examples.deltaiot;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.palladiosimulator.envdyn.api.entity.bn.DynamicBayesianNetwork;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.envdyn.environment.staticmodel.GroundRandomVariable;
import org.palladiosimulator.envdyn.environment.staticmodel.LocalProbabilisticNetwork;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.simexp.core.state.ArchitecturalConfiguration;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.state.SimulationRunnerHolder;
import org.palladiosimulator.simexp.core.statespace.SelfAdaptiveSystemStateSpaceNavigator;
import org.palladiosimulator.simexp.core.store.SimulatedExperienceStore;
import org.palladiosimulator.simexp.environmentaldynamics.entity.PerceivableEnvironmentalState;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTModelAccess;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import com.google.common.collect.Maps;

import tools.mdsd.probdist.api.entity.CategoricalValue;
import tools.mdsd.probdist.api.random.ISeedProvider;

public class DeltaIoTPartiallyEnvDynamics<R> extends DeltaIoTBaseEnvironemtalDynamics<R> {

    private final SelfAdaptiveSystemStateSpaceNavigator<PCMInstance, QVTOReconfigurator, R, List<InputValue<CategoricalValue>>> partiallyEnvProcess;

    public DeltaIoTPartiallyEnvDynamics(DynamicBayesianNetwork<CategoricalValue> dbn,
            SimulatedExperienceStore<QVTOReconfigurator, R> simulatedExperienceStore,
            DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess, Optional<ISeedProvider> seedProvider,
            SimulationRunnerHolder simulationRunnerHolder) {
        super(dbn, modelAccess, seedProvider);
        this.partiallyEnvProcess = createPartiallyEnvironmentalDrivenProcess(simulatedExperienceStore,
                simulationRunnerHolder);
    }

    public SelfAdaptiveSystemStateSpaceNavigator<PCMInstance, QVTOReconfigurator, R, List<InputValue<CategoricalValue>>> getEnvironmentProcess() {
        return partiallyEnvProcess;
    }

    private SelfAdaptiveSystemStateSpaceNavigator<PCMInstance, QVTOReconfigurator, R, List<InputValue<CategoricalValue>>> createPartiallyEnvironmentalDrivenProcess(
            SimulatedExperienceStore<QVTOReconfigurator, R> simulatedExperienceStore,
            SimulationRunnerHolder simulationRunnerHolder) {
        return new SelfAdaptiveSystemStateSpaceNavigator<PCMInstance, QVTOReconfigurator, R, List<InputValue<CategoricalValue>>>(
                envProcess, simulatedExperienceStore, simulationRunnerHolder) {

            class SNREquation {

                final double multiplier;
                final double constant;

                public SNREquation(double multiplier, double constant) {
                    this.multiplier = multiplier;
                    this.constant = constant;
                }

                public double getSNR(int power) {
                    return multiplier * power + constant;
                }
            }

            class SNRCalculator {

                private final Map<String, SNREquation> linkToSNR = Maps.newHashMap();

                private SNRCalculator() {
                    // Old SimCa settings
//                  linkToSNR.put("Unicast13to11", Lists.newArrayList(-3.8, -2.8, -2.3, -1.3, -2.0, -1.0, 0.0, 0.0, 0.0,
//                          3.3, 3.7, 4.0, 4.0, 4.3, 4.3, 4.7));
//                  linkToSNR.put("Unicast14to12", Lists.newArrayList(-6.6, -5.1, -4.2, -3.3, -2.6, -1.6, -1.0, -0.1,
//                          0.0, 0.8, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0));
//                  linkToSNR.put("Unicast15to12", Lists.newArrayList(-8.3, -7.3, -6.4, -5.6, -4.6, -3.8, -3.1, -2.3,
//                          -1.6, -0.9, -0.3, -0.5, -0.1, -0.1, 0.3, 0.4));
//                  linkToSNR.put("Unicast11to7", Lists.newArrayList(-4.0, -3.0, -2.0, -1.0, 0.0, 0.5, 1.0, 2.0, 3.0,
//                          4.0, 5.0, 5.0, 6.0, 6.0, 6.0, 6.0));
//                  linkToSNR.put("Unicast12to7", Lists.newArrayList(-13.0, -13.0, -12.0, -12.0, -12.0, -11.0, -11.0,
//                          -9.7, -8.9, -7.9, -6.7, -5.8, -4.9, -4.0, -3.1, -3.0));
//                  linkToSNR.put("Unicast12to3", Lists.newArrayList(6.0, 6.1, 6.2, 6.3, 6.4, 6.5, 6.6, 6.7, 6.8, 6.9,
//                          7.0, 7.1, 7.2, 7.3, 7.4, 7.5));
//                  linkToSNR.put("Unicast7to3", Lists.newArrayList(-7.9, -6.8, -5.9, -4.9, -4.1, -3.3, -2.4, -1.8,
//                          -0.9, -0.3, 0.0, 0.0, 0.3, 0.4, 0.8, 0.8));
//                  linkToSNR.put("Unicast7to2", Lists.newArrayList(-3.0, -2.1, -1.0, 0.0, 0.11, 1.0, 1.4, 2.0, 2.9,
//                          3.0, 4.0, 4.0, 4.6, 5.0, 5.0, 5.0));
//                  linkToSNR.put("Unicast2to4", Lists.newArrayList(7.0, 7.6, 7.8, 7.6, 7.6, 7.3, 7.3, 7.8, 7.8, 7.6,
//                          7.3, 7.9, 6.9, 7.3, 7.9, 8.0));
//                  linkToSNR.put("Unicast3to1", Lists.newArrayList(0.2, 1.0, 2.0, 2.9, 3.0, 4.0, 5.0, 6.0, 6.0, 6.0,
//                          6.8, 7.3, 7.4, 7.1, 7.5, 7.63));
//                  linkToSNR.put("Unicast8to1", Lists.newArrayList(-0.8, 0.0, 0.6, 1.2, 2.1, 3.0, 3.2, 4.3, 4.9, 5.3,
//                          6.0, 6.0, 6.0, 6.4, 6.8, 7.0));
//                  linkToSNR.put("Unicast4to1", Lists.newArrayList(-8.7, -7.8, -6.8, -5.8, -4.9, -3.8, -2.9, -1.9,
//                          -0.7, 0.0, 0.8, 1.1, 2.0, 2.6, 3.0, 3.0));
//                  linkToSNR.put("Unicast9to1", Lists.newArrayList(-8.0, -6.8, -5.8, -4.9, -3.9, -3.0, -1.9, -1.0, 0.0,
//                          0.4, 1.3, 2.0, 3.0, 3.1, 3.9, 4.4));
//                  linkToSNR.put("Unicast6to4", Lists.newArrayList(0.2, 1.0, 2.0, 2.9, 3.0, 4.0, 5.0, 6.0, 6.0, 6.0,
//                          6.8, 7.3, 7.4, 7.1, 7.5, 7.6));
//                  linkToSNR.put("Unicast10to6", Lists.newArrayList(-8.1, -6.9, -6.0, -4.8, -4.0, -2.9, -2.0, -1.1,
//                          -0.1, 0.4, 1.2, 2.0, 2.7, 3.0, 3.9, 4.0));
//                  linkToSNR.put("Unicast10to5", Lists.newArrayList(-3.5, -3.0, -2.0, -1.0, 0.0, 0.0, 1.0, 2.0, 3.0,
//                          3.0, 4.0, 5.0, 5.0, 5.0, 5.0, 6.0));
//                  linkToSNR.put("Unicast5to9", Lists.newArrayList(-6.0, -4.9, -4.0, -3.1, -2.1, -1.1, -0.7, 0.0, 0.1,
//                          1.0, 1.0, 1.1, 1.5, 1.5, 1.5, 2.6));

                    // SNR equations of SimCa paper
//                  linkToSNR.put("Unicast13to11", new SNREquation(0.6078, -3.6005));
//                  linkToSNR.put("Unicast14to12", new SNREquation(0.4886, -4.7704));
//                  linkToSNR.put("Unicast15to12", new SNREquation(0.5899, -7.1896));
//                  linkToSNR.put("Unicast11to7", new SNREquation(0.714, -3.1985));
//                  linkToSNR.put("Unicast12to7", new SNREquation(0.9254, -16.21));
//                  linkToSNR.put("Unicast12to3", new SNREquation(0.1, 6));
//                  linkToSNR.put("Unicast7to3", new SNREquation(0.5855, -6.644));
//                  linkToSNR.put("Unicast7to2", new SNREquation(0.5398, -2.0549));
//                  linkToSNR.put("Unicast2to4", new SNREquation(0.0169, 7.4076));
//                  linkToSNR.put("Unicast3to1", new SNREquation(0.4982, 1.2468));
//                  linkToSNR.put("Unicast8to1", new SNREquation(0.5298, -0.1031));
//                  linkToSNR.put("Unicast4to1", new SNREquation(0.8282, -8.1246));
//                  linkToSNR.put("Unicast9to1", new SNREquation(0.8284, -7.2893));
//                  linkToSNR.put("Unicast6to4", new SNREquation(0.6199, -9.8051));
//                  linkToSNR.put("Unicast10to6", new SNREquation(0.8219, -7.3331));
//                  linkToSNR.put("Unicast10to5", new SNREquation(0.6463, -3.0037));
//                  linkToSNR.put("Unicast5to9", new SNREquation(0.4932, -4.4898));

                    // Default SNR equations of deltaiot
                    linkToSNR.put("Unicast13to11", new SNREquation(-0.0210526315789, -2.81052631579));
                    linkToSNR.put("Unicast14to12", new SNREquation(0.0333333333333, 2.58947368421));
                    linkToSNR.put("Unicast15to12", new SNREquation(0.0438596491228, 1.31578947368));
                    linkToSNR.put("Unicast11to7", new SNREquation(0.380701754386, -2.12631578947));
                    linkToSNR.put("Unicast12to7", new SNREquation(0.317543859649, 2.95789473684));
                    linkToSNR.put("Unicast12to3", new SNREquation(-0.0157894736842, -3.77894736842));
                    linkToSNR.put("Unicast7to3", new SNREquation(0.168421052632, 2.30526315789));
                    linkToSNR.put("Unicast7to2", new SNREquation(-0.0157894736842, 3.77894736842));
                    linkToSNR.put("Unicast2to4", new SNREquation(0.0473684210526, -5.29473684211));
                    linkToSNR.put("Unicast3to1", new SNREquation(0.0280701754386, 4.25263157895));
                    linkToSNR.put("Unicast8to1", new SNREquation(0.00350877192982, 0.45263157895));
                    linkToSNR.put("Unicast4to1", new SNREquation(0.119298245614, -1.49473684211));
                    linkToSNR.put("Unicast9to1", new SNREquation(0.0701754385965, 2.89473684211));
                    linkToSNR.put("Unicast6to4", new SNREquation(0.0175438596491, -3.84210526316));
                    linkToSNR.put("Unicast10to6", new SNREquation(3.51139336547e-16, -2.21052631579));
                    linkToSNR.put("Unicast10to5", new SNREquation(0.250877192982, -1.75789473684));
                    linkToSNR.put("Unicast5to9", new SNREquation(-0.019298245614, 4.8));
                }

                public double calculateSNROf(GroundRandomVariable variable, int transmissionPower,
                        double wirelessInterference) {
                    String snrToQuery = LinkingResource.class.cast(variable.getAppliedObjects()
                        .get(0))
                        .getEntityName();
                    var snrValues = Optional.ofNullable(linkToSNR.get(snrToQuery));
                    if (snrValues.isEmpty()) {
                        throw new RuntimeException(String.format("There are no snr values for %s", snrToQuery));
                    }

                    // We assumed that the wireless interference has an impact on the SNR; However,
                    // this is not done in the
                    // deltaiot simulator code. Therefore, we follow the logic of the deltaiot
                    // simulator and adapt the SNR not
                    // w.r.t. to the wireless interference value.
                    // return snrValues.get().getSNR(transmissionPower) + wirelessInterference;
                    return snrValues.get()
                        .getSNR(transmissionPower);
                }

            }

            private final SNRCalculator snrCalculator = new SNRCalculator();

            @Override
            protected SelfAdaptiveSystemState<PCMInstance, QVTOReconfigurator, List<InputValue<CategoricalValue>>> determineStructuralState(
                    NavigationContext<QVTOReconfigurator> context) {
                LOGGER.info("Start with state transition.");
                long start = System.currentTimeMillis();
                QVToReconfiguration reconf = QVToReconfiguration.class.cast(context.getAction()
                    .get());
                ArchitecturalConfiguration<PCMInstance, QVTOReconfigurator> nextConfig = getCurrentArchitecture(context)
                    .apply(reconf);
                PerceivableEnvironmentalState<List<InputValue<CategoricalValue>>> currentEnvironment = getCurrentEnvironment(
                        context);
                PerceivableEnvironmentalState<List<InputValue<CategoricalValue>>> nextEnvironment = determineNextEnvState(
                        currentEnvironment, nextConfig);
                long end = System.currentTimeMillis();

                LOGGER.info("Stop with state transition, took : " + ((end - start) / 1000));
                return DeltaIoTBaseEnvironemtalDynamics.<QVTOReconfigurator> asPcmState(context.getSource())
                    .transitToNext(nextEnvironment, nextConfig);
            }

            private PerceivableEnvironmentalState<List<InputValue<CategoricalValue>>> determineNextEnvState(
                    PerceivableEnvironmentalState<List<InputValue<CategoricalValue>>> envState,
                    ArchitecturalConfiguration<PCMInstance, QVTOReconfigurator> pcmConf) {
                PerceivableEnvironmentalState<List<InputValue<CategoricalValue>>> nextEnvironment = envProcess
                    .determineNextGiven(envState);
                adjustArchitecturalDependentVariables(nextEnvironment, pcmConf);
                return nextEnvironment;
            }

            private void adjustArchitecturalDependentVariables(
                    PerceivableEnvironmentalState<List<InputValue<CategoricalValue>>> nextEnv,
                    ArchitecturalConfiguration<PCMInstance, QVTOReconfigurator> archConfig) {
                List<InputValue<CategoricalValue>> inputValues = toInputs(nextEnv.getValue()
                    .getValue());
                for (InputValue<CategoricalValue> each : inputValues) {
                    if (isSNRTemplate(each.getVariable())) {
                        LocalProbabilisticNetwork localNetwork = LocalProbabilisticNetwork.class.cast(each.getVariable()
                            .eContainer());

                        GroundRandomVariable wiVariable = findWirelessInterferenceVariable(localNetwork);
                        LinkingResource link = LinkingResource.class.cast(wiVariable.getAppliedObjects()
                            .get(0));
                        AssemblyContext sourceMote = modelAccess.findSourceMote(link, archConfig);

                        double wirelessInterference = modelAccess.retrieveWirelessInterference(wiVariable, nextEnv);
                        int transmissionPower = modelAccess.retrieveTransmissionPower(sourceMote, link);

                        each.setValue(computeSNR(each.getVariable(), wirelessInterference, transmissionPower));
                    }
                }
            }

            private CategoricalValue computeSNR(GroundRandomVariable variable, double wirelessInterference,
                    int transmissionPower) {
                double snr = snrCalculator.calculateSNROf(variable, transmissionPower, wirelessInterference);
                return CategoricalValue.create(Double.toString(snr));
            }

            @Override
            protected PerceivableEnvironmentalState<List<InputValue<CategoricalValue>>> determineInitial(
                    ArchitecturalConfiguration<PCMInstance, QVTOReconfigurator> initialArch) {
                return envProcess.determineInitial();
            }
        };
    }

    public static GroundRandomVariable findWirelessInterferenceVariable(LocalProbabilisticNetwork localNetwork) {
        return localNetwork.getGroundRandomVariables()
            .stream()
            .filter(isWITemplate())
            .findFirst()
            .orElseThrow(() -> new RuntimeException("There is no wireless interference template."));
    }

}
