import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class Block extends GameObject {
	
	private BufferedImage block_image;

	public Block(int x, int y, ID id, SpriteSheet ss) {
		super(x, y, id, ss);
		
		block_image = ss.grabImage(10, 4, 16, 16);
	}

	public void tick() {
		
		
	}

	public void render(Graphics g) {
		g.drawImage(block_image, x,y,32,32,null);
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, 8,8);
	}

}
