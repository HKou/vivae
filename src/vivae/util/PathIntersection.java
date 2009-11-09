package vivae.util;

import java.awt.geom.Point2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Line2D;

/**
 * Created by IntelliJ IDEA.
 * User: koutnij
 * Date: Nov 9, 2009
 * Time: 1:06:52 AM
 * To change this template use File | Settings | File Templates.
 * class is based on PathIntersection file downloaded from:
 * http://forums.sun.com/thread.jspa?threadID=5222850
 */

// usage:  Point2D.Double[] intxns = getIntersections(path1, path2);
public class PathIntersection {

    public Point2D.Double[] getIntersections(GeneralPath path1, GeneralPath path2) {
        Point2D.Double[] points = new Point2D.Double[0];
        double flatness = 0.01;
        PathIterator pit = path1.getPathIterator(null, flatness);
        double[] coords = new double[6];
        double lastX = 0, lastY = 0;
        while(!pit.isDone()) {
            int type = pit.currentSegment(coords);
            switch(type) {
                case PathIterator.SEG_MOVETO:
                    lastX = coords[0];
                    lastY = coords[1];
                    break;
                case PathIterator.SEG_LINETO:
                    Line2D.Double line = new Line2D.Double(lastX, lastY,
                                                           coords[0], coords[1]);
                    points = findIntersections(path2, flatness, line, points);
                    lastX = coords[0];
                    lastY = coords[1];
            }
            pit.next();
        }
        return points;
    }

    public Point2D.Double[] findIntersections(GeneralPath path, double flatness,
                                 Line2D.Double line, Point2D.Double[] pts) {
        PathIterator pit = path.getPathIterator(null, flatness);
        double[] coords = new double[6];
        double lastX = 0, lastY = 0;
        while(!pit.isDone()) {
            int type = pit.currentSegment(coords);
            switch(type) {
                case PathIterator.SEG_MOVETO:
                    lastX = coords[0];
                    lastY = coords[1];
                    break;
                case PathIterator.SEG_LINETO:
                    Line2D.Double next = new Line2D.Double(lastX, lastY,
                                                           coords[0], coords[1]);
                    if(next.intersectsLine(line)) {
                        Point2D.Double p = getLineIntxn(next, line);
                        pts = addPoint(pts, p);
                    }
                    lastX = coords[0];
                    lastY = coords[1];
            }
            pit.next();
        }
        return pts;
    }

    private Point2D.Double getLineIntxn(Line2D.Double line1, Line2D.Double line2) {
        double x1 = line1.getX1();
        double y1 = line1.getY1();
        double x2 = line1.getX2();
        double y2 = line1.getY2();
        double x3 = line2.getX1();
        double y3 = line2.getY1();
        double x4 = line2.getX2();
        double y4 = line2.getY2();
        double aDividend = (x4 - x3)*(y1 - y3) - (y4 - y3)*(x1 - x3);
        double aDivisor  = (y4 - y3)*(x2 - x1) - (x4 - x3)*(y2 - y1);
        double ua = aDividend / aDivisor;
        double bDividend = (x2 - x1)*(y1 - y3) - (y2 - y1)*(x1 - x3);
        double bDivisor  = (y4 - y3)*(x2 - x1) - (x4 - x3)*(y2 - y1);
        double ub = bDividend / bDivisor;
        Point2D.Double p = new Point2D.Double();
        p.x = x1 + ua * (x2 - x1);
        p.y = y1 + ua * (y2 - y1);
        return p;
    }

    private Point2D.Double[] addPoint(Point2D.Double[] pts, Point2D.Double p) {
        int n = pts.length;
        Point2D.Double[] temp = new Point2D.Double[n+1];
        System.arraycopy(pts, 0, temp, 0, n);
        temp[n] = p;
        return temp;
    }

    public double euclideanDistance(double x1, double y1, double x2, double y2){
        return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
    }
}
