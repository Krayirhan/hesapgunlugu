# Hızlı Refactoring Script
Remove-Item -Path "app\src\main\java\com\example\mynewapp\domain" -Recurse -Force -ErrorAction SilentlyContinue; .\gradlew clean assembleFreeDebug

