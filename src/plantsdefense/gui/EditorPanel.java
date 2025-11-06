package plantsdefense.gui;

import plantsdefense.dao.LevelDAO;
import plantsdefense.model.Tile;
import plantsdefense.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EditorPanel extends JPanel {
    private final Tile[][] grid;
    private final Button[] tileButtons = new Button[4];
    private final LevelDAO dao = new LevelDAO();
    private  int selectedType = Constants.tile_grass;

    public EditorPanel(){
        setLayout(null);
        setPreferredSize(new Dimension(Constants.window_width, Constants.window_height));
        setBackground(Color.LIGHT_GRAY);

        grid = dao.loadLevel("level1.txt");

        setupTileButtons();
        setupSaveButton();
        setupMouseInput();
    }

    private void setupTileButtons(){
        String[] labels = {"Grass", "Path", "Start", "End"};
        int[] types = {Constants.tile_grass, Constants.tile_path, Constants.tile_begin, Constants.tile_end};
        for(int i = 0; i < 4; i++){
            int  finalOfi = i;
            tileButtons[i] = new Button(labels[i], 10, 50 +i * 50, e -> selectedTileType(types[finalOfi]));
            add(tileButtons[i]);
        }
    }

    private void selectedTileType(int type){
        selectedType = type;
        for (Button btn : tileButtons) btn.deselect();
        for(Button btn : tileButtons){
            if (btn.getText().equals(getLabelForType(type))){
                btn.setSelected(true);
                break;
            }
        }
    }

    private String getLabelForType(int type){
        return switch (type){
            case Constants.tile_grass -> "Grass";
            case Constants.tile_path ->  "Path";
            case Constants.tile_begin -> "Start";
            case Constants.tile_end -> "End";
            default -> "Grass";
        };
    }

    private void setupSaveButton(){
        Button saveBtn = new Button("SAVE", 10,300, e -> dao.saveLevel("level1.txt", grid));
        add(saveBtn);
    }

    private void setupMouseInput(){
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mouseX = e.getX();
                int mouseY = e.getY();
                int gridX = mouseX / Constants.tile_size;
                int gridY = mouseY/ Constants.tile_size;

                if (gridX >=0 && gridX < Constants.cols && gridY >= 0 && gridY < Constants.rows){
                    grid[gridY][gridX].setType(selectedType);
                    repaint();
                }
            }
        });
    }
    @Override
    protected void paintComponent (Graphics g){
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for(int row = 0; row < Constants.rows; row ++){
            for(int col = 0; col < Constants.cols; col ++){
                Tile tile = grid[row][col];
                if (tile !=null){
                    g2D.drawImage(tile.getSprite(), tile.getX(), tile.getY(), null);
                }
            }
        }
    }
}
