package theking530.staticpower.blockentities.nonpowered.tank;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticcore.utilities.SDMath;
import theking530.staticpower.StaticPowerConfig;
import theking530.staticpower.blocks.StaticPowerItemBlock;
import theking530.staticpower.blocks.StaticPowerItemBlockCustomRenderer;
import theking530.staticpower.blocks.tileentity.StaticPowerBlockEntityBlock;
import theking530.staticpower.client.StaticPowerSprites;
import theking530.staticpower.client.rendering.blocks.TankMachineBakedModel;
import theking530.staticpower.client.rendering.items.dynamic.ItemTankSpecialRenderer;
import theking530.staticpower.client.utilities.GuiTextUtilities;
import theking530.staticpower.data.StaticPowerTiers;

public class BlockTank extends StaticPowerBlockEntityBlock implements ICustomModelSupplier {

	public BlockTank(ResourceLocation tier) {
		super(tier, Block.Properties.of(Material.METAL).strength(3.5f, 5.0f).sound(SoundType.METAL).noOcclusion());
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public void getTooltip(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, boolean isShowingAdvanced) {
		tooltip.add(Component.literal(ChatFormatting.GREEN.toString() + "� Capacity ").append(GuiTextUtilities.formatFluidToString(
				SDMath.multiplyRespectingOverflow(StaticPowerConfig.getTier(tier).defaultTankCapacity.get(), BlockEntityTank.MACHINE_TANK_CAPACITY_MULTIPLIER))));

		// Check to see if the stack has the serialized nbt.If it does, add the stored
		// amount of fluid to the tooltip.
		if (stack.hasTag() && stack.getTag().contains("SerializableNbt")) {
			// If it does, get the tank tag and then the subsequent fluid tag.
			CompoundTag tankTag = stack.getTag().getCompound("SerializableNbt").getCompound("FluidTank");
			CompoundTag fluidTank = tankTag.getCompound("fluidStorage");
			FluidStack fluid = FluidStack.loadFluidStackFromNBT(fluidTank.getCompound("tank"));
			tooltip.add(Component.literal(ChatFormatting.AQUA.toString() + "� Stored ").append(GuiTextUtilities.formatFluidToString(fluid.getAmount())));
		}
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.ALWAYS;
	}

	@Override
	public InteractionResult onStaticPowerBlockActivated(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		// Check if the player is holding anything.
		if (!player.getItemInHand(hand).isEmpty()) {
			// Get the held item and a pointer to the cauldron.
			ItemStack heldItem = player.getItemInHand(hand);
			BlockEntityTank cauldron = (BlockEntityTank) world.getBlockEntity(pos);
			FluidActionResult actionResult = null;
			boolean shouldManuallyReplace = heldItem.getCount() == 1;

			// If the cauldron already has contents, attempt to fill the held item,
			// otherwise, fill the cauldron.
			if (cauldron.fluidTankComponent.getFluidAmount() > 0) {
				actionResult = FluidUtil.tryFillContainerAndStow(heldItem, cauldron.fluidTankComponent,
						player.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null), 1000, player, true);
			} else {
				actionResult = FluidUtil.tryEmptyContainerAndStow(heldItem, cauldron.fluidTankComponent,
						player.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null), 1000, player, true);
			}

			// Check what happened.
			if (actionResult.isSuccess()) {
				if (shouldManuallyReplace) {
					player.setItemInHand(hand, actionResult.getResult());
				}
				return InteractionResult.SUCCESS;
			}
		}
		return super.onStaticPowerBlockActivated(state, world, pos, player, hand, hit);

	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	public BlockItem getItemBlock() {
		if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
			return new StaticPowerItemBlock(this);
		} else {
			return new StaticPowerItemBlockCustomRenderer(this, ItemTankSpecialRenderer::new);
		}
	}

	@Override
	public Component getDisplayName(ItemStack stack) {
		// Check to see if the stack has the serialized nbt.
		if (stack.hasTag() && stack.getTag().contains("SerializableNbt")) {
			// If it does, get the tank tag and then the subsequent fluid tag.
			CompoundTag tankTag = stack.getTag().getCompound("SerializableNbt").getCompound("FluidTank");
			CompoundTag fluidTank = tankTag.getCompound("fluidStorage");
			FluidStack fluid = FluidStack.loadFluidStackFromNBT(fluidTank.getCompound("tank"));
			if (!fluid.isEmpty()) {
				return Component.translatable(getDescriptionId()).append(" (").append(Component.translatable(fluid.getTranslationKey()).append(")"));
			}
		}
		return Component.translatable(getDescriptionId());
	}

	@Override
	public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
		BlockEntity te = world.getBlockEntity(pos);
		if (te instanceof BlockEntityTank) {
			FluidStack fluid = ((BlockEntityTank) te).fluidTankComponent.getFluid();
			return fluid.getFluid().getFluidType().getLightLevel();
		}
		return super.getLightEmission(state, world, pos);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BakedModel getModelOverride(BlockState state, BakedModel existingModel, ModelEvent.BakingCompleted event) {
		if (tier == StaticPowerTiers.IRON) {
			return new TankMachineBakedModel(existingModel, StaticPowerSprites.IRON_TANK);
		} else if (tier == StaticPowerTiers.BASIC) {
			return new TankMachineBakedModel(existingModel, StaticPowerSprites.BASIC_TANK);
		} else if (tier == StaticPowerTiers.ADVANCED) {
			return new TankMachineBakedModel(existingModel, StaticPowerSprites.ADVANCED_TANK);
		} else if (tier == StaticPowerTiers.STATIC) {
			return new TankMachineBakedModel(existingModel, StaticPowerSprites.STATIC_TANK);
		} else if (tier == StaticPowerTiers.ENERGIZED) {
			return new TankMachineBakedModel(existingModel, StaticPowerSprites.ENERGIZED_TANK);
		} else if (tier == StaticPowerTiers.LUMUM) {
			return new TankMachineBakedModel(existingModel, StaticPowerSprites.LUMUM_TANK);
		} else if (tier == StaticPowerTiers.CREATIVE) {
			return new TankMachineBakedModel(existingModel, StaticPowerSprites.CREATIVE_TANK);
		}
		return null;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.cutout();
	}

	@Override
	public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
		if (tier == StaticPowerTiers.IRON) {
			return BlockEntityTank.TYPE_IRON.create(pos, state);
		} else if (tier == StaticPowerTiers.BASIC) {
			return BlockEntityTank.TYPE_BASIC.create(pos, state);
		} else if (tier == StaticPowerTiers.ADVANCED) {
			return BlockEntityTank.TYPE_ADVANCED.create(pos, state);
		} else if (tier == StaticPowerTiers.STATIC) {
			return BlockEntityTank.TYPE_STATIC.create(pos, state);
		} else if (tier == StaticPowerTiers.ENERGIZED) {
			return BlockEntityTank.TYPE_ENERGIZED.create(pos, state);
		} else if (tier == StaticPowerTiers.LUMUM) {
			return BlockEntityTank.TYPE_LUMUM.create(pos, state);
		} else if (tier == StaticPowerTiers.CREATIVE) {
			return BlockEntityTank.TYPE_CREATIVE.create(pos, state);
		}
		return null;
	}
}
