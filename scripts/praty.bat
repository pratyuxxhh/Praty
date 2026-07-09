@echo off
SET SCRIPT_DIR=%~dp0
IF /I "%~1"=="cd" (
    IF "%~2"=="~" (
        cd /d "C:\Users\ishuk\OneDrive\Desktop"
        exit /b 0
    )
)
java -jar "%SCRIPT_DIR%praty.jar" %*