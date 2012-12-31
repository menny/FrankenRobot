package net.evendanan.frankenrobot;

/*
 * The injection entry point. This interface represents the builder.
 * Use the Lab.build method to create a concrete instance of this interface.
 */
public interface FrankenRobot {
	/**
	 * Returns an instance of the specified type.
	 * This type should be specified in the interfaces string-array, which was specified in the building of this object (see <tt>Lab</tt>).
	 * 
	 * @param interfaceToEmbody the interface to create a concrete class for.
	 * @see Lab
	 */
	Object embody(Class<?> interfaceToEmbody);
}
