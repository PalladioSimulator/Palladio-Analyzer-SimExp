package org.palladiosimulator.simexp.pcm.examples.deltaiot.strategy;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.resourceenvironment.LinkingResource;
import org.palladiosimulator.simexp.core.strategy.SharedKnowledge;
import org.palladiosimulator.simexp.core.util.Threshold;
import org.palladiosimulator.simexp.pcm.examples.deltaiot.util.DeltaIoTModelAccess;

import com.google.common.collect.Maps;

public class MoteContext {
	
	protected static class MoteContextFilter {

		private final List<MoteContext> contexts;

		public MoteContextFilter(SharedKnowledge knowledge) {
			this.contexts = knowledge.getValues().stream()
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

		public Map<MoteContext, LinkingResource> motesWithSNRLowerThan(Threshold lowerBound) {
			Map<MoteContext, LinkingResource> result = Maps.newHashMap();
			for (MoteContext each : contexts) {
				linksWithSNRLowerThan(lowerBound, each).forEach(link -> result.put(each, link));
			}
			return result;
		}
		
		public Map<MoteContext, LinkingResource> motesWithSNRHigherThan(Threshold lowerBound) {
			Map<MoteContext, LinkingResource> result = Maps.newHashMap();
			for (MoteContext each : contexts) {
				linksWithSNRHigherThan(lowerBound, each).forEach(link -> result.put(each, link));
			}
			return result;
		}

		public LinkingResource linkWithHighestSNR(MoteContext mote) {
			return orderBySNRValue(mote.linkDetails).get(0).getKey();
		}
		
		public LinkingResource linkWithHighestTransmissionPower(MoteContext mote) {
			return orderByTransmissionPowerValue(mote.linkDetails).get(0).getKey();
		}
		
		private List<LinkingResource> linksWithSNRLowerThan(Threshold lowerBound, MoteContext mote) {
			return orderBySNRValue(mote.linkDetails).stream()
					.takeWhile(each -> lowerBound.isSatisfied(each.getValue()))
					.map(each -> each.getKey())
					.collect(toList());
		}
		
		private List<LinkingResource> linksWithSNRHigherThan(Threshold upperBound, MoteContext mote) {
			return orderBySNRValue(mote.linkDetails).stream()
					.dropWhile(each -> upperBound.isNotSatisfied(each.getValue()))
					.map(each -> each.getKey())
					.collect(toList());
		}

		private List<Entry<LinkingResource, Double>> orderBySNRValue(
				Map<LinkingResource, LinkingResourceQuantity> links) {
			return links.entrySet().stream()
					.map(e -> Map.entry(e.getKey(), e.getValue().SNR))
					.sorted(Map.Entry.comparingByValue())
					.collect(toList());
		}
		
		private List<Entry<LinkingResource, Double>> orderByTransmissionPowerValue(
				Map<LinkingResource, LinkingResourceQuantity> links) {
			return links.entrySet().stream()
					.map(e -> Map.entry(e.getKey(), e.getValue().transmissionPower))
					.sorted(Map.Entry.comparingByValue())
					.collect(toList());
		}

	}
	
	protected static class LinkingResourceQuantity {
		
		public final double SNR;
		public final double transmissionPower;
		
		public LinkingResourceQuantity(double SNR, double transmissionPower) {
			this.SNR = SNR;
			this.transmissionPower = transmissionPower;
		}
	}
	
	public final AssemblyContext mote;
	public final Map<LinkingResource, LinkingResourceQuantity> linkDetails;
	
	public MoteContext(AssemblyContext mote, Map<LinkingResource, Double> linkToSNR) {
		this.mote = mote;
		this.linkDetails = Maps.newHashMap();
		
		initLinkDetails(mote, linkToSNR);
	}
	
	private Map<LinkingResource, LinkingResourceQuantity> initLinkDetails(AssemblyContext mote, 
			Map<LinkingResource, Double> linkToSNR) {
		for (LinkingResource each : linkToSNR.keySet()) {
			var SNR = linkToSNR.get(each);
			var transmissionPower = DeltaIoTModelAccess.get().retrieveTransmissionPower(mote, each);
			linkDetails.put(each, new LinkingResourceQuantity(SNR, transmissionPower));
		}
		return linkDetails;
	}
	
	public boolean hasTwoLinks() {
		return linkDetails.size() == 2;
	}
	
	public boolean hasEqualTransmissionPower() {
		if (hasTwoLinks() == false) {
			return false;
		}
		
		var linkQuantityIterator = linkDetails.values().iterator();
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