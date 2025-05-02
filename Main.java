import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("윷놀이 게임판");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
        frame.add(new GamePanel());
        frame.setVisible(true);
    }
}

class GamePanel extends JPanel {
    private final int cellSize = 80;
    private final int padding = 50;

   
    private final String[][] board = {
            {"도착", "○", "○", "○", "○"},
            {"○", "*", "○", "*", "○"},
            {"○", "○", "*", "○", "○"},
            {"○", "*", "○", "*", "○"},
            {"○", "○", "○", "○", "출발"}
    };

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        
        g2.setFont(new Font("SansSerif", Font.BOLD, 14));

       
        g2.setColor(Color.LIGHT_GRAY);
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                int x = padding + col * cellSize + cellSize / 2;
                int y = padding + row * cellSize + cellSize / 2;

                
                if (col < 4) {
                    int x2 = padding + (col + 1) * cellSize + cellSize / 2;
                    int y2 = y;
                    g2.drawLine(x, y, x2, y2);
                }

               
                if (row < 4) {
                    int x2 = x;
                    int y2 = padding + (row + 1) * cellSize + cellSize / 2;
                    g2.drawLine(x, y, x2, y2);
                }
            }
        }

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                int x = padding + col * cellSize;
                int y = padding + row * cellSize;

               
                g2.setColor(Color.BLACK);
                g2.drawRect(x, y, cellSize, cellSize);

                
                g2.setColor(Color.DARK_GRAY);
                String text = board[row][col];
                drawCenteredString(g2, text, x, y, cellSize, cellSize);
            }
        }
    }


    private void drawCenteredString(Graphics2D g2, String text, int x, int y, int width, int height) {
        FontMetrics fm = g2.getFontMetrics();
        int textX = x + (width - fm.stringWidth(text)) / 2;
        int textY = y + (height - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(text, textX, textY);
    }
}
