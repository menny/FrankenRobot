package net.evendanan.frankenrobot;

import java.lang.Class;

/**
 * The injection entry point. This interface represents the builder.
 * Use the Lab.build method to create a concrete instance of this interface.
 * @author menny
 *
 */
public interface FrankenRobot {

	/**
	 * Returns an instance of the specified type. This type should be specified
	 * in the interfaces string-array, which was specified in the building of
	 * this object (see <tt>Lab</tt>).
	 * 
	 * If the concrete class has a constructor which can take <tt>Diagram</tt> as an argument, then the provided
	 * instance will be passed to it.
	 * @param diagram
	 *            an object implementing <tt>Diagram</tt>.
	 * @see Lab
	 * @see Diagram
	 */
	<T> T embody(Diagram<T> diagram);

    /**
     * Returns an instance of the specified type. This type should be specified
     * in the interfaces string-array, which was specified in the building of
     * this object (see <tt>Lab</tt>).
     *
     * The default constructor will be used to create a concreate instance.
     * @param interfaceToEmbody the Class representing the monster to create.
     *
     * @see Lab
     * @see Diagram
     */
    <T> T embody(Class<T> interfaceToEmbody);
}
