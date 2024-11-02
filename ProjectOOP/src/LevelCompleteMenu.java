import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LevelCompleteMenu extends MouseAdapter {
    private final Game game;
    private final Handler handler;

    public LevelCompleteMenu(Game game, Handler handler) {
        if (game == null || handler == null) {
            throw new IllegalArgumentException("Game and Handler cannot be null");
        }
        this.game = game;
        this.handler = handler;
    }

    public void render(Graphics g) {
        // Set background color to gray
        g.setColor(Color.GRAY);
        g.fillRect(0, 0, game.getWidth(), game.getHeight());

        Font titleFont = new Font("arial", Font.BOLD, 50);
        Font buttonFont = new Font("arial", Font.PLAIN, 30);

        g.setFont(titleFont);
        g.setColor(Color.WHITE);
        int titleWidth = g.getFontMetrics(titleFont).stringWidth("Level Complete!");
        g.drawString("Level Complete!", (game.getWidth() - titleWidth) / 2, 200);

        g.setFont(buttonFont);
        g.setColor(Color.DARK_GRAY);
        g.fillRect((game.getWidth() - 200) / 2, 300, 200, 64);

        g.setColor(Color.WHITE);
        int nextLevelWidth = g.getFontMetrics(buttonFont).stringWidth("Next Level");
        g.drawString("Next Level", (game.getWidth() - nextLevelWidth) / 2, 340);
    }

    public void mousePressed(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        // Check if the "Next Level" button is clicked
        if (mouseOver(mouseX, mouseY, (game.getWidth() - 200) / 2, 300, 200, 64)) {
            System.out.println("Next Level button clicked"); // Debugging output
            game.loadNextLevel();  // Correctly call loadNextLevel() when "Next Level" button is clicked
        }
    }

    private boolean mouseOver(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }
}
