/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SettingManager {

   public String javaPath = "Java Path Unknow bug?                                           Browse -->";
   public File launcherPath = new File(".");
   
   
   File config = new File(launcherPath + "settings.yml");
   
   public void start() {
      if(!(config.exists())){
         this.create();
      }else{
         
      }
   }
   
   private void create(){
      if (!(config.exists())) {

         try {
            config.createNewFile();

            FileWriter fw = new FileWriter(config.getAbsoluteFile());
            try (BufferedWriter bw = new BufferedWriter(fw)) {
               bw.write("");
            }
            
            Logger.getGlobal().info("settings.yml created");
         } catch (IOException ex) {
            Logger.getLogger(SettingManager.class.getName()).log(Level.SEVERE, "", ex);
            
         }
      }
   }

}
