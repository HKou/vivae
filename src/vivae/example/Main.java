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
import vivae.arena.parts.Active;
import vivae.arena.Arena;

public class Main {

    public static void main(String[] args) {
        JFrame f = new JFrame("Arena");
        Arena arena = new Arena(f);
        arena.loadScenario("data/scenarios/ushape2.svg");
        Vector<Active> agents = arena.getActives();

        for (Active agent : agents) {
            arena.registerController(agent, new MyController());
        }

        arena.setAllArenaPartsAntialiased(true);
        f.setBounds(50, 0, arena.screenWidth, arena.screenHeight + 30);
        f.setResizable(false);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(arena);
        f.setVisible(true);
        arena.start();

    }
}

