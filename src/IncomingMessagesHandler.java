import javax.swing.*;
import java.io.*;
import java.net.SocketException;
import java.util.Objects;

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
            String text;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
                if (!message.contains(":")) {
                    System.out.println("Invalid message format: " + message);
                    continue; // Пропускаем обработку неверного формата
                }
                String username = message.split(":", 2)[0]; // Разбиваем только на первый разделитель
                text = message.split(":", 2)[1]; // Получаем оставшуюся часть после первого разделителя

                if (Objects.equals(username, gui.getUsername())){
                    username = "You";
                }

                // Update the GUI in the Event Dispatch Thread
                String finalMessage = username + ":\n    " + text;
                SwingUtilities.invokeLater(() -> gui.setText(finalMessage + "\n"));
            }
        } catch (SocketException e) {
            // Socket is closed, exiting the thread
            System.out.println("Socket closed, stopping IncomingMessagesHandler");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}