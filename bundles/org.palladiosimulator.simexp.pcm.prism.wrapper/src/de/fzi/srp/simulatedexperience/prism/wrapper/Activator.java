package de.fzi.srp.simulatedexperience.prism.wrapper;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.palladiosimulator.simexp.pcm.prism.service.PrismService;
import org.palladiosimulator.simexp.service.registry.ServiceEntry;
import org.palladiosimulator.simexp.service.registry.ServiceRegistry;

import de.fzi.srp.simulatedexperience.prism.wrapper.service.PrismInvocationService;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		ServiceRegistry.registerService(ServiceEntry.service(PrismService.class).isProvidedBy(PrismInvocationService.class));
	}

	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
