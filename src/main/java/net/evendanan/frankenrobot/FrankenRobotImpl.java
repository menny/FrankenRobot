package net.evendanan.frankenrobot;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.util.HashMap;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;

class FrankenRobotImpl implements FrankenRobot {

	private static final String TAG = "FrankenRobotImpl";
	private final HashMap<Class<?>, Class<?>> mMonsters = new HashMap<Class<?>, Class<?>>();

	public FrankenRobotImpl(Context appContext, int interacesResId,
			int concreteTypesResId) {
		Resources res = appContext.getResources();
		init(res.getStringArray(interacesResId), res.getStringArray(concreteTypesResId));
	}
	
	public FrankenRobotImpl(Context appContext, int interfacesAreMapping) {
		Resources res = appContext.getResources();
		String[] interfacesAreStrings = res.getStringArray(interfacesAreMapping);
		
		String[] interfaces = new String[interfacesAreStrings.length];
		String[] concretes = new String[interfacesAreStrings.length];
		
		for(int i=0; i<interfacesAreStrings.length; i++) {
			final String aMapping = interfacesAreStrings[i];
			if (TextUtils.isEmpty(aMapping))
				throw new InvalidParameterException("A mapping can not be empty!");
			if (Lab.LOG_VERBOSE)
				Log.d(TAG, "Raw mapping: "+aMapping);
			String[] split = interfacesAreStrings[i].split(" is ");
			if (split == null || split.length != 2)
				throw new InvalidParameterException("interfacesAreMapping array should be in the format of 'interface is concrete'. Entry "+interfacesAreStrings[i]);
			interfaces[i] = split[0];
			concretes[i] = split[1];
			//translating concrete if needed
			if (concretes[i].startsWith("@string/")) {
				int concreateId = res.getIdentifier(
						concretes[i].substring("@string/".length()), 
						"string", appContext.getPackageName());
				if (concreateId == 0)
					throw new InvalidParameterException("Could not find a string resource with name "+concretes[i]);
				concretes[i] = res.getString(concreateId);
			}
		}
		
		init(interfaces, concretes);
	}
	
	public FrankenRobotImpl(String[] interfaces, String[] actualClasses) {
		// basic sanity. More to follow.
		init(interfaces, actualClasses);
	}

	protected void init(String[] interfaces, String[] actualClasses) {
		if (interfaces == null || interfaces.length == 0)
			throw new InvalidParameterException(
					"interacesResId returned an empty interfaces list!");
		if (actualClasses == null || actualClasses.length == 0)
			throw new InvalidParameterException(
					"concreteTypesResId returned an empty classes list!");

		if (actualClasses.length != interfaces.length)
			throw new InvalidParameterException(
					"It does not make sense have a different count of interfaces and classes! interacesResId and concreteTypesResId should return a same length strings array.");

		// more sanity
		for (int i = 0; i < interfaces.length; i++) {
			if (TextUtils.isEmpty(interfaces[i]))
				throw new InvalidParameterException(
						"Interface can not be an empty string! Interface at index "
								+ i);
			//when requesting getString, and the value is @null, the framework will return @0
			//so I convert it to null. Note, that when requesting string-array, it will
			//return as null! Ho, the confusion!
			if ("@0".equals(actualClasses[i])) actualClasses[i] = null;
			
			// the implementation CAN be null!!
			if (actualClasses[i] == null && Lab.LOG_DEBUG)
				Log.d(TAG,
						"Concrete class is a NULL entry. This is OK, but please note. Concrete class at index "
								+ i);
		}

		// let's do some reflection
		for (int i = 0; i < interfaces.length; i++) {
			final String anInterface = interfaces[i];
			final String aConcreteClass = actualClasses[i];
			if (Lab.LOG_DEBUG)
				Log.d(TAG, "Interface " + anInterface
						+ " is mapped to concrete class " + aConcreteClass);
			final Class<?> interfaceType;
			final Class<?> actualType;
			try {
				interfaceType = Class.forName(anInterface);
			} catch (ClassNotFoundException e) {
				throw new InvalidParameterException(
						"Unable to locate the specified interface "
								+ anInterface
								+ " in the application's ClassLoader! A typo?");
			}
			if (mMonsters.containsKey(interfaceType))
				throw new InvalidParameterException(
						"The interface "
								+ anInterface
								+ " is mentioned twice in the interacesResId strings array!");
			if (aConcreteClass == null) {
				actualType = null;
				Log.d(TAG, "Concrete class is a NULL entry. This is OK, but please note.");
			} else {
				try {
					actualType = Class.forName(aConcreteClass);
				} catch (ClassNotFoundException e) {
					throw new InvalidParameterException(
							"Unable to locate the specified concrete class "
									+ aConcreteClass
									+ " in the application's ClassLoader! A typo?");
				}
			}
			mMonsters.put(interfaceType, actualType);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T embody(Diagram<T> diagram) {
		final Class<?> interfaceToEmbody = diagram.getInterfaceToEmbody();
		
		if (!mMonsters.containsKey(interfaceToEmbody))
			throw new InvalidParameterException("The specified interface "
					+ interfaceToEmbody.getName()
					+ " does not exist in the list of interfaces!");

		Class<?> concreteClass = mMonsters.get(interfaceToEmbody);
		//As I said, I will allow NULL entries.
		if (concreteClass == null)
			return null;
		
		try {
			Constructor<?> diagramConstructor;
			try {
				diagramConstructor = concreteClass.getConstructor(diagram.getClass());
			} catch(NoSuchMethodException e) {
				diagramConstructor = null;
			}
			
			if (diagramConstructor != null) {
				//dev specified. It is required to instantiaze a specific constructor
				try {
					return (T) diagramConstructor.newInstance(diagram);
				} catch (InstantiationException e) {
					String message = "Failed to instantiaze concrete class "
							+ concreteClass.getName()
							+ " when requested interface "
							+ interfaceToEmbody.getName()
							+ " due to InstantiationException (is a contructor for Diagram exists?) with message "
							+ e.getMessage();
					Log.e(TAG, message);
					throw new InvalidParameterException(message);
				} catch (IllegalArgumentException e) {
					//Ok... This is weird. I requested a specified constructor, provided that exactly
					//specified arguments, and it fails.. weird.
					String message = "Failed to instantiaze concrete class "
							+ concreteClass.getName()
							+ " when requested interface "
							+ interfaceToEmbody.getName()
							+ " due to IllegalArgumentException (it is weird. I have no tips for how to fix it.) with message "
							+ e.getMessage();
					Log.e(TAG, message);
					throw new InvalidParameterException(message);
				}
			} else {
				try {
					return (T) concreteClass.newInstance();
				} catch (InstantiationException e) {
					String message = "Failed to instantiaze concrete class "
							+ concreteClass.getName()
							+ " when requested interface "
							+ interfaceToEmbody.getName()
							+ " due to InstantiationException (is a default contructor exists?) with message "
							+ e.getMessage();
					Log.e(TAG, message);
					throw new InvalidParameterException(message);
				}
			}
		} catch (IllegalAccessException e) {
			String message = "Failed to instantiaze concrete class "
					+ concreteClass.getName()
					+ " when requested interface "
					+ interfaceToEmbody.getName()
					+ " due to IllegalAccessException (is the custroctor marked as public?) with message "
					+ e.getMessage();
			Log.e(TAG, message);
			throw new InvalidParameterException(message);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			String message = "Failed to instantiaze concrete class "
					+ concreteClass.getName()
					+ " when requested interface "
					+ interfaceToEmbody.getName()
					+ " due to InvocationTargetException with message "
					+ e.getMessage();
			Log.e(TAG, message);
			throw new InvalidParameterException(message);
		}
	}
}
