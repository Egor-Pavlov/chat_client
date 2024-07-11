import input_veryfiers.IPInputVerifier;
import input_veryfiers.PortInputVerifier;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.text.ParseException;
import java.util.Objects;

public class gui {
    private static String SERVER_ADDRESS = "localhost";
    private static int SERVER_PORT = 12345;
    private PrintWriter out;
    private JButton button1;
    private JTextField textField1;
    private JPanel panel1;
    private JTextArea textArea1;
    private JFormattedTextField usernameTextField;
    private JFormattedTextField ipTextField;
    private JFormattedTextField portTextField;

    public synchronized void setText(String text) {
        textArea1.append("\n" + text);
    }

    public gui() {
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
                out.println(username + ":" + text);
                textField1.setText("");
                textArea1.append("\nyou:\n    " + text);
            }
            else{
                // Обработка некорректного ввода
                JOptionPane.showMessageDialog(null, "IP сервера или порт введен некорректно.", "Ошибка ввода", JOptionPane.ERROR_MESSAGE);
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

        //Инициализация сокета для прослушивания сервера
        form.initializeSocket();
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
