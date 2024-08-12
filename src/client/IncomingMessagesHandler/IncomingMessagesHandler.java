package client.IncomingMessagesHandler;

import client.gui;
import client.model.Message;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Обработка сообщений от сервера в отдельном потоке
 */
public class IncomingMessagesHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger(IncomingMessagesHandler.class);
    private final BufferedReader in;
    private final client.gui gui;
    private TrayIcon trayIcon;

    public IncomingMessagesHandler(BufferedReader in, gui gui) {
        this.in = in;
        this.gui = gui;
        logger.info("IncomingMessagesHandler initialized");
        initializeTrayIcon();
    }

    private void initializeTrayIcon() {
        if (!SystemTray.isSupported()) {
            logger.warn("System tray is not supported!");
            return;
        }

        SystemTray tray = SystemTray.getSystemTray();
        Image image = Toolkit.getDefaultToolkit().getImage("path/to/icon.png"); // Замените на путь к иконке

        trayIcon = new TrayIcon(image, "Chat Application");
        trayIcon.setImageAutoSize(true);

        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            logger.error("TrayIcon could not be added.", e);
        }
    }

    private void showNotification(String title, String message) {
        if (trayIcon != null) {
            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
        } else {
            logger.warn("TrayIcon is not initialized.");
        }
    }

    private String convertDateTime(ZonedDateTime zonedDateTime) {
        String pattern = "HH:mm";
        int currentYear = ZonedDateTime.now().getYear();
        int currentDay = ZonedDateTime.now().getDayOfMonth();

        if (zonedDateTime.getDayOfMonth() != currentDay) {
            pattern = "dd.MM HH:mm";
        }
        if (zonedDateTime.getYear() != currentYear) {
            pattern = "dd.MM.yyyy HH:mm";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return zonedDateTime.format(formatter);
    }

    @Override
    public void run() {
        try {
            logger.info("IncomingMessagesHandler thread started");
            String message;

            while ((message = in.readLine()) != null) {
                logger.debug("New message received: " + message);
                if (message.equals("Username already taken")) {
                    logger.warn("Username already taken: " + gui.getUsername());
                    JOptionPane.showMessageDialog(null, "Имя пользователя \"" + gui.getUsername() + "\" уже занято!", "Error", JOptionPane.ERROR_MESSAGE);
                    gui.clearUsernameTextField();
                } else {
                    try {
                        Message incomingMessage = Message.fromJson(message);
                        String displayedUsername = "You";

                        ZoneId currentZoneId = ZoneId.systemDefault();
                        ZonedDateTime dateTime = incomingMessage.timestamp().withZoneSameInstant(currentZoneId);

                        if (!incomingMessage.username().equals(gui.getUsername())) {
                            displayedUsername = incomingMessage.username();
                        }

                        String finalMessage = convertDateTime(dateTime) + " " + displayedUsername + ":\n    " + incomingMessage.text();
                        logger.debug("Processed message:\n " + finalMessage);
                        SwingUtilities.invokeLater(() -> gui.setText(finalMessage + "\n"));
                    } catch (Exception e) {
                        logger.error("Failed to parse message: " + message, e);
                    }
                }
            }
        } catch (SocketException e) {
            logger.info("Socket closed, stopping IncomingMessagesHandler", e);
        } catch (IOException e) {
            logger.error("IO error occurred in IncomingMessagesHandler", e);
            e.printStackTrace();
        }
    }
}
