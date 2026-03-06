import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class Input extends KeyAdapter {

    Handler handler;
    private final Game game;

    public Input(Handler handler, Game game){
        this.handler = handler;
        this.game = game;
    }


    public void keyPressed(KeyEvent e){
        if (!game.isGameRunning()) {
            return;
        }

        int key = e.getKeyCode();

        for(int i = 0; i < handler.object.size(); i++){
            GameObject tempObject = handler.object.get(i);

            if (tempObject.getId()==ID.Player){
                if(key == KeyEvent.VK_W) handler.setUp(true);
                if(key == KeyEvent.VK_S) handler.setDown(true);
                if(key == KeyEvent.VK_D) handler.setRight(true);
                if(key == KeyEvent.VK_A) handler.setLeft(true);
            }
        }

    }

    public void keyReleased(KeyEvent e){
        if (!game.isGameRunning()) {
            return;
        }

        int key = e.getKeyCode();

        for(int i = 0; i < handler.object.size(); i++){
            GameObject tempObject = handler.object.get(i);

            if (tempObject.getId()==ID.Player){
                if(key == KeyEvent.VK_W) handler.setUp(false);
                if(key == KeyEvent.VK_S) handler.setDown(false);
                if(key == KeyEvent.VK_D) handler.setRight(false);
                if(key == KeyEvent.VK_A) handler.setLeft(false);
            }
        }



    }


}
