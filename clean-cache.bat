@echo off
echo ============================================
echo Gradle Cache Temizleme Scripti
echo ============================================
echo.

REM Gradle daemon durdur
echo [1/5] Gradle Daemon durduruluyor...
call gradlew.bat --stop 2>nul
taskkill /F /IM java.exe /T 2>nul

echo.
echo [2/5] Proje build klasorleri temizleniyor...
cd /d "%~dp0"

REM Proje build klasorlerini sil
if exist "build" rmdir /s /q "build"
if exist "app\build" rmdir /s /q "app\build"
if exist ".gradle" rmdir /s /q ".gradle"

REM Core modulleri
for /d %%i in (core\*) do (
    if exist "%%i\build" rmdir /s /q "%%i\build"
)

REM Feature modulleri
for /d %%i in (feature\*) do (
    if exist "%%i\build" rmdir /s /q "%%i\build"
)

REM Diger moduller
if exist "baselineprofile\build" rmdir /s /q "baselineprofile\build"
if exist "benchmark-macro\build" rmdir /s /q "benchmark-macro\build"

echo.
echo [3/5] Gradle transforms cache temizleniyor...
REM Tum transforms klasorlerini sil
for /d %%i in ("%USERPROFILE%\.gradle\caches\*") do (
    if exist "%%i\transforms" (
        echo Siliniyor: %%i\transforms
        rmdir /s /q "%%i\transforms" 2>nul
    )
)

echo.
echo [4/5] Gradle 8.13 cache tamamen temizleniyor...
if exist "%USERPROFILE%\.gradle\caches\8.13" (
    echo Siliniyor: %USERPROFILE%\.gradle\caches\8.13
    rmdir /s /q "%USERPROFILE%\.gradle\caches\8.13"
)

echo.
echo [5/5] Kotlin metadata temizleniyor...
if exist "%USERPROFILE%\.gradle\caches\modules-2\metadata-*" (
    for /d %%i in ("%USERPROFILE%\.gradle\caches\modules-2\metadata-*") do (
        rmdir /s /q "%%i" 2>nul
    )
)

echo.
echo ============================================
echo TAMAMLANDI!
echo ============================================
echo.
echo Lutfen su adimlari takip edin:
echo 1. Android Studio'yu tamamen kapatin
echo 2. Bu scripti calistirin (calistirdiniz)
echo 3. Android Studio'yu yeniden acin
echo 4. "File ^> Sync Project with Gradle Files" yapin
echo.
pause
