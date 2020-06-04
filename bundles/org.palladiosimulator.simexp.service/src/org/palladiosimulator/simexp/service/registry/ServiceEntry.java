package org.palladiosimulator.simexp.service.registry;

public class ServiceEntry<T> {
	
	private final Class<T> requiredClass;
	private final Class<? extends T> providedClass; 
	
	private ServiceEntry(Class<T> requiredClass, Class<? extends T> providedClass) {
		this.requiredClass = requiredClass;
		this.providedClass = providedClass;
	}

	public Class<T> getRequiredClass() {
		return requiredClass;
	}
	
	public Class<? extends T> getProvidedClass() {
		return providedClass;
	}
	
	public static class ServiceEntryBuilder<U> {
		
		private Class<U> required;
		
		public ServiceEntryBuilder(Class<U> required) {
			this.required = required;
		}
		
		public ServiceEntry<U> isProvidedBy(Class<? extends U> provided) {
			if (required.isAssignableFrom(provided)) {
				return new ServiceEntry<U>(required, provided); 
			}
			
			//TODO exception handling
			throw new RuntimeException("");
		}
	}
	
	public static <U> ServiceEntryBuilder<U> service(Class<U> required) {
		return new ServiceEntryBuilder<U>(required);
	}
	
}
