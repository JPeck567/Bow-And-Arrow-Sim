package coursework._190055002;

import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

import GraphicsLab.Colour;
import GraphicsLab.FloatBuffer;
import GraphicsLab.Normal;
import GraphicsLab.Vertex;

public class Bow {
	public static final float FIRE_SPEED = 20f;
	private static final float MAX_STRETCH = 0.2f;
	private static final float PULL_SPEED = 0.0005f;
	private static final float ANGLE_RANGE = 20f; // in deg
	private static final float Y_ROTATE_SPEED = 0.1f;
	private static final float Z_ROTATE_SPEED = 0.1f;
	
	private final float stringRange;
	private float bowStringGradient;
	private boolean bowReady;
	private float bowZAngle;
	private float bowYAngle;
	
	public Bow(float stringRange) {
		this.stringRange = stringRange;
		bowStringGradient = 0.0f;
		bowReady = true;
		bowZAngle = 0.0f;
		bowYAngle = 0.0f;
	}
	
	public void draw(float animationScale, Texture woodTexture) {
		GL11.glRotatef(bowYAngle * animationScale, 0.0f, 1.0f, 0.0f);
		GL11.glRotatef(bowZAngle * animationScale, 0.0f, 0.0f, 1.0f);
		
		GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
		GL11.glDisable(GL11.GL_LIGHTING);
		
		// bow frame
		GL11.glPushMatrix();
		{
        	float shininess  = 10.0f;            
        	float specular[] = {0.2f, 0.2f, 0.2f, 1.0f};
            float diffuse[]  = {0.5f, 0.5f, 0.5f, 1.0f};
    
            GL11.glMaterialf(GL11.GL_FRONT, GL11.GL_SHININESS, shininess);
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_SPECULAR, FloatBuffer.wrap(specular));
            GL11.glMaterial(GL11.GL_FRONT, GL11.GL_DIFFUSE, FloatBuffer.wrap(diffuse));

            GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, woodTexture.getTextureID());
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);    
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
			Colour.WHITE.submit();
			drawBowFrame();
			
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glPopAttrib();
		}
		GL11.glPopMatrix();
		
		// bow string
		GL11.glPushMatrix();
		{	
			GL11.glTranslatef(-1.25f, 0.0f, 0.0f);
			GL11.glRotatef(270.0f, 0.0f, 0.0f, 1.0f);
			drawBowString();
		}
		GL11.glPopMatrix();
		
		GL11.glPopAttrib();
	}
	
	public void drawBowString() {
		GL11.glBegin(GL11.GL_LINE_STRIP);  // joins up vertexes

		float y = 0;
		float step = 0.25f; // smaller the step, the more lines which means a smoother curve
		float stringOffset = bowStringGradient*stringRange*stringRange; // the max the y pos can be within the range 'range'
		
		for(float x = -stringRange; x <= stringRange; x += step) {
			y = (float) (bowStringGradient*(x*x)) - stringOffset;  // quadratic in the form bowStringGradient(x^2) - stringOffset
			GL11.glVertex3f(x, y , 0.0f); // draw next point
		}
		GL11.glEnd();
	}
	
	private void drawBowFrame() {
		Vertex v1 = new Vertex(-1.25f, 2.25f, 0.0f);
		Vertex v2 = new Vertex(-0.5f, 1.75f, -0.125f);
		Vertex v3 = new Vertex(-0.25f, 1.75f, -0.125f);
		Vertex v4 = new Vertex(-0.25f, 1.75f, 0.125f);
		Vertex v5 = new Vertex(-0.5f, 1.75f, 0.125f);
		
		Vertex v6 = new Vertex(0.125f, 0.75f, -0.125f);
		Vertex v7 = new Vertex(0.125f, 0.75f, 0.125f);
		Vertex v8 = new Vertex(-0.125f, 0.75f, 0.125f);
		Vertex v9 = new Vertex(-0.125f, 0.75f, -0.125f);
		
		Vertex v10 = new Vertex(0.125f, -0.75f, -0.125f);
		Vertex v11 = new Vertex(0.125f, -0.75f, 0.125f);
		Vertex v12 = new Vertex(-0.125f, -0.75f, 0.125f);
		Vertex v13 = new Vertex(-0.125f, -0.75f, -0.125f);
		
		Vertex v14 = new Vertex(-0.25f, -1.75f, 0.125f);
		Vertex v15 = new Vertex(-0.25f, -1.75f, -0.125f);
		Vertex v16 = new Vertex(-0.5f, -1.75f, -0.125f);
		Vertex v17 = new Vertex(-0.5f, -1.75f, 0.125f);
		Vertex v18 = new Vertex(-1.25f, -2.25f, 0.0f);
		
		//top prisim

		// near face
		GL11.glBegin(GL11.GL_TRIANGLES);
		{
			new Normal(v1.toVector(), v5.toVector(), v4.toVector()).submit();
			
			GL11.glTexCoord2f(0.0f, 0.0f);
			v1.submit();
			GL11.glTexCoord2f(1.0f, 0.0f);
			v5.submit();
			GL11.glTexCoord2f(1.0f, 1.0f);
			v4.submit();
			
		}
		GL11.glEnd();
		
		// right face
		GL11.glBegin(GL11.GL_TRIANGLES);
		{
			new Normal(v1.toVector(), v4.toVector(), v3.toVector()).submit();
			
			v1.submit();
			GL11.glTexCoord2f(1.0f, 0.0f);
			v4.submit();
			GL11.glTexCoord2f(1.0f, 1.0f);
			v3.submit();
			GL11.glTexCoord2f(0.0f, 1.0f);
		}
		GL11.glEnd();
		
		// rear face
		GL11.glBegin(GL11.GL_TRIANGLES);
		{
			new Normal(v2.toVector(), v1.toVector(), v3.toVector()).submit();
			
			v2.submit();
			GL11.glTexCoord2f(1.0f, 0.0f);
			v1.submit();
			GL11.glTexCoord2f(1.0f, 1.0f);
			v3.submit();
			GL11.glTexCoord2f(0.0f, 1.0f);
		}
		GL11.glEnd();
		
		// left face
		GL11.glBegin(GL11.GL_TRIANGLES);
		{
			new Normal(v2.toVector(), v5.toVector(), v1.toVector()).submit();
			
			v2.submit();
			GL11.glTexCoord2f(1.0f, 0.0f);
			v5.submit();
			GL11.glTexCoord2f(1.0f, 1.0f);
			v1.submit();
			GL11.glTexCoord2f(0.0f, 1.0f);
		}
		GL11.glEnd();
		
		// top rectangle
		
		// near face
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v4.toVector(), v5.toVector(), v8.toVector(), v7.toVector()).submit();
			
			GL11.glTexCoord2f(0.0f, 0.0f);
			v4.submit();
			GL11.glTexCoord2f(1.0f, 0.0f);
			v5.submit();
			GL11.glTexCoord2f(1.0f, 1.0f);
			v8.submit();
			GL11.glTexCoord2f(0.0f, 1.0f);
			v7.submit();
		}
		GL11.glEnd();
		
		// right face
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v4.toVector(), v7.toVector(), v6.toVector(), v3.toVector()).submit();
			
			GL11.glTexCoord2f(0.0f, 0.0f);
			v4.submit();
			GL11.glTexCoord2f(1.0f, 0.0f);
			v7.submit();
			GL11.glTexCoord2f(1.0f, 1.0f);
			v6.submit();
			GL11.glTexCoord2f(0.0f, 1.0f);
			v3.submit();
		}
		GL11.glEnd();
		
		// rear face
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v2.toVector(), v3.toVector(), v6.toVector(), v9.toVector()).submit();
			
			GL11.glTexCoord2f(0.0f, 0.0f);
			v2.submit();
			GL11.glTexCoord2f(1.0f, 0.0f);
			v3.submit();
			GL11.glTexCoord2f(1.0f, 1.0f);
			v6.submit();
			GL11.glTexCoord2f(0.0f, 1.0f);
			v9.submit();
		}
		GL11.glEnd();
		
		// left face
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v5.toVector(), v2.toVector(), v9.toVector(), v8.toVector()).submit();
			
			GL11.glTexCoord2f(0.0f, 0.0f);
			v5.submit();
			GL11.glTexCoord2f(1.0f, 0.0f);
			v2.submit();
			GL11.glTexCoord2f(1.0f, 1.0f);
			v9.submit();
			GL11.glTexCoord2f(0.0f, 1.0f);
			v8.submit();
		}
		GL11.glEnd();
		
		// middle rectangle
		// bottom face
//		GL11.glBegin(GL11.GL_POLYGON);
//		{
//			new Normal(v10.toVector(), v13.toVector(), v12.toVector(), v11.toVector());
//			
//			v10.submit();
//			v13.submit();
//			v12.submit();
//			v11.submit();
//		}
//		GL11.glEnd();
		
		// near face
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v8.toVector(), v12.toVector(), v11.toVector(), v7.toVector()).submit();
			
			GL11.glTexCoord2f(0.0f, 0.0f);
			v8.submit();
			GL11.glTexCoord2f(1.0f, 0.0f);
			v12.submit();
			GL11.glTexCoord2f(1.0f, 1.0f);
			v11.submit();
			GL11.glTexCoord2f(0.0f, 1.0f);
			v7.submit();
			
		}
		GL11.glEnd();
		
		// right face
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v6.toVector(), v7.toVector(), v11.toVector(), v10.toVector()).submit();
			
			GL11.glTexCoord2f(0.0f, 0.0f);
			v6.submit();
			GL11.glTexCoord2f(1.0f, 0.0f);
			v7.submit();
			GL11.glTexCoord2f(1.0f, 1.0f);
			v11.submit();
			GL11.glTexCoord2f(0.0f, 1.0f);
			v10.submit();
		}
		GL11.glEnd();
		
		// rear face
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v9.toVector(), v6.toVector(), v10.toVector(), v13.toVector()).submit();
			
			v9.submit();
			GL11.glTexCoord2f(0.0f, 0.0f);
			v6.submit();
			GL11.glTexCoord2f(1.0f, 0.0f);
			v10.submit();
			GL11.glTexCoord2f(1.0f, 1.0f);
			v13.submit();
			GL11.glTexCoord2f(0.0f, 1.0f);
		}
		GL11.glEnd();
		
		// left face
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v8.toVector(), v9.toVector(), v13.toVector(), v12.toVector()).submit();
			
			v8.submit();
			GL11.glTexCoord2f(0.0f, 0.0f);
			v9.submit();
			GL11.glTexCoord2f(1.0f, 0.0f);
			v13.submit();
			GL11.glTexCoord2f(1.0f, 1.0f);
			v12.submit();
			GL11.glTexCoord2f(0.0f, 1.0f);
		}
		GL11.glEnd();
		
		// bottom rectangle
			
		// near face
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v12.toVector(), v17.toVector(), v14.toVector(), v11.toVector()).submit();
			
			v12.submit();
			GL11.glTexCoord2f(0.0f, 0.0f);
			v17.submit();
			GL11.glTexCoord2f(1.0f, 0.0f);
			v14.submit();
			GL11.glTexCoord2f(1.0f, 1.0f);
			v11.submit();
			GL11.glTexCoord2f(0.0f, 1.0f);
		}
		GL11.glEnd();
		
		// right face
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v10.toVector(), v11.toVector(), v14.toVector(), v15.toVector()).submit();
			
			v10.submit();
			GL11.glTexCoord2f(0.0f, 0.0f);
			v11.submit();
			GL11.glTexCoord2f(1.0f, 0.0f);
			v14.submit();
			GL11.glTexCoord2f(1.0f, 1.0f);
			v15.submit();
			GL11.glTexCoord2f(0.0f, 1.0f);
		}
		GL11.glEnd();
		
		// rear face
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v13.toVector(), v10.toVector(), v15.toVector(), v16.toVector()).submit();
			
			v13.submit();
			GL11.glTexCoord2f(0.0f, 0.0f);
			v10.submit();
			GL11.glTexCoord2f(1.0f, 0.0f);
			v15.submit();
			GL11.glTexCoord2f(1.0f, 1.0f);
			v16.submit();
			GL11.glTexCoord2f(0.0f, 1.0f);
		}
		GL11.glEnd();
		
		// left face
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v13.toVector(), v16.toVector(), v17.toVector(), v12.toVector()).submit();
			
			v13.submit();
			GL11.glTexCoord2f(0.0f, 0.0f);
			v16.submit();
			GL11.glTexCoord2f(1.0f, 0.0f);
			v17.submit();
			GL11.glTexCoord2f(1.0f, 1.0f);
			v12.submit();
			GL11.glTexCoord2f(0.0f, 1.0f);
		}
		GL11.glEnd();
		
		// bottom prism
		
		// top face
		GL11.glBegin(GL11.GL_TRIANGLES);
		{
			new Normal(v17.toVector(), v18.toVector(), v14.toVector()).submit();
			
			v17.submit();
			GL11.glTexCoord2f(1.0f, 0.0f);
			v18.submit();
			GL11.glTexCoord2f(1.0f, 1.0f);
			v14.submit();
			GL11.glTexCoord2f(0.0f, 1.0f);
		}
		GL11.glEnd();
		
		// right face
		GL11.glBegin(GL11.GL_TRIANGLES);
		{
			new Normal(v18.toVector(), v15.toVector(), v14.toVector()).submit();
			
			v18.submit();
			GL11.glTexCoord2f(1.0f, 0.0f);
			v15.submit();
			GL11.glTexCoord2f(1.0f, 1.0f);
			v14.submit();
			GL11.glTexCoord2f(0.0f, 1.0f);
		}
		GL11.glEnd();
		
		// bottom face
		GL11.glBegin(GL11.GL_TRIANGLES);
		{
			new Normal(v15.toVector(), v18.toVector(), v16.toVector()).submit();
			
			v15.submit();
			GL11.glTexCoord2f(1.0f, 0.0f);
			v18.submit();
			GL11.glTexCoord2f(1.0f, 1.0f);
			v16.submit();
			GL11.glTexCoord2f(0.0f, 1.0f);
		}
		GL11.glEnd();
		
		// left face
		GL11.glBegin(GL11.GL_TRIANGLES);
		{
			new Normal(v18.toVector(), v17.toVector(), v16.toVector()).submit();
			
			v18.submit();
			GL11.glTexCoord2f(1.0f, 0.0f);
			v17.submit();
			GL11.glTexCoord2f(1.0f, 1.0f);
			v16.submit();
			GL11.glTexCoord2f(0.0f, 1.0f);
		}
		GL11.glEnd();
	}
	
	public void pullBack(float animationScale) {
		if(bowStringGradient <= MAX_STRETCH) {  // increase gradient, therefore moving string back and increasing width
			addBowStringGradient(PULL_SPEED, animationScale);
		}
	}
	
	public void pushForward(float animationScale) {
		if(bowStringGradient > 0f) {  // string gradient not negative, so needs to be pushed back
			addBowStringGradient(-1.0f, animationScale); // decrease gradient
		} else {  // if already at 0f or negative
			resetBowStringGradient();
		}
	}
	
	public void rotateYBow(float ive) {
		float newAngle = bowYAngle + Y_ROTATE_SPEED * ive;
		bowYAngle = (Math.abs(newAngle) > ANGLE_RANGE) ? ANGLE_RANGE * ive : newAngle;
	}
	
	public void rotateZBow(float ive) {
		float newAngle = bowZAngle + Z_ROTATE_SPEED * ive;
		bowZAngle = (Math.abs(newAngle) > ANGLE_RANGE) ? ANGLE_RANGE * ive : newAngle;
	}
	
	
	private void addBowStringGradient(float speed, float animationScale) {  // either +ive or -ive
		bowStringGradient += speed * animationScale;
	}
	
	private void resetBowStringGradient() {
		bowStringGradient = 0f;
	}

	public void resetAngles() {
		bowZAngle = 0.0f;
		bowYAngle = 0.0f;
	}
	
	public float getStringPeak() {
		return bowStringGradient*stringRange*stringRange; // calculate the y coord at the first point on the the strings curve, 
														  // which is the furthest from the peak of the parabola
	}
	
	public float getBowYAngle() {
		return bowYAngle;
	}
	
	public float getBowZAngle() {
		return bowZAngle;
	}
	
	public boolean isReady() {
		return bowReady;
	}

}
