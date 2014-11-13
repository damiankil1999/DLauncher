/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */
package dlauncher.theme;

import java.util.Arrays;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ListModel;
import javax.swing.UIManager;

public class Theme {

   public ComboBoxModel getArrivableThemes() {

      UIManager.LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();
      String[] looks = new String[lookAndFeels.length];
      for (int i = 0; i < looks.length; i++) {
         looks[i] = lookAndFeels[i].getName();
      }
      Arrays.sort(looks);

      DefaultComboBoxModel model = new DefaultComboBoxModel();
      for (String look : looks) {
         model.addElement(look);
      }

      return model;
   }
   
   

}
