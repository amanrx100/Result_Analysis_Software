package projectfinalpackage;


import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class WelcomeScreen extends JFrame {

    private JLabel label;

    public WelcomeScreen() {
        setTitle("Welcome");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(Frame.MAXIMIZED_BOTH); // Maximize the frame
        setUndecorated(true); // Remove window decorations (title bar, borders, etc.)
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        add(panel);

        label = new JLabel(" Welcome to Result Analysis Software", SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 40));
        panel.add(label, BorderLayout.CENTER);

        Timer timer = new Timer(10000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                dispose();
            }
        });
        timer.setRepeats(false); // Stop the timer after one execution
        timer.start();

        setVisible(true);
    }

    public static void invoke() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
			public void run() {
                new WelcomeScreen();
            }
        });
    }
}

