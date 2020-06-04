package org.palladiosimulator.simexp.service.guice;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.AbstractModule;

//import com.google.inject.multibindings.Multibinder;

public class SRPModule<T, U extends T> extends AbstractModule {

	private final Class<T> required;
	private final List<Class<U>> provided;
	
	public SRPModule(Class<T> required) {
		this.required = required;
		this.provided = new ArrayList<>();
	}
	
	public void addProvidedClass(Class<U> providedClass) {
		provided.add(providedClass);
	}
	
	@Override
	protected void configure() {
//		Multibinder<T> binder = Multibinder.newSetBinder(binder(), required);
//		for (Class<U> each : provided) {
//			binder.addBinding().to(each);
//		}
	}
	
}
