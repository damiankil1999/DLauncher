/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DLauncher;

import DLauncher.Logger.Console;
import DLauncher.Logger.TextAreaHandler;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author damian
 */
public class Launcher extends javax.swing.JFrame {

   Information Information = new Information();

   /**
    * Creates new form Launcher
    */
   public Launcher() {
      initComponents();

      try {
         setTitle("DLauncher - " + Information.getVersion());
         setIconImage(getToolkit().getImage(getClass().getResource("/Images/download.jpg")));
         jEditorPane1.setPage(Information.getNewsLink());
      } catch (IOException ex) {
         Logger.getLogger(Launcher.class.getName()).log(Level.SEVERE, "", ex);
      }
   }

   /**
    * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this method is always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      jTabbedPane1 = new javax.swing.JTabbedPane();
      jScrollPane1 = new javax.swing.JScrollPane();
      jEditorPane1 = new javax.swing.JEditorPane();
      jTabbedPane2 = new javax.swing.JTabbedPane();
      jTabbedPane3 = new javax.swing.JTabbedPane();
      jPanel1 = new javax.swing.JPanel();
      jComboBox1 = new javax.swing.JComboBox();
      jButton1 = new javax.swing.JButton();

      setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
      setTitle("DLauncher - OH NO SOMETHING WENT WRONG!");
      setForeground(java.awt.Color.lightGray);

      jTabbedPane1.setBackground(new java.awt.Color(68, 69, 70));
      jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.RIGHT);
      jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
      jTabbedPane1.setFont(new java.awt.Font("Britannic Bold", 1, 36)); // NOI18N
      jTabbedPane1.setOpaque(true);

      jScrollPane1.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
      jScrollPane1.setAutoscrolls(true);

      jEditorPane1.setEditable(false);
      jEditorPane1.setBackground(new java.awt.Color(102, 107, 111));
      jEditorPane1.setContentType("text"); // NOI18N
      jEditorPane1.setText("ERROR:\n\nCan't connect to the site\n\nPossibly Resons:\n - No Internet Connection,\n - No Stabel Internet Connection,\n - Site Offline,\n - Running DLauncher wrong,\n\nRegards, DLauncher");
      jEditorPane1.setOpaque(false);
      jScrollPane1.setViewportView(jEditorPane1);

      jTabbedPane1.addTab("News", null, jScrollPane1, "Display The News");

      jTabbedPane2.setBackground(new java.awt.Color(102, 107, 111));
      jTabbedPane2.setOpaque(true);
      jTabbedPane1.addTab("Pack's", null, jTabbedPane2, "Display The Arivable ModPack's");

      jTabbedPane3.setBackground(new java.awt.Color(102, 107, 111));
      jTabbedPane3.setOpaque(true);
      jTabbedPane1.addTab("tab3", jTabbedPane3);

      jPanel1.setBackground(new java.awt.Color(45, 46, 49));

      jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Select An Acount" }));

      jButton1.setText("Status");
      jButton1.setToolTipText("Display The Current Status Of Minecraft");
      jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

      javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
      jPanel1.setLayout(jPanel1Layout);
      jPanel1Layout.setHorizontalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
            .addContainerGap(384, Short.MAX_VALUE)
            .addComponent(jButton1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
      );
      jPanel1Layout.setVerticalGroup(
         jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(jPanel1Layout.createSequentialGroup()
            .addGap(7, 7, 7)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(jButton1))
            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
      );

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
      getContentPane().setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
         .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE))
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addGap(0, 336, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
         .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
               .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
               .addGap(39, 39, 39)))
      );

      pack();
      setLocationRelativeTo(null);
   }// </editor-fold>//GEN-END:initComponents

   /**
    * @param args the command line arguments
    */
   public static void main(String args[]) {
      /* Set the Nimbus look and feel */
      //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
       * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
       */
      try {
         for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
               javax.swing.UIManager.setLookAndFeel(info.getClassName());
               break;
            }
         }
      } catch (ClassNotFoundException ex) {
         java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (InstantiationException ex) {
         java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (IllegalAccessException ex) {
         java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (javax.swing.UnsupportedLookAndFeelException ex) {
         java.util.logging.Logger.getLogger(Launcher.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      }
      //</editor-fold>

      /* Create and display the form */
      java.awt.EventQueue.invokeLater(new Runnable() {
         @Override
         public void run() {
            Console console = new Console();
            console.setVisible(true);
            Logger.getLogger(Launcher.class.getName()).addHandler(new TextAreaHandler(console.jTextArea1));
            Logger.getLogger(Launcher.class.getName()).log(Level.INFO, "Started!");
            
            new Launcher().setVisible(true);

         }
      });
   }

   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JButton jButton1;
   private javax.swing.JComboBox jComboBox1;
   private javax.swing.JEditorPane jEditorPane1;
   private javax.swing.JPanel jPanel1;
   private javax.swing.JScrollPane jScrollPane1;
   private javax.swing.JTabbedPane jTabbedPane1;
   private javax.swing.JTabbedPane jTabbedPane2;
   private javax.swing.JTabbedPane jTabbedPane3;
   // End of variables declaration//GEN-END:variables
}