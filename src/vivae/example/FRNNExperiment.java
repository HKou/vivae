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
import vivae.util.FrictionBuffer;
import vivae.fitness.FitnessFunction;
import vivae.fitness.AverageSpeed;
import vivae.fitness.MovablesOnTop;

public class FRNNExperiment{
    Arena arena;
    JFrame f;

    public void createArena(String svgFilename, boolean visible){
        f = new JFrame("FRNN Experiment");
        arena = new Arena(f);
        arena.loadScenario(svgFilename);
        arena.setAllArenaPartsAntialiased(true);
        f.setBounds(50, 0, arena.screenWidth, arena.screenHeight+30);
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(arena);
        f.setVisible(visible);
        arena.isVisible=visible;
        if(!visible)arena.setLoopSleepTime(0);
        arena.frictionBuffer = new FrictionBuffer(arena);
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

    public void startExperiment(){
        arena.start();
        //System.out.println("end");

    }

    public static void main(String[] args) {
        
        FRNNExperiment exp = new FRNNExperiment();
        exp.createArena("data/scenarios/arena1.svg",true);
        exp.setupExperiment(5,50,25); // (5+5) sensors, distance sensor up to 50, surface at 25.
        FitnessFunction mot = new MovablesOnTop(exp.arena);//initialize fitness
        FitnessFunction avg = new AverageSpeed(exp.arena);
        exp.startExperiment();
        System.out.println("average speed fitness = "+ avg.getFitness());
        System.out.println("average ontop fitness = "+ mot.getFitness());
    }

}

