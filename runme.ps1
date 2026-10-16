# Compile and launch the Exercise 3 WindowBlind GUI.
# Put this file in the project root beside the src/ and exercise3/ folders.
# Run with: powershell -NoProfile -ExecutionPolicy Bypass -File .\runme.ps1

$ErrorActionPreference = 'Stop'
Set-Location -LiteralPath $PSScriptRoot

# Use the JDK for both compiling and running to avoid Java version mismatches.
$compiler = $null
if ($env:JAVA_HOME) {
    $candidate = Join-Path $env:JAVA_HOME 'bin\javac.exe'
    if (Test-Path -LiteralPath $candidate -PathType Leaf) {
        $compiler = $candidate
    }
}
if (-not $compiler) {
    $cmd = Get-Command 'javac.exe' -ErrorAction SilentlyContinue
    if ($cmd) { $compiler = $cmd.Source }
}
if (-not $compiler) {
    throw 'No JDK javac.exe found. Install a Java JDK (8 or newer) and put its bin folder on PATH, or set JAVA_HOME to the JDK folder.'
}

$java = Join-Path (Split-Path -Parent $compiler) 'java.exe'
if (-not (Test-Path -LiteralPath $java -PathType Leaf)) {
    $cmd = Get-Command 'java.exe' -ErrorAction SilentlyContinue
    if (-not $cmd) { throw 'Cannot find java.exe to run the application.' }
    $java = $cmd.Source
}

# These are the source files used by the command from the README.
$requiredFiles = @(
    'src/Clock.java',
    'src/ScaledTimeTimerService.java',
    'exercise3/WindowBlinds.java',
    'exercise3/WindowBlind.java',
    'exercise3/WindowBlindPanel.java',
    'exercise3/WindowBlindControl.java'
)
$runtimeFolders = @('src/com/yakindu/core', 'src/com/yakindu/core/rx')
foreach ($folder in $runtimeFolders) {
    if (-not (Test-Path -LiteralPath $folder -PathType Container)) {
        throw "Missing source folder: $folder (runme.ps1 must be in the project root)."
    }
}
foreach ($file in $requiredFiles) {
    if (-not (Test-Path -LiteralPath $file -PathType Leaf)) {
        throw "Missing source file: $file"
    }
}

$sources = @()
foreach ($folder in $runtimeFolders) {
    $sources += @(Get-ChildItem -LiteralPath $folder -Filter '*.java' -File | ForEach-Object { $_.FullName })
}
foreach ($file in $requiredFiles) {
    $sources += (Resolve-Path -LiteralPath $file).Path
}
# Your alternate version uses this class to pause/resume the simulated Clock.
# Include it only if it exists in the selected project.
foreach ($optionalFile in @('src/PausableClockTimerService.java', 'exercise3/PausableClockTimerService.java')) {
    if (Test-Path -LiteralPath $optionalFile -PathType Leaf) {
        $sources += (Resolve-Path -LiteralPath $optionalFile).Path
    }
}

$buildDir = Join-Path $PSScriptRoot '.runme-build\exercise3'
New-Item -ItemType Directory -Path $buildDir -Force | Out-Null

Write-Host 'Compiling WindowBlind GUI...'
& $compiler -d $buildDir @sources
if ($LASTEXITCODE -ne 0) {
    throw "Java compilation failed (exit code $LASTEXITCODE). The GUI was not started."
}

foreach ($file in @('html/images/window-blind-open.png', 'html/images/window-blind-half-open.png', 'html/images/window-blind-closed.png')) {
    if (-not (Test-Path -LiteralPath $file -PathType Leaf)) {
        Write-Warning "Image not found: $file (check the html/images folder if the GUI displays blank images)."
    }
}

$manualAsset = 'html/images/window-blind-manual-mode.png'
$panelSource = Get-Content -LiteralPath 'exercise3/WindowBlindPanel.java' -Raw
if ($panelSource.Contains('window-blind-manual-mode.png') -and
    -not (Test-Path -LiteralPath $manualAsset -PathType Leaf)) {
    Write-Warning "Manual-mode image not found: $manualAsset"
}

Write-Host 'Launching WindowBlind GUI...'
& $java -cp $buildDir WindowBlind
if ($LASTEXITCODE -ne 0) {
    throw "WindowBlind exited with code $LASTEXITCODE."
}
