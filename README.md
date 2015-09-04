#FrankenRobot#

[![Build Status](https://api.shippable.com/projects/540f735a21c97efdb898a1ae/badge?branchName=master)](https://app.shippable.com/projects/540f735a21c97efdb898a1ae/builds/latest)

A simple injection library for Android: uses [Android resources qualification mechanism](http://developer.android.com/guide/topics/resources/providing-resources.html#table2) to map interfaces to concrete implementations.<br>
FrankenRobot takes two _string-array_ resources; one of canonical interface names, and the second of canonical concrete implementations.<br>
You can specify a different implementation for every imaginable resource qualifier (be it API level, screen-size, locale, etc.), and FrankenRobot will instantize the most appropriate implementation using Android's resource qualifier mechanism.

##How To Use##
### Dependency ###
I'm using [![JitPack](https://img.shields.io/github/release/menny/FrankenRobot.svg?label=JitPack)](https://jitpack.io/#menny/FrankenRobot) to publish this library.

Add it in your build.gradle at the end of repositories:
```
repositories {
    //...
    maven { url "https://jitpack.io" }
}
```
Step 2. Add the dependency in the form
```
dependencies {
    compile 'com.github.menny:FrankenRobot:1.1.5'
}
```

### Usage ###
This is how [AnySoftKeyboard](https://github.com/AnySoftKeyboard/AnySoftKeyboard) uses FrankenRobot:
Create two _string-array_ resources in the _res/values_ folder (say, in a designated _frankenrobot.xml_ file):<br>
One for the interfaces definition

    <string-array name="frankenrobot_interfaces">
        <item>com.anysoftkeyboard.devicespecific.DeviceSpecific</item>
    </string-array>

And another _string-array_ resource for the concrete implementations

    <string-array name="frankenrobot_concreate_classes">
        <item>@string/frankenrobot_device_specific_implementation</item>
    </string-array>

Specify the concrete implementation class name, first in the _res/values_ folder:

    <string name="frankenrobot_device_specific_implementation">com.anysoftkeyboard.devicespecific.DeviceSpecific_V3</string>

Where special implemenations are needed:<br>
In the _res/values-v5_ folder:

    <string name="frankenrobot_device_specific_implementation">com.anysoftkeyboard.devicespecific.DeviceSpecific_V5</string>

In the _res/values-v7_ folder:

    <string name="frankenrobot_device_specific_implementation">com.anysoftkeyboard.devicespecific.DeviceSpecific_V7</string>

Etc.<br>

Initialize FrankenRobot, and embody the interface

    FrankenRobot frank = Lab.build(getApplicationContext(),
            R.array.frankenrobot_interfaces,
            R.array.frankenrobot_concreate_classes);
    //using a diagram to create the monster
    DeviceSpecific deviceSpecific = frank.embody(new FrankenRobot.Diagram<DeviceSpecific>(){});

It is guaranteed that the returned instance is the most suitable, based on the qualifier rules.<br>
_AnySoftKeyboard_ achives impressive backword compatibility using this method, using API-level-bound implementations, with no complex coding, and no reflection.
