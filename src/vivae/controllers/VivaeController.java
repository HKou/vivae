/**
 * This is VIVAE (Visual Vector Agent Environment)
 * a library allowing for simulations of agents in co-evolution 
 * written as a bachelor project 
 * by Petr Smejkal
 * at Czech Technical University in Prague
 * in 2008
 */

package vivae.controllers;

import vivae.arena.parts.Active;

/**
 * An abstract class for all controllers that are used to control the movement and behavior of active agents. 
 * You can create your own controllers by extending this class. Specify the logic of your controller in the moveControlledObject.
 * After creating a controller, register it to an Active ArenaPart and it will follow it's behavioral pattern.
 * @author Petr Smejkal
 */
public abstract class VivaeController {

    /**
     * The Active ArenaPart that is to be controlled by this controller.
     */
    protected Active controlledObject;

   /**
    * Specify your logic here. 
    * This procedure is called in every iteration of the Arena loop to determine Active objects' movement.
    */
    abstract public void moveControlledObject();

    public Active getControlledObject() {
        return controlledObject;
    }

    public void setControlledObject(Active controlledObject) {
        this.controlledObject = controlledObject;
    }
}
