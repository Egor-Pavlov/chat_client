package client.IncomingMessagesHandler;

import client.gui;
import client.model.Message;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
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

    private String convertDateTime(ZonedDateTime zonedDateTime) {
        String pattern = "HH:mm";
        // Получение текущих значений года, месяца и дня
        int currentYear = ZonedDateTime.now().getYear();
        int currentDay = ZonedDateTime.now().getDayOfMonth();

        // Проверка дня
        if (zonedDateTime.getDayOfMonth() != currentDay) {
            pattern = "dd.MM HH:mm";
        }

        // Проверка года
        if (zonedDateTime.getYear() != currentYear) {
            pattern = "dd.MM.yyyy HH:mm";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return zonedDateTime.format(formatter);
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
                /**
                 * Обработка ответа о занятом имени пользователя
                 */
                System.out.println(message);
                if (message.equals("Username already taken")) {
                    JOptionPane.showMessageDialog(null, "Имя пользователя \""  + gui.getUsername() + "\" уже занято!", "Error", JOptionPane.ERROR_MESSAGE);
                    gui.clearUsernameTextField();
                }
                /**
                 * Парсинг и вывод сообщений пользователей (и новых сообщений и истории)
                 */
                else{
                    try{
                        Message incomingMessage = Message.fromJson(message);
                        String displayedUsername = "You";

                        // Получение текущей временной зоны
                        ZoneId currentZoneId = ZoneId.systemDefault();

                        // Преобразование даты и времени в текущую временную зону
                        ZonedDateTime dateTime = incomingMessage.getTimestamp().withZoneSameInstant(currentZoneId);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

                        if (!incomingMessage.getUsername().equals(gui.getUsername())) {
                            displayedUsername = incomingMessage.getUsername();
                        }

                        String finalMessage = convertDateTime(dateTime) + " " + displayedUsername + ":\n    " + incomingMessage.getText();
                        SwingUtilities.invokeLater(() -> gui.setText(finalMessage + "\n"));
                    }
                    catch(Exception e){
                        System.out.println("Message could not be parsed");
                        e.printStackTrace();
                    }
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