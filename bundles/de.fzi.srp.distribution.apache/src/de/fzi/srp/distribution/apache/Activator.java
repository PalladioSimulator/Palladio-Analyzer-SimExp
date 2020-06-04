package de.fzi.srp.distribution.apache;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.palladiosimulator.simexp.service.registry.ServiceEntry;
import org.palladiosimulator.simexp.service.registry.ServiceRegistry;

import de.fzi.srp.distribution.apache.factory.ProbabilityDistributionFactoryAdapter;
import de.fzi.srp.distribution.factory.ProbabilityDistributionFactory;

public class Activator implements BundleActivator {

	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		
		ServiceRegistry.registerService(ServiceEntry.service(ProbabilityDistributionFactory.class).isProvidedBy(ProbabilityDistributionFactoryAdapter.class));
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
