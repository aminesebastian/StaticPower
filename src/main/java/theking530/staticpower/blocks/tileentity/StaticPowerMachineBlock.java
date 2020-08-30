package theking530.staticpower.blocks.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.staticpower.blocks.interfaces.ICustomModelSupplier;
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
	public IBakedModel getModelOverride(BlockState state, IBakedModel existingModel, ModelBakeEvent event) {
		return new DefaultMachineBakedModel(existingModel);
	}
}
