/**
 * This is VIVAE (Visual Vector Agent Environment)
 * a library allowing for simulations of agents in co-evolution
 * written as a bachelor project
 * by Petr Smejkal
 * at Czech Technical University in Prague
 * in 2008
 */
package vivae.example;

import java.util.Vector;
import javax.swing.JFrame;
import nn.FRNN;
import vivae.arena.parts.Active;
import vivae.arena.Arena;
import vivae.util.Util;
import vivae.util.FrictionBuffer;
import vivae.fitness.FitnessFunction;
import vivae.fitness.AverageSpeed;
import vivae.fitness.MovablesOnTop;

public class FRNNExperiment {

    Arena arena = null;
    JFrame f = null;
    Vector<Active> agents = null;

    public void createArena(String svgFilename, boolean visible) {
        if (visible) {
            f = new JFrame("FRNN Experiment");
            arena = new Arena(f);
            arena.loadScenario(svgFilename);
            arena.setAllArenaPartsAntialiased(true);
            f.setBounds(50, 0, arena.screenWidth, arena.screenHeight + 30);
            f.setResizable(false);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.getContentPane().add(arena);
            f.setVisible(visible);
            arena.isVisible = visible;
        } else {
            arena = new Arena(f);
            arena.loadScenario(svgFilename);
            //TODO: otestovat, jestli je toto volani nutne..
//            arena.setAllArenaPartsAntialiased(true);
            arena.isVisible = false;
            arena.setLoopSleepTime(0);
        }
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
    public void setupAgent(int number, double[][] wm, double maxDistance, double frictionDistance) {
        Active agent = agents.get(number);
        int neurons = wm.length;
        int snum = (wm[0].length - neurons - 1);
        double sangle = -Math.PI / 2;
        double ai = Math.PI / (snum / 2 - 1);
        FRNNController frnnc = new FRNNController();
        frnnc.initFRNN(Util.subMat(wm, 0, snum - 1),
                Util.subMat(wm, snum, snum + neurons - 1),
                Util.flatten(Util.subMat(wm, snum + neurons, snum + neurons)));
        arena.registerController(agent, frnnc);
        if (agent instanceof FRNNControlledRobot) {
            ((FRNNControlledRobot) agent).setSensors(snum / 2, sangle, ai, maxDistance, frictionDistance);
        }
    }

    public void setupAgent(final int index, final FRNN net,
            final double maxDistance, final double frictionDistance) {
        Active agent = agents.get(index);
        int neurons = net.getNeuronsCount();
        int sensors = net.getSensorsCount();
        double sangle = -Math.PI / 2;
        double ai = Math.PI / (sensors / 2 - 1);
        FRNNController frnnc = new FRNNController();
        frnnc.setFRNN(net);
        arena.registerController(agent, frnnc);
        //TODO: Zjistit o co jde a proc je potreba tak osklivej kod.. instanceof neni moc OO.
        if (agent instanceof FRNNControlledRobot) {
            ((FRNNControlledRobot) agent).setSensors(sensors / 2, sangle, ai, maxDistance, frictionDistance);
        }
    }

    /**
     *
     * @param wm Weight matrices to be setup to the controllers. The weight matrices are evenly distributed among the agents.
     * @param maxDistance
     * @param frictionDistance
     */
    public void setupExperiment(double[][][] wm, double maxDistance, double frictionDistance) {
        int agentnum = agents.size();
        for (int i = 0; i < agentnum; i++) {
            setupAgent(i, wm[i % wm.length], maxDistance, frictionDistance);
        }
    }

    public void setupExperiment(FRNN[] nets, double maxDistance, double frictionDistance) {
        int agentnum = agents.size();
        for (int i = 0; i < agentnum; i++) {
            setupAgent(i, nets[i % nets.length], maxDistance, frictionDistance);
        }
    }

    public void startExperiment() {
        arena.start();
    }

    public Arena getArena() {
        return arena;
    }

    


    public static void main(String[] args) {

        FRNNExperiment exp = new FRNNExperiment();
        exp.createArena("data/scenarios/arena1.svg",true);
        // random weight matrices as 3D array
        // 3 robots,
        int sensors=5; // 5 for distance and 5 for surface
        int neurons=2;
        int robots=3;
        double[][][] wm = Util.randomArray3D(robots,neurons,2*sensors+neurons+1,-5,5);
        exp.setupExperiment(wm,50,25);
        FitnessFunction mot = new MovablesOnTop(exp.arena);//initialize fitness
        FitnessFunction avg = new AverageSpeed(exp.arena);
        exp.startExperiment();
        System.out.println("average speed fitness = "+ avg.getFitness());
        System.out.println("average ontop fitness = "+ mot.getFitness());
    }
}

