package org.palladiosimulator.simexp.service.registry;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServiceRegistry {
	
	private final static class DefaultServiceDiscovery implements ServiceDiscovery {

		@Override
		public void setUp(List<ServiceEntry<?>> services) {
			
		}

		@Override
		public <T> Optional<T> findService(Class<T> requiredClass) {
			for (ServiceEntry<?> each : register) {
				if (each.getRequiredClass().equals(requiredClass)) {
					return Optional.ofNullable(toServiceImpl(each));
				}
			}
			return Optional.empty();
		}

		@Override
		public <T> List<T> findServices(Class<T> requiredClass) {
			List<T> results = new ArrayList<>();
			for (ServiceEntry<?> each : register) {
				if (each.getRequiredClass().equals(requiredClass)) {
					results.add(toServiceImpl(each));
				}
			}
			return results;
		}
		
		@SuppressWarnings("unchecked")
		private <T> T toServiceImpl(ServiceEntry<?> serviceEntry) {
			T result = null;
			try {
				result = (T) serviceEntry.getProvidedClass().getDeclaredConstructor().newInstance();
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				//TODO logging
			}
			return result;
		}
		
	}
	
	private final static List<ServiceEntry<?>> register = new ArrayList<>();
	
	private static ServiceDiscovery discoveryInstance = null;
	
	private ServiceRegistry() {
		
	}
	
	public static ServiceDiscovery get() {
		if (discoveryInstance == null) {
			//discoveryInstance = new GuiceServiceDiscovery();
			discoveryInstance = new DefaultServiceDiscovery();
			discoveryInstance.setUp(register);
		}
		return discoveryInstance;
	}

	public static void registerService(ServiceEntry<?> serviceToRegister) {
		register.add(serviceToRegister);
	}
	
}
