import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseInput extends MouseAdapter {

    private Handler handler;
    private Camera camera;
    private final Game game;

    public MouseInput(Handler handler, Camera camera, Game game){
        this.handler = handler;
        this.camera = camera;
        this.game = game;
    }

    public void mousePressed(MouseEvent e){
        if (!game.isGameRunning()) {
            game.handleMenuClick(e.getX(), e.getY());
            return;
        }

        int mx = (int) (e.getX() + camera.getX());
        int my = (int) (e.getY() + camera.getY());

        for(int i = 0; i < handler.object.size();i++){
            GameObject tempObject = handler.object.get(i);

            if(tempObject.getId() == ID.Player){
                handler.addObject(new Projectile(tempObject.getX()+16, tempObject.getY()+24, ID.Bullet, handler, mx, my));
            }
        }

    }

}
