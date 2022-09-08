package theking530.staticpower.blocks.tileentity;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import theking530.api.energy.StaticPowerEnergyDataTypes.StaticVoltageRange;
import theking530.api.energy.StaticPowerStorage;
import theking530.api.energy.utilities.StaticPowerEnergyTextUtilities;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blockentities.interfaces.IBreakSerializeable;
import theking530.staticpower.client.rendering.blocks.DefaultMachineBakedModel;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.StaticPowerTier;

public abstract class StaticPowerMachineBlock extends StaticPowerBlockEntityBlock implements ICustomModelSupplier {
	/**
	 * This property is used by blocks that can be turned on and off and change
	 * their model accordingly.
	 */
	public static final BooleanProperty IS_ON = BooleanProperty.create("is_on");

	protected StaticPowerMachineBlock(ResourceLocation tier) {
		this(tier, Properties.of(Material.METAL, MaterialColor.RAW_IRON));
	}

	protected StaticPowerMachineBlock(ResourceLocation tier, Properties properies) {
		super(tier, properies);
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

	public StaticVoltageRange getInputVoltageRange() {
		if (tier == null) {
			return StaticVoltageRange.ZERO_VOLTAGE;
		}
		return StaticPowerConfig.getTier(tier).getDefaultMachineInputVoltageRange();
	}

	public double getMaximumInputCurrent() {
		if (tier == null) {
			return 0;
		}
		return StaticPowerConfig.getTier(tier).defaultMachineMaximumInputCurrent.get();
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		// Add tooltip for any break serializeable values.
		if (IBreakSerializeable.doesItemStackHaveSerializeData(stack)) {
			CompoundTag nbt = IBreakSerializeable.getSerializeDataFromItemStack(stack);
			if (nbt.contains("MainEnergyStorage") && nbt.getCompound("MainEnergyStorage").contains("storage")) {
				StaticPowerStorage storage = StaticPowerStorage.fromTag(nbt.getCompound("MainEnergyStorage").getCompound("storage"));
				GuiTextUtilities.addColoredBulletTooltip(tooltip, "gui.staticpower.stored_power", ChatFormatting.GREEN,
						StaticPowerEnergyTextUtilities.formatPowerToString(storage.getStoredPower()).getString());
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
		if (getInputVoltageRange() != StaticVoltageRange.ZERO_VOLTAGE) {
			GuiTextUtilities.addColoredBulletTooltip(tooltip, "gui.staticpower.input_voltage", ChatFormatting.BLUE,
					StaticPowerEnergyTextUtilities.formatVoltageRangeToString(getInputVoltageRange()).getString());
		}

		if (getMaximumInputCurrent() > 0) {
			GuiTextUtilities.addColoredBulletTooltip(tooltip, "gui.staticpower.max_input_current", ChatFormatting.RED,
					StaticPowerEnergyTextUtilities.formatCurrentToString(getMaximumInputCurrent()).getString());
		}

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
