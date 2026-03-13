import java.util.LinkedList;
import java.awt.Graphics;

public class Handler {

    private static final int ENEMY_RESPAWN_TICKS = 180;

    LinkedList<GameObject> object = new LinkedList<GameObject>();
    private final LinkedList<EnemyRespawn> pendingEnemyRespawns = new LinkedList<>();

    private boolean up = false, down=false, right=false, left= false;
    private int enemiesKilled = 0;


    public void tick(){
        for(int i = 0; i<object.size(); i++){
            GameObject tempObject = object.get(i);

            tempObject.tick();

        }

        processEnemyRespawns();
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

    public void handleEnemyDefeat(GameObject enemy) {
        if (enemy == null || enemy.getId() != ID.Enemy) {
            return;
        }

        pendingEnemyRespawns.add(new EnemyRespawn(enemy.getX(), enemy.getY(), ENEMY_RESPAWN_TICKS));
        removeObject(enemy);
        registerEnemyKill();
    }

    public int getEnemiesKilled() {
        return enemiesKilled;
    }

    public void resetRoundStats() {
        enemiesKilled = 0;
        pendingEnemyRespawns.clear();
    }

    private void processEnemyRespawns() {
        for (int i = 0; i < pendingEnemyRespawns.size(); i++) {
            EnemyRespawn respawn = pendingEnemyRespawns.get(i);
            respawn.ticksUntilSpawn--;

            if (respawn.ticksUntilSpawn <= 0) {
                addObject(new Enemy(respawn.x, respawn.y, ID.Enemy, this));
                pendingEnemyRespawns.remove(i);
                i--;
            }
        }
    }

    private static class EnemyRespawn {
        private final int x;
        private final int y;
        private int ticksUntilSpawn;

        private EnemyRespawn(int x, int y, int ticksUntilSpawn) {
            this.x = x;
            this.y = y;
            this.ticksUntilSpawn = ticksUntilSpawn;
        }
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
