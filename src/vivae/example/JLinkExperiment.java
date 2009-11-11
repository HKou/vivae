package vivae.example;

import vivae.arena.Arena;
import vivae.util.FrictionBuffer;
import vivae.fitness.FitnessFunction;
import vivae.fitness.AverageSpeed;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: koutnij
 * Date: Nov 11, 2009
 * Time: 5:09:19 PM
 * To change this template use File | Settings | File Templates.
 */
public class JLinkExperiment extends FRNNExperiment{
    FitnessFunction avg;
    public void createArena(String svgFilename, boolean visible){
        f = new JFrame("FRNN Experiment");
        arena = new Arena(f);
        arena.loadScenario(svgFilename);
        arena.setAllArenaPartsAntialiased(true);
        f.setBounds(50, 0, arena.screenWidth, arena.screenHeight+30);
        f.setResizable(false);
        //f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(arena);
        f.setVisible(visible);
        arena.isVisible=visible;
        if(!visible)arena.setLoopSleepTime(0);
        arena.frictionBuffer = new FrictionBuffer(arena);
        agents = arena.getActives();
    }


    public double evaluate(){
        avg = new AverageSpeed(arena);
        startExperiment();
        return avg.getFitness();
    }
}
