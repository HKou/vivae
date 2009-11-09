/**
 * This is VIVAE (Visual Vector Agent Environment)
 * a library allowing for simulations of agents in co-evolution
 * written as a bachelor project
 * by Petr Smejkal
 * at Czech Technical University in Prague
 * in 2008
 */

package vivae.example;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import javax.swing.JFrame;
import vivae.arena.parts.Active;
import vivae.arena.Arena;
import vivae.util.Util;

public class FRNNExperiment{
    Arena arena;
    JFrame f;

    public void createArena(String svgFilename){
        f = new JFrame("FRNN Experiment");
        arena = new Arena(f);
        arena.loadScenario(svgFilename);
    }

    public void setupExperiment(int snum, double maxDistance, double frictionDistance){
        Vector<Active> agents = arena.getActives();
        double sangle=-Math.PI/2;
        double ai=Math.PI/(snum-1);
        FRNNController frnnc;
        for (Iterator<Active> it = agents.iterator(); it.hasNext();) {
            Active agent = it.next();
            frnnc = new FRNNController();
            // put a different random FRNN in each controller
            frnnc.initFRNN(Util.randomArray2D(2,2*snum,-5,5),Util.randomArray2D(2,2,-5,5),Util.randomArray1D(2,-5,5));
            arena.registerController(agent, frnnc);
            if(agent instanceof FRNNControlledRobot)((FRNNControlledRobot)agent).setSensors(snum,sangle,ai,maxDistance,frictionDistance);
        }
    }

    public void startExperiment(boolean visible){
        arena.setAllArenaPartsAntialiased(true);
        f.setBounds(50, 0, arena.screenWidth, arena.screenHeight+30);
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(arena);
        f.setVisible(visible);
        arena.isVisible=visible;
        arena.start();
        System.out.println("end");

    }

    public static void main(String[] args) {
        
        FRNNExperiment exp = new FRNNExperiment();
        exp.createArena("data/scenarios/arena1.svg");
        exp.setupExperiment(5,50,25); // (5+5) sensors, distance sensor up to 50, surface at 25.
        exp.startExperiment(false);
    }

}

