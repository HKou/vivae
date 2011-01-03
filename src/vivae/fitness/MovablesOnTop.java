package vivae.fitness;

import vivae.arena.Arena;
import vivae.arena.parts.Passive;
import vivae.arena.parts.Movable;
import java.util.Iterator;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: koutnij
 * Date: Nov 9, 2009
 * Time: 9:18:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class MovablesOnTop extends FitnessFunction {

    Arena arena;
    double before;
    int num;

    public MovablesOnTop(Arena arena) {
        this.arena = arena;
        before = getDistances();
    }

    @Override
    public double getFitness() {
        return (before - getDistances()) / (arena.screenHeight * num);
    }

    public double getDistances() {
        double res = 0d;
        Vector<Passive> passives = arena.getPassives();
        num = 0;
        for (Iterator<Passive> it = passives.iterator(); it.hasNext();) {
            Passive ag = it.next();
            try {
                Movable mvbl = (Movable) ag;
                res += mvbl.getY();
                num++;
            } catch (ClassCastException cce) {
            }
        }
        return res;
    }
}
