/*
Copyright (c) 2016, Mesmotronic Limited
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
	
  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS 'AS IS' AND
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

package com.mesmotronic.ane
{
	import flash.desktop.NativeApplication;
	import flash.display.Stage;
	import flash.display.StageDisplayState;
	import flash.events.Event;
	import flash.events.StatusEvent;
	import flash.external.ExtensionContext;
	import flash.system.Capabilities;
	
	public class ImmersiveMode
	{
		static public const ANDROID_WINDOW_FOCUS_IN:String = 'androidWindowFocusIn';
		static public const ANDROID_WINDOW_FOCUS_OUT:String = 'androidWindowFocusOut';
		
		static private var context:ExtensionContext;
		static private var _stage:Stage;
		
		/**
		 * Is this ANE supported?
		 */
		public static function get isSupported():Boolean
		{
			return !!context;
		}
		
		public static function get stage():Stage
		{
			return _stage;
		}
		
		public static function set stage(value:Stage):void
		{
			if (!(value is Stage))
			{
				throw new Error('stage cannot be undefined!');
			}
			
			var version:String = Capabilities.version.substr(0,3);
			
			_stage = value;
			
			switch (version)
			{
				case 'AND':
				{
					context = ExtensionContext.createExtensionContext('com.mesmotronic.ane.immersivemode', '');
					
					if (context && context.call('isSupported'))
					{
						_stage.displayState = StageDisplayState.NORMAL;
						
						context.call('init');
						context.addEventListener(StatusEvent.STATUS, context_statusHandler);
						break;
					}
				}
				
				default:
				{
					_stage.displayState = StageDisplayState.FULL_SCREEN_INTERACTIVE;
					context = null;
					break;
				}
			}
		}
		
		/**
		 * The width of the screen in the best available full screen mode
		 * @return		int
		 */
		static public function get fullScreenWidth():int
		{
			if (context) return context.call('immersiveWidth') as int;
			return Capabilities.screenResolutionX;
		}
		
		/**
		 * The height of the screen in the best available full screen mode
		 * @return		int
		 */
		static public function get fullScreenHeight():int
		{
			if (context) return context.call('immersiveHeight') as int;
			return Capabilities.screenResolutionY;
		}
		
		/**
		 * Dispatch status events from the ANE via the NativeApplication object
		 */
		private static function context_statusHandler(event:StatusEvent):void
		{
			NativeApplication.nativeApplication.dispatchEvent(new Event(event.code));
		}
	}
}
