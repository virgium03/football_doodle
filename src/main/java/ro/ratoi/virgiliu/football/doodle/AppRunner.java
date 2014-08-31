package ro.ratoi.virgiliu.football.doodle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

/**
 * Created by vigi on 8/16/2014.
 */
@Configuration
class AppRunner implements CommandLineRunner {

    @Autowired
    private FootballPanel footballPanel;

    @Autowired
    private AppConfigParams appConfigParams;

    @Override
    public void run(String... args) throws Exception {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.


        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                UIManager.put("swing.boldMetal", Boolean.FALSE);
                try {
                    createAndShowGUI(footballPanel);
                } catch (IOException e) {
                    throw new RuntimeException("Cannot load football image.");
                }
            }
        });
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private void createAndShowGUI(FootballPanel footballPanel) throws IOException {
        //Create and set up the window.
        final JFrame frame = new JFrame("Football Doodle");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        ClassPathResource res = new ClassPathResource("football-icon.png");
        ImageIcon img = new ImageIcon(res.getURL());
        frame.setIconImage(img.getImage());
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                String s1 = "Quit";
                String s2 = "Cancel";
                Object[] options = {s1, s2};
                int n = JOptionPane.showOptionDialog(frame,
                        "Do you really want to quit the Football Doodle application?",
                        "Quit Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        s1);
                if (n == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        //Add content to the window.
        frame.add(footballPanel);
        JLabel statusLabel = new JLabel("Created by: vigi. Version: " + appConfigParams.getVersion());
        statusLabel.setPreferredSize(new Dimension(frame.getWidth(), 16));
        statusLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        frame.add(statusLabel, java.awt.BorderLayout.SOUTH);

        //Display the window.
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
