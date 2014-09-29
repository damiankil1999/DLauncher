package DLauncher;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 * @author damian
 */
public class Information {

   private final String version = "Alpha 0.0.0.1";
   private final String newsLink = "http://www.minecraft.net/";
   private final String defaultIcon = "/Images/test.png";

   public Information() {
      Logger.getGlobal().log(Level.INFO, "Launcher Version " + version + " Loaded!");
   }

   public String getVersion() {
      return version;
   }

   public String getNewsLink(){
      return newsLink;
   }

   public void setIcon(JFrame frame) {
      frame.setIconImage(frame.getToolkit().getImage(getClass().getResource(this.defaultIcon)));
   }

   public void setIcon(JFrame frame, String img) {
      frame.setIconImage(frame.getToolkit().getImage(getClass().getResource("/Images/" + img)));
   }
}
