/*
Copyright (c) 2015, Mesmotronic Limited
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

* Redistributions of source code must retain the above copyright notice, this
  list of conditions and the following disclaimer.

* Redistributions in binary form must reproduce the above copyright notice, this
  list of conditions and the following disclaimer in the documentation and/or
  other materials provided with the distribution.
	
* Neither the name of the {organization} nor the names of its
  contributors may be used to endorse or promote products derived from
  this software without specific prior written permission.
	
  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.mesmotronic.ane.immersivemode;

import java.util.HashMap;
import java.util.Map;

import android.os.Build;
import android.util.Log;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.view.accessibility.AccessibilityEvent;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.mesmotronic.ane.immersivemode.functions.ImmersiveHeightFunction;
import com.mesmotronic.ane.immersivemode.functions.ImmersiveWidthFunction;
import com.mesmotronic.ane.immersivemode.functions.InitFunction;
import com.mesmotronic.ane.immersivemode.functions.IsSupportedFunction;

public class ImmersiveModeContext extends FREContext 
{

    final public static String TAG = "ImmersiveANE";
	@Override
	public void dispose() 
	{
		// Not required
	}
	
	@Override
	public Map<String, FREFunction> getFunctions() 
	{
		Map<String, FREFunction> functions = new HashMap<String, FREFunction>();
		
		functions.put("init", new InitFunction());
		functions.put("isSupported", new IsSupportedFunction());
		functions.put("immersiveWidth", new ImmersiveWidthFunction());
		functions.put("immersiveHeight", new ImmersiveHeightFunction());

		return functions;
	}
	
	private Window getWindow()
	{
		return getActivity().getWindow();
	}
	
	private View getDecorView()
	{
		return getWindow().getDecorView();
	}
	
	private void setSystemUiVisibility(int visibility)
	{
		getDecorView().setSystemUiVisibility(visibility);
	}
	
	public void init()
	{
		try
		{
            final Window window = getWindow();
            final Window.Callback windowCallback = getWindow().getCallback();

            final int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE // 16
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // 16
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // 16
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // 14
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // 16
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY; // 19

			// If it's supported, switch to immersive mode
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
			{
                Log.d(TAG, "Setting Immersive mode");
				setSystemUiVisibility(uiOptions);

				// It's sticky, so we add listeners to ensure immersive mode is maintained
                final View.OnFocusChangeListener focusChangeListener = getDecorView().getOnFocusChangeListener();
                final View decorView = getDecorView();
				decorView.setOnFocusChangeListener(new View.OnFocusChangeListener() 
				{
					@Override
					public void onFocusChange(View v, boolean hasFocus) 
					{
						if (hasFocus)
						{
							setSystemUiVisibility(uiOptions);
						}
						focusChangeListener.onFocusChange(v, hasFocus);
					}
				});
				
				decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener()
				{
					@Override
					public void onSystemUiVisibilityChange(int visibility) 
					{
						setSystemUiVisibility(uiOptions);
					}
				});
			}
			
			window.setCallback(new Window.Callback()
			{
				
				@Override
				public ActionMode onWindowStartingActionMode(Callback callback) 
				{
					return windowCallback.onWindowStartingActionMode(callback);
				}
				
				@Override
				public void onWindowFocusChanged(boolean hasFocus) 
				{
					// If it's supported, switch back to immersive mode
					if (hasFocus && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
					{
						setSystemUiVisibility(uiOptions);
					}
					
					try
					{
						String type = hasFocus
							? "androidWindowFocusIn"
							: "androidWindowFocusOut";
						
						dispatchStatusEventAsync(type, "");
					}
					catch (Exception e)
					{
						// Ignore errors
					}
					
					windowCallback.onWindowFocusChanged(hasFocus);
				}
				
				@Override
				public void onWindowAttributesChanged(LayoutParams attrs) 
				{
					windowCallback.onWindowAttributesChanged(attrs);
				}
				
				@Override
				public boolean onSearchRequested() 
				{
					return windowCallback.onSearchRequested();
				}
				
				@Override
				public boolean onPreparePanel(int featureId, View view, Menu menu) 
				{
					return windowCallback.onPreparePanel(featureId, view, menu);
				}
				
				@Override
				public void onPanelClosed(int featureId, Menu menu)
				{
					windowCallback.onPanelClosed(featureId, menu);
				}
				
				@Override
				public boolean onMenuOpened(int featureId, Menu menu) 
				{
					return windowCallback.onMenuOpened(featureId, menu);
				}
				
				@Override
				public boolean onMenuItemSelected(int featureId, MenuItem item) 
				{
					return windowCallback.onMenuItemSelected(featureId, item);
				}
				
				@Override
				public void onDetachedFromWindow() 
				{
					windowCallback.onDetachedFromWindow();
				}
				
				@Override
				public View onCreatePanelView(int featureId) 
				{
					return windowCallback.onCreatePanelView(featureId);
				}
				
				@Override
				public boolean onCreatePanelMenu(int featureId, Menu menu) 
				{
					return windowCallback.onCreatePanelMenu(featureId, menu);
				}
				
				@Override
				public void onContentChanged()
				{
					windowCallback.onContentChanged();
				}
				
				@Override
				public void onAttachedToWindow() 
				{
					windowCallback.onAttachedToWindow();
				}
				
				@Override
				public void onActionModeStarted(ActionMode mode)
				{
					windowCallback.onActionModeStarted(mode);
				}
				
				@Override
				public void onActionModeFinished(ActionMode mode) 
				{
					windowCallback.onActionModeFinished(mode);
				}
				
				@Override
				public boolean dispatchTrackballEvent(MotionEvent event) 
				{
					return windowCallback.dispatchTrackballEvent(event);
				}
				
				@Override
				public boolean dispatchTouchEvent(MotionEvent event) 
				{
					return windowCallback.dispatchTouchEvent(event);
				}
				
				@Override
				public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) 
				{
					return windowCallback.dispatchPopulateAccessibilityEvent(event);
				}
				
				@Override
				public boolean dispatchKeyShortcutEvent(KeyEvent event) 
				{
					return windowCallback.dispatchKeyShortcutEvent(event);
				}
				
				@Override
				public boolean dispatchKeyEvent(KeyEvent event) 
				{
					return windowCallback.dispatchKeyEvent(event);
				}
				
				@Override
				public boolean dispatchGenericMotionEvent(MotionEvent event) 
				{
					return windowCallback.dispatchGenericMotionEvent(event);
				}
			});
		}
		catch (Exception e)
		{
            Log.w(TAG, e.toString());
		}
	}
}
