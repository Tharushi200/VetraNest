@echo off
REM =============================================================================
REM VetraNest - Windows EXE Packager using jpackage
REM Run this script AFTER: mvn clean package -DskipTests
REM Output: dist\VetraNest-installer.exe  OR  dist\VetraNest\VetraNest.exe
REM =============================================================================

echo [jpackage] Building VetraNest Windows EXE...

REM Clean previous dist
if exist dist rmdir /s /q dist
mkdir dist

REM Use jpackage to create a self-contained Windows app (no JRE required by user)
jpackage ^
  --type app-image ^
  --name VetraNest ^
  --input target ^
  --main-jar VetraNest-1.0.0.jar ^
  --main-class com.vetranest.VetraNestApp ^
  --dest dist ^
  --app-version 1.0.0 ^
  --description "VetraNest Veterinary Clinic Management System" ^
  --vendor "VetraNest Team" ^
  --icon src\main\resources\assets\icons\app_icon.ico ^
  --java-options "-Xmx512m"

if %ERRORLEVEL% EQU 0 (
    echo.
    echo [SUCCESS] EXE built at: dist\VetraNest\VetraNest.exe
    echo [INFO] Share the entire 'dist\VetraNest\' folder or zip it.
) else (
    echo.
    echo [ERROR] jpackage failed. Trying without icon...
    jpackage ^
      --type app-image ^
      --name VetraNest ^
      --input target ^
      --main-jar VetraNest-1.0.0.jar ^
      --main-class com.vetranest.VetraNestApp ^
      --dest dist ^
      --app-version 1.0.0 ^
      --description "VetraNest Veterinary Clinic Management System" ^
      --vendor "VetraNest Team" ^
      --java-options "-Xmx512m"
    if %ERRORLEVEL% EQU 0 (
        echo [SUCCESS] EXE built at: dist\VetraNest\VetraNest.exe
    ) else (
        echo [FAILED] Check that jpackage is in PATH and JAR was built first.
    )
)
pause
