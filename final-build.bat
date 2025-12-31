@echo off
echo ================================================
echo   MyNewApp - Final Build Script
echo ================================================
echo.

echo [1/4] Gradle daemon durduruluyor...
call gradlew.bat --stop
timeout /t 2 >nul

echo [2/4] Clean yapiliyor...
call gradlew.bat clean
timeout /t 2 >nul

echo [3/4] Build baslatiliyor...
echo.
call gradlew.bat assembleFreeDebug --stacktrace

echo.
echo ================================================
if %ERRORLEVEL% EQU 0 (
    echo   BUILD BASARILI!
    echo   APK konumu: app\build\outputs\apk\free\debug\
) else (
    echo   BUILD HATASI! Yukaridaki hatalari kontrol edin.
)
echo ================================================
echo.

pause

