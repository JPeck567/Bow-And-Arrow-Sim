/* CS2150Coursework.java
 * 
 * University Username: 190055002
 * Full Name: Jorge Peck
 * Course: Computer Science BSc, 2nd Year
 * 
 * --!!-- 
 * The code within the coursework package is my own work. This includes this class, the Bow class and the Arrow class.
 * It does not use code from any other source that I haven't made myself. Textures have been retrieved online and are refrerenced in comments.
 * --!!--
 * 
 * --------------------------------------------------------------------------------------------------------------
 * Scene Graph:
 *  Scene origin
 *  |
 *  +-- [Rz(90) S(256,1,256) T(0,-75,0)] Sky plane
 *  |
 *  +-- [T(0, FLOOR_Y, 0) S(256,1,256)] Grass plane
 *  |
 *  +-- [Ry(bowYAngle*animationScale), Rz(bowZAngle*animationScale)] Bow frame
 *  |	|
 *  |   +-- [T(-1.25,0,0) Rz(270)] Bow string
 *  |
 *  +-- [Ry(xyAngle*animationScale) Rz(xzAngle*animationScale) T(arrowPosition.getX() * animationScale,
 *	|	|				arrowPosition.getY() * animationScale,
 *	|	|				arrowPosition.getZ() * animationScale))] Arrow
 *	|	|
 *  |	+-- [Ry(90)] Arrow body
 *  |	|	|
 *  |	|	+-- [] Arrow length
 *  |	|	|
 *  |	|	+-- [] Arrow circular base
 *  |	|
 *  |	+-- [S(0.325,0.325,0.325) T(6.625,0,0)] Arrow head
 *  |
 *  |
 *  +-- [Ry(xyAngle*animationScale) Rz(xzAngle*animationScale) T(arrowPosition.getX() * animationScale,
 *		|				arrowPosition.getY() * animationScale,
 *		|				arrowPosition.getZ() * animationScale))] Used Arrows
 *		|
 *  	+-- [Ry(90)] Arrow body
 *  	|	|
 *  	|	+-- [] Arrow length
 *  	|	|
 *  	|	+-- [] Arrow circular base
 *  	|
 *  	+-- [S(0.325,0.325,0.325) T(6.625,0,0)] Arrow head
 * 
 * Controls: 
 * 	Bow Controls -
 *  	LEFT SHIFT:
 *  		Pulls back the bow's string. Letting go will fire the arrow.
 *  	A + D:
 *  		'A' rotates the bow left (anticlockwise) around the Y axis, whilst 'D' does the opposite, right (clockwise) on the Y axis.
 *  	S + W:
 *  		'S' tilts the bow up around the Z axis, whilst 'W' tilts the bow up around the Z axis
 *	Camera Controls -
 *		LEFT + RIGHT:
 *			LEFT pans the camera to the right (anticlockwise) and RIGHT pans the camera to the left (clockwise)
 *		UP ARROW:
 *			Zooms the camera in towards the bow
 *		DOWN ARROW:
 *			Zooms the camera away from the bow
 *	Misc -
 *		R:
 *			Resets the camera's position to face perpendicular to the bow, at a close zoom
 *		E:
 *			Resets the bow's rotation to face the positive X at 0 degrees and sets it's tilt pointing at 45 degrees
 *		SPACE:
 *			Resets both the camera and bow as R and E would do combined
 *	
 *
 */
package coursework._190055002;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Cylinder;
import org.lwjgl.util.glu.GLU;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.opengl.Texture;
import GraphicsLab.*;

/**
 * <h2>Bow and Arrow Simulation</h2>
 * 
 * <h4>Program Description:</h4>
 * <p>
 * 	This program is a game interface involving a bow and arrow simulation. The user can control the camera by moving towards or away from
 *  the bow, as well as rotate around the bow. The bow can be rotated in all cardinal directions. These positioning controls are to set bounds.
 *	The user can fire the bow with an arrow. The arrow will follow a line of trajectory in with a given magnitude. This is set by how much the user
 *  pulls the bow back, and at what orientation the bow was at when the arrow was fired. The arrow will also angle itself based on it's magnitude. 
 *  The arrow will land on the ground, and stop. The user can fire several arrows.
 * </p>
 *
 * <h4> Controls:</h4>
 * <ul>
 * <li>Press the escape key to exit the application.
 * <li>Hold the x, y and z keys to view the scene along the x, y and z axis,
 * respectively
 * <li>While viewing the scene along the x, y or z axis, use the up and down
 * cursor keys to increase or decrease the viewpoint's distance from the scene
 * origin
 * </ul>
 * <b>Bow Controls -</b>
 * <ul>
 * <li>LEFT SHIFT: Pulls back the bow's string. Letting go will fire the arrow.
 * <li>A + D: 'A' rotates the bow left (anticlockwise) around the Y axis, whilst 'D' does the opposite, right (clockwise) on the Y axis.
 * <li>S + W: 'S' tilts the bow up around the Z axis, whilst 'W' tilts the bow up around the Z axis
 * </ul>
 * <b>Camera Controls -</b>
 * <ul>
 * <li>LEFT + RIGHT: LEFT pans the camera to the right (anticlockwise) and RIGHT pans the camera to the left (clockwise)
 * <li>UP ARROW: Zooms the camera in towards the bow
 * <li>DOWN ARROW: Zooms the camera away from the bow
 * </ul>
 * <b>Misc -</b>
 * <ul>
 * <li>R: Resets the camera's position to face perpendicular to the bow, at a close zoom
 * <li>E: Resets the bow's rotation to face the positive X at 0 degrees and sets it's tilt pointing at 45 degrees
 * <li>SPACE: Resets both the camera and bow as R and E would do combined
 * </ul>
 */
public class CS2150Coursework extends GraphicsLab {
	public static final float GRAVITY = -0.98f;
	public static final Colour SILVER = new Colour(169,169,169);
	public static final float FLOOR_Y = -2.5f;
	
	private final float FOV = 0.75f;
	private final float YAW_ROT_SPEED = 0.001f;
	private final float YAW_ROT_CENTRE = (float) Math.PI; // exactly +180 from 0
	private final float CAM_RADIUS_SPEED = 0.001f;
	private final float CAM_RADIUS_UPPER_LIMIT = 0.5f;
	private final float CAM_RADIUS_CENTRE = 0.35f;
	private final float CAM_RADIUS_LOWER_LIMIT = 0.2f;
	private final int planeList = 1;
	
	private float yawRot;
	private float camRadius;
	
	private Texture grassTexture;
	private Texture skyTexture;
	private Texture woodTexture;
	
	private Bow bow;
	private ArrayList<Arrow> usedArrows;
	private Arrow heldArrow;
	
	private boolean bowIsDrawn;

	public static void main(String args[]) {
		new CS2150Coursework().run(WINDOWED, "Bow and Arrow Simulation", 1f);
	}

	protected void initScene() throws Exception {
		GL11.glEnable(GL11.GL_CULL_FACE);   // No back face culling, saves rendering faces not in view
		
		yawRot = YAW_ROT_CENTRE;
		camRadius = CAM_RADIUS_LOWER_LIMIT;
		
		// texture sourced from: https://www.deviantart.com/architecturendu/art/Grass-Pack-I-351636983
		grassTexture = loadTexture("coursework\\_190055002\\textures\\grassTexture.jpg");
		// texture sourced from: https://all-free-download.com/free-photos/download/dawn-sky-texture_552881.html
		skyTexture = loadTexture("coursework\\_190055002\\textures\\skyTexture.jpg");
		// texture sourced from: https://www.deviantart.com/architecturendu/art/Wood-Pack-I-351619614 
		woodTexture = loadTexture("coursework\\_190055002\\textures\\woodTexture.jpg");
		
		GL11.glNewList(planeList, GL11.GL_COMPILE);  // pre render planes, as do not need animation
		{
			drawPlane();
		}
		GL11.glEndList();
		
		bow = new Bow(2.25f);
		usedArrows = new ArrayList<Arrow>();
		heldArrow = getNewArrow();
		
		bowIsDrawn = false;
		
		// global ambient light level
		float globalAmbient[] = { 0.5f, 0.5f, 0.5f, 1.0f };  // 4th val is for transparency
		// set ambient lighting
		GL11.glLightModel(GL11.GL_LIGHT_MODEL_AMBIENT, FloatBuffer.wrap(globalAmbient));
		
		float diffuse0[] = {0.5f, 0.5f, 0.5f, 1.0f}; // light colour, to be same as ambient
		float ambient0[] = {0.1f, 0.1f, 0.1f, 1.0f}; // light colour again
		float position0[] = { 7f, 10f, 0.0f, 1.0f}; // up + in front of the bow

		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_AMBIENT, FloatBuffer.wrap(ambient0));
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_DIFFUSE, FloatBuffer.wrap(diffuse0));
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_SPECULAR, FloatBuffer.wrap(diffuse0));
		GL11.glLight(GL11.GL_LIGHT0, GL11.GL_POSITION, FloatBuffer.wrap(position0));
		
		GL11.glEnable(GL11.GL_LIGHT0);
		GL11.glEnable(GL11.GL_LIGHTING);  // enables calculations
		GL11.glEnable(GL11.GL_NORMALIZE);  // ensure that all normals are re-normalised after transformations automatically
	}

	private Arrow getNewArrow() {
		return new Arrow();
	}

	protected void checkSceneInput() {
		if(Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {  // if s is pressed down
			bow.pullBack(getAnimationScale());
			bowIsDrawn = true; // needed otherwise bow will fire as soon as scene begins, w/o it being drawn first by user
		} else { // if player not pulling bow as key not held for it
			if(bowIsDrawn) {  // and player was pulling bow before they let go of key
				heldArrow.setStateToFired(bow.getStringPeak());  // fire arrow
				bowIsDrawn = false;
				usedArrows.add(heldArrow); // add to used arrows
			}
			bow.pushForward(getAnimationScale());
			
			if(bow.isReady()) {
				heldArrow = getNewArrow();  // can fire arrow again, as string is back at m=0
			}
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			bow.rotateYBow(1.0f);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			bow.rotateYBow(-1.0f);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			bow.rotateZBow(1.0f);
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			bow.rotateZBow(-1.0f);
		}
		
		if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			yawRot = (yawRot + YAW_ROT_SPEED > YAW_ROT_CENTRE + FOV) ? YAW_ROT_CENTRE + FOV: yawRot + YAW_ROT_SPEED;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			yawRot = (yawRot - YAW_ROT_SPEED < YAW_ROT_CENTRE - FOV) ? YAW_ROT_CENTRE - FOV: yawRot - YAW_ROT_SPEED;
			
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			camRadius = (camRadius - CAM_RADIUS_SPEED < CAM_RADIUS_LOWER_LIMIT) ? CAM_RADIUS_LOWER_LIMIT: camRadius - CAM_RADIUS_SPEED;
		}
		
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			camRadius = (camRadius + CAM_RADIUS_SPEED > CAM_RADIUS_UPPER_LIMIT) ? CAM_RADIUS_UPPER_LIMIT: camRadius + CAM_RADIUS_SPEED;
		}

		if(Keyboard.isKeyDown(Keyboard.KEY_R)) {
			resetCam();
		} 
		
		if(Keyboard.isKeyDown(Keyboard.KEY_E)) {
			resetBow();
		} 
		
		if(Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			resetBow();
			resetCam();
		} 
	}

	private void resetCam() {
		yawRot = YAW_ROT_CENTRE;
		camRadius = CAM_RADIUS_LOWER_LIMIT;
	}
	
	private void resetBow() {
		bow.resetAngles();
	}

	protected void updateScene() {
		heldArrow.update(bow, getAnimationScale());
		
		for(Arrow a : usedArrows) {
			a.update(bow, getAnimationScale());
		}
	}

	protected void renderScene() {
		
		// sky plane
		GL11.glPushMatrix();
		{
			GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);			
			GL11.glDisable(GL11.GL_LIGHTING);
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST); 
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
						
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, skyTexture.getTextureID());
			
			Colour.WHITE.submit();
			GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f);
			GL11.glRotatef(-90.0f, 0.0f, 1.0f, 0.0f);
			GL11.glScalef(128.0f, 1.0f, 128.0f);
			GL11.glTranslatef(0.0f, -75.0f, 0.0f);
			GL11.glCallList(planeList);

			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glPopAttrib();
		}
		GL11.glPopMatrix();
		
		// grass plane
		GL11.glPushMatrix();
		{
			GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);
			GL11.glDisable(GL11.GL_LIGHTING);
			
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			
			// if texture were too small, set it so it will be copied again for the remaining areas
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST); 
			GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
			
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, grassTexture.getTextureID());
			
			// so texture appears bright
			Colour.WHITE.submit();
			GL11.glTranslatef(0.0f, FLOOR_Y, 0.0f);
			GL11.glScalef(256.0f, 1.0f, 256.0f);
			GL11.glCallList(planeList);
			
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glPopAttrib();
		}
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		{
			bow.draw(getAnimationScale(), woodTexture);
		}
		GL11.glPopMatrix();
		
		GL11.glPushMatrix();
		{
			heldArrow.draw(getAnimationScale(), woodTexture);
		}
		GL11.glPopMatrix();
		
		for(Arrow usedArrow : usedArrows) {
			GL11.glPushMatrix();
			{
				usedArrow.draw(getAnimationScale(), woodTexture);
			}
			GL11.glPopMatrix();
		}
		
	}
	
	private void drawPlane() {
		Vertex v1 = new Vertex(-0.5f, 0.0f, -0.5f); // left, back
		Vertex v2 = new Vertex(0.5f, 0.0f, -0.5f); // right, back
		Vertex v3 = new Vertex(0.5f, 0.0f, 0.5f); // right, front
		Vertex v4 = new Vertex(-0.5f, 0.0f, 0.5f); // left, front

		// draw the plane geometry. order the vertices so that the plane faces up
		GL11.glBegin(GL11.GL_POLYGON);
		{
			new Normal(v4.toVector(), v3.toVector(), v2.toVector(), v1.toVector()).submit();

			GL11.glTexCoord2f(0.0f, 0.0f);
			v4.submit();

			GL11.glTexCoord2f(1.0f, 0.0f);
			v3.submit();

			GL11.glTexCoord2f(1.0f, 1.0f);
			v2.submit();

			GL11.glTexCoord2f(0.0f, 1.0f);
			v1.submit();
		}
		GL11.glEnd();
	}

	protected void setSceneCamera() {
		super.setSceneCamera();

		GLU.gluLookAt( // cam location. uses cos and sin to provide circular movement + scales them by the zoom factor.
				(float) Math.toDegrees(Math.cos(yawRot)) * camRadius * getAnimationScale() ,
				0.0f,
				(float) Math.toDegrees(Math.sin(yawRot)) * camRadius * getAnimationScale() ,
				
				0.0f, 0.0f, 0.0f, // looking at coordinates. always on origin to bow
				0.0f, 1.0f, 0.0f // view-up
				); 	
	}

	protected void cleanupScene() {
		grassTexture.release();
		skyTexture.release();
		woodTexture.release();
	}

}
