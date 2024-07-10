import java.io.*;

/**
 * Обработка сообщений от сервера в отдельном потоке
 */
class IncomingMessagesHandler implements Runnable {
    private BufferedReader in;

    public IncomingMessagesHandler(BufferedReader in) {
        this.in = in;
    }

    @Override
    public void run() {
        String message;
        try {
            while ((message = in.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}