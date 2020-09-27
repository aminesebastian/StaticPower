package theking530.staticpower.items.fluidcapsule;

import java.util.List;

import javax.annotation.Nullable;

import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import theking530.staticpower.blocks.interfaces.ICustomModelSupplier;
import theking530.staticpower.client.rendering.items.FluidCapsuleItemModel;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.items.StaticPowerItem;
import theking530.staticpower.utilities.WorldUtilities;

public class FluidCapsule extends StaticPowerItem implements ICustomModelSupplier {
	public final ResourceLocation tier;

	public FluidCapsule(String name, ResourceLocation tier) {
		super(name, new Properties().maxStackSize(1).setNoRepair());
		this.tier = tier;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void getBasicTooltip(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip) {
		FluidUtil.getFluidHandler(stack).ifPresent(fluidHandler -> {
			tooltip.add(new StringTextComponent(TextFormatting.WHITE.toString()).append(GuiTextUtilities
					.formatFluidToString(fluidHandler.getFluidInTank(0).getAmount(), fluidHandler.getTankCapacity(0))));
		});
	}

	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return getDurabilityForDisplay(stack) < 1.0f;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		IFluidHandlerItem handler = FluidUtil.getFluidHandler(stack).orElse(null);
		if (handler != null) {
			return 1.0 - (double) handler.getFluidInTank(0).getAmount() / (double) handler.getTankCapacity(0);
		}
		return 0.0f;
	}

	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable net.minecraft.nbt.CompoundNBT nbt) {
		return new FluidCapsuleCapability(stack, tier, tier == StaticPowerTiers.CREATIVE);
	}

	@Override
	public boolean hasEffect(ItemStack stack) {
		return tier == StaticPowerTiers.CREATIVE;
	}

	@Override
	public ITextComponent getDisplayName(ItemStack stack) {
		IFluidHandlerItem containerHandler = FluidUtil.getFluidHandler(stack).orElse(null);
		if (containerHandler == null || containerHandler.getFluidInTank(0).isEmpty()) {
			return new TranslationTextComponent(this.getTranslationKey(stack));
		}
		return new TranslationTextComponent(this.getTranslationKey(stack)).appendString(" (")
				.append(containerHandler.getFluidInTank(0).getDisplayName()).appendString(")");
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	protected ActionResultType onStaticPowerItemUsedOnBlock(ItemUseContext context, World world, BlockPos pos,
			Direction face, PlayerEntity player, ItemStack item) {
		IFluidHandler fluidHandler = FluidUtil.getFluidHandler(item).orElse(null);
		if (fluidHandler != null) {
			BlockPos fluidTargetPos = pos.offset(face);
			FluidState usedFluidState = world.getFluidState(fluidTargetPos);
			if (usedFluidState.isEmpty()) {
				WorldUtilities.tryPlaceFluid(player, world, context.getHand(), fluidTargetPos, fluidHandler,
						fluidHandler.getFluidInTank(0));
			} else {
				WorldUtilities.tryPickUpFluid(item, player, world, fluidTargetPos, face);
			}
		}
		return ActionResultType.PASS;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public IBakedModel getModelOverride(BlockState state, IBakedModel existingModel, ModelBakeEvent event) {
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

	public static class FluidCapsuleItemJEIInterpreter implements ISubtypeInterpreter {
		@Override
		public String apply(ItemStack itemStack) {
			IFluidHandlerItem containerHandler = FluidUtil.getFluidHandler(itemStack).orElse(null);
			ResourceLocation tier = ((FluidCapsule) itemStack.getItem()).tier;
			if (containerHandler == null || containerHandler.getFluidInTank(0).isEmpty()) {
				return (tier.toString());
			}
			return tier.toString() + itemStack.getItem().getRegistryName().toString()
					+ containerHandler.getFluidInTank(0).getFluid().getRegistryName().toString()
					+ containerHandler.getFluidInTank(0).getFluid().getRegistryName()
					+ containerHandler.getFluidInTank(0).getAmount();
		}
	}
}