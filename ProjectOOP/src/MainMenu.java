import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainMenu extends MouseAdapter {
    private final Game game;
    private final Handler handler;

    public MainMenu(Game game, Handler handler) {
        if (game == null || handler == null) {
            throw new IllegalArgumentException("Game and Handler cannot be null");
        }
        this.game = game;
        this.handler = handler;
    }

    public void render(Graphics g) {
        Font titleFont = new Font("arial", Font.BOLD, 50);
        Font buttonFont = new Font("arial", Font.PLAIN, 30);

        g.setFont(titleFont);
        g.setColor(Color.WHITE);
        int titleWidth = g.getFontMetrics(titleFont).stringWidth("Survival Wizard!");
        g.drawString("Survival Wizard!", (game.getWidth() - titleWidth) / 2, 200);

        g.setFont(buttonFont);
        int startWidth = g.getFontMetrics(buttonFont).stringWidth("Start");
        int exitWidth = g.getFontMetrics(buttonFont).stringWidth("Exit");

        g.drawRect((game.getWidth() - 200) / 2, 300, 200, 64);
        g.drawString("Start", (game.getWidth() - startWidth) / 2, 340);

        g.drawRect((game.getWidth() - 200) / 2, 400, 200, 64);
        g.drawString("Exit", (game.getWidth() - exitWidth) / 2, 440);
    }

    public void mousePressed(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        if (mouseOver(mouseX, mouseY, (game.getWidth() - 200) / 2, 300, 200, 64)) {
            Game.gameState = Game.STATE.Game;
        }

        if (mouseOver(mouseX, mouseY, (game.getWidth() - 200) / 2, 400, 200, 64)) {
            System.exit(0);
        }
    }

    private boolean mouseOver(int mouseX, int mouseY, int x, int y, int width, int height) {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }
}
