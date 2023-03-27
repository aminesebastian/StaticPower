package theking530.staticcore.block;

import javax.annotation.Nullable;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.network.NetworkHooks;
import theking530.api.IBreakSerializeable;
import theking530.api.wrench.RegularWrenchMode;
import theking530.api.wrench.SneakWrenchMode;
import theking530.staticcore.blockentity.BlockEntityBase;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent;
import theking530.staticcore.blockentity.components.control.sideconfiguration.SideConfigurationComponent.SideIncrementDirection;
import theking530.staticcore.world.WorldUtilities;

public abstract class StaticCoreBlockEntityBlock extends StaticCoreTieredBlock implements EntityBlock {
	protected enum HasGuiType {
		NEVER, ALWAYS, SNEAKING_ONLY;
	}

	private boolean shouldDropContentsOnBreak;

	protected StaticCoreBlockEntityBlock(CreativeModeTab tab) {
		this(tab, null, Block.Properties.of(Material.METAL).strength(3.5f, 5.0f).sound(SoundType.METAL));
	}

	protected StaticCoreBlockEntityBlock(CreativeModeTab tab, ResourceLocation tier) {
		this(tab, tier, Block.Properties.of(Material.METAL).strength(3.5f, 5.0f).sound(SoundType.METAL));
	}

	protected StaticCoreBlockEntityBlock(CreativeModeTab tab, Properties properties) {
		this(tab, null, properties);
	}

	protected StaticCoreBlockEntityBlock(CreativeModeTab tab, ResourceLocation tier, Properties properties) {
		super(tab, tier, properties);
		this.shouldDropContentsOnBreak = true;
	}

	@Override
	public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(world, pos, state, placer, stack);

		if (world.getBlockEntity(pos) != null) {
			BlockEntity te = world.getBlockEntity(pos);
			if (te instanceof IBreakSerializeable) {
				IBreakSerializeable.deserializeToTileEntity(world, pos, state, placer, stack);
			}
		}
	}

	public boolean shouldDropContentsOnBreak() {
		return shouldDropContentsOnBreak;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public RenderType getRenderType() {
		return RenderType.cutout();
	}

	@Override
	public InteractionResult onStaticPowerBlockActivated(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		BlockEntityBase te = (BlockEntityBase) world.getBlockEntity(pos);

		if (te != null) {
			// If we are able to fill this block entity from a held fluid container, or fill
			// a fluid container from the fluids in this container, leave early.
			if (fillFromHeldFluidContainer(te, world, pos, player, hand, hit)) {
				return InteractionResult.CONSUME;
			} else if (drainToHeldFluidContainer(te, world, pos, player, hand, hit)) {
				return InteractionResult.CONSUME;
			}

			HasGuiType guiType = hasGuiScreen(te, state, world, pos, player, hand, hit);
			// Ensure we meet the criteria for entering the GUI.
			if (guiType == HasGuiType.ALWAYS || (guiType == HasGuiType.SNEAKING_ONLY && player.isShiftKeyDown())) {
				if (!world.isClientSide) {
					enterGuiScreen(te, state, world, te.getContainerReferencedBlockPos(), player, hand, hit);
				}
				te.onGuiEntered(state, player, hand, hit);
				return InteractionResult.CONSUME;
			}
		}
		return InteractionResult.PASS;
	}

	@Override
	@Nullable
	public abstract BlockEntity newBlockEntity(final BlockPos pos, final BlockState state);

	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return BlockEntityBase::tick;
	}

	/**
	 * Server side only. This method givens the inheritor the opportunity to enter a
	 * GUI.By default, the standard GUI opening flow is followed calling openGui on
	 * {@link NetworkHooks}.
	 * 
	 * @param tileEntity
	 * @param state
	 * @param world
	 * @param pos
	 * @param player
	 * @param hand
	 * @param hit
	 * @return
	 */
	public void enterGuiScreen(BlockEntityBase tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (!world.isClientSide) {
			NetworkHooks.openScreen((ServerPlayer) player, tileEntity, pos);
		}
	}

	/**
	 * If the current activation situation matches the returned value here, it will
	 * cause the activation method to call
	 * {@link #enterGuiScreen(TileEntity, BlockState, World, BlockPos, PlayerEntity, Hand, BlockRayTraceResult)}
	 * and enter a GUI for the tile entity this block represents. OVerride that
	 * method if any additional checks must be performed when entering a Gui.
	 * 
	 * @param tileEntity
	 * @param state
	 * @param world
	 * @param pos
	 * @param player
	 * @param hand
	 * @param hit
	 * @return
	 */
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.NEVER;
	}

	@Override
	public InteractionResult wrenchBlock(Player player, RegularWrenchMode mode, ItemStack wrench, Level world, BlockPos pos, Direction facing, boolean returnDrops) {
		BlockEntityBase blockEntity = (BlockEntityBase) world.getBlockEntity(pos);
		if (blockEntity.hasComponentOfType(SideConfigurationComponent.class)) {
			blockEntity.getComponent(SideConfigurationComponent.class).modulateWorldSpaceSideMode(facing, SideIncrementDirection.FORWARD);
			return InteractionResult.SUCCESS;
		}
		return super.wrenchBlock(player, mode, wrench, world, pos, facing, returnDrops);
	}

	@Override
	public InteractionResult sneakWrenchBlock(Player player, SneakWrenchMode mode, ItemStack wrench, Level world, BlockPos pos, Direction facing, boolean returnDrops) {
		// If we're on the server and this machine has a tile entity of type
		// IBreakSerializeable.
		if (!world.isClientSide && world.getBlockEntity(pos) instanceof IBreakSerializeable) {
			if (((IBreakSerializeable) world.getBlockEntity(pos)).shouldSerializeWhenBroken(player)) {
				ItemStack machineStack = IBreakSerializeable.createItemDrop(this, player, world, pos);
				// Drop the item.
				WorldUtilities.dropItem(world, pos, machineStack);

				// Swap this block to air (break it).
				world.removeBlockEntity(pos);
				world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				return InteractionResult.SUCCESS;
			}
		}
		return super.sneakWrenchBlock(player, mode, wrench, world, pos, facing, returnDrops);
	}

	@Override
	public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
		BlockEntity tileEntity = blockAccess.getBlockEntity(pos);
		if (tileEntity instanceof BlockEntityBase) {
			return ((BlockEntityBase) tileEntity).getWeakPower(blockState, blockAccess, pos, side);
		}
		return 0;
	}

	@Override
	public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
		BlockEntity tileEntity = blockAccess.getBlockEntity(pos);
		if (tileEntity instanceof BlockEntityBase) {
			return ((BlockEntityBase) tileEntity).getStrongPower(blockState, blockAccess, pos, side);
		}
		return 0;
	}

	protected boolean fillFromHeldFluidContainer(BlockEntityBase blockEntity, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (world.isClientSide()) {
			return false;
		}
		// See if we're holding and item, and if so, see if it has a fluid that we can
		// put into the block entity.
		ItemStack heldItem = player.getItemInHand(hand);
		IFluidHandlerItem itemFluidContainer = FluidUtil.getFluidHandler(heldItem).orElse(null);
		IFluidHandler tank = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, hit.getDirection()).orElse(null);
		if (itemFluidContainer != null && tank != null) {
			FluidStack simulatedDrain = itemFluidContainer.drain(1000, FluidAction.SIMULATE);
			int filledAmount = tank.fill(simulatedDrain, FluidAction.EXECUTE);
			if (filledAmount > 0) {
				itemFluidContainer.drain(filledAmount, FluidAction.EXECUTE);
				WorldUtilities.playBucketEmptySound(simulatedDrain, player, world, pos);
				player.setItemInHand(hand, itemFluidContainer.getContainer());
				return true;
			}
		}
		return false;
	}

	protected boolean drainToHeldFluidContainer(BlockEntityBase blockEntity, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		if (world.isClientSide()) {
			return false;
		}

		// See if we're holding and item, and if so, see if it can be filled with fluid
		// from this machine.
		ItemStack heldItem = player.getItemInHand(hand);
		IFluidHandlerItem itemFluidContainer = FluidUtil.getFluidHandler(heldItem).orElse(null);
		IFluidHandler tank = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, hit.getDirection()).orElse(null);
		if (itemFluidContainer != null && tank != null) {
			FluidStack simulatedDrain = tank.drain(1000, FluidAction.SIMULATE);
			int simulatedFill = itemFluidContainer.fill(simulatedDrain, FluidAction.SIMULATE);
			if (simulatedFill > 0) {
				FluidStack actualDrain = tank.drain(simulatedFill, FluidAction.EXECUTE);
				itemFluidContainer.fill(actualDrain, FluidAction.EXECUTE);
				WorldUtilities.playBucketFillSound(simulatedDrain, player, world, pos);
				player.setItemInHand(hand, itemFluidContainer.getContainer());
				return true;
			}
		}
		return false;
	}
}