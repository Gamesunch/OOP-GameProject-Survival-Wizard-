import java.awt.Graphics;
import java.util.Iterator;
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
    
    public int getEnemyCount() {
        int count = 0;
        for (GameObject obj : object) {
            if (obj.getId() == ID.Enemy) {
                count++;
            }
        }
        return count;
    }


    public void tick() {
        for (int i = 0; i < object.size(); i++) {
            GameObject tempObject = object.get(i);
            tempObject.tick();
        }

        // Use an iterator to safely remove objects
        Iterator<GameObject> iterator = object.iterator();
        while (iterator.hasNext()) {
            GameObject tempObject = iterator.next();
            if (tempObject.isMarkedForRemoval()) { // Add a method or condition to mark objects for removal
                iterator.remove();
            }
        }
    }

    

    public void render(Graphics g) {
        for (int i = 0; i < object.size(); i++) {
            GameObject tempObject = object.get(i);
            tempObject.render(g);
        }

        object.removeAll(toRemove);
        toRemove.clear();
    }
    
    public synchronized void addObject(GameObject object) {
        this.object.add(object);
    }

    public synchronized void removeObject(GameObject object) {
        this.object.remove(object);
    }

}
