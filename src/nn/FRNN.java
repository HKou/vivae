package nn;

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
    double[][] wIn;
    double[][] wRec;
    double[] wThr;

    public void FRNN(){

    }

    public void init(double[][] wIn, double[][] wRec, double[] wThr){
         state = new double[wThr.length];
         newState = new double[wThr.length];
         for(int i=0;i<state.length;i++){state[i]=0;newState[i]=0;}
         this.wIn = new double[wIn.length][wIn[0].length];
         this.wRec = new double[wRec.length][wRec[0].length];
         this.wThr = new double[wThr.length];
         for(int i=0;i<wIn.length;i++)
           for(int j=0;j<wIn[0].length;j++)
             this.wIn[i][j]=wIn[i][j];
         for(int i=0;i<wRec.length;i++)
           for(int j=0;j<wRec[0].length;j++)
             this.wRec[i][j]=wRec[i][j];
         for(int i=0;i<wThr.length;i++)
             this.wThr[i]=wThr[i];
    }

    public void init(double[][] wIn, double[][] wRec, double[] wThr, double[] state){
        init(wIn,wRec,wThr);
        setState(state);
    }

    public void setState(double[] state){
        for(int i=0;i<state.length;i++)this.state[i]=state[i];
    }

    public double[] evalNetwork(double[] in){
         double exc;
         for(int i=0;i<state.length;i++){
             exc=0;
             for(int j=0;j<in.length;j++)exc+=in[j]*wIn[i][j];
             for(int j=0;j<state.length;j++)exc+=state[j]*wRec[i][j];
             exc+=wThr[i];
             newState[i]=sigmoidBipolar(exc);
         }
         for(int i=0;i<state.length;i++)state[i]=newState[i];
         return state;
    }

    public double sigmoidBipolar(double x){
        return 2/(1+Math.exp(-x))-1;
    }
    public double sigmoidUnipolar(double x){
        return 1/(1+Math.exp(-x));
    }

    public static void main(String[] arg){
        double[][] wIn ={{1,-2},{0,0},{-1,1}};
        double[][] wRec={{1,1,1},{0,0,0},{-1,-1,-1}};
        double[] wThr={0,0,0};
        double[] in={1,1};

        FRNN frnn = new FRNN();
        frnn.init(wIn,wRec,wThr);
        double[] eval;
        for(int k=0;k<5;k++){
            eval=frnn.evalNetwork(in);
            for(int i=0;i<3;i++)System.out.print(eval[i]+" ");
            System.out.println("");
           
        }
    }
}
