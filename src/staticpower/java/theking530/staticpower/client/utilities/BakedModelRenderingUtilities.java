package theking530.staticpower.client.utilities;

import java.util.ArrayList;
import java.util.List;

import com.mojang.math.Vector3f;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockElementRotation;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import theking530.staticcore.utilities.Vector2D;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.utilities.ModelUtilities;

public class BakedModelRenderingUtilities {
	private static final FaceBakery FACE_BAKER = new FaceBakery();

	public static List<BakedQuad> getBakedQuadsForToolPowerBar(BlockState state, Direction side, RandomSource rand, ModelData data, float percentage, Vector2D position,
			Vector2D scale, float rotationAngle, boolean includeBottomOffset) {
		List<BakedQuad> output = new ArrayList<BakedQuad>();
		try {
			// Get the atlas texture.
			BlockElementRotation rotation = new BlockElementRotation(new Vector3f(0.5f, 0.0f, 0.0f), Direction.Axis.Z, rotationAngle, false);

			// Draw the durability background.
			TextureAtlasSprite blackSprite = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(StaticPowerSprites.BLACK_TEXTURE);
			BlockFaceUV durabilityBgUv = new BlockFaceUV(new float[] { 0.0f, 0.0f, 16.0f, 16.0f }, 0);
			BlockElementFace durabilityPartFace = new BlockElementFace(null, -1, blackSprite.getName().toString(), durabilityBgUv);
			BakedQuad durabilityBackground = FACE_BAKER.bakeQuad(new Vector3f(position.getX(), position.getY() - (includeBottomOffset ? 1 : 0), 8.5f),
					new Vector3f(scale.getX(), scale.getY(), 8.51f), durabilityPartFace, blackSprite, Direction.SOUTH, ModelUtilities.IDENTITY, rotation, false,
					new ResourceLocation("dummy_name"));
			output.add(durabilityBackground);

			// Draw the durability bar.
			float xUVCoord = percentage * 15.999f;
			TextureAtlasSprite durabilityTexture = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(StaticPowerSprites.TOOL_POWER_BAR);
			BlockFaceUV blockFaceUV = new BlockFaceUV(new float[] { xUVCoord, 0.0f, xUVCoord, 16.0f }, 0);
			BlockElementFace durabilityBarFace = new BlockElementFace(null, -1, durabilityTexture.getName().toString(), blockFaceUV);
			BakedQuad durabilityBar = FACE_BAKER.bakeQuad(new Vector3f(position.getX(), position.getY(), 8.51f), new Vector3f(scale.getX() * percentage, scale.getY(), 8.52f),
					durabilityBarFace, durabilityTexture, Direction.SOUTH, ModelUtilities.IDENTITY, rotation, false, new ResourceLocation("dummy_name"));

			output.add(durabilityBar);
		} catch (Exception e) {
			// No nothing -- this is just for those edge cases where resources are reloaded.
		}

		return output;
	}
}
