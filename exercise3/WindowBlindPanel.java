import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

public class WindowBlindPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private boolean open, half_open, close, child;
    public boolean manual_mode;
    public ImageIcon blindOpen, blindHalfOpen, blindClose, manualMode;
    private JLabel imageLabel;
    public Graphics page;
	
   private ImageIcon resizeIcon(String path, int width, int height) {
       ImageIcon original = new ImageIcon(path);
       Image resized = original.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
       return new ImageIcon(resized);
   }

    public WindowBlindPanel() {
        int imageWidth = 400;
        int imageHeight = 500;

        blindOpen = resizeIcon("html/images/window-blind-open.png", imageWidth, imageHeight);
        blindHalfOpen = resizeIcon("html/images/window-blind-half-open.png", imageWidth, imageHeight);
        blindClose = resizeIcon("html/images/window-blind-closed.png", imageWidth, imageHeight);
        manualMode = resizeIcon("html/images/window-blind-manual-mode.png", imageWidth, imageHeight);

        open = false;
        half_open = false;
        close = false;
        imageLabel = new JLabel(blindClose);
        add(imageLabel);
    }

    //-----------------------------------------------------------------
   //  Paints the panel using the appropriate image.
   //-----------------------------------------------------------------
   public void paintComponent(Graphics page)
   {
      super.paintComponent(page);

      if (manual_mode) {
          imageLabel.setIcon(manualMode);
      } else if (open) {
          imageLabel.setIcon(blindOpen);
      } else if (half_open) {
          imageLabel.setIcon(blindHalfOpen);
      } else if (close) {
          imageLabel.setIcon(blindClose);
      }
   }

   //-----------------------------------------------------------------
   //  Sets the status of the window blind.
   //-----------------------------------------------------------------
   public void setOpen(boolean winBlindOpen, boolean winBlindHalfOpen, boolean winBlindClose)
   {
       open = winBlindOpen;
       half_open = winBlindHalfOpen;
       close = winBlindClose;
       manual_mode = false;
   }
   
   public void setChildMode(boolean childModeOn)
   {
       child = childModeOn;
   }
   
   public void setManualMode() {
       open = false;
       half_open = false;
       close = false;
       manual_mode = true;
   }
}