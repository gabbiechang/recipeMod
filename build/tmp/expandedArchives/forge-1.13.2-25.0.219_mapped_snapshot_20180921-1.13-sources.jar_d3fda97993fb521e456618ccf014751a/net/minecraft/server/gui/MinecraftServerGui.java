package net.minecraft.server.gui;

import com.mojang.util.QueueLogAppender;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MinecraftServerGui extends JComponent {
   private static final Font SERVER_GUI_FONT = new Font("Monospaced", 0, 12);
   private static final Logger LOGGER = LogManager.getLogger();
   private final DedicatedServer server;
   private Thread field_206932_d;

   /**
    * Creates the server GUI and sets it visible for the user.
    */
   public static void createServerGui(final DedicatedServer serverIn) {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception var3) {
         ;
      }

      MinecraftServerGui minecraftservergui = new MinecraftServerGui(serverIn);
      JFrame jframe = new JFrame("Minecraft server");
      jframe.add(minecraftservergui);
      jframe.pack();
      jframe.setLocationRelativeTo((Component)null);
      jframe.setVisible(true);
      jframe.addWindowListener(new WindowAdapter() {
         public void windowClosing(WindowEvent p_windowClosing_1_) {
            serverIn.initiateShutdown();

            while(!serverIn.isServerStopped()) {
               try {
                  Thread.sleep(100L);
               } catch (InterruptedException interruptedexception) {
                  interruptedexception.printStackTrace();
               }
            }

            System.exit(0);
         }
      });
      minecraftservergui.start();
   }

   public MinecraftServerGui(DedicatedServer serverIn) {
      this.server = serverIn;
      this.setPreferredSize(new Dimension(854, 480));
      this.setLayout(new BorderLayout());

      try {
         this.add(this.getLogComponent(), "Center");
         this.add(this.getStatsComponent(), "West");
      } catch (Exception exception) {
         LOGGER.error("Couldn't build server GUI", (Throwable)exception);
      }

   }

   /**
    * Generates new StatsComponent and returns it.
    */
   private JComponent getStatsComponent() throws Exception {
      JPanel jpanel = new JPanel(new BorderLayout());
      jpanel.add(new StatsComponent(this.server), "North");
      jpanel.add(this.getPlayerListComponent(), "Center");
      jpanel.setBorder(new TitledBorder(new EtchedBorder(), "Stats"));
      return jpanel;
   }

   /**
    * Generates new PlayerListComponent and returns it.
    */
   private JComponent getPlayerListComponent() throws Exception {
      JList<?> jlist = new PlayerListComponent(this.server);
      JScrollPane jscrollpane = new JScrollPane(jlist, 22, 30);
      jscrollpane.setBorder(new TitledBorder(new EtchedBorder(), "Players"));
      return jscrollpane;
   }

   private JComponent getLogComponent() throws Exception {
      JPanel jpanel = new JPanel(new BorderLayout());
      JTextArea jtextarea = new JTextArea();
      JScrollPane jscrollpane = new JScrollPane(jtextarea, 22, 30);
      jtextarea.setEditable(false);
      jtextarea.setFont(SERVER_GUI_FONT);
      JTextField jtextfield = new JTextField();
      jtextfield.addActionListener((p_210465_2_) -> {
         String s = jtextfield.getText().trim();
         if (!s.isEmpty()) {
            this.server.handleConsoleInput(s, this.server.getCommandSource());
         }

         jtextfield.setText("");
      });
      jtextarea.addFocusListener(new FocusAdapter() {
         public void focusGained(FocusEvent p_focusGained_1_) {
         }
      });
      jpanel.add(jscrollpane, "Center");
      jpanel.add(jtextfield, "South");
      jpanel.setBorder(new TitledBorder(new EtchedBorder(), "Log and chat"));
      this.field_206932_d = new Thread(() -> {
         String s;
         while((s = QueueLogAppender.getNextLogEvent("ServerGuiConsole")) != null) {
            this.appendLine(jtextarea, jscrollpane, s);
         }

      });
      this.field_206932_d.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER));
      this.field_206932_d.setDaemon(true);
      return jpanel;
   }

   private java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(1);
   public void start() {
      this.field_206932_d.start();
      latch.countDown();
   }

   public void appendLine(JTextArea textArea, JScrollPane scrollPane, String line) {
      try {
         latch.await();
      } catch (InterruptedException e){} //Prevent logging until after constructor has ended.
      if (!SwingUtilities.isEventDispatchThread()) {
         SwingUtilities.invokeLater(() -> {
            this.appendLine(textArea, scrollPane, line);
         });
      } else {
         Document document = textArea.getDocument();
         JScrollBar jscrollbar = scrollPane.getVerticalScrollBar();
         boolean flag = false;
         if (scrollPane.getViewport().getView() == textArea) {
            flag = (double)jscrollbar.getValue() + jscrollbar.getSize().getHeight() + (double)(SERVER_GUI_FONT.getSize() * 4) > (double)jscrollbar.getMaximum();
         }

         try {
            document.insertString(document.getLength(), line, (AttributeSet)null);
         } catch (BadLocationException var8) {
            ;
         }

         if (flag) {
            jscrollbar.setValue(Integer.MAX_VALUE);
         }

      }
   }
}