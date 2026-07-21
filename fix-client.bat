@echo off
chcp 65001 >/dev/null
set "PROJECT_CACHE=D:\殷宇泽\妖怪们的归家-移植\GensokyoLegacy-standalone\GensokyoLegacy-standalone\.gradle\caches\minecraft\versions\1.21.1\client.jar"
set "GOOD_FILE=C:\Users\qiji2\.gradle\caches\minecraft\versions\1.21.1\client.jar"

echo [1/3] Removing read-only if set...
attrib -R "%PROJECT_CACHE%" 2>/dev/null

echo [2/3] Copying correct client.jar...
copy /Y "%GOOD_FILE%" "%PROJECT_CACHE%"

echo [3/3] Setting read-only to prevent overwrite...
attrib +R "%PROJECT_CACHE%"

echo Done. client.jar size:
for %%A in ("%PROJECT_CACHE%") do echo %%~zA bytes
