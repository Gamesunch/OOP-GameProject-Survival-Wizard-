import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game extends Canvas implements Runnable {

    private boolean isRunning = false;
    private Thread thread;
    private Handler handler;
    private Camera camera;
    private SpriteSheet ssFloor;
    private SpriteSheet ssWall;
    private SpriteSheet ssPlayer;
    private SpriteSheet ssEnemy1;
    private SpriteSheet ssEnemy2;
    private SpriteSheet ssETC;

    private BufferedImage level = null;
    private BufferedImage floor_sprite_sheets = null;
    private BufferedImage wall_sprite_sheets = null;
    private BufferedImage player_sprite_sheets = null;
    private BufferedImage enemy1_sprite_sheets = null;
    private BufferedImage enemy2_sprite_sheets = null;
    private BufferedImage etc_sprite_sheets = null;
    private BufferedImage floor = null;

    public int ammo = 51;
    public int hp = 100;
    public int timeLeft = 120;
    private long lastTimerUpdate;

    public enum STATE {
        Menu,
        Game,
        Win,
        GameOver,
        Timeout
    }

    public static STATE gameState = STATE.Menu;
    private MainMenu mainMenu;

    public int currentLevel = 0;
    public BufferedImage[] levels;

    public Game() {
    	JFrame frame = new JFrame("Survival Wizard!");	
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.add(this);
        frame.setVisible(true);

        handler = new Handler();
        camera = new Camera(0, 0);
        mainMenu = new MainMenu(this, handler);

        // Initially, only add MainMenu listener
        this.addMouseListener(mainMenu);
        this.addKeyListener(new KeyInput(handler));

        BufferedImageLoader loader = new BufferedImageLoader();

        // Load levels
        levels = new BufferedImage[]{
            loader.loadImage("/Level.png"),
            loader.loadImage("/Level2.png"),
            loader.loadImage("/Level3.png") // Add more levels as needed
        };
        level = levels[currentLevel];

        floor_sprite_sheets = loader.loadImage("/atlas_floor-16x16.png");
        wall_sprite_sheets = loader.loadImage("/atlas_walls_low-16x16.png");
        player_sprite_sheets = loader.loadImage("/player_spritesheet.png");
        enemy1_sprite_sheets = loader.loadImage("/enemy1_spritesheet.png");
        enemy2_sprite_sheets = loader.loadImage("/spritesheet_Enemy2.png");
        etc_sprite_sheets = loader.loadImage("/spritesheet_Etc.png");

        ssFloor = new SpriteSheet(floor_sprite_sheets);
        ssWall = new SpriteSheet(wall_sprite_sheets);
        ssPlayer = new SpriteSheet(player_sprite_sheets);
        ssEnemy1 = new SpriteSheet(enemy1_sprite_sheets);
        ssEnemy2 = new SpriteSheet(enemy2_sprite_sheets);
        ssETC = new SpriteSheet(etc_sprite_sheets);

        floor = ssFloor.grabImage(1, 1, 16, 16);

        this.addMouseListener(new MouseInput(handler, camera, this, ssFloor));
        loadLevel(currentLevel);
        start();
    }

    private synchronized void start() {
        if (isRunning) return;
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    private synchronized void stop() {
        if (!isRunning) return;
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int updates = 0;
        int frames = 0;
        this.requestFocus();
        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                tick();
                updates++;
                delta--;
            }
            render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                System.out.println("FPS: " + frames + " TICKS: " + updates);
                frames = 0;
                updates = 0;
            }
        }
        stop();
    }

    public void tick() {
        if (gameState == STATE.Game) {
            synchronized (handler) {
                for (int i = 0; i < handler.object.size(); i++) {
                    if (handler.object.get(i).getId() == ID.player) {
                        camera.tick(handler.object.get(i));
                    }
                }
                if (handler != null) handler.tick();
            }

            // Check if player died
            if (hp <= 0) {
                gameState = STATE.GameOver;
                return;
            }

            // Update time left
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastTimerUpdate >= 1000) {
                timeLeft--;
                lastTimerUpdate = currentTime;
                if (timeLeft <= 0) {
                    gameState = STATE.Timeout;
                }
            }

            // Check if all enemies are eliminated
            boolean allEnemiesEliminated;
            synchronized (handler) {
                allEnemiesEliminated = handler.object.stream().noneMatch(obj -> obj.getId() == ID.Enemy);
            }
            if (allEnemiesEliminated) {
                System.out.println("All enemies eliminated. Loading next level...");
                loadNextLevel();
            }
        }
    }



    public void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null) {
            this.createBufferStrategy(2);
            return;
        }

        Graphics g = bs.getDrawGraphics();
        Graphics2D g2d = (Graphics2D) g;

        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());

        if (gameState == STATE.Game) {
            g2d.translate(-camera.getX(), -camera.getY());

            // Draw the level floor
            for (int xx = 0; xx < level.getWidth() * 32; xx += 32) {
                for (int yy = 0; yy < level.getHeight() * 32; yy += 32) {
                    g.drawImage(floor, xx, yy, 32, 32, null);
                }
            }
            synchronized (handler) {
                handler.render(g);
            }

            g2d.translate(camera.getX(), camera.getY());

            // HUD
            g.setColor(Color.gray);
            g.fillRect(5, 5, 200, 32);
            g.setColor(Color.green);
            g.fillRect(5, 5, Math.max(0, hp * 2), 32);
            g.setColor(Color.black);
            g.drawRect(5, 5, 200, 32);

            Font gameFont = new Font("MinecraftCHMC", Font.BOLD, 48);
            g.setFont(gameFont);
            g.setColor(Color.white);
            g.drawString("Mana : " + ammo, 5, 100);

            // Draw the number of enemies still alive
            int enemyCount;
            synchronized (handler) {
                enemyCount = (int) handler.object.stream().filter(obj -> obj.getId() == ID.Enemy).count();
            }
            g.drawString("Enemies Alive: " + enemyCount, 5, 140);

            // Draw current level
            g.drawString("Current Level: " + (currentLevel + 1), 5, 180);

            // Draw time left
            g.drawString("Time Left: " + timeLeft + " sec", getWidth() / 2 - 100, 50);
        } else if (gameState == STATE.Menu) {
            mainMenu.render(g);
        } else if (gameState == STATE.Win) {
            g.setColor(Color.GREEN);
            g.setFont(new Font("MinecraftCHMC", Font.BOLD, 100));
            int textWidth = g.getFontMetrics().stringWidth("You Escaped!");
            int textHeight = g.getFontMetrics().getHeight();
            g.drawString("You Escaped!", (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2);
        } else if (gameState == STATE.GameOver) {
            g.setColor(Color.red);
            g.setFont(new Font("MinecraftCHMC", Font.BOLD, 100));
            int textWidth = g.getFontMetrics().stringWidth("You Died");
            int textHeight = g.getFontMetrics().getHeight();
            g.drawString("You Died", (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2);
        } else if (gameState == STATE.Timeout) {
            g.setColor(Color.orange);
            g.setFont(new Font("MinecraftCHMC", Font.BOLD, 100));
            int textWidth = g.getFontMetrics().stringWidth("Time Out");
            int textHeight = g.getFontMetrics().getHeight();
            g.drawString("Time Out", (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2);
        }

        g.dispose();
        bs.show();
    }



    public void loadLevel(int levelIndex) {
        if (levelIndex < levels.length) {
            level = levels[levelIndex];
            handler.clearObjects(); // Clear all objects from the previous level
            loadLevel(level);
        }
    }

    private void loadLevel(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();

        for (int xx = 0; xx < w; xx++) {
            for (int yy = 0; yy < h; yy++) {
                int pixel = image.getRGB(xx, yy);
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = (pixel) & 0xff;

                if (red == 105 && green == 0 && blue == 0) {
                    handler.addObject(new Block(xx * 32, yy * 32, ID.Wall1, ssWall));
                }
                if (red == 255 && green == 255 && blue == 0) {
                    handler.addObject(new WallLeft(xx * 32, yy * 32, ID.Wall1, ssWall));
                }
                if (red == 255 && green == 0 && blue == 255) {
                    handler.addObject(new WallRight(xx * 32, yy * 32, ID.Wall1, ssWall));
                }
                if (red == 0 && green == 50 && blue == 0) {
                    handler.addObject(new WallButtomLeft(xx * 32, yy * 32, ID.Wall1, ssWall));
                }
                if (red == 0 && green == 60 && blue == 0) {
                    handler.addObject(new WallTopLeft(xx * 32, yy * 32, ID.Wall1, ssWall));
                }
                if (red == 0 && green == 70 && blue == 0) {
                    handler.addObject(new WallTopRight(xx * 32, yy * 32, ID.Wall1, ssWall));
                }
                if (red == 0 && green == 80 && blue == 0) {
                    handler.addObject(new WallButtomRight(xx * 32, yy * 32, ID.Wall1, ssWall));
                }
                if (red == 100 && green == 100 && blue == 100) {
                    handler.addObject(new WallVoid(xx * 32, yy * 32, ID.Wall1, ssWall));
                }
                if (red == 110 && green == 0 && blue == 100) {
                    handler.addObject(new WallTopInLeft(xx * 32, yy * 32, ID.Wall1, ssWall));
                }
                if (red == 120 && green == 0 && blue == 100) {
                    handler.addObject(new WallTopInRight(xx * 32, yy * 32, ID.Wall1, ssWall));
                }
                if (red == 130 && green == 0 && blue == 100) {
                    handler.addObject(new WallButtomInRight(xx * 32, yy * 32, ID.Wall1, ssWall));
                }
                if (red == 140 && green == 0 && blue == 100) {
                    handler.addObject(new WallButtomInLeft(xx * 32, yy * 32, ID.Wall1, ssWall));
                }
                if (red == 0 && green == 130 && blue == 0) {
                    handler.addObject(new WallHL(xx * 32, yy * 32, ID.Wall1, ssWall));
                }
                if (red == 0 && green == 110 && blue == 0) {
                    handler.addObject(new WallHR(xx * 32, yy * 32, ID.Wall1, ssWall));
                }
                if (blue == 255 && green == 0 && red == 0) {
                    handler.addObject(new Wizard(xx * 32, yy * 32, ID.player, handler, this, ssPlayer));
                }
                if (green == 255 && blue == 0 && red == 0) {
                    handler.addObject(new EnemyBig(xx * 32, yy * 32, ID.Enemy, handler, ssEnemy1));
                }
                if (green == 180 && blue == 0 && red == 0) {
                    handler.addObject(new EnemySmall(xx * 32, yy * 32, ID.Enemy, handler, ssEnemy2));
                }
                if (green == 255 && blue == 255) {
                    handler.addObject(new Crate(xx * 32, yy * 32, ID.Crate, ssETC));
                }
            }
        }
    }

    public void loadNextLevel() {
        System.out.println("loadNextLevel() called."); // Debugging output
        currentLevel++;
        if (currentLevel < levels.length) {
            System.out.println("Loading level: " + currentLevel);
            loadLevel(currentLevel);
            gameState = STATE.Game;
            timeLeft = 120; // Reset timer for new level

            // Set listeners correctly
            removeMouseListener(mainMenu);
            addMouseListener(new MouseInput(handler, camera, this, ssFloor));
        } else {
            System.out.println("No more levels available.");
            gameState = STATE.Win;
        }
    }

    public static void main(String args[]) {
        new Game();
    }
}