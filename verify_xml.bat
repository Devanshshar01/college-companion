@echo off
echo ================================================
echo XML Namespace Verification Tool
echo ================================================
echo.

REM Check values/themes.xml
echo Checking: app\src\main\res\values\themes.xml
findstr /C:"xmlns:tools" "app\src\main\res\values\themes.xml" >nul
if %errorlevel% equ 0 (
    echo [✓] Namespace declaration found
) else (
    echo [✗] WARNING: xmlns:tools not found!
)

REM Check values-night/themes.xml  
echo Checking: app\src\main\res\values-night\themes.xml
findstr /C:"xmlns:tools" "app\src\main\res\values-night\themes.xml" >nul
if %errorlevel% equ 0 (
    echo [✓] Namespace declaration found
) else (
    echo [✗] WARNING: xmlns:tools not found!
)

echo.
echo ================================================
echo Verification complete!
echo.
echo If both files show [✓], the XML files are correct.
echo The error is caused by cached build files.
echo.
echo Follow the steps in XML_FIX_GUIDE.md to resolve.
echo ================================================
echo.
pause
