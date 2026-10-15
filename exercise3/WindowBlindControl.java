import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class WindowBlindControl extends JPanel {
    private static final long serialVersionUID = 1L;

    private enum BlindPosition {
        OPEN,
        HALF_OPEN,
        CLOSED
    }

    private final WindowBlindPanel win;
    public static JButton onButton, offButton, halfButton, toggleButton, childButton, manualButton;
    protected static boolean manual_mode, blind_open, blind_close, blind_half_open, child_mode, day_time;
    protected static long room_temp;
    private final WindowBlinds windowBlind;
    private final Clock clock;
    private final ScaledTimeTimerService blindTimerService;
    private final ScaledTimeTimerService clockTimerService;
    private final Timer refreshTimer;
    private BlindPosition rememberedBlindPosition;
    private boolean rememberedChildMode;

    public WindowBlindControl(WindowBlindPanel blindPanel) {
        this.win = blindPanel;
        // ADDED: Initialize the statechart and clock, and set up the buttons and their action listeners.
        blindTimerService = new ScaledTimeTimerService();
        clockTimerService = new ScaledTimeTimerService();
        clock = new Clock();
        windowBlind = new WindowBlinds();
        clock.setTimerService(clockTimerService);
        windowBlind.setTimerService(blindTimerService);
        windowBlind.setClock(clock);
        windowBlind.enter();

        onButton = new JButton("Open");
        onButton.setMnemonic('o');
        onButton.setToolTipText("Open the blind");
        onButton.addActionListener(event -> {
            performBlindAction(windowBlind::raiseOpen);
        });

        offButton = new JButton("Close");
        offButton.setMnemonic('f');
        offButton.setToolTipText("Close the blind");
        offButton.addActionListener(event -> {
            performBlindAction(windowBlind::raiseClose);
        });

        halfButton = new JButton("Half Open");
        halfButton.setMnemonic('h');
        halfButton.setToolTipText("Half open the blind");
        halfButton.addActionListener(event -> {
            performBlindAction(windowBlind::raiseHalf_open);
        });

        toggleButton = new JButton("Temp");
        toggleButton.setMnemonic('t');
        toggleButton.setToolTipText("Cycle the room temperature");
        toggleButton.addActionListener(event -> changeTemperature());

        childButton = new JButton("Child Mode");
        childButton.setMnemonic('c');
        childButton.setToolTipText("Toggle child mode");
        childButton.addActionListener(event -> {
            windowBlind.raiseToggle_child_mode();
            updateFromStatechart();
        });

        manualButton = new JButton("Manual Override");
        manualButton.setMnemonic('m');
        // ADDED: Toggle manual override and remember the current blind position and child mode when entering manual mode, and restore them when leaving manual mode.
        manualButton.setToolTipText("Toggle manual override"); 
        manualButton.addActionListener(event -> {
            if (windowBlind.isStateActive(WindowBlinds.State.MAIN_REGION_MANUAL_OVERRIDE)) {
                leaveManualMode();
            } else {
                rememberedBlindPosition = getActiveBlindPosition();
                rememberedChildMode = windowBlind.getChild_mode();
                if (rememberedChildMode) {
                    windowBlind.raiseToggle_child_mode();
                }
                windowBlind.raiseManual();
            }
            updateFromStatechart();
        });

        setBackground(Color.black);
        //setLayout(new GridLayout(8, 8));
        add(onButton);
        add(offButton);
        add(halfButton);
        add(toggleButton);
        add(childButton);
        add(manualButton);

        refreshTimer = new Timer(200, event -> updateFromStatechart());
        refreshTimer.start();
        updateFromStatechart();
    }

    private void performBlindAction(Runnable action) {
        if (windowBlind.getChild_mode()) {
            return;
        }
        if (windowBlind.isStateActive(WindowBlinds.State.MAIN_REGION_MANUAL_OVERRIDE)) {
            leaveManualMode();
        }
        action.run();
        rememberedBlindPosition = getActiveBlindPosition();
        updateFromStatechart();
    }

    private void leaveManualMode() {
        windowBlind.raiseManual();
        restoreRememberedBlindPosition();
        if (rememberedChildMode) {
            windowBlind.raiseToggle_child_mode();
        }
        rememberedBlindPosition = null;
        rememberedChildMode = false;
    }

    private void restoreRememberedBlindPosition() {
        if (rememberedBlindPosition == null
                || rememberedBlindPosition == getActiveBlindPosition()) {
            return;
        }

        switch (rememberedBlindPosition) {
            case OPEN:
                windowBlind.raiseOpen();
                break;
            case HALF_OPEN:
                windowBlind.raiseHalf_open();
                break;
            case CLOSED:
                windowBlind.raiseClose();
                break;
            default:
                throw new IllegalStateException("Unsupported remembered blind position");
        }
    }

    private BlindPosition getActiveBlindPosition() {
        if (windowBlind.isStateActive(
                WindowBlinds.State.MAIN_REGION_SMARTWINDOWBLINDSYSTEM_WINDOW_BLIND_WINDOWBLIND_BLINDS_STATUS_OPEN)) {
            return BlindPosition.OPEN;
        }
        if (windowBlind.isStateActive(
                WindowBlinds.State.MAIN_REGION_SMARTWINDOWBLINDSYSTEM_WINDOW_BLIND_WINDOWBLIND_BLINDS_STATUS_HALFOPEN)) {
            return BlindPosition.HALF_OPEN;
        }
        if (windowBlind.isStateActive(
                WindowBlinds.State.MAIN_REGION_SMARTWINDOWBLINDSYSTEM_WINDOW_BLIND_WINDOWBLIND_BLINDS_STATUS_CLOSE)) {
            return BlindPosition.CLOSED;
        }
        return null;
    }

    private void changeTemperature() {
        long temperature = windowBlind.getTemp();
        if (temperature >= windowBlind.getThreshold_2()) {
            temperature = 20;
        } else if (temperature >= windowBlind.getThreshold_1()) {
            temperature = 30;
        } else {
            temperature = 25;
        }
        windowBlind.setTemp(temperature);
        updateFromStatechart();
    }

    private void updateFromStatechart() {
        manual_mode = windowBlind.isStateActive(WindowBlinds.State.MAIN_REGION_MANUAL_OVERRIDE);
        blind_open = windowBlind.isStateActive(
                WindowBlinds.State.MAIN_REGION_SMARTWINDOWBLINDSYSTEM_WINDOW_BLIND_WINDOWBLIND_BLINDS_STATUS_OPEN);
        blind_half_open = windowBlind.isStateActive(
                WindowBlinds.State.MAIN_REGION_SMARTWINDOWBLINDSYSTEM_WINDOW_BLIND_WINDOWBLIND_BLINDS_STATUS_HALFOPEN);
        blind_close = windowBlind.isStateActive(
                WindowBlinds.State.MAIN_REGION_SMARTWINDOWBLINDSYSTEM_WINDOW_BLIND_WINDOWBLIND_BLINDS_STATUS_CLOSE);
        child_mode = manual_mode
                ? rememberedChildMode // ADDED:If in manual mode, use the remembered child mode
                : windowBlind.getChild_mode();
        day_time = clock.isStateActive(Clock.State.MAIN_REGION_CLOCKSYSTEM_DAY_AND_NIGHT_NIGHT);

        if (manual_mode && rememberedBlindPosition != null) {
            blind_open = rememberedBlindPosition == BlindPosition.OPEN;
            blind_half_open = rememberedBlindPosition == BlindPosition.HALF_OPEN;
            blind_close = rememberedBlindPosition == BlindPosition.CLOSED;
        }
        if (blind_open) {
            WindowBlind.blind_state.setText("Blind State: Open");
        } else if (blind_half_open) {
            WindowBlind.blind_state.setText("Blind State: Half Open");
        } else if (blind_close) {
            WindowBlind.blind_state.setText("Blind State: Close");
        }
        WindowBlind.child_mode.setText("Child Mode: " + child_mode);
        WindowBlind.manual_mode.setText("Manual Mode: " + manual_mode);
        WindowBlind.room_temperature.setText( // ADDED: Display the current room temperature and thresholds
                "Room Temp: " + windowBlind.getTemp() + " C (hot at "
                        + windowBlind.getThreshold_1() + ", too hot at "
                        + windowBlind.getThreshold_2() + ")");
        WindowBlind.day_and_night.setText(// ADDED: Display whether it is day or night and the current hour
                (day_time ? "Night Time!" : "Morning Time!")
                        + " Hour: " + clock.getHour());

        if (manual_mode) {
            win.setManualMode();
        } else {
            win.setOpen(blind_open, blind_half_open, blind_close);
        }
        win.setChildMode(child_mode);
        win.repaint();
        updateButtonAvailability(manual_mode, child_mode);
    }
    // ADDED: Greyout out buttons that are not available in the current mode, and change the text of the manual button to indicate whether it will enter or exit manual mode.
    private void updateButtonAvailability(boolean manual, boolean childMode) {
        onButton.setEnabled(!manual && !childMode);
        offButton.setEnabled(!manual && !childMode);
        halfButton.setEnabled(!manual && !childMode);
        toggleButton.setEnabled(!manual);
        childButton.setEnabled(!manual);
        manualButton.setText(manual ? "Return to Smart Mode" : "Manual Override");
        manualButton.setEnabled(true);
    }

    public void stopSimulation() {
        refreshTimer.stop();
        windowBlind.exit();
        clock.exit();
        windowBlind.setClock(null);
        blindTimerService.cancel();
        clockTimerService.cancel();
    }
}
