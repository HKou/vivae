/**
 * This is VIVAE (Visual Vector Agent Environment)
 * a library allowing for simulations of agents in co-evolution 
 * written as a bachelor project 
 * by Petr Smejkal
 * at Czech Technical University in Prague
 * in 2008
 */

package vivae.example;

import vivae.controllers.*;
import java.util.Iterator;
import vivae.arena.parts.Movable;
import vivae.arena.parts.sensors.Sensor;

/**
 * A sample Controller extending RobotWithSensorController. 
 * It's logic is very simple. If it spots a fixed obstacle it turn the opposite way.
 * It's up to you to create more advanced controllers!!
 * @author Petr Smejkal
 */
public class MyController extends RobotWithSensorController{

    @Override
    public void moveControlledObject() {
       allObjects = robot.getArena().getVivaes();
       float angle = 0f; 
        for (Iterator<Sensor> it = sensors.iterator(); it.hasNext();) {
            Sensor sensor = it.next();
            objectsOnSight = sensor.getVivaesOnSight(allObjects);

            if(objectsOnSight != null) {
                if(!objectsOnSight.isEmpty()) {
                    angle = sensor.getAngle();
                    vivae.arena.parts.VivaeObject vo = objectsOnSight.get(0);
                    org.apache.batik.ext.awt.geom.Polygon2D shape = (org.apache.batik.ext.awt.geom.Polygon2D)vo.getShape();
                    java.awt.geom.PathIterator pi = shape.getPathIterator(vo.getTranslation());
                    float[] coords = new float[6];
                    shape.getPolygon();
                    while(!pi.isDone()){
                        pi.currentSegment(coords);
                        

                        pi.next();
                    }
                    if(angle > 0 && !(objectsOnSight.get(0) instanceof Movable)) robot.rotate(-robot.getRotationIncrement());
                    else if(angle <= 0 && !(objectsOnSight.get(0) instanceof Movable)) {
                        robot.rotate(robot.getRotationIncrement());
                    }
                    break;
                }
            }
        }
        controlledObject.accelerate(controlledObject.getAcceleration());
    }

}
