@echo off

copy ..\jar\bin\immersivemode-ane-jar.jar android

call adt ^
 -package ^
 -target ane ./ImmersiveMode.ane extension.xml ^
 -swc swc/immersivemode-ane-swc.swc ^
 -platform Android-ARM -C android . ^
 -platform Android-x86 -C android . ^
 -platform default -C default . 
