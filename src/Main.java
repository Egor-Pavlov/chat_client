import java.io.*;
import java.net.*;

/**
 * Клиентская часть приложения. Подключается к серверу, читает ввод пользователя, отправляет на сервер
 */
public class Main {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        //Обработка входящих сообщений от сервера и сообщений пользователя
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))) {
            //в отдельном потоке запускается обработчик
            new Thread(new IncomingMessagesHandler(in)).start();

            //чтение строки пользователя
            String message;
            while ((message = userInput.readLine()) != null) {
                //пишем в сокет
                out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}