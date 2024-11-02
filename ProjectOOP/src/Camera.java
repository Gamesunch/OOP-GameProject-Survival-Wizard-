public class Camera {

    private float x, y;
    
    public Camera(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public void tick(GameObject object) {
        // Center the camera on the character
        x += ((object.getX() - x) - 1920 / 2) * 0.05f;
        y += ((object.getY() - y) - 1080 / 2) * 0.05f;
        
        // Keep the camera within bounds
        if (x <= 0) x = 0;
        if (x >= 1032 / 4) x = 1032 / 4; // Adjust these values based on the map size
        if (y <= 0) y = 0;
        if (y >= 563 + 450) y = 563 + 450; // Adjust these values based on the map size
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
