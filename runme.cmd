@echo off
REM Double-click this file on Windows to compile and launch the GUI.
REM Keep runme.cmd and runme.ps1 together in the project root.
powershell.exe -NoProfile -ExecutionPolicy Bypass -File "%~dp0runme.ps1"
if errorlevel 1 (
    echo.
    echo The GUI did not start. See the error message above.
    pause
)
