Immersive Mode ANE for Adobe AIR
================================

This is a simplified version of our popular [Android Full Screen ANE](https://github.com/mesmotronic/air-ane-fullscreen) that puts your app into the best available full screen mode.

On Android 4.4+, it switches your app into [immersive mode](http://developer.android.com/training/system-ui/immersive.html), falling back to `FULL_SCREEN_INTERACTIVE` display state on earlier versions of Android and other mobile platforms.

Released under BSD license, see LICENSE for details. Requires Adobe AIR 20+.

Just give me the ANE!
---------------------

Download the latest ANE from the [releases page](https://github.com/mesmotronic/air-ane-fullscreen/releases) on GitHub.

Before you start
----------------

To avoid cropping, ensure that you're using `<fullScreen>false</fullScreen>` in your `app.xml` and `stage.displayState` is set to `StageDisplayState.NORMAL`.

Code example
------------

Using the ANE in your app couldn't be easier:

```as3
import com.mesmotronic.ane.ImmersiveMode;

// Put your app into the best available full screen mode

ImmersiveMode.stage = stage;

// Get the maximum full screen resolution of your app  

trace(ImmersiveMode.fullScreenWidth);
trace(ImmersiveMode.fullScreenHeight);

// Events (should work on most versions of Android)

function focusHandler(event:Event):void
{
	trace(event.type);
} 

NativeApplication.nativeApplication.addEventListener(ImmersiveMode.ANDROID_WINDOW_FOCUS_IN, focusHandler);
NativeApplication.nativeApplication.addEventListene(ImmersiveMode.ANDROID_WINDOW_FOCUS_OUT, focusHandler);
```

Starling
--------

To use this ANE with Starling,  add `Starling.handleLostContext = true;` at the start of your ActionScript code to prevent Stage3D lost context errors breaking your app when it switches into immersive mode.

If you need to fix it, fork it!
-------------------------------

This is a free, open-source project, so if you find the ANE doesn't work as you might like with a specific device, configuration or library you're using: fork it, fix it, let us know.
