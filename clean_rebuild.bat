@echo off
echo Cleaning College Companion Project...
echo.

REM Navigate to project directory
cd /d "%~dp0"

echo Step 1: Deleting build directories...
if exist "build" rmdir /s /q "build"
if exist "app\build" rmdir /s /q "app\build"
if exist ".gradle" rmdir /s /q ".gradle"

echo Step 2: Deleting Android Studio cache files...
if exist ".idea\caches" rmdir /s /q ".idea\caches"
if exist ".idea\libraries" rmdir /s /q ".idea\libraries"

echo Step 3: Deleting Gradle cache...
if exist "%USERPROFILE%\.gradle\caches\transforms-3" rmdir /s /q "%USERPROFILE%\.gradle\caches\transforms-3"
if exist "%USERPROFILE%\.gradle\caches\build-cache-1" rmdir /s /q "%USERPROFILE%\.gradle\caches\build-cache-1"

echo.
echo Clean complete!
echo.
echo Next steps:
echo 1. Open Android Studio
echo 2. Click File ^> Invalidate Caches... ^> Invalidate and Restart
echo 3. After restart, click File ^> Sync Project with Gradle Files
echo 4. Run your app
echo.
pause
