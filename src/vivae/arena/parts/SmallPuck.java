/**
 * This is VIVAE (Visual Vector Agent Environment)
 * a library allowing for simulations of agents in co-evolution
 * written as a bachelor project
 * by Petr Smejkal
 * at Czech Technical University in Prague
 * in 2008
 */


package vivae.arena.parts;


import java.awt.Shape;
import vivae.arena.Arena;


/**
 * @author Petr Smejkal
 */
public class SmallPuck extends Puck {

    public static final int DIAMETER = 15; 

    public SmallPuck(Shape shape, int layer, Arena arena) {
        this((float) shape.getBounds2D().getMinX(), (float) shape.getBounds2D().getMinY());
    }

    public SmallPuck(float x, float y) {
        super(x, y);
        // TODO Auto-generated constructor stub
    }


    @Override
    protected int getDiameter() {
        return DIAMETER;
    }

    @Override
    protected float getMass() {
        // TODO Auto-generated method stub
        return 0;
    }
}

