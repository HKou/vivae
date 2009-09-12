package vivae.util;

import vivae.arena.Arena;
import vivae.arena.parts.Surface;
import vivae.arena.parts.Road;
import java.awt.geom.Area;
import java.awt.*;
import java.util.Vector;

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

    public FrictionBuffer(Arena arena) {
        Area a;
        Vector<Surface> arenaSurfaces = arena.getSurfaces();
        Surface arenaSurf;
        Vector<Surface> sensorSurfaces;
        width = arena.screenWidth;
        height = arena.screenHeight;
        double friction;
        int surfacesExamined;
        boolean cont;
        buffer = new double[width][height];
        for (int i = 0; i < width; i++) {
           // if (i % 50 == 0) System.out.println("i = " + i);

            for (int j = 0; j < height; j++) {
                friction=-1;surfacesExamined=0;
                while(/*friction == -1 &&*/ surfacesExamined<arenaSurfaces.size()){
                   //a = new Area(new Rectangle2D.Double(i, j, 1, 1));
                   arenaSurf=arenaSurfaces.get(surfacesExamined);
                   //a.intersect(arenaSurf.getArea());
                   cont = arenaSurf.getShape().contains(i,j);

//                   if(cont){
//                       if(arenaSurfaces.get(surfacesExamined) instanceof Road){
//                           friction=arenaSurf.getFriction();
//
//                       }
//                   }
                   if(cont) friction=arenaSurf.getFriction();
                   surfacesExamined++;
                }
                //if(friction==-1)friction=10f; // grass
                buffer[i][j]=friction;
                /*
                sensorSurfaces = new Vector<Surface>();
                for (Surface s : arenaSurfaces) {
                    a = new Area(new Rectangle2D.Double(i, j, 1, 1));
                    a.intersect(s.getArea());
                    if (!a.isEmpty()) {
                        sensorSurfaces.add(s);
                    }
                }
                if (sensorSurfaces.isEmpty()) {
                    buffer[i][j] = 0f;
                } else {
                    Surface srfc = sensorSurfaces.get(sensorSurfaces.size() - 1);
                    buffer[i][j] = srfc.getFriction();
                } */
            }
        }
    }

    public double getFriction(int x, int y) {
        if(x<0 || y<0 || x>=width || y>=height)return 0;
        else return buffer[x][y];
    }

    public void paintSensorBuffer(Graphics2D g){
        for(int i=0;i<width;i++)
            for(int j=0;j<height;j++){
                float c=(float)buffer[i][j]/10;
                g.setColor(new Color(c,c,c));
                g.fillRect(i,j,1,1);
            }
    }
}
