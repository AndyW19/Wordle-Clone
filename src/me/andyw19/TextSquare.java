package me.andyw19;

import javax.swing.*;
import java.awt.*;

public class TextSquare {

    // Background colours for the JTextField
    private final Color emptyColor = new Color(46, 63, 71);
    private final Color notExistColor = new Color(76, 102, 115);
    private final Color existInWordColor = new Color(201, 199, 71);
    private final Color existAtPosColor = new Color(9, 173, 43);

    enum SquareStatus {
        Empty,
        NotInWord,
        ExistsInWord,
        ExistsAtPos,
    }

    private SquareStatus status;
    private Color color;
    private JTextField textField;
    private JFrame j;

    public TextSquare(JFrame jFrame) {
        j = jFrame;
        textField = new JTextField();
        status = SquareStatus.Empty;
        color = emptyColor;
        setStatus(SquareStatus.Empty);
    }

    public void setSquareText(char str) {
        textField.setText(String.valueOf(str));
    }

    public String getSquareText() {
        return textField.getText();
    }

    // Changes the background colour depending on status
    public void setStatus(SquareStatus status) {
        this.status = status;

        switch (status) {
            case Empty:
                color = emptyColor;
                break;
            case NotInWord:
                color = notExistColor;
                break;
            case ExistsInWord:
                color = existInWordColor;
                break;
            case ExistsAtPos:
                color = existAtPosColor;
                break;
        }

        textField.setBackground(color);

    }

    // Reset TextSquare to default state
    public void resetSquare() {
        textField.setText("");
        setStatus(SquareStatus.Empty);
    }

    public SquareStatus getStatus() {
        return status;
    }

    public JTextField getTextField() {
        return textField;
    }

}
