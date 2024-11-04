import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;

/**
 * Handler class for managing all game objects.
 * It includes methods for updating, rendering, adding, and removing game objects.
 */
public class Handler {
    
    LinkedList<GameObject> object = new LinkedList<GameObject>();

    List<GameObject> toRemove = new LinkedList<>();

    private boolean up = false, down = false, right = false, left = false;
    
    public LinkedList<GameObject> getObject() {
        return object;
    }
    
    public void clearObjects() {
        object.clear();
    }

    public void setObject(LinkedList<GameObject> object) {
        this.object = object;
    }

    public boolean isUp() {
        return up;
    }

    public void setUp(boolean up) {
        this.up = up;
    }

    public boolean isDown() {
        return down;
    }

    public void setDown(boolean down) {
        this.down = down;
    }

    public boolean isRight() {
        return right;
    }

    public void setRight(boolean right) {
        this.right = right;
    }

    public boolean isLeft() {
        return left;
    }

    public void setLeft(boolean left) {
        this.left = left;
    }

    public void tick() {
        for (int i = 0; i < object.size(); i++) {
            GameObject tempObject = object.get(i);
            tempObject.tick();
        }

        object.removeAll(toRemove);
        toRemove.clear();
    }
    

    public void render(Graphics g) {
        for (int i = 0; i < object.size(); i++) {
            GameObject tempObject = object.get(i);
            tempObject.render(g);
        }

        object.removeAll(toRemove);
        toRemove.clear();
    }
    
    public void addObject(GameObject tempObject) {
        object.add(tempObject);
    }
    

    public void removeObject(GameObject tempObject) {
        toRemove.add(tempObject);
    }
}
