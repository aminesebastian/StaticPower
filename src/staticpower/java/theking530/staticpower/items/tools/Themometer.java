package theking530.staticpower.items.tools;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.fluids.FluidStack;
import theking530.api.heat.CapabilityHeatable;
import theking530.api.heat.HeatStorageUtilities;
import theking530.api.heat.IHeatStorage;
import theking530.staticcore.client.ICustomModelProvider;
import theking530.staticpower.client.rendering.items.ThermometerItemModel;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.crafting.RecipeMatchParameters;
import theking530.staticpower.data.crafting.StaticPowerRecipeRegistry;
import theking530.staticpower.data.crafting.wrappers.thermalconductivity.ThermalConductivityRecipe;
import theking530.staticpower.init.ModCreativeTabs;
import theking530.staticpower.init.ModRecipeTypes;
import theking530.staticpower.items.StaticPowerItem;
import theking530.staticpower.utilities.RaytracingUtilities;

public class Themometer extends StaticPowerItem implements ICustomModelProvider {

	public Themometer() {
		super(new Item.Properties().stacksTo(1).tab(ModCreativeTabs.TOOLS));
	}

	@Override
	protected InteractionResultHolder<ItemStack> onStaticPowerItemRightClicked(Level world, Player player, InteractionHand hand, ItemStack item) {
		if (!world.isClientSide()) {
			int temperature = HeatStorageUtilities.getBiomeAmbientTemperature(world, player.getOnPos());

			BlockHitResult traceResult = RaytracingUtilities.findPlayerRayTrace(world, player, ClipContext.Fluid.ANY);
			if (traceResult.getType() == HitResult.Type.MISS) {
				MutableComponent chatComponent = Component.literal("Temperature (").append(ChatFormatting.GREEN + GuiTextUtilities.formatHeatToString(temperature).getString())
						.append(")");
				player.sendSystemMessage(chatComponent);
				return InteractionResultHolder.consume(item);
			}
		}
		return InteractionResultHolder.pass(player.getItemInHand(hand));
	}

	@Override
	protected InteractionResult onPreStaticPowerItemUsedOnBlock(UseOnContext context, Level world, BlockPos pos, Direction face, Player player, ItemStack item) {
		if (!world.isClientSide()) {
			int temperature = HeatStorageUtilities.getBiomeAmbientTemperature(world, player.getOnPos());
			MutableComponent chatComponent = Component.literal("Temperature (").append(ChatFormatting.GREEN + GuiTextUtilities.formatHeatToString(temperature).getString())
					.append(")");

			boolean found = false;
			BlockEntity be = world.getBlockEntity(pos);
			if (be != null) {
				IHeatStorage otherStorage = be.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY, face)
						.orElse(be.getCapability(CapabilityHeatable.HEAT_STORAGE_CAPABILITY, null).orElse(null));
				if (otherStorage != null) {
					temperature = otherStorage.getCurrentHeat();
					found = true;
				}
			}

			// Get the block and fluid states at the pos.
			FluidState fluidState = world.getFluidState(pos);
			BlockState blockstate = world.getBlockState(pos);

			// If there is a recipe for thermal conductivity for this block
			Optional<ThermalConductivityRecipe> recipe = StaticPowerRecipeRegistry.getRecipe(ModRecipeTypes.THERMAL_CONDUCTIVITY_RECIPE_TYPE.get(),
					new RecipeMatchParameters(blockstate).setFluids(new FluidStack(fluidState.getType(), 1000)));
			if (recipe.isPresent()) {
				if (recipe.get().hasActiveTemperature()) {
					temperature = recipe.get().getTemperature();
					found = true;
				}
			}

			if (found) {
				chatComponent = chatComponent.append(" ")
						.append(Component.literal("Block (").append(ChatFormatting.GREEN + GuiTextUtilities.formatHeatToString(temperature).getString()).append(")"));
			}
			player.sendSystemMessage(chatComponent);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getBlockModeOverride(BlockState state, BakedModel existingModel, ModelEvent.BakingCompleted event) {
		return new ThermometerItemModel(existingModel);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getAdvancedTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip) {
	}
}
