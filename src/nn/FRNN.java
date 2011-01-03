package nn;

import java.util.Arrays;
import static java.lang.System.*;

/**
 * Created by IntelliJ IDEA.
 * User: koutnij
 * Date: Sep 24, 2009
 * Time: 1:11:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class FRNN {

    double[] state;
    double[] newState;
    private double[][] wIn;
    private double[][] wRec;
    private double[] wThr;

    public FRNN() {
    }
    
    FRNN(double[][] wIn, double[][] wRec, double[] wThr) {
        this.init(wIn, wRec, wThr);
    }

    /**
     * 
     * @param wIn matice vstupnich vah - rozmery dany poctem vstupu a poctem neuronu (i x n)?
     * @param wRec matice vah rekurentnich vazeb (mezi neurony) - pocet dan poctem neuronu (n x n)
     * @param wThr matice prahu neuronu
     */
    public void init(double[][] wIn, double[][] wRec, double[] wThr) {
        state = new double[wThr.length];
        newState = new double[wThr.length];
        this.wThr = Arrays.copyOf(wThr, wThr.length);
        this.wIn = new double[wIn.length][];
        this.wRec = new double[wRec.length][];
        //predpoklad, ze wIn a wRec maji stejny pocet radku (ktery odpovida poctu neuronu)
        for (int i = 0; i < wIn.length; i++) {
            this.wIn[i] = Arrays.copyOf(wIn[i], wIn[i].length);
            this.wRec[i] = Arrays.copyOf(wRec[i], wRec[i].length);
        }
    }

    public void init(double[][] wIn, double[][] wRec, double[] wThr, double[] state) {
        init(wIn, wRec, wThr);
        setState(state);
    }

    public void setState(double[] state) {
        arraycopy(state, 0, this.state, 0, state.length);
    }

    /**
     * Fills state vector with zeros.
     */
    public void resetState() {
        Arrays.fill(state, 0.);
    }

    public double[] evalNetwork(double[] in) {
        for (int i = 0; i < state.length; i++) {
            double exc = 0;
            for (int j = 0; j < in.length; j++) {
                exc += in[j] * wIn[i][j];
            }
            for (int j = 0; j < state.length; j++) {
                exc += state[j] * wRec[i][j];
            }
            exc += wThr[i];
            newState[i] = sigmoidBipolar(exc);
        }
        arraycopy(newState, 0, state, 0, state.length);
        return state;
    }

    public int getNeuronsCount() {
        return wRec == null? 0 : wRec.length;
    }

    public int getSensorsCount() {
        return wIn == null? 0 : wIn[0].length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(FRNN.class.getCanonicalName()).append("@").append(hashCode()).append(":\n");
        sb.append("State = ").append(Arrays.toString(state)).append("\n");
        sb.append("Treshold = ").append(Arrays.toString(wThr)).append("\n");
        sb.append("Input weights:\n");
        for (double[] ds : wIn) {
            sb.append(Arrays.toString(ds)).append("\n");
        }
        sb.append("Recurrent weights:\n");
        for (double[] ds : wRec) {
            sb.append(Arrays.toString(ds)).append("\n");
        }
        sb.append("]");
        return sb.toString();
    }

    public static double sigmoidBipolar(double x) {
        return (2 / (1 + Math.exp(-x))) - 1;
    }

    public static double sigmoidUnipolar(double x) {
        return 1 / (1 + Math.exp(-x));
    }
}
