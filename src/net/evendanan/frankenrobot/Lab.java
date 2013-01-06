package net.evendanan.frankenrobot;

import android.content.Context;

public class Lab {
	public static final boolean LOG_VERBOSE = true;
	public static final boolean LOG_DEBUG = LOG_VERBOSE && true;
	
	public static FrankenRobot build(Context appContext, int interacesResId, int concreteTypesResId) {
		return new FrankenRobotImpl(appContext, interacesResId, concreteTypesResId);
	}
	
	public static FrankenRobot build(Context appContext, int interfacesAreMapping) {
		return new FrankenRobotImpl(appContext, interfacesAreMapping);
	} 
}
