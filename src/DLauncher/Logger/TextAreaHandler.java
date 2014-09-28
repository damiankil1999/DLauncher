package DLauncher.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.LogRecord;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/*
 * Made by Edwin Dalorzo @ stackoverflow.com
 * modifyed
 */
public class TextAreaHandler extends java.util.logging.Handler {

   public TextAreaHandler(JTextArea textArea) {
      this.textArea = textArea;
   }

   private final JTextArea textArea;

   @Override
   public void publish(final LogRecord record) {
      SwingUtilities.invokeLater(new Runnable() {

         @Override
         public void run() {
            StringWriter text = new StringWriter();
            PrintWriter out = new PrintWriter(text);
            out.println(textArea.getText()); // 10:10:10-2014[INFO]: lol
            
            Date date = new Date(record.getMillis());
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            sdf.setTimeZone(TimeZone.getDefault());
            String formattedDate = sdf.format(date);
            
            out.printf("%s [%s] {%s} %s", formattedDate, record.getLevel(),
                    record.getLoggerName(), record.getMessage());
            textArea.setText(text.toString());
         }

      });
   }

   @Override
   public void flush() {
   }

   @Override
   public void close() throws SecurityException {
   }
}
