package theking530.staticpower.items.fluidcapsule;

import java.util.List;

import javax.annotation.Nullable;

import mezz.jei.api.ingredients.subtypes.IIngredientSubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticpower.client.rendering.items.FluidCapsuleItemModel;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.StaticPowerTiers;
import theking530.staticpower.items.StaticPowerItem;
import theking530.staticpower.utilities.WorldUtilities;

public class FluidCapsule extends StaticPowerItem implements ICustomModelSupplier {
	public final ResourceLocation tier;

	public FluidCapsule(ResourceLocation tier) {
		super(new Properties().stacksTo(1).setNoRepair());
		this.tier = tier;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean showAdvanced) {
		FluidUtil.getFluidHandler(stack).ifPresent(fluidHandler -> {
			tooltip.add(Component.literal(ChatFormatting.WHITE.toString())
					.append(GuiTextUtilities.formatFluidToString(fluidHandler.getFluidInTank(0).getAmount(), fluidHandler.getTankCapacity(0))));
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
	public int getBarColor(ItemStack stack) {
		double hue = (170.0f / 360.0f) + (stack.getDamageValue()) * (40.0f / 360.0f);
		return Mth.hsvToRgb((float) hue, 1.0F, 1.0F);
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
			return Component.translatable(this.getDescriptionId(stack));
		}
		return Component.translatable(this.getDescriptionId(stack)).append(" (").append(containerHandler.getFluidInTank(0).getDisplayName()).append(")");
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
		ItemStack itemstack = pPlayer.getItemInHand(pHand);
		IFluidHandler fluidHandler = FluidUtil.getFluidHandler(itemstack).orElse(null);
		if (fluidHandler == null) {
			return InteractionResultHolder.fail(itemstack);
		}

		BlockHitResult blockhitresult = getPlayerPOVHitResult(pLevel, pPlayer, ClipContext.Fluid.SOURCE_ONLY);
		InteractionResultHolder<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onBucketUse(pPlayer, pLevel, itemstack, blockhitresult);
		if (ret != null)
			return ret;
		if (blockhitresult.getType() == HitResult.Type.MISS) {
			return InteractionResultHolder.pass(itemstack);
		} else if (blockhitresult.getType() != HitResult.Type.BLOCK) {
			return InteractionResultHolder.pass(itemstack);
		} else {
			BlockPos blockpos = blockhitresult.getBlockPos();
			Direction direction = blockhitresult.getDirection();
			BlockPos blockpos1 = blockpos.relative(direction);
			if (pLevel.mayInteract(pPlayer, blockpos) && pPlayer.mayUseItemAt(blockpos1, direction, itemstack)) {
				BlockState blockstate1 = pLevel.getBlockState(blockpos);
				if (pPlayer.isCrouching()) {
					if (blockstate1.getBlock() instanceof BucketPickup) {
						FluidStack targetStack = new FluidStack(pLevel.getFluidState(blockhitresult.getBlockPos()).getType(), 1000);
						if (!targetStack.isEmpty() && fluidHandler.fill(targetStack, FluidAction.SIMULATE) == 1000) {
							BucketPickup bucketpickup = (BucketPickup) blockstate1.getBlock();
							ItemStack itemstack1 = bucketpickup.pickupBlock(pLevel, blockpos, blockstate1);
							if (!itemstack1.isEmpty()) {
								pPlayer.awardStat(Stats.ITEM_USED.get(this));
								WorldUtilities.playBucketFillSound(targetStack, pPlayer, pLevel, blockpos);
								pLevel.gameEvent(pPlayer, GameEvent.FLUID_PICKUP, blockpos);
								fluidHandler.fill(targetStack, FluidAction.EXECUTE);
								if (!pLevel.isClientSide) {
									CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) pPlayer, itemstack1);
								}

								return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
							}
						}
					}
					return InteractionResultHolder.fail(itemstack);
				} else {
					BlockState blockstate = pLevel.getBlockState(blockpos);
					BlockPos blockpos2 = WorldUtilities.canBlockContainFluid(fluidHandler, pLevel, blockpos, blockstate) ? blockpos : blockpos1;
					if (WorldUtilities.tryPlaceFluid(fluidHandler.getFluidInTank(0), pPlayer, pLevel, blockpos2, blockhitresult)) {
						fluidHandler.drain(1000, FluidAction.EXECUTE);

						if (pPlayer instanceof ServerPlayer) {
							CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) pPlayer, blockpos2, itemstack);
						}

						pPlayer.awardStat(Stats.ITEM_USED.get(this));
						return InteractionResultHolder.sidedSuccess(itemstack, pLevel.isClientSide());
					} else {
						return InteractionResultHolder.fail(itemstack);
					}
				}
			} else {
				return InteractionResultHolder.fail(itemstack);
			}
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, BakedModel existingModel, ModelEvent.BakingCompleted event) {
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

			ResourceLocation itemRegistryName = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
			ResourceLocation fluidRegistryName = ForgeRegistries.FLUIDS.getKey(containerHandler.getFluidInTank(0).getFluid());

			return tier.toString() + itemRegistryName.toString() + fluidRegistryName.toString() + containerHandler.getFluidInTank(0).getAmount();
		}
	}
}