package org.palladiosimulator.simexp.pcm.examples.loadbalancing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.palladiosimulator.envdyn.api.entity.bn.InputValue;
import org.palladiosimulator.pcm.repository.BasicComponent;
import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.simexp.core.entity.SimulatedMeasurement;
import org.palladiosimulator.simexp.core.state.ArchitecturalConfiguration;
import org.palladiosimulator.simexp.core.state.SelfAdaptiveSystemState;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.markovian.activity.Policy;
import org.palladiosimulator.simexp.markovian.model.markovmodel.markoventity.State;
import org.palladiosimulator.simexp.pcm.action.EmptyQVToReconfiguration;
import org.palladiosimulator.simexp.pcm.action.QVToReconfiguration;
import org.palladiosimulator.simexp.pcm.state.PcmMeasurementSpecification;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.core.models.PCMInstance;

import tools.mdsd.probdist.api.entity.CategoricalValue;

public class LinearLoadBalancerStrategy<C, A> implements Policy<QVTOReconfigurator, QVToReconfiguration> {

    private final static String LINEAR_ADAPTATION_STRATEGY_NAME = "LinearLoadBalancerAdaptationStrategy";
    private final static String OUT_SOURCE = "LinearOutsourcing";
    private final static String SCALE_IN = "LinearScaleIn";
    private final static double MAX_RT = 10.0;
    private final static double MAX_RECONFIGURATION_STEP = 0.5;
    private final static double SET_POINT_RT = 1.0;
    private final static Map<Predicate<Double>, Integer> balanceStepMap = new HashMap<>();
    static {
        balanceStepMap.put(v -> v < 0.05, 0);
        balanceStepMap.put(v -> v >= 0.05 && v < 0.15, 1);
        balanceStepMap.put(v -> v >= 0.15 && v < 0.25, 2);
        balanceStepMap.put(v -> v >= 0.25 && v < 0.35, 3);
        balanceStepMap.put(v -> v >= 0.35 && v < 0.45, 4);
        balanceStepMap.put(v -> v >= 0.45, 5);
    }

    private final Threshold setPointThreshold;
    private final PcmMeasurementSpecification pcmSpec;

    public LinearLoadBalancerStrategy(PcmMeasurementSpecification pcmSpec) {
        this.pcmSpec = pcmSpec;
        this.setPointThreshold = Threshold.greaterThan(SET_POINT_RT);
    }

    @Override
    public QVToReconfiguration select(State source, Set<QVToReconfiguration> options) {
        // TODO Exception handling
        if ((source instanceof SelfAdaptiveSystemState) == false) {
            throw new RuntimeException("");
        }

        SelfAdaptiveSystemState<C, A, List<InputValue<CategoricalValue>>> sassState = (SelfAdaptiveSystemState<C, A, List<InputValue<CategoricalValue>>>) source;
        SimulatedMeasurement simMeasurement = sassState.getQuantifiedState()
            .findMeasurementWith(pcmSpec)
            .orElseThrow(() -> new RuntimeException(""));
        Double value = simMeasurement.getValue();
        if (setPointThreshold.isSatisfied(value)) {
            int outSourceFactor = computeOutSourceFactor(value);
            outSourceFactor = adjustOutSourceFactor(outSourceFactor, sassState.getArchitecturalConfiguration());
            return linearOutSource(outSourceFactor, asReconfigurations(options));
        } else {
            int scaleInFactor = computeScaleInFactor(value);
            scaleInFactor = adjustScaleInFactor(scaleInFactor, sassState.getArchitecturalConfiguration());
            return linearScaleIn(scaleInFactor, asReconfigurations(options));
        }
    }

    private int adjustOutSourceFactor(int outSourceFactor, ArchitecturalConfiguration<C, A> archConf) {
        PCMInstance pcm = (PCMInstance) archConf.getConfiguration();
        ProbabilisticBranchTransition probServer1 = findBranchProbability(pcm);
        double branchProb = probServer1.getBranchProbability();
        for (int i = outSourceFactor; i > 0; i--) {
            double adjustedOSF = ((double) i) / 10;
            if ((branchProb - adjustedOSF) >= 0.5) {
                return i;
            }
        }
        return 0;
    }

    private ProbabilisticBranchTransition findBranchProbability(PCMInstance pcm) {
        BasicComponent component = pcm.getRepositories()
            .stream()
            .flatMap(e -> e.getComponents__Repository()
                .stream())
            .filter(e -> e instanceof BasicComponent)
            .map(e -> (BasicComponent) e)
            .filter(e -> e.getEntityName()
                .equals("LoadBalancer"))
            .findFirst()
            .get();
        return findBranchProbability(component);
    }

    private ProbabilisticBranchTransition findBranchProbability(BasicComponent component) {
        TreeIterator<EObject> iterator = component.eAllContents();
        while (iterator.hasNext()) {
            EObject next = iterator.next();
            if (branchProbabilityServer1().test(next)) {
                return (ProbabilisticBranchTransition) next;
            }
        }

        throw new RuntimeException("Could not find the branch probability transition object.");
    }

    private Predicate<EObject> branchProbabilityServer1() {
        return e -> (e instanceof ProbabilisticBranchTransition) && ((ProbabilisticBranchTransition) e).getEntityName()
            .equals("delegateToServer1");
    }

    private int computeOutSourceFactor(Double rt) {
        double outsourceFactor = getOutSourceSlope() * rt;
        return normalize(outsourceFactor);
    }

    private QVToReconfiguration linearOutSource(int outSourceFactor, List<QVToReconfiguration> options) {
        if (outSourceFactor == 0) {
            return EmptyQVToReconfiguration.empty();
        }
        String reconf = OUT_SOURCE + Integer.toString(outSourceFactor);
        return findReconfiguration(reconf, options);
    }

    private int adjustScaleInFactor(int scaleInFactor, ArchitecturalConfiguration<C, A> archConf) {
        PCMInstance pcm = (PCMInstance) archConf.getConfiguration();
        ProbabilisticBranchTransition probServer1 = findBranchProbability(pcm);
        double branchProb = probServer1.getBranchProbability();
        for (int i = scaleInFactor; i > 0; i--) {
            double adjustedSIF = ((double) i) / 10;
            if ((branchProb + adjustedSIF) <= 1.0) {
                return i;
            }
        }
        return 0;
    }

    private int computeScaleInFactor(Double rt) {
        double scaleInFactor = getScaleInSlope() * rt;
        return normalize(scaleInFactor);
    }

    private QVToReconfiguration linearScaleIn(int scaleInFactor, List<QVToReconfiguration> options) {
        if (scaleInFactor == 0) {
            return EmptyQVToReconfiguration.empty();
        }
        String reconf = SCALE_IN + Integer.toString(scaleInFactor);
        return findReconfiguration(reconf, options);
    }

    private double getOutSourceSlope() {
        return MAX_RECONFIGURATION_STEP / (MAX_RT - SET_POINT_RT);
    }

    private double getScaleInSlope() {
        return MAX_RECONFIGURATION_STEP / SET_POINT_RT;
    }

    private int normalize(double factor) {
        return balanceStepMap.entrySet()
            .stream()
            .filter(each -> each.getKey()
                .test(factor))
            .map(each -> each.getValue())
            .findFirst()
            .get();
    }

    private List<QVToReconfiguration> asReconfigurations(Set<QVToReconfiguration> options) {
        return options.stream()
            .map(each -> each)
            .collect(Collectors.toList());
    }

    private QVToReconfiguration findReconfiguration(String name, List<QVToReconfiguration> options) {
        for (QVToReconfiguration each : options) {
            String reconfName = each.getReconfigurationName();
            if (reconfName.equals(name)) {
                return each;
            }
        }

        throw new RuntimeException("");
    }

    @Override
    public String getId() {
        return LINEAR_ADAPTATION_STRATEGY_NAME;
    }

}
