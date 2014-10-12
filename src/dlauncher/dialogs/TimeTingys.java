/*
 * This work is licensed under a Creative Commons Attribution-ShareAlike 3.0 Unported License.
 * http://creativecommons.org/licenses/by-sa/3.0/
 * Authors: Damian Lamers, Fernando van Loenhout
 */


/* 
 * Hey Hey nice that you are looking for Eastereggs,
 * Keep on looking for it!
 */
package dlauncher.dialogs;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import javax.swing.JPasswordField;


public class TimeTingys {

   private final JPasswordField pfield;
   //Password field:
   private final char defaultchar = '\u2022'; // •  
   private final char xmaschar1 = '\u2603'; // ☃
   private final char xmaschar2 = '\u2746'; // ❆
   private final char minecraftBD = '\u220e'; // ∎
   
   //

   private String getDate() {
      Date date = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat("dd-MM");
      sdf.setTimeZone(TimeZone.getDefault());
      String formattedDate = sdf.format(date);
      return formattedDate;
   }

   private String getDate1() {
      Date date = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
      sdf.setTimeZone(TimeZone.getDefault());
      String formattedDate = sdf.format(date);
      return formattedDate;
   }

   private String getTime() {
      Date date = new Date();
      SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
      sdf.setTimeZone(TimeZone.getDefault());
      String formattedTime = sdf.format(date);
      return formattedTime;
   }

   public TimeTingys(JPasswordField field) {
      this.pfield = field;
   }

   public void setup() {
      JPasswordField field = this.pfield;
      field.setEchoChar(defaultchar);

      switch (getDate()) {
         case "25-12":
            field.setEchoChar(xmaschar1);
            break;
         case "26-12":
            field.setEchoChar(xmaschar2);
            break;
         case "18-11":
            field.setEchoChar(minecraftBD);
            break;
      }
   }

}
