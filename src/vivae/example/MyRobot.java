/**
 * This is VIVAE (Visual Vector Agent Environment)
 * a library allowing for simulations of agents in co-evolution
 * written as a bachelor project
 * by Petr Smejkal
 * at Czech Technical University in Prague
 * in 2008
 */
package vivae.example;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import net.phys2d.math.Vector2f;
import net.phys2d.raw.Body;
import net.phys2d.raw.World;
import net.phys2d.raw.shapes.Box;
import vivae.arena.Arena;
import vivae.arena.parts.sensors.*;
import vivae.arena.parts.Robot;

/**
 * @author Petr Smejkal
 */
public class MyRobot extends Robot {

    public static int robotsCounter = 0;
    public static final float ACCELERATION = 15f;
    public static final float ROTATION = 80f;
    protected static final float MAX_SPEED = 50f;
    protected int diameter;
    protected Vector<Sensor> sensors; // = new Vector<Sensor>();
    protected boolean isShowingSensors = true;
    protected int sensorNumber = 0;
    protected Map<Integer, Sensor> sensorsMap; // = new HashMap<Integer, Sensor>();
    protected World world;

    public MyRobot(float x, float y) {
        super(x, y);
        sensors = new Vector<Sensor>();
        sensorsMap = new HashMap<Integer, Sensor>();
    }

    public MyRobot(Shape shape, int layer, Arena arena) {
        this((float) shape.getBounds2D().getCenterX(), (float) shape.getBounds2D().getCenterY(), arena);
    }

    public MyRobot(float x, float y, Arena arena) {
        this(x, y);
        diameter = 12;
        boundingCircleRadius = (float) Math.sqrt(2 * diameter * diameter) / 2;
        myNumber = getNumber();
        this.arena = arena;
        this.world = arena.getWorld();
        body = new Body("Robot", new Box(diameter, diameter), 50f);
        body.setPosition((float) x, (float) y);
        body.setRotation(0);
        body.setDamping(new Float(baseDamping));
        body.setRotDamping(new Float(ROT_DAMPING_MUTIPLYING_CONST * baseDamping));
        setShape(new Rectangle2D.Double(0, 0, diameter, diameter));
        Rectangle r = getShape().getBounds();
        centerX = new Float(r.getCenterX());
        centerY = new Float(r.getCenterY());
        setSensors(3, -Math.PI / 6, Math.PI / 6);
    }

    public void setSensors(int howMany, double startingAngle, double angleIncrement) {
        for (int i = 0; i < howMany; i++) {
            addSensor(startingAngle + i * angleIncrement);
        }
        DistanceSensor s = new DistanceSensor(this, 0f, sensorNumber, 75);
        sensors.add(s);
        sensorsMap.put(sensorNumber, s);
        sensorNumber++;
        SurfaceFrictionSensor sf = new SurfaceFrictionSensor(this, 0f, sensorNumber, 75);
        sensors.add(sf);
        sensorsMap.put(sensorNumber, sf);
        sensorNumber++;
    }

    @Override
    public void moveComponent() {
        inMotion = true;
        direction = body.getRotation();
        net.phys2d.math.ROVector2f p = body.getPosition();
        x = p.getX();
        y = p.getY();
        for (Sensor s : sensors) {
            s.moveComponent();
        }

    }

    @Override
    public AffineTransform getTranslation() {
        AffineTransform af = AffineTransform.getTranslateInstance(x - diameter / 2, y - diameter / 2);
        af.rotate(direction, centerX, centerY);
        return af;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Object hint = new Object();
        if (isAntialiased()) {
            hint = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        translation = getTranslation();
        Color oldColor = g2.getColor();
        g2.setColor(new Color(230, 230, 250));
        g2.fill(translation.createTransformedShape(getShape()));
        g2.setColor(Color.BLACK);
        g2.draw(translation.createTransformedShape(getShape()));
        if (isShowingSensors) {
            for (Iterator<Sensor> sIter = sensors.iterator(); sIter.hasNext();) {
                Sensor s = (Sensor) sIter.next();
                s.paintComponent(g2);
            }
        }
        if (isShowingStatusFrame) {
            paintStatusFrame(g2);
        }
        g2.setColor(oldColor);
        if (isAntialiased()) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, hint);
        }
    }

    @Override
    public void accelerate(float s) {

        setSpeed(body.getVelocity().length());
        s = Math.min(s, getMaxSpeed() - (float) getSpeed());
        float dx = (float) (s * (float) Math.cos(body.getRotation() - Math.PI / 2));
        float dy = (float) (s * (float) Math.sin(body.getRotation() - Math.PI / 2));
        body.adjustVelocity(new Vector2f(dx, dy));

    }

    public void decelerate(float s) {
        setSpeed(body.getVelocity().length());
        s = Math.max(s, 0);
        float dx = (float) (s * (float) Math.cos(body.getRotation() - Math.PI / 2));
        float dy = (float) (s * (float) Math.sin(body.getRotation() - Math.PI / 2));
        body.adjustVelocity(new Vector2f(-dx, -dy));
    }

    @Override
    public void rotate(float radius) {
        body.adjustAngularVelocity(radius);
        this.direction = body.getRotation();
    }

    @Override
    public int getNumber() {
        return robotsCounter++;
    }

    @Override
    public String getActiveName() {
        return "Robot";
    }

    @Override
    public float getAcceleration() {
        return Robot.ACCELERATION;
    }

    @Override
    public float getMaxSpeed() {
        return Robot.MAX_SPEED;
    }

    @Override
    public float getRotationIncrement() {
        return Robot.ROTATION;
    }

    @Override
    public String toString() {
        return "Robot " + myNumber;
    }

    public Vector<Sensor> getSensors() {
        return sensors;
    }

    public void addSensor(Double angle) {
        Sensor s = new LineSensor(this, angle, sensorNumber);
        sensors.add(s);
        sensorsMap.put(sensorNumber, s);
        sensorNumber++;
    }

    @Override
    public void reportObjectOnSight(Sensor s, Body b) {
        System.out.println("Object seen from sensor " + s);
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setShowingSensors(boolean showingSensors) {
        isShowingSensors = showingSensors;
    }
}

