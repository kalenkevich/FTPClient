package ftp.client.component.tab;

import javafx.scene.control.TextArea;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Created by a.kalenkevich on 08.02.2017.
 */
public class TextFieldLoggerAppender extends AppenderSkeleton {
    private javafx.scene.control.TextArea textField;

    public TextFieldLoggerAppender(TextArea textField) {
        this.textField = textField;
    }

    @Override
    protected void append(LoggingEvent event) {
        if (event.getLevel().equals(Level.INFO)) {
            String message;
            message =
                    event.getLocationInformation().getClassName() +
                    " . " +
                    event.getLocationInformation().getMethodName() +
                    " : " +
                    event.getLocationInformation().getLineNumber() +
                    " - " +
                    event.getMessage().toString();
            appendText(message);
        }
    }

    private void appendText(String text) {
        textField.setText(textField.getText() + '\n' + text);
    }

    @Override
    public void close() {

    }

    @Override
    public boolean requiresLayout() {
        return false;
    }
}