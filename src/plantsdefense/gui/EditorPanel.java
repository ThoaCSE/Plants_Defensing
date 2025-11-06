package plantsdefense.gui;

import plantsdefense.dao.LevelDAO;
import plantsdefense.model.Tile;
import plantsdefense.util.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class EditorPanel extends JPanel {

    private final Tile[][] grid;
    private final Button[] tileButtons = new Button[4];
    private final LevelDAO dao = new LevelDAO();
    private int selectedType = Constants.tile_grass;

    public EditorPanel() {
        setLayout(null);
        setPreferredSize(new Dimension(Constants.window_width, Constants.window_height));
        setBackground(Color.DARK_GRAY);

        grid = dao.loadLevel("level1.txt");

        setupTileButtons();
        setupSaveButton();
        setupMouseInput();

        repaint();
    }

    private void setupTileButtons() {
        String[] labels = {"Grass", "Path", "Start", "End"};
        int[] types = {Constants.tile_grass, Constants.tile_path, Constants.tile_begin, Constants.tile_end};

        for (int i = 0; i < 4; i++) {
            int idx = i;
            tileButtons[i] = new Button(labels[i], 10, 50 + i * 50, e -> selectTileType(types[idx]));
            add(tileButtons[i]);
        }
        tileButtons[0].setSelected(true);   // default = Grass
    }

    private void selectTileType(int type) {
        selectedType = type;
        for (Button b : tileButtons) b.deselect();
        for (Button b : tileButtons) {
            if (b.getText().equals(labelFor(type))) {
                b.setSelected(true);
                break;
            }
        }
    }

    private String labelFor(int type) {
        return switch (type) {
            case Constants.tile_grass -> "Grass";
            case Constants.tile_path  -> "Path";
            case Constants.tile_begin -> "Start";
            case Constants.tile_end   -> "End";
            default -> "Grass"; // change to water or something else later
        };
    }

    private void setupSaveButton() {
        Button save = new Button("SAVE", 10, 300, e -> { dao.saveLevel("level1.txt", grid); JOptionPane.showMessageDialog(this, "Saved!"); });
        add(save);
    }

    private void setupMouseInput() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int mouseX = e.getX(), my = e.getY();
                int gx = mouseX / Constants.tile_size;
                int gy = my / Constants.tile_size;

                if (gx >= 0 && gx < Constants.cols && gy >= 0 && gy < Constants.rows) {
                    grid[gy][gx].setType(selectedType);
                    repaint();
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int row = 0; row < Constants.rows; row++) {
            for (int col = 0; col < Constants.cols; col++) {
                Tile tile = grid[row][col];
                if (tile == null) continue;

                // sprite
                g2D.drawImage(tile.getSprite(), tile.getX(), tile.getY(), null);

                // DEBUG outline (remove later)
                g2D.setColor(Color.WHITE);
                g2D.drawRect(tile.getX(), tile.getY(),
                        Constants.tile_size, Constants.tile_size);
            }
        }
    }
}