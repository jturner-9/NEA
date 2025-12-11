import javax.swing.JFrame;
import java.awt.Dimension;


public class Window {

    public Window(int width, int height, String title, Game game){// constructor of window

        JFrame frame = new JFrame(title);
        frame.setPreferredSize(new Dimension(width,height)); // sets dimensions for program window
        frame.setMinimumSize(new Dimension(width,height));
        frame.setMaximumSize(new Dimension(width,height));

        frame.add(game); // adds game class to canvas
        frame.setResizable(false); // cannot resize window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null); // window centre on screen
        frame.setVisible(true);

    }



}
