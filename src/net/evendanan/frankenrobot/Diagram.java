package net.evendanan.frankenrobot;

import java.lang.reflect.ParameterizedType;

/**
 * The diagram from which to build the monster.
 * You might ask yourself "why has he made this class abstract?", well, let me answer:
 * two reasons:
 * 1) If you look at the constructor's code, you'll see that I'm referencing to the genericSuperclass, 
 * which exists only if you inherit from this class - abstract will force to do so.
 * 2) I will pass the provided instance, if possible, in the concrete class constructor. This will allow you, the developer,
 * to pass additional parameters while constructing the monster.
 * 
 * @author menny
 *
 * @param <T> the interface to embody
 */
public abstract class Diagram<T> {

	private final Class<?> mInterfaceToEmbody;

	protected Diagram() {
		// using reflection to determine the actual runtime type.
		mInterfaceToEmbody = (Class<?>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
	}

	Class<?> getInterfaceToEmbody() {
		return mInterfaceToEmbody;
	}
	
	/*
	 * You might want to add some more functions, or beans here, for,if the concrete class has a constructor
	 * which take Diagram<T>, then the diagram instance will be passed to it.
	 */
}