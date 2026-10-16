# Window Blinds — Running the GUI and exercise notes

## Run the GUI

The included launchers compile the Java files in **`exercise3/`**, the itemis CREATE/Yakindu runtime classes under **`src/com/yakindu/core/`** and **`src/com/yakindu/core/rx/`**, and the supporting `Clock.java` and `ScaledTimeTimerService.java` files. They then launch the **`WindowBlind`** GUI.

**Requirements:** A Java Development Kit (JDK 8 or newer) containing `javac` and `java`. Run the launcher from the project containing the `src/` and `exercise3/` folders, with the image assets in `html/images/`.

### Windows (PowerShell)

Run`runme.ps1` in the project root:

```powershell
powershell -NoProfile -ExecutionPolicy Bypass -File .\runme.ps1
```

You can also **double-click `runme.cmd`** when both `runme.cmd` and `runme.ps1` are in the project root. If Java compilation fails, the GUI will not launch; check the error shown in the terminal.

### Linux/macOS (Bash)

Run `runme.sh` in the project root:

```bash
bash ./runme.sh
```

Or make it executable once (`chmod +x runme.sh`) and use `./runme.sh`.

### Manual Linux/macOS command (optional)

```bash
mkdir -p /tmp/exercise3-classes && javac -d /tmp/exercise3-classes \
  src/com/yakindu/core/*.java src/com/yakindu/core/rx/*.java \
  src/Clock.java src/ScaledTimeTimerService.java \
  exercise3/WindowBlinds.java exercise3/WindowBlind.java \
  exercise3/WindowBlindPanel.java exercise3/WindowBlindControl.java \
  && java -cp /tmp/exercise3-classes WindowBlind
```


## Exercises 4 and 5 —  GUI comments and changes

- We added a Status labels which is updated by the controls as the blind and its modes change. (blind_state, child_mode, manual_mode, room_temperature, day_and_night)
- We also added a display for the clock to help us visualize the time of day and quality of life when testing. Along side the day and night status
- We ensure to grey out buttons to indicate they are unavailable in the current mode.
- Changed the text of the manual button to indicate whether it will enter or exit manual mode.

## Exercise 6

- Added a `Status` variable or status indicator to make it easier to check whether `window1`, `window2`, and `window3` are in their expected states.

