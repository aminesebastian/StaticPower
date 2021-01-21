package theking530.staticpower.blocks.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticpower.client.rendering.blocks.DefaultMachineBakedModel;

public abstract class StaticPowerMachineBlock extends StaticPowerTileEntityBlock implements ICustomModelSupplier {
	/**
	 * This property is used by blocks that can be turned on and off and change
	 * their model accordingly.
	 */
	public static final BooleanProperty IS_ON = BooleanProperty.create("is_on");

	protected StaticPowerMachineBlock(String name) {
		super(name);
		this.setDefaultState(stateContainer.getBaseState().with(IS_ON, false));
	}

	protected StaticPowerMachineBlock(String name, Properties properies) {
		super(name, properies);
		this.setDefaultState(stateContainer.getBaseState().with(IS_ON, false));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(IS_ON);
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IBakedModel getModelOverride(BlockState state, IBakedModel existingModel, ModelBakeEvent event) {
		return new DefaultMachineBakedModel(existingModel);
	}

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		// Check to see if we have the IS_ON property and it is true. If so, light up.
		if (state.hasProperty(IS_ON) && state.get(IS_ON)) {
			return 15;
		}
		return state.getLightValue();
	}

}
