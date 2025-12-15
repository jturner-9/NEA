import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;

public class Game extends Canvas implements Runnable{

    private static final long serialVersionUID= 1L;

    private boolean isRunning = false;
    private Thread thread;
    private Handler handler;

    private BufferedImage level = null;

    public Game(){
        new Window(1000, 563, "Shooter", this);
        start();

        handler= new Handler();
        this.addKeyListener(new Input(handler));

        ImageLoader loader = new ImageLoader();
        level = loader.loadImage("/game_level.png");

        loadLevel(level);

    }

    private void start(){
        isRunning = true;
        thread= new Thread(this);
        thread.start();
    }

    private void stop(){
        isRunning= false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public void run(){
        this.requestFocus();
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) /ns;
            lastTime = now;
            while(delta >= 1) {
                tick();
                //updates++;
                delta--;
            }
            render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                frames = 0;
                //updates = 0;
            }
        }
        stop();
    }

    public void tick(){
        handler.tick();
    }

    public void render(){
        BufferStrategy bs = this.getBufferStrategy();// buffer strategy created null and when this happens 3 arguements are created
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
/// ///////////////////////////////////////
        g.setColor(Color.white);
        g.fillRect(0,0,1000,563);

        handler.render(g);



/// ////////////////////////////////////////
        g.dispose();
        bs.show();


    }

    private void loadLevel(BufferedImage image){
        int w = image.getWidth();
        int h = image.getHeight();

        for(int xx = 0; xx< w; xx++){
            for(int yy = 0; yy < h; yy++){
                int pixel = image.getRGB(xx,yy);
                int red = (pixel>>16) & 0xff;
                int green= (pixel>>8) & 0xff;
                int blue = (pixel) & 0xff;

                if(red==255)
                    handler.addObject(new Block(xx*32,yy*32,ID.Block));

                if(blue== 255)
                    handler.addObject(new Player(xx*32,yy*32, ID.Player,handler));

            }
        }


    }

    public static void main(String args[]){
        new Game();
    }


}
