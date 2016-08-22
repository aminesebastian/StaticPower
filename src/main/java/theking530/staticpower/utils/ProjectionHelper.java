package theking530.staticpower.utils;
/**
 * Thanks to mikeemoo and the openmods team for an amazing open source mod.
 */
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.util.math.BlockPos;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class ProjectionHelper {

	private IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);

	private FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);

	private FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);

	private FloatBuffer objectCoords = GLAllocation.createDirectFloatBuffer(3);

	private FloatBuffer winCoords = GLAllocation.createDirectFloatBuffer(3);

	public void updateMatrices() {
		GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview);
		GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection);
		GL11.glGetInteger(GL11.GL_VIEWPORT, viewport);
	}

	public BlockPos unproject(float winX, float winY, float winZ) {
		GLU.gluUnProject(winX, winY, winZ, modelview, projection, viewport, objectCoords);

		float objectX = objectCoords.get(0);
		float objectY = objectCoords.get(1);
		float objectZ = objectCoords.get(2);

		return new BlockPos(objectX, objectY, objectZ);
	}

	public BlockPos project(float objX, float objY, float objZ) {
		GLU.gluProject(objX, objY, objZ, modelview, projection, viewport, winCoords);

		float winX = winCoords.get(0);
		float winY = winCoords.get(1);
		float winZ = winCoords.get(2);
		
		return new BlockPos(winX, winY, winZ);
	}
}