package org.palladiosimulator.simexp.pcm.util;

import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.palladiosimulator.analyzer.workflow.core.blackboard.PCMResourceSetPartition;
import org.palladiosimulator.commons.emfutils.EMFCopyHelper;
import org.palladiosimulator.pcm.core.entity.Entity;
import org.palladiosimulator.pcm.repository.Repository;

import de.uka.ipd.sdq.workflow.mdsd.blackboard.ResourceSetPartition;

public class PcmUtil {

	public static PCMResourceSetPartition copyPCMPartition(ResourceSetPartition partitionToCopy) {
		PCMResourceSetPartition copy = new PCMResourceSetPartition();

		List<EObject> modelCopy = EMFCopyHelper.deepCopyToEObjectList(partitionToCopy.getResourceSet());
		for (int i = 0; i < modelCopy.size(); i++) {
			Optional<URI> uri = retrieveUri(modelCopy.get(i), partitionToCopy);
			if (uri.isPresent()) {
				final Resource resource = copy.getResourceSet().createResource(uri.get());
				resource.getContents().add(modelCopy.get(i));
			}
		}

		copy.resolveAllProxies();

		return copy;
	}

	private static Optional<URI> retrieveUri(EObject model, ResourceSetPartition pcmPartition) {
		if (pcmPartition.hasElement(model.eClass())) {
			List<EObject> result = pcmPartition.getElement(model.eClass());
			if (result.size() == 1) {
				return Optional.of(result.get(0).eResource().getURI());
			} else if (result.size() > 1) {
				if (Entity.class.isInstance(model)) {
					return result.stream().map(Entity.class::cast)
							.filter(e -> e.getId().equals(Entity.class.cast(model).getId()))
							.map(e -> e.eResource().getURI()).findFirst();
				}
			}
		}
		return Optional.empty();
	}

	public static List<String> toIdentifiables(List<Repository> repos) {
		return repos.stream().map(each -> each.getId()).collect(Collectors.toList());
	}

	public static BinaryOperator<String> stringConcatenation() {
		return (s1, s2) -> String.format("%1s_%2s", s1, s2);
	}

}
