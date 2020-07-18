package theking530.staticpower.client.rendering.blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.util.Direction;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.data.IModelData;
import theking530.common.utilities.Color;
import theking530.staticpower.client.StaticPowerAdditionalModels;
import theking530.staticpower.tileentities.StaticPowerTileEntityBlock;

public class TestMachineModel extends AbstractBakedModel {

	public TestMachineModel(ModelBakeEvent event) {
		super(event.getModelRegistry().get(StaticPowerAdditionalModels.DEFAULT_MACHINE_MODEL));
	}

	@Override
	protected List<BakedQuad> getBakedQuadsFromIModelData(BlockState state, Direction side, Random rand, IModelData data) {
		Direction facing = state.get(StaticPowerTileEntityBlock.FACING);

		List<BakedQuad> rotatedQuads = rotateQuadsToFaceDirection(BaseModel, facing, side, state, rand);
		List<BakedQuad> coloredQuads = new ArrayList<BakedQuad>();

		for (BakedQuad rotated : rotatedQuads) {
			coloredQuads.add(new ColoredQuad(rotated, new Color(141.0f, 177.0f, 225.0f)));
		}

		return coloredQuads;
	}

}
