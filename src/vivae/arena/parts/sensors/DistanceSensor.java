package vivae.arena.parts.sensors;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.*;
import java.util.Vector;
import net.phys2d.raw.Body;
import net.phys2d.raw.shapes.Box;
import vivae.arena.parts.Active;
import vivae.arena.parts.VivaeObject;
import vivae.arena.parts.Fixed;
import vivae.arena.parts.Puck;
import vivae.util.PathIntersection;

/**
 * @author HKou
 */
public class DistanceSensor extends Sensor{

    protected Active owner;
    protected Body ownerBody;
    protected float ray_length = 100f;
    protected float ray_width = 1f;
    protected float angle = 0f;
    protected AlphaComposite opacityOfRay = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.20f);
    protected boolean isRayTransparent = true;
    protected int sensorNumber = 0;
    protected int sensorX, sensorY;

    /**
     * This method removes all VivaeObjects that are further from owner of this Sensor
     * than length of the Sensor is.
     * @param objects Vector of all VivaeObjects that are checked for distance from owner of this Sensor.
     * @param walls Vector of walls that can contain enclosing walls in Arena.
     * @return new Vector of VivaeObjects that are close enough to be in range of Sensor.
     */
    public Vector<VivaeObject> getCloseVivaes(Vector<VivaeObject> objects, Vector<Fixed> walls) {

        Vector<VivaeObject> closeObjects = new Vector<VivaeObject>();
        for (VivaeObject vivae: objects) {
            if(vivae.getBoundingCircleRadius()+ray_length > vivae.getBody().getPosition().distance(ownerBody.getPosition())) {
                closeObjects.add(vivae);

            }
        }
        if (!owner.getArena().isEnclosedWithWalls()) return closeObjects;
        float xPos = owner.getBody().getPosition().getX();
        float yPos = owner.getBody().getPosition().getY();
        if (ray_length > yPos) closeObjects.add(walls.get(0));
        else if (owner.getArena().screenHeight - ray_length < yPos) closeObjects.add(walls.get(1));
        if (ray_length > xPos) closeObjects.add(walls.get(3));
        else if (owner.getArena().screenWidth - ray_length < xPos) closeObjects.add(walls.get(2));
        return closeObjects;
    }

    /**
     * Method intersects area of Sensor and all VivaeObjects and returns those that
     * have non-zero intersection.
     * @param objects Vector of VivaeObjects that are checked for collision with the body of Sensor.
     * @return Vector of VivaeObjects that are in collision with the body of Sensor.
     */
    public Vector<VivaeObject> getVivaesOnSight(Vector<VivaeObject> objects){
        Vector<VivaeObject> objectsOnSight = new Vector<VivaeObject>();
        //for (VivaeObject vivaeObject : objects) {
        GeneralPath thisPath = new GeneralPath(this.getTransformedShape());
        PathIntersection pi = new PathIntersection();
        Point2D.Double[] points;
        for (VivaeObject vivaeObject : getCloseVivaes(objects, owner.getArena().getWalls())) {
            if (vivaeObject != this.owner) {
                GeneralPath gp = new GeneralPath(vivaeObject.getTransformedShape());
                points = pi.getIntersections(thisPath,gp);
                System.out.println(points.length);
                /*
                for(Point2D.Double point : points){
                    System.out.println("x = "+point.getX()+" y = "+point.getY());
                    System.out.println("owner x = "+owner.getX()+" owner y = "+owner.getY());
                    System.out.println(pi.euclideanDistance(point.getX(),point.getY(),owner.getX(),owner.getY()));
                }*/
                if(points.length>0)objectsOnSight.add(vivaeObject);
                /*Area actArea = (Area) vivaeObject.getArea().clone();
                actArea.intersect(this.getArea());
                if (!actArea.isEmpty()) objectsOnSight.add(vivaeObject);             */
                //             if(vivaeObject instanceof roboneat.RoboNeatRobot)System.out.println("robot seen by"+this.owner);
            }
        }
        return objectsOnSight;
    }
    public double getDistance(Vector<VivaeObject> objects){
        GeneralPath thisPath = new GeneralPath(this.getTransformedShape());
        PathIntersection pi = new PathIntersection();
        Point2D.Double[] points;
        double dist = ray_length;
        double nd;
        for (VivaeObject vivaeObject : getCloseVivaes(objects, owner.getArena().getWalls())) {
            if (vivaeObject != this.owner) {
                GeneralPath gp = new GeneralPath(vivaeObject.getTransformedShape());
                points = pi.getIntersections(thisPath,gp);
                for(Point2D.Double point : points){
                    nd=pi.euclideanDistance(point.getX(),point.getY(),owner.getX(),owner.getY());
                    if(nd<dist)dist=nd;
                    //System.out.println(dist);
                }
            }
        }
        return 1d-dist/ray_length;
    }



    public DistanceSensor(Active owner, double angle, int number, double maxDistance){
        this(owner, number,maxDistance);
        setAngle((float)angle);
        ray_length=(float)maxDistance;

    }

    /*
    public LineSensor(Active owner){
        super(owner);
        this.ownerBody=owner.getBody();
        this.owner=owner;
        //this.sensorNumber  = number();
    }
     */

    public DistanceSensor(Active owner, int number, double maxDistance) {
        super(owner);
        this.owner = owner;
        this.ownerBody = owner.getBody();
        this.sensorNumber = number;
        ray_length=(float)maxDistance;
        body = new Body("Sensor", new Box(ray_length, ray_width), 50f);
        body.addExcludedBody(owner.getBody());
        body.setDamping(baseDamping);
        body.setRotDamping(ROT_DAMPING_MUTIPLYING_CONST * baseDamping);
        setShape(new Line2D.Double(0,0,ray_length, 0));
    }


    @Override
    public void moveComponent(){
        inMotion = true;
        direction = owner.getDirection() - (float)Math.PI/2;
        direction += angle;
        net.phys2d.math.ROVector2f op = ownerBody.getPosition();
        x = op.getX();
        y = op.getY();
        float newX = (float)(x + (ray_length/2)*Math.cos(direction));
        float newY = (float)(y + (ray_length/2)*Math.sin(direction));
        body.setPosition(newX,newY);
        body.setRotation(direction);
    }

    public AffineTransform getTranslation(){
        AffineTransform translation = AffineTransform.getTranslateInstance(x, y-ray_width/2);
        translation.rotate(direction, 0, ray_width/2);
        return translation;
    }



    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        Object hint = new Object();
        if(isAntialiased()){
            hint = g2.getRenderingHint(RenderingHints.KEY_ANTIALIASING);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        }
        translation = getTranslation();
        Color oldColor = g2.getColor();
        Composite oldComposite = g2.getComposite();
        if(isRayTransparent) g2.setComposite(opacityOfRay);
        g2.setColor(Color.RED);
        g2.draw(translation.createTransformedShape(getShape()));
        g2.fill(translation.createTransformedShape(getShape()));
        g2.setComposite(opacityFront);
        g2.setComposite(oldComposite);
        g2.setColor(oldColor);
        if(isAntialiased()) g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,hint);
    }


    @Override
    public void accelerate(float speed) {
        // TODO Auto-generated method stub
    }

    @Override
    public void decelerate(float speed) {
        // TODO Auto-generated method stub
    }

    @Override
    public float getAcceleration() {
        return 0;
    }

    @Override
    public String getActiveName() {
        // TODO Auto-generated method stub
        return "Sensor";
    }

    @Override
    public float getMaxSpeed() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getNumber() {
        // TODO Auto-generated method stub
        return 0;

    }

    @Override
    public float getRotationIncrement() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void rotate(float radius) {
        // TODO Auto-generated method stub

    }

    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public boolean isRayTransparent() {
        return isRayTransparent;
    }

    public void setRayTransparent(boolean isRayTransparent) {
        this.isRayTransparent = isRayTransparent;
    }

    @Override
    public String toString(){
        return "Sensor " + sensorNumber + " on " + owner.toString();
    }

    @Override
    public void reportObjectOnSight(Sensor s, Body b) {
            // TODO Auto-generated method stub
    }

}

