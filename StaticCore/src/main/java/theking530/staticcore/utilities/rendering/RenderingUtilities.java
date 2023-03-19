package theking530.staticcore.utilities.rendering;

import com.mojang.blaze3d.systems.RenderSystem;

import theking530.staticcore.utilities.math.RectangleBounds;

public class RenderingUtilities {
	public static void applyScissorMask(RectangleBounds bounds) {
		RenderSystem.enableScissor((int) bounds.getX(), (int) bounds.getY(), bounds.getWidth(), bounds.getHeight());
	}

	public static void clearScissorMask() {
		RenderSystem.disableScissor();
	}
}
