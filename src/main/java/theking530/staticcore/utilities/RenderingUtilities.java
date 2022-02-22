package theking530.staticcore.utilities;

import com.mojang.blaze3d.systems.RenderSystem;

public class RenderingUtilities {
	public static void applyScissorMask(Vector4D mask) {
		RenderSystem.enableScissor(mask.getXi(), mask.getYi(), mask.getZi(), mask.getWi());
	}

	public static void clearScissorMask() {
		RenderSystem.disableScissor();
	}
}
