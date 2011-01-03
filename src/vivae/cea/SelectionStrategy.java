package vivae.cea;

import java.util.ArrayList;

/**
 *
 * @author Bc. Ramunas Belkauskas (ramunas.belkauskas@gmail.com)
 */
//TODO: nejdriv refaktorovat a uhladit rozhrani, pak se uvidi, ktere tridy budou public..
interface SelectionStrategy {
    public Individual select(ArrayList<Individual> population);
}
