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
    Vector<Active> agents;

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
        agents = arena.getActives();
    }

    //JLink does not allow to call System.exit()


    /**
     *
     * @param number number of the agent/robot
     * @param wm composed weight matrix of size neurons*(inputs+neurons+1)
     * @param maxDistance maximum distance of distance sensors
     * @param frictionDistance distance of friction point sensors
     *
     * Number of sensors as well as number of neurons is determined from the size
     * of the weight matrix. You can use either this function called number of agents time, or
     * use setupExperiment function, which distributs the weight matrices evenly.
     */
    public void setupAgent(int number, double[][] wm,double maxDistance, double frictionDistance){
        Active agent = agents.get(number);
        int neurons=wm.length;
        int snum=(wm[0].length-neurons-1);
        double sangle=-Math.PI/2;
        double ai=Math.PI/(snum/2-1);
        FRNNController frnnc = new FRNNController();
        frnnc.initFRNN(Util.subMat(wm,0,snum-1),
                       Util.subMat(wm,snum,snum+neurons-1),
                       Util.flatten(Util.subMat(wm,snum+neurons,snum+neurons)));
        arena.registerController(agent, frnnc);
        if(agent instanceof FRNNControlledRobot)((FRNNControlledRobot)agent).setSensors(snum/2,sangle,ai,maxDistance,frictionDistance);
    }

    /**
     *
     * @param wm Weight matrices to be setup to the controllers. The weight matrices are evenly distributed among the agents.
     * @param maxDistance
     * @param frictionDistance
     */
    public void setupExperiment(double[][][] wm, double maxDistance, double frictionDistance){
        int agentnum=agents.size();
        for(int i=0;i<agentnum;i++){
            setupAgent(i,wm[i % wm.length],maxDistance,frictionDistance);
        }
    }

    public void startExperiment(){
        arena.start();
    }

    public static void main(String[] args) {

        FRNNExperiment exp = new FRNNExperiment();
//        exp.createArena("data/scenarios/arena2.svg",true);
        exp.createArena("data/scenarios/ushape2.svg",true);
        // random weight matrices as 3D array
        // 3 robots,
        int sensors=5; // 5 for distance and 5 for surface
        int neurons=2;
        double[][][] wm = Util.randomArray3D(3,neurons,2*sensors+neurons+1,-5,5);
        exp.setupExperiment(wm,50,25); 
        FitnessFunction mot = new MovablesOnTop(exp.arena);//initialize fitness
        FitnessFunction avg = new AverageSpeed(exp.arena);
        exp.startExperiment();
        System.out.println("average speed fitness = "+ avg.getFitness());
        System.out.println("average ontop fitness = "+ mot.getFitness());
    }

}

