

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowBlind
{
   // Status labels updated by the controls as the blind and its modes change.
	public static JTextPane blind_state, child_mode, manual_mode, room_temperature, day_and_night;
   //-----------------------------------------------------------------
   //  Creates and displays the window-blind interface.
   //-----------------------------------------------------------------
   public static void main (String[] args)
   {
      // Create and update Swing components on the event-dispatch thread.
      SwingUtilities.invokeLater(() -> {
         JFrame windowFrame = new JFrame ("WindowBlinds.ysc - USER INTERFACE");
         windowFrame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

         // Initialize labels before constructing controls that may update them.
         blind_state = new JTextPane();
         child_mode = new JTextPane();
         manual_mode = new JTextPane();
         room_temperature = new JTextPane();
         day_and_night = new JTextPane();

         blind_state.setBackground(Color.white);
         child_mode.setBackground(Color.white);
         manual_mode.setBackground(Color.white);
         room_temperature.setBackground(Color.white);
         day_and_night.setBackground(Color.white);
         blind_state.setEditable(false);
         child_mode.setEditable(false);
         manual_mode.setEditable(false);
         room_temperature.setEditable(false);
         day_and_night.setEditable(false);

         WindowBlindPanel door = new WindowBlindPanel();
         WindowBlindControl controls = new WindowBlindControl(door);
         windowFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent event) {
               controls.stopSimulation();
            }
         });

         // Stack the blind display, status labels, and controls vertically.
         JPanel panel = new JPanel();
         panel.setBackground (Color.black);
         panel.setLayout (new BoxLayout(panel, BoxLayout.Y_AXIS));
         panel.add (Box.createRigidArea (new Dimension (0, 16)));
         panel.add (door);
         panel.add(blind_state);
         panel.add(child_mode);
         panel.add(manual_mode);
         panel.add(room_temperature);
         panel.add(day_and_night);
         panel.add (Box.createRigidArea (new Dimension (0, 12)));
         panel.add (controls);
         panel.add (Box.createRigidArea (new Dimension (0, 16)));

         windowFrame.getContentPane().add(panel);
         windowFrame.pack();
         windowFrame.setLocationRelativeTo(null);
         windowFrame.setVisible(true);
      });
   }
}