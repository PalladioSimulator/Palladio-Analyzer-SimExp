package org.palladiosimulator.simexp.workflow.trafo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;

public class TrafoNameProviderCache implements ITrafoNameProvider {
    private final ITrafoNameProvider trafoNameProvider;

    private final Map<URI, List<String>> experimentCache;

    public TrafoNameProviderCache(ITrafoNameProvider trafoNameProvider) {
        this.trafoNameProvider = trafoNameProvider;
        this.experimentCache = new HashMap<>();
    }

    @Override
    public List<String> getAvailableTransformations(URI experimentsFile) {
        List<String> trafos = experimentCache.get(experimentsFile);
        if (trafos == null) {
            trafos = trafoNameProvider.getAvailableTransformations(experimentsFile);
            experimentCache.put(experimentsFile, trafos);
        }
        return trafos;
    }
}
