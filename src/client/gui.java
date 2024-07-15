package client;

import client.IncomingMessagesHandler.IncomingMessagesHandler;
import client.input_veryfiers.IPInputVerifier;
import client.input_veryfiers.PortInputVerifier;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.Objects;

/**
 * Класс, отвечающий за функционал интерфейса (Клиентская часть)
 */
public class gui {
    private static String SERVER_ADDRESS = "localhost";
    private static int SERVER_PORT = 12345;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private JButton button1;
    private JTextField textField1;
    private JPanel panel1;
    private JTextArea textArea1;
    private JFormattedTextField usernameTextField;
    private JFormattedTextField ipTextField;
    private JFormattedTextField portTextField;
    private JButton connectButton;

    /**
     * Добавление текста в поле для отображения диалога
     * @param text - текст для добавления (входящее сообщение)
     */
    public synchronized void setText(String text) {
        textArea1.append("\n" + text);
    }

    /**
     * Получение имени пользователя из текстового поля на интерфейсе
     * @return - введенное имя пользователя
     */
    public synchronized String getUsername() {
        return usernameTextField.getText();
    }

    /**
     * Прослушивание событий интерфейса
     */
    public gui() {
        /**
         * Нажатие на кнопку "Отправить"
         */
        button1.addActionListener(event -> {
            String username = "anonymous";
            if (!Objects.equals(usernameTextField.getText(), "")){
                username = usernameTextField.getText();
            }

            if (textField1.getText().equals("")){
                return;
            }
            // Send text to the socket
            if (out != null && ipTextField.getInputVerifier().verify(ipTextField) && portTextField.getInputVerifier().verify(portTextField)) {
                String text = textField1.getText();
                out.println(username + "|" + text);
                textField1.setText("");
            }
            else{
                // Обработка некорректного ввода
                JOptionPane.showMessageDialog(null, "IP сервера или порт введен некорректно.", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
            }
        });

        /**
         * Нажатие на кнопку "Подключиться".
         * Закрытие старого сокета, если он есть, очистка полей для ввода и вывода сообщений, установка адреса сервера и порта из текстовых полей, вызов метода создания сокета
         *
         */
        connectButton.addActionListener(actionEvent -> {
            closeSocket();
            textField1.setText("");
            textArea1.setText("");
            SERVER_ADDRESS = ipTextField.getText();
            SERVER_PORT = Integer.parseInt(portTextField.getText());
            initializeSocket();
        });
    }

    /**
     * Отключение от сервера (закрытие сокета)
     */
    private void closeSocket() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Очистка поля с именем пользователя
     */
    public void clearUsernameTextField() {
        usernameTextField.setText("");
    }

    /**
     * Подключение к серверу.
     * Открывается сокет и передается имя пользователя, затем создается поток прослушивания входящих сообщений.
     */
    private void initializeSocket() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.println(usernameTextField.getText());
            // Начинаем слушать входящие сообщения в отдельном потоке (в текущем крутится интерфейс)
            new Thread(new IncomingMessagesHandler(in, this)).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Точка входа, создание окна, вызов конструктора класса gui
     * @param args
     */
    public static void main(String[] args) {
        //создание гуи
        gui form = new gui();
        //Создание окна
        JFrame frame = new JFrame("Lab12");

        //Запихивание элементов в окно
        frame.setContentPane(form.panel1);
        //Установка действия на кнопку Х
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Упаковка элементов
        frame.pack();
        //Установка минимальных размеров окна
        frame.setMinimumSize(new Dimension(1000, 600));
        //Установка видимости окна
        frame.setVisible(true);
    }

    /**
     * Кастомное создание элементов - в них помещается валидатор значений
     */
    private void createUIComponents() {
        // Настройка поля для IP-адреса без маски
        ipTextField = new JFormattedTextField();
        ipTextField.setColumns(15);
        ipTextField.setInputVerifier(new IPInputVerifier());

        // Настройка поля для порта без маски
        portTextField = new JFormattedTextField();
        portTextField.setColumns(5);
        portTextField.setInputVerifier(new PortInputVerifier());
    }

}
