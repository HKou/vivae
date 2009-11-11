package vivae.util;

/**
 * Created by IntelliJ IDEA.
 * User: koutnij
 * Date: Nov 9, 2009
 * Time: 4:34:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class Util {
    public static double rescale(double v, double min, double max){
        return (v-min)/(max-min);
    }
    public static double[] rescaleArray1D(double[] v, double min, double max){
        double[] res = new double[v.length];
        for(int i=0;i<v.length;i++)res[i]=rescale(v[i],min,max);
        return res;
    }
    public static double[] flatten(double[][] array){
        double[] res = new double[array.length*array[0].length];
        for(int i=0;i<res.length;i++)res[i]=array[i/array[0].length][i%array[0].length];
        return res;
    }
    public static void main(String[] arg){
        double [][] d = {{1d,2d,3d},{3d,4d,5d}};
        double [] r;
        r=flatten(d);
    }
    public static double[] randomArray1D(int size,double min, double max){
        double [] res = new double[size];
        for(int i=0;i<size;i++)res[i]=Math.random()*(max-min)+min;
        return res;
    }
    public static double[][] randomArray2D(int h, int w, double min, double max){
        double [][] res = new double[h][w];
        for(int i=0;i<h;i++)
            for(int j =0;j<w;j++)res[i][j]=Math.random()*(max-min)+min;
        return res;

    }
    public static double[][][] randomArray3D(int d,int h, int w, double min, double max){
        double [][][] res = new double[d][h][w];
        for(int i=0;i<d;i++)
            for(int j =0;j<h;j++)
                for(int k =0;k<w;k++)
                    res[i][j][k]=Math.random()*(max-min)+min;
        return res;

    }

   public static double euclideanDistance(double x1, double y1, double x2, double y2){
        return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
    }
   public static double[][] subMat(double[][] mat, int from, int to){
       double[][] res = new double[mat.length][to-from+1];
       for(int i=0;i<mat.length;i++){
           for(int j=from;j<=to;j++){
               res[i][j-from]=mat[i][j];
           }
       }
       return res;
   }
}
