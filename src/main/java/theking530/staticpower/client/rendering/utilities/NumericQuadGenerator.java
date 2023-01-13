package theking530.staticpower.client.rendering.utilities;

import java.util.HashMap;
import java.util.Map;

import com.mojang.math.Vector3f;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import theking530.staticpower.utilities.ModelUtilities;

public class NumericQuadGenerator {
	private static final FaceBakery FACE_BAKER = new FaceBakery();
	private static final int HORIZONTAL_OFFSET = 3;
	private static final int VERTICAL_OFFSET = 5;
	private static final Map<Integer, BlockFaceUV> UV_MAP = new HashMap<>();
	static {
		for (int x = 0; x < 5; x++) {
			for (int y = 0; y < 2; y++) {
				int digit = x + (5 * y);
				BlockFaceUV uv = new BlockFaceUV(new float[] { HORIZONTAL_OFFSET * x, VERTICAL_OFFSET * y, HORIZONTAL_OFFSET * (x + 1), VERTICAL_OFFSET * (y + 1) }, 0);
				UV_MAP.put(digit, uv);
			}
		}
	}

	public static BakedQuad generateQuadForDigit(TextureAtlasSprite sprite, int digit, Direction side, Vector3f position, float scale) {
		BlockElementFace blockPartFace = new BlockElementFace(side, -1, sprite.getName().toString(), getUVForDigit(digit));

		Vector3f start = new Vector3f(0f, 0f, 0f);
		start.add(position);

		Vector3f end = new Vector3f(9.6f, 16.0f, 0.0f);
		end.add(start);

		return FACE_BAKER.bakeQuad(start, end, blockPartFace, sprite, side, ModelUtilities.IDENTITY, null, true, new ResourceLocation("dummy_name"));
	}

	public static BlockFaceUV getUVForDigit(int digit) {
		return UV_MAP.get(digit);
	}
}
