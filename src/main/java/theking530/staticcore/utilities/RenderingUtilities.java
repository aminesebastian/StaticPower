package theking530.staticcore.utilities;

import com.mojang.blaze3d.systems.RenderSystem;

public class RenderingUtilities {
	public static void applyScissorMask(RectangleBounds bounds) {
		RenderSystem.enableScissor(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
	}

	public static void clearScissorMask() {
		RenderSystem.disableScissor();
	}
}
