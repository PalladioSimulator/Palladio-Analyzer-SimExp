package org.palladiosimulator.simexp.service.registry;

import java.util.List;
import java.util.Optional;

public interface ServiceDiscovery {

	public void setUp(List<ServiceEntry<?>> services);
	
	public <T> Optional<T> findService(Class<T> requiredClass);
	
	public <T> List<T> findServices(Class<T> requiredClass);
	
}
