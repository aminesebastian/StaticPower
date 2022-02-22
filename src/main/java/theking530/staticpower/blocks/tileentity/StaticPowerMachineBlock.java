package theking530.staticpower.blocks.tileentity;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
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
		this.registerDefaultState(stateDefinition.any().setValue(IS_ON, false));
	}

	protected StaticPowerMachineBlock(String name, Properties properies) {
		super(name, properies);
		this.registerDefaultState(stateDefinition.any().setValue(IS_ON, false));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(IS_ON);
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, BakedModel existingModel, ModelBakeEvent event) {
		return new DefaultMachineBakedModel(existingModel);
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
		// Check to see if we have the IS_ON property and it is true. If so, light up.
		if (state.hasProperty(IS_ON) && state.getValue(IS_ON)) {
			return 15;
		}
		return super.getLightEmission(state, world, pos);
	}
}
