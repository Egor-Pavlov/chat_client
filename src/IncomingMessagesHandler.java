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
            String time;
            String username;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
                if (message.contains("|")) {
                    time = message.split("\\|", 3)[0]; // Разбиваем только на первый разделитель
                    username = message.split("\\|", 3)[1]; // Разбиваем только на первый разделитель
                    text = message.split("\\|", 3)[2]; // Получаем оставшуюся часть после первого разделителя

                    if (Objects.equals(username, gui.getUsername())) {
                        username = "You";
                    }

                    // Update the GUI in the Event Dispatch Thread
                    String finalMessage = time + " " + username + ":\n    " + text;
                    SwingUtilities.invokeLater(() -> gui.setText(finalMessage + "\n"));
                }

                else if (message.equals("Username already taken")) {
                    JOptionPane.showMessageDialog(null, "Имя пользователя \""  + gui.getUsername() + "\" уже занято!", "Error", JOptionPane.ERROR_MESSAGE);
                    gui.clearUsernameTextField();
                }
            }
        } catch (SocketException e) {
            // Socket is closed, exiting the thread
            System.out.println("Socket closed, stopping IncomingMessagesHandler");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}