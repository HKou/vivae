package vivae.util;

import vivae.arena.Arena;
import vivae.arena.parts.Surface;
import java.awt.*;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: koutnij
 * Date: Oct 17, 2008
 * Time: 4:11:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class FrictionBuffer {

    double[][] buffer;
    int width, height;
    private Arena arena;

    public FrictionBuffer(Arena arena) {
        this.arena = arena;
        width = arena.screenWidth;
        height = arena.screenHeight;
        buffer = new double[width][height];
        //fill buffer with default value.. NaN means the value was not yet computed
        for (int i = 0; i < width; i++) {
            Arrays.fill(buffer[i], Double.NaN);
        }
    }

    public double getFriction(int x, int y) {
        if (x < 0 || y < 0 || x >= width || y >= height) {
            return 0;
        } else {
            if (Double.isNaN(buffer[x][y])) {
                double friction = -1;
                for (Surface arenaSurf : arena.getSurfaces()) {
                    if (arenaSurf.getShape().contains(x, y)) {
                        friction = arenaSurf.getFriction();
                    }
                }
                buffer[x][y] = friction;
            }
            return buffer[x][y];
        }
    }

    public void paintSensorBuffer(Graphics2D g) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                float c = (float) buffer[i][j] / 10;
                g.setColor(new Color(c, c, c));
                g.fillRect(i, j, 1, 1);
            }
        }
    }
}
