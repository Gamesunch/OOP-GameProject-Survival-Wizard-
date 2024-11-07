
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;


public class WallVoid extends GameObject {
	
	private BufferedImage block_image;

	public WallVoid(int x, int y, ID id, SpriteSheet ss) {
		super(x, y, id, ss);
		block_image = ss.grabImage(10, 3, 16, 16);
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render(Graphics g) {
		g.drawImage(block_image, x,y,32,32,null);
		
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(x, y, 48,48);
	}

}
