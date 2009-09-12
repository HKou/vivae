/**
 * This is VIVAE (Visual Vector Agent Environment)
 * a library allowing for simulations of agents in co-evolution 
 * written as a bachelor project 
 * by Petr Smejkal
 * at Czech Technical University in Prague
 * in 2008
 */



package vivae.controllers;

import java.util.Vector;
import vivae.arena.parts.Active;
import vivae.arena.parts.Robot;
import vivae.arena.parts.Sensor;
import vivae.arena.parts.VivaeObject;

/**
 * One of the extensions of the basic VivaeController class specifiing Active object's behavior. 
 * This type of Controller is used for Robots equipped with sensors.
 * 
 * @author Petr Smejkal
 */
public abstract class RobotWithSensorController extends VivaeController{
    
    /**
     * A boolean variable specifiing if the Robot has been set up.
     */
    protected boolean isRobotSet = false;
    /**
     * The Active object controlled by this controller. This time it's Robot, one of Active's ascendants, because the Active object here has to have Sensors.
     */
    protected Robot robot;
    /**
     * A Vector of Snesors of the controlled robot.
     */
    protected Vector<Sensor> sensors;
    /**
     * This Vector of VivaeObjects can be used to store all Objects in the Arena.
     * The sensors will then search them for those of them that are on sight. 
     */
    protected Vector<VivaeObject> allObjects = new Vector<VivaeObject>();
    /**
     * This Vector of VivaeObjects can be used to store those ArenaObjects in the Arena, that are on sight of the sensors.
     */
    protected Vector<VivaeObject> objectsOnSight = new Vector<VivaeObject>();

    @Override
    public Active getControlledObject() {
        return robot;
    }

    @Override
    public void setControlledObject(Active controlledObject) {
        this.controlledObject = controlledObject;
        if(controlledObject instanceof Robot) {
            this.robot = (Robot) controlledObject;
            this.isRobotSet = true;
            this.sensors = this.robot.getSensors();
        }
        else {
            System.err.println("Error, cannot assign non-Robot type as a controlled object for RobotWithSensorController");
            
        }
    }
    
    
    @Override
    public abstract void moveControlledObject();

}
