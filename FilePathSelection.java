package projectfinalpackage;

import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class FilePathSelection {
    public static String filePathSelection(String title) {
        AtomicReference<String> input = new AtomicReference<>(null);
        String path=null;

        JFrame frame = new JFrame("Input GUI");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        JTextField textField = new JTextField(20);
        JButton submitButton = new JButton("Submit");

        // ActionListener for submit button
        ActionListener submitAction = e -> {
            String inputValue = textField.getText();
            input.set(inputValue);
            JOptionPane.showMessageDialog(frame, "You entered: " + inputValue);
            System.out.println("The path of the excel file is " + inputValue);
            // Close the frame after submit button is clicked
            frame.dispose();
        };
        submitButton.addActionListener(submitAction);

        // ActionListener for Enter key press in text field
        textField.addActionListener(submitAction);

        panel.add(new JLabel(" "+title ));
        panel.add(textField);
        panel.add(submitButton);

        frame.add(panel);
        frame.pack();
        frame.setVisible(true);

        // Wait for the user to input something before returning
        while (input.get() == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        return input.get();
    }
}


