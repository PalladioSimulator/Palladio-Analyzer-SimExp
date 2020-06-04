package org.palladiosimulator.simexp.service.guice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.palladiosimulator.simexp.service.registry.ServiceDiscovery;
import org.palladiosimulator.simexp.service.registry.ServiceEntry;

import com.google.inject.AbstractModule;
import com.google.inject.Binding;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

public class GuiceServiceDiscovery implements ServiceDiscovery {
	
	private class ServiceEntryAllocator {
		
		private Map<String, AbstractModule> allocations;
		
		public ServiceEntryAllocator() {
			allocations = new HashMap<>();
		}
		
		public List<AbstractModule> getModules() {
			return new ArrayList<AbstractModule>(allocations.values());
		}
		
		public <T> void allocate(ServiceEntry<T> entry) {
			String key = entry.getRequiredClass().getSimpleName();
			if (allocations.containsKey(key)) {
				allocateExistingBinding(key, entry);
			} else {
				allocateNewBinding(entry.getRequiredClass(), entry.getProvidedClass());
			}
		}

		private <T,U extends T> void allocateNewBinding(Class<T> required, Class<U> provided) {
			SRPModule<T, U> module = new SRPModule<T, U>(required);
			module.addProvidedClass(provided);
			allocations.put(required.getSimpleName(), module);			
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		private void allocateExistingBinding(String key, ServiceEntry<?> entry) {
			((SRPModule) allocations.get(key)).addProvidedClass(entry.getProvidedClass());
		}
		
	}
	
	private Injector injector;
	
	@Override
	public void setUp(List<ServiceEntry<?>> services) {
		ServiceEntryAllocator serviceAllocator = new ServiceEntryAllocator();
		for (ServiceEntry<?> each : services) {
			serviceAllocator.allocate(each);
		}
		createInjector(serviceAllocator.getModules());
	}

	private void createInjector(List<AbstractModule> modules) {
		if (modules.isEmpty() == false) {
			injector = Guice.createInjector(modules);
		}
	}

	@Override
	public <T> Optional<T> findService(Class<T> required) {
		return filterBindings(required).map(r -> r.getProvider().get()).findFirst();
	}

	@Override
	public <T> List<T> findServices(Class<T> required) {
		return filterBindings(required).map(each -> each.getProvider().get()).collect(Collectors.toList());
	}
	
	private <T> Stream<Binding<T>> filterBindings(Class<T> required) {
		return injector.findBindingsByType(TypeLiteral.get(required)).stream();
	}
	
}
