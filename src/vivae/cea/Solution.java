package vivae.cea;

import nn.FRNN;

/**
 * Class representing the solution of the problem. It holds all the necessary information.
 *
 * @author Bc. Ramunas Belkauskas (ramunas.belkauskas@gmail.com)
 */
public class Solution {
    public FRNN network;
    Solution(FRNN network) {
        this.network = network;
    }
}
