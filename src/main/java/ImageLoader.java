import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageLoader {

    private BufferedImage image;

    public BufferedImage loadImage(String path){
        try {
            image = ImageIO.read(getClass().getResource(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return image;
    }

}
