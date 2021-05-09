package coursework._190055002;

import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.Sphere;
import org.newdawn.slick.opengl.Texture;

import java.lang.Math;

import GraphicsLab.Colour;
import GraphicsLab.FloatBuffer;
import GraphicsLab.Normal;
import GraphicsLab.Vector;
import GraphicsLab.Vertex;

public class Arrow {
	public final float INITIAL_ARROW_X_OFFSET = -1.25f;
	private final float ARROW_LENGTH = 2.0f;
	
	private float arrowRadius; 
	private Vector arrowPosition;
	private Vector prevArrowPosition;
	private float xyAngle;
	private float xzAngle;
	
	private float vX;  // before fired, used as position directly
	private float uX;

	private float vY;
	private float uY;
	
	private float vZ;
	private float uZ;
	
	private ArrowState arrowState;

	private long firedTime; // time fired in milliseconds

	enum ArrowState {
		HELD,
		FIRED, 
		STATIONARY
	}
	
	public Arrow() {
		arrowRadius = 0.05f;
		arrowPosition = new Vector(0.0f, 0.0f, 0.0f);
		prevArrowPosition = new Vector(0.0f, 0.0f, 0.0f);
		xyAngle = 0.0f;
		xzAngle = 0.0f;
		vX = 0.0f;
		vY = 0.0f;
		vZ = 0.0f;
		arrowState = ArrowState.HELD;
	}
	
	public void update(Bow bow, float animationScale) {
		if(arrowState == ArrowState.HELD) {
			xyAngle = bow.getBowYAngle();
			xzAngle = bow.getBowZAngle();
			arrowPosition = new Vector(-bow.getStringPeak() + (INITIAL_ARROW_X_OFFSET / animationScale), vY, vZ);
		} else if (arrowState == ArrowState.FIRED) {
			float time = (System.currentTimeMillis() - firedTime) / 500f;
			
			// displacement for x and y. x is constant, whilst y is not
			// s = ut + (1/2)(a)(t^2)
			vX = uX * time;
			vY = uY * time + (0.5f * (CS2150Coursework.GRAVITY * time * time));
			vZ = uZ * time;
					
			// save old pos, and record new ones
			prevArrowPosition = arrowPosition;
			arrowPosition = new Vector(vX, vY, vZ);
			
			if(!collisionDetect()) {
				// calculate arrow based on angle between previous position and current position, using inverse of tan
				xzAngle = getxyFlightAngle(xzAngle);
			}
			
		}
	}

	public void draw(float animationScale, Texture woodTexture) {
		System.out.println(vX + "	" + vY + "	" + vZ);
		switch(arrowState) {
			case HELD:
				GL11.glRotatef(xyAngle * animationScale, 0.0f, 1.0f, 0.0f);
				GL11.glRotatef(xzAngle * animationScale, 0.0f, 0.0f, 1.0f);
				GL11.glTranslatef(
						arrowPosition.getX() * animationScale,
						arrowPosition.getY() * animationScale,
						arrowPosition.getZ() * animationScale);
				break;
			case FIRED:
			case STATIONARY:
				GL11.glTranslatef(
						arrowPosition.getX() * animationScale, 
						arrowPosition.getY() * animationScale, 
						arrowPosition.getZ() * animationScale);
				GL11.glRotatef(xyAngle * animationScale, 0.0f, 1.0f, 0.0f);  // NB: when fired, xy should be the angle to adjust, however it only works if I adjust xz instead? 
				GL11.glRotatef(xzAngle * animationScale, 0.0f, 0.0f, 1.0f);
				
				break;
		default:
			break;
		}
		drawArrow(woodTexture);
	}
	
	private void drawCircle(float radius) {
	    GL11.glBegin(GL11.GL_POLYGON);

	    for (float i = 0f; i <= 360f; i += 0.1f) {
	        GL11.glVertex2f((float) (java.lang.Math.sin(i) * arrowRadius), 
	        		        (float) (java.lang.Math.cos(i) * arrowRadius));
	    }
	    
	    GL11.glEnd();
	}
	
	private void drawArrow(Texture woodTexture) {
		// arrow length
		GL11.glPushMatrix();
		{
        	float shininess  = 100.0f;            
        	float specular[] = {0.2f, 0.2f, 0.2f, 1.0f};  // reflection
            float diffuse[]  = {0.5f, 0.5f, 0.5f, 1.0f};

            GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, shininess);
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(specular));
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(diffuse));

			Colour.WHITE.submit();
			
			GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
			drawArrowLength();
		}
		GL11.glPopMatrix();
		
		// arrow head
		GL11.glPushMatrix();
		{
			float shininess = 128.0f;  // shine of arrow tip - really shiny, as metallic
			float specular[] = { 0.9f, 0.9f, 0.9f, 1.0f };  // reflection of arrow tip
			float diffuse[] = { 0.5f, 0.5f, 0.5f, 1.0f };  // same as ambient
        	float[] matEmission = {0.3f, 0.2f, 0.2f, 1.0f};
        	float noMat[] = {0.0f, 0.0f, 0.0f, 1.0f};

			GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, shininess);
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(specular));
			GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(diffuse));
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_EMISSION, FloatBuffer.wrap(matEmission));
			
			GL11.glScalef(0.325f, 0.325f, 0.325f);
			GL11.glTranslatef(6.625f, 0.0f, 0.0f);
			drawArrowHead();
			
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_EMISSION, FloatBuffer.wrap(noMat));
		}
		GL11.glPopMatrix();
	}
	
	private void drawArrowLength() {
		new Cylinder().draw(arrowRadius, arrowRadius, ARROW_LENGTH, 20, 10);
		drawCircle(arrowRadius);  // for bottom
	}
	
	private void drawArrowHead() {
		Vertex v1 = new Vertex(0.5f, 0.0f, 0.0f);
		Vertex v2 = new Vertex(-0.5f, 0.5f, -0.5f);
		Vertex v3 = new Vertex(-0.5f, 0.5f, 0.5f);
		Vertex v4 = new Vertex(-0.5f, -0.5f, 0.5f);
		Vertex v5 = new Vertex(-0.5f, -0.5f, -0.5f);

		// left face
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v3.toVector(), v2.toVector(), v5.toVector(), v4.toVector()).submit();

			v3.submit();
			v2.submit();
			v5.submit();
			v4.submit();
		}
		GL11.glEnd();
		
		// top face
		GL11.glBegin(GL11.GL_TRIANGLES);
		{
			new Normal(v2.toVector(), v3.toVector(), v1.toVector()).submit();

			v2.submit();
			v3.submit();
			v1.submit();
		}
		GL11.glEnd();
		
		// front face
		GL11.glBegin(GL11.GL_TRIANGLES);
		{
			new Normal(v1.toVector(), v3.toVector(), v4.toVector()).submit();

			v1.submit();
			v3.submit();
			v4.submit();
		}
		GL11.glEnd();
		
		// back face
		GL11.glBegin(GL11.GL_TRIANGLES);
		{
			new Normal(v2.toVector(), v1.toVector(), v5.toVector()).submit();

			v2.submit();
			v1.submit();
			v5.submit();
		}
		GL11.glEnd();
		
		// bottom face
		GL11.glBegin(GL11.GL_TRIANGLES);
		{
			new Normal(v1.toVector(), v4.toVector(), v5.toVector()).submit();

			v1.submit();
			v4.submit();
			v5.submit();
		}
		GL11.glEnd();
	}

	public void setStateToFired(float initialVelocity) {
		initialVelocity *= Bow.FIRE_SPEED;  // scale up initial vel
		arrowState = ArrowState.FIRED;
		
		// work out initial magnitude for each component : x^2 + y^2 + z^2 = initialVelocity
		// angle and hypotenuse is known. we need the opposite (y component)
		// adjacent needed for x component

		uX = (float) (initialVelocity * Math.cos(Math.toRadians(xzAngle)) * Math.cos(Math.toRadians(xyAngle))); // constant
		uY = (float) (initialVelocity * Math.sin(Math.toRadians(xzAngle)) * Math.cos(Math.toRadians(xyAngle)));
		uZ = (float) (initialVelocity * Math.sin(Math.toRadians(-xyAngle))); // also constant

		firedTime = System.currentTimeMillis();
	}
	
	
	private boolean collisionDetect() {
		if(vY < -1f) {  // position smaller than -1f, approx the length of the arrow from the floor plane
			arrowState = ArrowState.STATIONARY;
			return true;
		}
		
		return false;
	}
	
	public float getxyFlightAngle(float angle) {
		float newAngle = (float) Math.toDegrees(Math.atan2(arrowPosition.getY() - prevArrowPosition.getY(),
														   arrowPosition.getX() - prevArrowPosition.getX())); 
		if(newAngle == 0) {
			newAngle = angle;
		}
		
		return newAngle;
	}

	public void resetArrow() {
		arrowRadius = 0.08f;
		arrowPosition = new Vector(0.0f, 0.0f, 0.0f);
		prevArrowPosition = new Vector(0.0f, 0.0f, 0.0f);
		xyAngle = 0.0f;
		xzAngle = 0.0f;
		vX = 0.0f;
		vY = 0.0f;
		vZ = 0.0f;
		arrowState = ArrowState.HELD;
	}

	public boolean isFired() {
		return arrowState == ArrowState.FIRED;
	}
}
