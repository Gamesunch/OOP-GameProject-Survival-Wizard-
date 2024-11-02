import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Crate extends GameObject {
	
	private BufferedImage carte_image;

	public Crate(int x, int y, ID id,SpriteSheet ss) {
		super(x, y, id, ss);
		carte_image = ss.grabImage(1, 1, 16, 24);
	}


	public void tick() {
		
	}


	public void render(Graphics g) {
		g.drawImage(carte_image, x,y,32,32,null);
		
	}


	public Rectangle getBounds() {
		return new Rectangle(x, y, 32, 48);
	}

}
