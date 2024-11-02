import java.awt.image.BufferedImage;

public class SpriteSheet {
	private BufferedImage image;
	
	public SpriteSheet(BufferedImage image) {
		this.image = image;
	}
	
	public BufferedImage grabImage(int col, int row, int width,int height) {
		return image.getSubimage((col*16)-16, (row*16)-16, width, height);
	}
	
	public BufferedImage grabImageEnemy(int col, int row, int width,int height) {
		return image.getSubimage((col*32)-32, (row*36)-36, width, height);
	}
}
