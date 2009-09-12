/** * This is VIVAE (Visual Vector Agent Environment) * a library allowing for simulations of agents in co-evolution  * written as a bachelor project  * by Petr Smejkal * at Czech Technical University in Prague * in 2008 */package vivae.arena.parts;import java.awt.Graphics;import java.awt.geom.AffineTransform;import java.awt.geom.Area;import java.util.logging.Logger;import net.phys2d.raw.Body;/** * @author Petr Smejkal */public abstract class VivaeObject extends ArenaPart{	protected Body body;	protected AffineTransform translation;	protected float baseDamping = 1f;	protected float direction;	protected double speed;	protected float centerX, centerY;	public static final int ROT_DAMPING_MUTIPLYING_CONST = 1000;	protected boolean isBlinking = false;	protected boolean isBlinkedNow = false;        protected float boundingCircleRadius = 0f;        public boolean inMotion = true;        protected Area area = null;   // protected boolean positionValid = false;   // protected Area ar;    public abstract  AffineTransform getTranslation();	VivaeObject(float x, float y) {            super(x, y);            this.speed = 0;            this.direction = new Float(Math.PI);	}        @Override	public Area getArea(){            //if(true) {            if(inMotion) {                if(getShape() != null) {                    area = new Area(getTranslation().createTransformedShape(getShape()));                    inMotion = false;                    //Logger.getLogger("vivae").info("area is set.");                }                else {                    //Logger.getLogger("vivae").info("Error: Shape of "+this.getClass()+" is null.");                    return null;                }            }            return area;                }	public void moveComponent(){                       direction = body.getRotation();            x = body.getPosition().getX();            y = body.getPosition().getY();            speed = body.getVelocity().length();//            if (speed != 0) {//                inMotion = true;//                Logger.getLogger("vivae").info(this.getClass()+" se pohybuje.");            inMotion = true;//            }                                            }	public void paintComponent(Graphics g, boolean isBlinkingEnable){            if((isBlinkingEnable && (isBlinking && isBlinkedNow) || !isBlinking) || !isBlinkingEnable){                    paintComponent(g);            }            isBlinkedNow = !isBlinkedNow;	}		public void setDamping(float damping) {            body.setDamping(new Float(baseDamping + damping));	}        public float getDamping(){            return body.getDamping();        }        	public Body getBody() {            return body;	}	public void setBody(Body body) {            this.body = body;	}	public void setTranslation(AffineTransform translation) {            this.translation = translation;	}	public float getBaseDamping() {            return baseDamping;	}	public void setBaseDamping(float baseDamping) {            this.baseDamping = baseDamping;	}	public float getDirection() {            return direction;	}	public void setDirection(float direction) {            this.direction = direction;	}        	public double getSpeed() {            return speed;	}	public void setSpeed(double speed) {            this.speed = speed;	}	public boolean isBlinking() {            return isBlinking;	}	public void setBlinking(boolean isBlinking) {            this.isBlinking = isBlinking;	}    public float getCenterX() {        return centerX;    }    public float getCenterY() {        return centerY;    }    public void setCenterX(float centerX) {        this.centerX = centerX;    }    public void setCenterY(float centerY) {        this.centerY = centerY;    }        public float getBoundingCircleRadius() {        return boundingCircleRadius;    }}