import javax.swing.*;
import java.io.*;

/**
 * Обработка сообщений от сервера в отдельном потоке
 */
class IncomingMessagesHandler implements Runnable {
    private BufferedReader in;
    private gui gui;

    public IncomingMessagesHandler(BufferedReader in, gui gui) {
        this.in = in;
        this.gui = gui;
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                // Update the GUI in the Event Dispatch Thread
                String finalMessage = message;
                SwingUtilities.invokeLater(() -> gui.setText(finalMessage));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}