import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class spells extends GameObject {

    private Handler handler;
    private static final double SPEED = 10.0; // Constant speed for all spells

    public spells(int x, int y, ID id, Handler handler, int mx, int my,SpriteSheet ss) {
        super(x, y, id, ss);
        this.handler = handler;

        double dx = mx - x;
        double dy = my - y;
        double distance = Math.sqrt(dx*dx + dy*dy);

        if (distance > 0) {
            velX = (float) ((dx / distance) * SPEED);
            velY = (float) ((dy / distance) * SPEED);
        } else {
            velX = 0;
            velY = 0;
        }
    }

	@Override
	public void tick() {
		x += velX;
		y += velY;
		
		for(int i = 0;i<handler.object.size();i++) {
			GameObject tempObject = handler.object.get(i);
			
			if(tempObject.getId() == ID.Wall1) {
				if(getBounds().intersects(tempObject.getBounds())) {
					handler.removeObject(this);
				}
			}
		}
		
	}

	@Override
	public void render(Graphics g) {
		g.setColor(Color.cyan);
		g.fillOval(x, y, 8, 8);
		
	}

	@Override
	public Rectangle getBounds() {
		return new Rectangle(x,y,8,8);
	}

}
