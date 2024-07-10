import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;

public class gui {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private PrintWriter out;
    private JButton button1;
    private JTextField textField1;
    private JPanel panel1;
    private JTextArea textArea1;

    public synchronized void setText(String text) {
        textArea1.append("\n" + text);
    }

    public gui() {
        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // Send text to the socket
                if (out != null) {
                    String text = textField1.getText();
                    out.println(text);
                    textField1.setText("");
                    textArea1.append("\n");
                    textArea1.append("you: " + text);
                }
            }
        });
    }

    private void initializeSocket() {
        try {
            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Start a new thread to handle incoming messages
            new Thread(new IncomingMessagesHandler(in, this)).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Create GUI instance
        gui form = new gui();
        // Create JFrame window
        JFrame frame = new JFrame("Lab12");

        // Set the main panel of the form as the content of the window
        frame.setContentPane(form.panel1);
        // Set the default close operation
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Pack the window to fit the contents
        frame.pack();
        // Set minimum window size
        frame.setMinimumSize(new Dimension(300, 200));
        // Make the window visible
        frame.setVisible(true);

        // Initialize the socket connection
        form.initializeSocket();
    }
}
