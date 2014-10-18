/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */

package dlauncher.launcher;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

public class Information {

   private final String version = "0.0.0.1";
   private final String newsLink = "http://dlauncher.byethost13.com/news.html";
   private final String defaultIcon = "/images/test.png";

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
      frame.setIconImage(frame.getToolkit().getImage(getClass().getResource("/images/" + img)));
   }
}
