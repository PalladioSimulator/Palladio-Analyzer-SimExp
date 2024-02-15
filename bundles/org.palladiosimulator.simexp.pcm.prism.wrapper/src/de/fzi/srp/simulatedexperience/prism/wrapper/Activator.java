package de.fzi.srp.simulatedexperience.prism.wrapper;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.Platform;
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

	@Override
	public void start(BundleContext bundleContext) throws Exception {
		ILog logger = Platform.getLog(getClass());
		Activator.context = bundleContext;
		
		String prism = System.getenv("PRISM");
		if (prism == null) {
			// In the launcher under Environment add environment variable 'PATH' with '${env_var:PRISM}\lib'
			// See https://github.com/prismmodelchecker/prism/wiki/Setting-up-Eclipse
			logger.error("environment variable 'PRISM' not found -> unable to register PrismService");
		} else {
			ServiceRegistry.registerService(ServiceEntry.service(PrismService.class).isProvidedBy(PrismInvocationService.class));
		}
	}

	@Override
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
