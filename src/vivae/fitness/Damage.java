package vivae.fitness;

import java.util.Vector;

import vivae.arena.Arena;
import vivae.arena.parts.Active;

/**
 * Created by IntelliJ IDEA.
 * User: koutnij
 * Date: Nov 9, 2009
 * Time: 9:18:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class Damage extends FitnessFunction {
    private Arena arena;
    
    public Damage(Arena arena) {
        this.arena = arena;
    }

    public double getFitness() {
        double res = 0d;
        final Vector<Active> actives = arena.getActives();
        
        for (Active active : actives) {
			res += active.overallDeceleration;
		}
        
        return Math.exp(0.01*-res/actives.size());
     }
}
