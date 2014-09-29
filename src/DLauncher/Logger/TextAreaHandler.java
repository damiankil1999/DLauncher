package DLauncher.Logger;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class TextAreaHandler extends java.util.logging.Handler {

   public TextAreaHandler(JTextArea textArea) {
      this.textArea = textArea;
      this.setFormatter(new Formatter() {
         @Override
         public String format(LogRecord record) {
          StringBuilder sb = new StringBuilder();
          SimpleDateFormat dt1 = new SimpleDateFormat("[HH:mm:ss] ");
          sb.append(dt1.format(new Date())).append('[').append(record.getLevel().getLocalizedName()).append("] ")
                  .append(formatMessage(record));
          Throwable exception = record.getThrown();
          if (exception != null) {
           try {
            StringWriter sw = new StringWriter();
            try (PrintWriter pw = new PrintWriter(sw)) {
             exception.printStackTrace(pw);
            }
            sb.append(sw.toString());
           } catch (Exception ex) {
            ex.addSuppressed(exception);
            ex.printStackTrace();
           }
          }

          return sb.toString();
         }
        });
   }

   private final JTextArea textArea;

   @Override
   public void publish(final LogRecord record) {
      Runnable r = new Runnable() {

         @Override
         public void run() {
            StringWriter text = new StringWriter();
            PrintWriter out = new PrintWriter(text);
            out.println(textArea.getText());
            out.print(getFormatter().format(record));
            textArea.setText(text.toString());
         }

      };

      if (SwingUtilities.isEventDispatchThread()) {
         r.run();
      } else {
         SwingUtilities.invokeLater(r);
      }
   }

   @Override
   public void flush() {
   }

   @Override
   public void close() throws SecurityException {
   }
   

}