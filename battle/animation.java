package battle;
import java.awt.Image;
import javax.imageio.ImageIO;

public class animation {
    Image imageIcon;
    double xCoord;
    double yCoord;
    int timeLeft;
    double velX;
    double velY;
    float opacity;

    public void createAnimation(double x, double y, int time, Image imageToDisplay, double velX, double velY, float opacity) {
        this.xCoord = x;
        this.yCoord = y;
        this.timeLeft = time;
        this.imageIcon = imageToDisplay;
        this.velX = velX;
        this.velY = velY;
        this.opacity = opacity;
    }
}
