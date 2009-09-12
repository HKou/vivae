/**
 * This is VIVAE (Visual Vector Agent Environment)
 * a library allowing for simulations of agents in co-evolution 
 * written as a bachelor project 
 * by Petr Smejkal
 * at Czech Technical University in Prague
 * in 2008
 */

package vivae.controllers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * An extension of VivaeController for controlling Active ArenaParts by Keyboard. 
 * This type of controller needs to be added as a KeyListener to the parent window of the Arena.
 * @author Petr Smejkal
 */
public class KeyboardVivaeController extends VivaeController implements KeyListener{
    protected boolean isLeftKeyDown = false,  
                    isRightKeyDown = false,  
                    isUpKeyDown = false,  
                    isDownKeyDown = false;

    
    
 
    @Override
    public void moveControlledObject() {
        
        if (isLeftKeyDown) {
            controlledObject.rotate(-controlledObject.getRotationIncrement());
        } else if (isRightKeyDown) {
            controlledObject.rotate(controlledObject.getRotationIncrement());
        }
        if (isDownKeyDown) {
            controlledObject.decelerate(2 * controlledObject.getAcceleration());
        } else if (isUpKeyDown) {
            controlledObject.accelerate(controlledObject.getAcceleration());
        }
    }

    public void keyTyped(KeyEvent e) {
       //
    }

       @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                isLeftKeyDown = true;
                isRightKeyDown = false;
                break;
            case KeyEvent.VK_RIGHT:
                isRightKeyDown = true;
                isLeftKeyDown = false;
                break;
            case KeyEvent.VK_DOWN:
                isDownKeyDown = true;
                isUpKeyDown = false;
                break;
            case KeyEvent.VK_UP:
                isUpKeyDown = true;
                isDownKeyDown = false;
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                isLeftKeyDown = false;
                break;
            case KeyEvent.VK_RIGHT:
                isRightKeyDown = false;
                break;
            case KeyEvent.VK_DOWN:
                isDownKeyDown = false;
                break;
            case KeyEvent.VK_UP:
                isUpKeyDown = false;
                break;
        }
    }
}
