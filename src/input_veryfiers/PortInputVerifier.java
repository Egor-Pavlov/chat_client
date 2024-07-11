package input_veryfiers;

import javax.swing.*;

/**
 * Проверка введенного порта на соответствие диапазону портов
 */
public class PortInputVerifier extends InputVerifier {
    @Override
    public boolean verify(JComponent input) {
        try {
            int port = Integer.parseInt(((JTextField) input).getText());
            return port >= 0 && port <= 65535;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}