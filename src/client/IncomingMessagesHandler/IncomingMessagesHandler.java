package client.IncomingMessagesHandler;

import client.gui;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;
import java.util.Objects;

/**
 * Обработка сообщений от сервера в отдельном потоке
 */
public class IncomingMessagesHandler implements Runnable {
    private BufferedReader in;
    private client.gui gui;

    public IncomingMessagesHandler(BufferedReader in, gui gui) {
        this.in = in;
        this.gui = gui;
    }

    /**
     * Обработчик входящих сообщений. Полученное сообщение парсится и если оно отправлено текущим пользователем, то имя заменяется на "you".
     * При подключении к серверу на сервер отправляется имя пользователя, и если оно не уникально - ответ обработается в блоке "else if"
     */
    @Override
    public void run() {
        try {
            String message;
            String text;
            String time;
            String username;
            /**
             * Обработка входящих сообщений
             */
            while ((message = in.readLine()) != null) {
                System.out.println(message);
                /**
                 * Парсинг и вывод сообщений пользователей (и новых сообщений и истории)
                 */
                if (message.contains("|")) {
                    time = message.split("\\|", 3)[0]; // Разбиваем только на первый разделитель
                    username = message.split("\\|", 3)[1]; // Разбиваем только на первый разделитель
                    text = message.split("\\|", 3)[2]; // Получаем оставшуюся часть после первого разделителя

                    /**
                     * Замена имени пользователя на "you", если это сообщение от текущего пользователя
                     */
                    if (Objects.equals(username, gui.getUsername())) {
                        username = "You";
                    }

                    /**
                     * Вывод обработанного сообщения на экран
                     */
                    String finalMessage = time + " " + username + ":\n    " + text;
                    SwingUtilities.invokeLater(() -> gui.setText(finalMessage + "\n"));
                }

                /**
                 * Обработка ответа о занятом имени пользователя
                 */
                else if (message.equals("Username already taken")) {
                    JOptionPane.showMessageDialog(null, "Имя пользователя \""  + gui.getUsername() + "\" уже занято!", "Error", JOptionPane.ERROR_MESSAGE);
                    gui.clearUsernameTextField();
                }
            }
        } catch (SocketException e) {
            // Socket is closed, exiting the thread
            System.out.println("Socket closed, stopping client.gui.IncomingMessagesHandler");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}