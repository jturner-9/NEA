import java.awt.*;
import java.awt.geom.Line2D;

public class Enemy extends GameObject {

    private static final float SPEED = 2.5f;
    private static final float SPOT_DISTANCE = 260f;

    private final Handler handler;
    private boolean spottedPlayer;

    public Enemy(int x, int y, ID id, Handler handler) {
        super(x, y, id);
        this.handler = handler;
    }

    @Override
    public void tick() {
        GameObject player = handler.getPlayer();
        if (player == null) {
            velX = 0;
            velY = 0;
            return;
        }

        spottedPlayer = canSpotPlayer(player);

        if (spottedPlayer) {
            float dx = player.getX() - x;
            float dy = player.getY() - y;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);

            if (distance > 0.0001f) {
                velX = SPEED * (dx / distance);
                velY = SPEED * (dy / distance);
            }
        } else {
            velX = 0;
            velY = 0;
        }

        x += velX;
        resolveBlockCollision(true);

        y += velY;
        resolveBlockCollision(false);
    }

    private boolean canSpotPlayer(GameObject player) {
        float enemyCenterX = x + 16;
        float enemyCenterY = y + 16;
        float playerCenterX = player.getX() + 16;
        float playerCenterY = player.getY() + 24;

        float dx = playerCenterX - enemyCenterX;
        float dy = playerCenterY - enemyCenterY;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance > SPOT_DISTANCE) {
            return false;
        }

        Line2D.Float sightLine = new Line2D.Float(enemyCenterX, enemyCenterY, playerCenterX, playerCenterY);
        for (GameObject object : handler.object) {
            if (object.getId() == ID.Block && sightLine.intersects(object.getBounds())) {
                return false;
            }
        }
        return true;
    }

    private void resolveBlockCollision(boolean horizontalMove) {
        for (GameObject object : handler.object) {
            if (object.getId() == ID.Block && getBounds().intersects(object.getBounds())) {
                if (horizontalMove) {
                    x -= velX;
                } else {
                    y -= velY;
                }
                return;
            }
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(spottedPlayer ? Color.RED : new Color(130, 20, 20));
        g.fillRect(x, y, 32, 32);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, 32, 32);
    }
}
