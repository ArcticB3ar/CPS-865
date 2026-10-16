#!/usr/bin/env bash
# Compile and launch the Exercise 3 WindowBlind GUI on Linux/macOS.
# Put this file in the project root beside src/ and exercise3/.
set -euo pipefail

project_root="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$project_root"

if ! command -v javac >/dev/null 2>&1; then
    echo 'A JDK (javac) is required. Install JDK 8 or newer.' >&2
    exit 1
fi
if ! command -v java >/dev/null 2>&1; then
    echo 'Java runtime (java) not found.' >&2
    exit 1
fi

required=(
    src/Clock.java
    src/ScaledTimeTimerService.java
    exercise3/WindowBlinds.java
    exercise3/WindowBlind.java
    exercise3/WindowBlindPanel.java
    exercise3/WindowBlindControl.java
)
for path in "${required[@]}"; do
    if [[ ! -f "$path" ]]; then
        echo "Missing source file: $path" >&2
        exit 1
    fi
done

shopt -s nullglob
core_sources=(src/com/yakindu/core/*.java)
rx_sources=(src/com/yakindu/core/rx/*.java)
if (( ${#core_sources[@]} == 0 || ${#rx_sources[@]} == 0 )); then
    echo 'Missing Yakindu runtime files in src/com/yakindu/core/ or src/com/yakindu/core/rx/.' >&2
    exit 1
fi

sources=("${core_sources[@]}" "${rx_sources[@]}" "${required[@]}")
for optional in src/PausableClockTimerService.java exercise3/PausableClockTimerService.java; do
    [[ ! -f "$optional" ]] || sources+=("$optional")
done

build_dir="$project_root/.runme-build/exercise3"
mkdir -p "$build_dir"

echo 'Compiling WindowBlind GUI...'
javac -d "$build_dir" "${sources[@]}"

for asset in html/images/window-blind-open.png html/images/window-blind-half-open.png html/images/window-blind-closed.png; do
    if [[ ! -f "$asset" ]]; then
        echo "Warning: image not found: $asset" >&2
    fi
done

if grep -q 'window-blind-manual-mode.png' exercise3/WindowBlindPanel.java && [[ ! -f html/images/window-blind-manual-mode.png ]]; then
    echo 'Warning: manual-mode image not found: html/images/window-blind-manual-mode.png' >&2
fi

echo 'Launching WindowBlind GUI...'
java -cp "$build_dir" WindowBlind
