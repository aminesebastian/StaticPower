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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
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
			return new TranslatableComponent(this.getDescriptionId(stack));
		}
		return new TranslatableComponent(this.getDescriptionId(stack)).append(" (").append(containerHandler.getFluidInTank(0).getDisplayName()).append(")");
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
								playFillSound(targetStack, pPlayer, pLevel, blockpos);
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
					BlockPos blockpos2 = canBlockContainFluid(fluidHandler, pLevel, blockpos, blockstate) ? blockpos : blockpos1;
					if (this.placeFluid(fluidHandler, pPlayer, pLevel, blockpos2, blockhitresult)) {
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

	public boolean placeFluid(IFluidHandler fluidHandler, @Nullable Player p_150716_, Level p_150717_, BlockPos p_150718_, @Nullable BlockHitResult p_150719_) {
		if (fluidHandler.drain(1000, FluidAction.SIMULATE).getAmount() != 1000) {
			return false;
		}

		Fluid content = fluidHandler.getFluidInTank(0).getFluid();
		BlockState blockstate = p_150717_.getBlockState(p_150718_);
		Block block = blockstate.getBlock();
		Material material = blockstate.getMaterial();
		boolean flag = blockstate.canBeReplaced(content);
		boolean flag1 = blockstate.isAir() || flag || block instanceof LiquidBlockContainer && ((LiquidBlockContainer) block).canPlaceLiquid(p_150717_, p_150718_, blockstate, content);
		if (!flag1) {
			return p_150719_ != null && placeFluid(fluidHandler, p_150716_, p_150717_, p_150719_.getBlockPos().relative(p_150719_.getDirection()), (BlockHitResult) null);
		} else if (p_150717_.dimensionType().ultraWarm() && content.is(FluidTags.WATER)) {
			int i = p_150718_.getX();
			int j = p_150718_.getY();
			int k = p_150718_.getZ();
			p_150717_.playSound(p_150716_, p_150718_, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (p_150717_.random.nextFloat() - p_150717_.random.nextFloat()) * 0.8F);

			for (int l = 0; l < 8; ++l) {
				p_150717_.addParticle(ParticleTypes.LARGE_SMOKE, (double) i + Math.random(), (double) j + Math.random(), (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
			}
			fluidHandler.drain(1000, FluidAction.EXECUTE);
			return true;
		} else if (block instanceof LiquidBlockContainer && ((LiquidBlockContainer) block).canPlaceLiquid(p_150717_, p_150718_, blockstate, content)) {
			((LiquidBlockContainer) block).placeLiquid(p_150717_, p_150718_, blockstate, ((FlowingFluid) content).getSource(false));
			this.playEmptySound(fluidHandler, p_150716_, p_150717_, p_150718_);
			fluidHandler.drain(1000, FluidAction.EXECUTE);
			return true;
		} else {
			if (!p_150717_.isClientSide && flag && !material.isLiquid()) {
				p_150717_.destroyBlock(p_150718_, true);
			}

			if (!p_150717_.setBlock(p_150718_, content.defaultFluidState().createLegacyBlock(), 11) && !blockstate.getFluidState().isSource()) {
				return false;
			} else {
				this.playEmptySound(fluidHandler, p_150716_, p_150717_, p_150718_);
				fluidHandler.drain(1000, FluidAction.EXECUTE);
				return true;
			}
		}
	}

	protected void playFillSound(FluidStack incomingFluid, @Nullable Player pPlayer, LevelAccessor pLevel, BlockPos pPos) {
		Fluid content = incomingFluid.getFluid();
		SoundEvent soundevent = content.getAttributes().getFillSound();
		if (soundevent == null)
			soundevent = content.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_EMPTY_LAVA;
		pLevel.playSound(pPlayer, pPos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
		pLevel.gameEvent(pPlayer, GameEvent.FLUID_PLACE, pPos);
	}

	protected void playEmptySound(IFluidHandler fluidHandler, @Nullable Player pPlayer, LevelAccessor pLevel, BlockPos pPos) {
		Fluid content = fluidHandler.getFluidInTank(0).getFluid();
		SoundEvent soundevent = content.getAttributes().getEmptySound();
		if (soundevent == null)
			soundevent = content.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
		pLevel.playSound(pPlayer, pPos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
		pLevel.gameEvent(pPlayer, GameEvent.FLUID_PLACE, pPos);
	}

	private boolean canBlockContainFluid(IFluidHandler fluidHandler, Level worldIn, BlockPos posIn, BlockState blockstate) {
		Fluid content = fluidHandler.getFluidInTank(0).getFluid();
		return blockstate.getBlock() instanceof LiquidBlockContainer && ((LiquidBlockContainer) blockstate.getBlock()).canPlaceLiquid(worldIn, posIn, blockstate, content);
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