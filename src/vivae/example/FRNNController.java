package vivae.example;

import vivae.controllers.RobotWithSensorController;
import vivae.util.Util;


import nn.FRNN;

/**
 * Created by IntelliJ IDEA.
 * User: koutnij
 * Date: Nov 9, 2009
 * Time: 2:57:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class FRNNController extends RobotWithSensorController {

    protected FRNN frnn = new FRNN();

    public void initFRNN(double[][] wIn, double[][] wRec, double[] wThr) {
        frnn.init(wIn, wRec, wThr);
    }

    @Override
    public void moveControlledObject() {

        if (robot instanceof FRNNControlledRobot) {
            double[] input = Util.flatten(((FRNNControlledRobot) robot).getSensoryData());

            double[] eval = frnn.evalNetwork(input);

            double lWheel = eval[0];
            double rWheel = eval[eval.length - 1];
            double angle;
            double acceleration = 5.0 * (lWheel + rWheel);
            if (acceleration < 0) {
                acceleration = 0; // negative speed causes problems, why?
            }
            double speed = Math.abs(robot.getSpeed() / robot.getMaxSpeed());
            speed = Math.min(Math.max(speed, -1), 1);
            if (rWheel > lWheel) {
                angle = 10 * (1.0 - speed);
            } else {
                angle = -10 * (1.0 - speed);
            }
            robot.rotate((float) angle);
            robot.accelerate((float) acceleration);
        }

    }

    void setFRNN(FRNN net) {
        this.frnn = net;
    }
}
