/**
 * This is VIVAE (Visual Vector Agent Environment)
 * a library allowing for simulations of agents in co-evolution 
 * written as a bachelor project 
 * by Petr Smejkal
 * at Czech Technical University in Prague
 * in 2008
 */
package vivae.arena.parts;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Polygon;
import org.apache.batik.ext.awt.geom.Polygon2D;
import vivae.arena.Arena;
import vivae.util.ArenaPartsGenerator;

/**
 * @author Petr Smejkal
 */
public class FixedObstacle extends Fixed {

    public FixedObstacle(float x, float y, Point2D.Float[] points) {
        super(x, y);
        int nPoints = points.length;
        Vector2f[] vectors = new Vector2f[nPoints];
        float[] xPts = new float[points.length];
        float[] yPts = new float[points.length];

        for (int i = 0; i < nPoints; i++) {
            xPts[i] = points[i].x;
            yPts[i] = points[i].y;
            vectors[i] = new Vector2f(xPts[i], yPts[i]);
        }

        shape = new Polygon2D(xPts, yPts, nPoints);
        boundingCircleRadius = (float) Math.sqrt((shape.getBounds2D().getCenterX() - shape.getBounds2D().getMinX()) * (shape.getBounds2D().getCenterX() - shape.getBounds2D().getMinX()) + (shape.getBounds2D().getCenterY() - shape.getBounds2D().getMinY()) * (shape.getBounds2D().getCenterY() - shape.getBounds2D().getMinY()));
        body = new Body("FixedObstacle", new Polygon(vectors), 300f);
        body.setMoveable(false);
        body.setRotatable(false);
        body.setPosition(x, y);
        body.setRotation(0);
        setBaseDamping(4.5f);
        body.setDamping(baseDamping);
        body.setRotDamping(ROT_DAMPING_MUTIPLYING_CONST * baseDamping);
    }

    public FixedObstacle(Shape shape, int layer, Arena arena) {
        this((float) shape.getBounds2D().getCenterX(), (float) shape.getBounds2D().getCenterY(), ArenaPartsGenerator.getTraversalPoints(shape));
    }

    @Override
    public AffineTransform getTranslation() {
        AffineTransform trans = AffineTransform.getTranslateInstance(x, y);
        trans.rotate(direction, centerX, centerY);
        return trans;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Object hint = null;
        if (isAntialiased()) {
            hint = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        translation = getTranslation();
        Color oldColor = g2.getColor();
        g2.setColor(new Color(140, 130, 230));
        g2.fill(translation.createTransformedShape(getShape()));
        g2.setColor(Color.BLACK);
        g2.draw(translation.createTransformedShape(getShape()));
        g2.setColor(oldColor);
        if (isAntialiased()) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, hint);
        }
    }
}

