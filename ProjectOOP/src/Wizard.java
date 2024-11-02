import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class Wizard extends GameObject {
    
    Handler handler;
    Game game;
    // Constants for movement
    private final float maxSpeed = 3f;     // Max speed
    private boolean facingLeft = false;
    
    private BufferedImage[] wizard_image_idel = new BufferedImage[4];
    private BufferedImage[] wizard_image_run = new BufferedImage[4];
    
    Animation animIdle;
    Animation animRun;

    public Wizard(int x, int y, ID id, Handler handler, Game game,SpriteSheet ss) {
        super(x, y, id, ss);
        this.handler = handler;
        this.game = game;
        
        wizard_image_idel[0] = ss.grabImage(1, 1, 16, 27); 
        wizard_image_idel[1] = ss.grabImage(2, 1, 16, 27); 
        wizard_image_idel[2] = ss.grabImage(3, 1, 16, 27); 
        wizard_image_idel[3] = ss.grabImage(4, 1, 16, 27);
        
        wizard_image_run[0] = ss.grabImage(5, 1, 16, 27); 
        wizard_image_run[1] = ss.grabImage(6, 1, 16, 27); 
        wizard_image_run[2] = ss.grabImage(7, 1, 16, 27); 
        wizard_image_run[3] = ss.grabImage(8, 1, 16, 27);
        
        animIdle = new Animation(5,wizard_image_idel[0],wizard_image_idel[1],wizard_image_idel[2],wizard_image_idel[3]);
        animRun = new Animation(5,wizard_image_run[0],wizard_image_run[1],wizard_image_run[2],wizard_image_run[3]);
    }

    public void tick() {
        x += velX;
        y += velY;
        
        collision();
        
        // Horizontal movement (A and D)
        if (handler.isLeft() && handler.isRight()) {
            velX = 0; // Stop if both left and right are pressed
        } else if (handler.isLeft()) {
            velX = -maxSpeed; // Move left
            facingLeft = true; // Set facing direction to left
        } else if (handler.isRight()) {
            velX = maxSpeed;  // Move right
            facingLeft = false; // Set facing direction to right
        } else {
            velX = 0; // Stop if neither left nor right are pressed
        }

        // Vertical movement (W and S)
        if (handler.isUp() && handler.isDown()) {
            velY = 0; // Stop if both up and down are pressed
        } else if (handler.isUp()) {
            velY = -maxSpeed; // Move up
        } else if (handler.isDown()) {
            velY = maxSpeed;  // Move down
        } else {
            velY = 0; // Stop if neither up nor down are pressed
        }
        
        animIdle.runAnimation();
        animRun.runAnimation();
    }

    
    private void collision(){
    	for(int i = 0;i< handler.object.size();i++) {
    		GameObject tempObject = handler.object.get(i);
    		
    		if(tempObject.getId() == ID.Wall1) {
    			
    			if(getBounds().intersects(tempObject.getBounds())) {
    				x += velX * -1;
    				y += velY * -1;
    			}
    			
    		}
    		
    		if(tempObject.getId() == ID.Crate) {
    			
    			if(getBounds().intersects(tempObject.getBounds())) {
    				game.ammo += 10;
    				handler.removeObject(tempObject);
    			}
    			
    		}
    		
    		if(tempObject.getId() == ID.Enemy) {
    			
    			if(getBounds().intersects(tempObject.getBounds())) {
    				
    				if(game.hp > 0) {
    					game.hp -= 10;
        				handler.removeObject(tempObject);
    				}
    			}
    			
    		}
    		
    	}
}


    public void render(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        AffineTransform original = g2d.getTransform(); // Save original transform
        
        // Check if the wizard was last facing left
        if (facingLeft) {
            // Flip the image along the Y-axis
            g2d.scale(-1, 1);
            g2d.translate(-x - 32, y); // Adjust position after flipping
        } else {
            g2d.translate(x, y); // Normal rendering when facing right
        }
        
        if (velX == 0 && velY == 0) {
            animIdle.drawAnimationWizard(g2d, 0, 0, 0);
        } else {
            animRun.drawAnimationWizard(g2d, 0, 0, 0);
        }

        g2d.setTransform(original); // Restore original transform
    }



    public Rectangle getBounds() {
        return new Rectangle(x, y, 16, 32);
    }

    
}
