package org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy;

import static java.util.stream.Collectors.toList;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.pcm.seff.ProbabilisticBranchTransition;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTModelAccess;
import org.palladiosimulator.simulizar.reconfiguration.qvto.QVTOReconfigurator;
import org.palladiosimulator.solver.models.PCMInstance;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class MoteContext {

    public static class MoteContextFilter {

        private final List<MoteContext> contexts;

        public MoteContextFilter(SharedKnowledge knowledge) {
            this.contexts = knowledge.getValues()
                .stream()
                .filter(MoteContext.class::isInstance)
                .map(MoteContext.class::cast)
                .collect(toList());
        }

        public List<MoteContext> getAllMoteContexts() {
            return contexts;
        }

        public List<MoteContext> motesWithTwoLinks() {
            return contexts.stream()
                .filter(MoteContext::hasTwoLinks)
                .collect(toList());
        }

        public Map<MoteContext, WirelessLink> motesWithSNRLowerThan(Threshold lowerBound) {
            Map<MoteContext, WirelessLink> result = Maps.newHashMap();
            for (MoteContext each : contexts) {
                linksWithSNRLowerThan(lowerBound, each).forEach(link -> result.put(each, link));
            }
            return result;
        }

        public Map<MoteContext, WirelessLink> motesWithSNRHigherThan(Threshold lowerBound) {
            Map<MoteContext, WirelessLink> result = Maps.newHashMap();
            for (MoteContext each : contexts) {
                linksWithSNRHigherThan(lowerBound, each).forEach(link -> result.put(each, link));
            }
            return result;
        }

        public WirelessLink linkWithSmallestSNR(MoteContext mote) {
            return orderBySNRValue(mote.links).get(0);
        }

        public WirelessLink linkWithHighestTransmissionPower(MoteContext mote) {
            var values = orderByTransmissionPowerValue(mote.links);
            return values.get(values.size() - 1);
        }

        private List<WirelessLink> linksWithSNRLowerThan(Threshold lowerBound, MoteContext mote) {
            return orderBySNRValue(mote.links).stream()
                .takeWhile(each -> lowerBound.isSatisfied(each.SNR))
                .collect(toList());
        }

        private List<WirelessLink> linksWithSNRHigherThan(Threshold upperBound, MoteContext mote) {
            return orderBySNRValue(mote.links).stream()
                .dropWhile(each -> upperBound.isNotSatisfied(each.SNR))
                .collect(toList());
        }

        private List<WirelessLink> orderBySNRValue(Set<WirelessLink> links) {
            var snrComparator = new Comparator<WirelessLink>() {

                @Override
                public int compare(WirelessLink l1, WirelessLink l2) {
                    return Double.compare(l1.SNR, l2.SNR);
                }
            };
            var result = orderBy(snrComparator, links);
            return result;
        }

        private List<WirelessLink> orderByTransmissionPowerValue(Set<WirelessLink> links) {
            var tpComparator = new Comparator<WirelessLink>() {

                @Override
                public int compare(WirelessLink l1, WirelessLink l2) {
                    return Double.compare(l1.transmissionPower, l2.transmissionPower);
                }
            };
            return orderBy(tpComparator, links);
        }

        private List<WirelessLink> orderBy(Comparator<WirelessLink> linkComparator, Set<WirelessLink> links) {
            return links.stream()
                .sorted(linkComparator)
                .collect(toList());
        }

    }

    public static class WirelessLink {

        public final LinkingResource pcmLink;
        public final double SNR;
        public final int transmissionPower;
        public final double distributionFactor;

        private WirelessLink(LinkingResource pcmLink, double SNR, int transmissionPower, double distributionFactor) {
            this.pcmLink = pcmLink;
            this.SNR = SNR;
            this.transmissionPower = transmissionPower;
            this.distributionFactor = distributionFactor;
        }
    }

    private final DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess;
    public final AssemblyContext mote;
    public final Set<WirelessLink> links;

    public MoteContext(DeltaIoTModelAccess<PCMInstance, QVTOReconfigurator> modelAccess, AssemblyContext mote,
            Map<LinkingResource, Double> linkToSNR) {
        this.modelAccess = modelAccess;
        this.mote = mote;
        this.links = initLinks(mote, linkToSNR);
    }

    private Set<WirelessLink> initLinks(AssemblyContext mote, Map<LinkingResource, Double> linkToSNR) {
        Set<WirelessLink> links = Sets.newHashSet();
        for (LinkingResource each : linkToSNR.keySet()) {
            var SNR = linkToSNR.get(each);
            int transmissionPower = modelAccess.retrieveTransmissionPower(mote, each);
            var distFactor = modelAccess.retrieveCommunicatingBranch(mote, each)
                .map(ProbabilisticBranchTransition::getBranchProbability)
                .orElse(1.0);
            links.add(new WirelessLink(each, SNR, transmissionPower, distFactor));
        }
        return links;
    }

    public boolean hasTwoLinks() {
        return links.size() == 2;
    }

    public boolean hasEqualTransmissionPower() {
        if (hasTwoLinks() == false) {
            return false;
        }

        var linkQuantityIterator = links.iterator();
        var tp1 = linkQuantityIterator.next().transmissionPower;
        var tp2 = linkQuantityIterator.next().transmissionPower;

        return tp1 == tp2;
    }

    public boolean hasUnequalTransmissionPower() {
        return hasEqualTransmissionPower() == false;
    }

    public String getId() {
        return mote.getId();
    }

}
