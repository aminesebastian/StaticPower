package theking530.staticpower.items.fluidcapsule;

import java.util.List;

import javax.annotation.Nullable;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticpower.client.rendering.items.FluidCapsuleItemModel;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.items.StaticPowerItem;
import theking530.staticpower.utilities.WorldUtilities;

public class FluidCapsule extends StaticPowerItem implements ICustomModelSupplier {
	public final ResourceLocation tier;

	public FluidCapsule(String name, ResourceLocation tier) {
		super(name, new Properties().stacksTo(1).setNoRepair());
		this.tier = tier;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		FluidUtil.getFluidHandler(stack).ifPresent(fluidHandler -> {
			tooltip.add(new TextComponent(ChatFormatting.WHITE.toString()).append(GuiTextUtilities.formatFluidToString(fluidHandler.getFluidInTank(0).getAmount(), fluidHandler.getTankCapacity(0))));
		});
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		IFluidHandlerItem handler = FluidUtil.getFluidHandler(stack).orElse(null);
		if (handler != null) {
			return !handler.getFluidInTank(0).isEmpty();
		}
		return false;
	}

	@Override
	public int getBarWidth(ItemStack stack) {
		IFluidHandlerItem handler = FluidUtil.getFluidHandler(stack).orElse(null);
		if (handler != null) {
			double fillRatio = (double) handler.getFluidInTank(0).getAmount() / (double) handler.getTankCapacity(0);
			return (int) (fillRatio * 13);
		}
		return 0;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable net.minecraft.nbt.CompoundTag nbt) {
		return new FluidCapsuleCapability(stack, tier, tier == StaticPowerTiers.CREATIVE);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return tier == StaticPowerTiers.CREATIVE;
	}

	@Override
	public Component getName(ItemStack stack) {
		IFluidHandlerItem containerHandler = FluidUtil.getFluidHandler(stack).orElse(null);
		if (containerHandler == null || containerHandler.getFluidInTank(0).isEmpty()) {
			return new TranslatableComponent(this.getDescriptionId(stack));
		}
		return new TranslatableComponent(this.getDescriptionId(stack)).append(" (").append(containerHandler.getFluidInTank(0).getDisplayName()).append(")");
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	protected InteractionResult onStaticPowerItemUsedOnBlock(UseOnContext context, Level world, BlockPos pos, Direction face, Player player, ItemStack item) {
		IFluidHandler fluidHandler = FluidUtil.getFluidHandler(item).orElse(null);
		if (fluidHandler != null) {
			BlockPos fluidTargetPos = pos.relative(face);
			FluidState usedFluidState = world.getFluidState(fluidTargetPos);
			if (usedFluidState.isEmpty()) {
				WorldUtilities.tryPlaceFluid(player, world, context.getHand(), fluidTargetPos, fluidHandler, fluidHandler.getFluidInTank(0));
			} else {
				WorldUtilities.tryPickUpFluid(item, player, world, fluidTargetPos, face);
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, BakedModel existingModel, ModelBakeEvent event) {
		return new FluidCapsuleItemModel(existingModel);
	}

	public ItemStack getFilledVariant(Fluid fluid) {
		ItemStack output = new ItemStack(this, 1);
		IFluidHandlerItem containerHandler = FluidUtil.getFluidHandler(output).orElse(null);
		if (containerHandler != null) {
			containerHandler.fill(new FluidStack(fluid, containerHandler.getTankCapacity(0)), FluidAction.EXECUTE);
		}
		return output;
	}

	public static class FluidCapsuleItemJEIInterpreter implements IIngredientSubtypeInterpreter<ItemStack> {
		@Override
		public String apply(ItemStack itemStack, UidContext context) {
			IFluidHandlerItem containerHandler = FluidUtil.getFluidHandler(itemStack).orElse(null);
			ResourceLocation tier = ((FluidCapsule) itemStack.getItem()).tier;
			if (containerHandler == null || containerHandler.getFluidInTank(0).isEmpty()) {
				return (tier.toString());
			}
			return tier.toString() + itemStack.getItem().getRegistryName().toString() + containerHandler.getFluidInTank(0).getFluid().getRegistryName().toString()
					+ containerHandler.getFluidInTank(0).getFluid().getRegistryName() + containerHandler.getFluidInTank(0).getAmount();
		}
	}
}