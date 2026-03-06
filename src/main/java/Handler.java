import java.util.LinkedList;
import java.awt.Graphics;

public class Handler {

    LinkedList<GameObject> object = new LinkedList<GameObject>();

    private boolean up = false, down=false, right=false, left= false;
    private int enemiesKilled = 0;


    public void tick(){
        for(int i = 0; i<object.size(); i++){
            GameObject tempObject = object.get(i);

            tempObject.tick();

        }
    }

    public void render(Graphics g){
        for(int i= 0; i < object.size(); i++){
            GameObject tempObject = object.get(i);

            tempObject.render(g);
        }
    }


    public void addObject(GameObject tempObject){
        object.add(tempObject);
    }

    public void removeObject (GameObject tempObject){
        object.remove(tempObject);
    }

    public void registerEnemyKill() {
        enemiesKilled++;
    }

    public int getEnemiesKilled() {
        return enemiesKilled;
    }

    public void resetRoundStats() {
        enemiesKilled = 0;
    }

    public GameObject getPlayer() {
        for (GameObject gameObject : object) {
            if (gameObject.getId() == ID.Player) {
                return gameObject;
            }
        }
        return null;
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
}
