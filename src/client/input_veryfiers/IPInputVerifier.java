package client.input_veryfiers;

import javax.swing.*;
import java.util.regex.Pattern;

/**
 * Проверка значения в поле ip по регулярке
 */
public class IPInputVerifier extends InputVerifier {
    private final Pattern pattern = Pattern.compile(
            "^((25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}" +
                    "(25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)$");

    @Override
    public boolean verify(JComponent input) {
        String text = ((JTextField) input).getText();
        return pattern.matcher(text).matches();
    }
}
