import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Random;

public class EnemySmall extends GameObject {
    
    private Handler handler;
    private GameObject player;
    Random r = new Random();
    int choose = 0;
    int hp = 100;
    int detectionRange = 200;
    private float speed = 4f;
    private boolean facingLeft = false;
    
    private BufferedImage[] enemy_image_idle = new BufferedImage[4];
    private BufferedImage[] enemy_image_run = new BufferedImage[4];
    
    Animation animIdle;
    Animation animRun;

    public EnemySmall(int x, int y, ID id, Handler handler, SpriteSheet ss) {
        super(x, y, id, ss);
        this.handler = handler;
     
        enemy_image_idle[0] = ss.grabImageEnemy(1, 1, 16, 16); 
        enemy_image_idle[1] = ss.grabImageEnemy(2, 1, 16, 16); 
        enemy_image_idle[2] = ss.grabImageEnemy(3, 1, 16, 16); 
        enemy_image_idle[3] = ss.grabImageEnemy(4, 1, 16, 16);
        
        enemy_image_run[0] = ss.grabImageEnemy(1, 1, 16, 16); 
        enemy_image_run[1] = ss.grabImageEnemy(2, 1, 16, 16); 
        enemy_image_run[2] = ss.grabImageEnemy(3, 1, 16, 16); 
        enemy_image_run[3] = ss.grabImageEnemy(4, 1, 16, 16);
        
        animIdle = new Animation(10, enemy_image_idle[0], enemy_image_idle[1], enemy_image_idle[2], enemy_image_idle[3]);
        animRun = new Animation(10, enemy_image_run[0], enemy_image_run[1], enemy_image_run[2], enemy_image_run[3]);
    }

    public void tick() {
        for (GameObject obj : handler.object) {
            if (obj.getId() == ID.player) {
                this.player = obj;
                break;
            }
        }

        if (player != null && getDistanceToPlayer() <= detectionRange) {
            moveToPlayer();
        } else {
            randomMovement();
        }

        x += velX;
        y += velY;

        if (velX < 0) {
            facingLeft = true;
        } else if (velX > 0) {
            facingLeft = false;
        }

        checkCollisions();

        if (hp <= 0) handler.removeObject(this);

        animIdle.runAnimation();
        animRun.runAnimation();
    }

    private void randomMovement() {
        if (choose == 0) {
            velX = r.nextInt(3) - 1;
            velY = r.nextInt(3) - 1;
        }
        choose = r.nextInt(100);

        if (velX == 0 && velY == 0) {
            randomMovement();
        }
    }

    private double getDistanceToPlayer() {
        float diffX = player.getX() - x;
        float diffY = player.getY() - y;
        return Math.sqrt((diffX * diffX) + (diffY * diffY));
    }

    private void moveToPlayer() {
        float diffX = player.getX() - x;
        float diffY = player.getY() - y;

        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (diffX > 0 && !isCollidingOnDirection(1, 0)) {
                velX = speed;
                velY = 0;
            } else if (diffX < 0 && !isCollidingOnDirection(-1, 0)) {
                velX = -speed;
                velY = 0;
            }
        } else {
            if (diffY > 0 && !isCollidingOnDirection(0, 1)) {
                velX = 0;
                velY = speed;
            } else if (diffY < 0 && !isCollidingOnDirection(0, -1)) {
                velX = 0;
                velY = -speed;
            }
        }
    }

    private boolean isCollidingOnDirection(int xDir, int yDir) {
        Rectangle futureBounds = new Rectangle(x + (xDir * (int) speed), y + (yDir * (int) speed), 32, 32);
        for (GameObject tempObject : handler.object) {
            if (tempObject.getId() == ID.Wall1) {
                if (futureBounds.intersects(tempObject.getBounds())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void checkCollisions() {
        for (int i = 0; i < handler.object.size(); i++) {
            GameObject tempObject = handler.object.get(i);

            if (tempObject.getId() == ID.Wall1) {
                if (getBoundsBig().intersects(tempObject.getBounds())) {
                    handleWallCollision();
                }
            }

            if (tempObject.getId() == ID.Spells) {
                if (getBoundsBig().intersects(tempObject.getBounds())) {
                    hp -= 50;
                    handler.removeObject(tempObject);
                }
            }
        }
    }

    private void handleWallCollision() {
        if (velX != 0) {
            velX = 0;
        }
        if (velY != 0) {
            velY = 0;
        }
    }

    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        AffineTransform original = g2d.getTransform();
        
        if (facingLeft) {
            g2d.scale(-1, 1);
            g2d.translate(-x - 32, y);
        } else {
            g2d.translate(x, y);
        }
        
        if (velX == 0 && velY == 0) {
            animIdle.drawAnimationEnemy2(g2d, 0, 0, 0);
        } else {
            animRun.drawAnimationEnemy2(g2d, 0, 0, 0);
        }

        g2d.setTransform(original);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, 16, 16);
    }

    public Rectangle getBoundsBig() {
        return new Rectangle(x, y, 32, 32);
    }
}
