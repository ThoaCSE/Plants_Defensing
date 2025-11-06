package plantsdefense.gui;

import plantsdefense.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class Button  extends JButton{
    private boolean selected = false;

    public Button(String text, int x, int y, ActionListener listener){
        setText(text);
        setFont(new Font("Lora", Font.BOLD, 16));
        setBounds(x, y, 120,40);
        setFocusPainted(false);
        addActionListener(e -> {
            listener.actionPerformed(e);
            setSelected(true);
        });
    }

    public void setSelected(boolean selected){
        this.selected = selected;
        setBackground(selected ? new Color(100, 180, 255): UIManager.getColor("Button.background"));
    }

    public void deselect(){
        setSelected(false);
    }

    public static Button tileScaled(String text, int gridX, int gridY, ActionListener listener){
        int x = gridX * Constants.tile_size + 10;
        int y = gridY * Constants.tile_size + 10;
        return new Button(text, x, y, listener);
    }
}
