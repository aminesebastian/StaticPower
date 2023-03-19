package theking530.staticcore.cablenetwork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.util.ForgeSoundType;
import theking530.api.wrench.RegularWrenchMode;
import theking530.api.wrench.SneakWrenchMode;
import theking530.staticcore.StaticCore;
import theking530.staticcore.block.StaticCoreBlockEntityBlock;
import theking530.staticcore.blockentity.components.ComponentUtilities;
import theking530.staticcore.cablenetwork.CableBoundsHoverResult.CableBoundsHoverType;
import theking530.staticcore.cablenetwork.attachment.AbstractCableAttachment;
import theking530.staticcore.cablenetwork.data.CableConnectionState.CableConnectionType;
import theking530.staticcore.client.ICustomModelProvider;
import theking530.staticcore.network.NetworkGUI;
import theking530.staticcore.utilities.IBlockItemCreativeTabProvider;
import theking530.staticcore.world.WorldUtilities;

public abstract class AbstractCableBlock extends StaticCoreBlockEntityBlock implements ICustomModelProvider, IBlockItemCreativeTabProvider {
	public static final ForgeSoundType METAL_CABLE = new ForgeSoundType(1.0F, 0.75F, () -> SoundEvents.COPPER_BREAK, () -> SoundEvents.COPPER_STEP, () -> SoundEvents.COPPER_PLACE,
			() -> SoundEvents.COPPER_HIT, () -> SoundEvents.COPPER_FALL);
	public static final ForgeSoundType CLOTH_CABLE = new ForgeSoundType(1.0F, 1.0F, () -> SoundEvents.WOOL_BREAK, () -> SoundEvents.WOOL_STEP, () -> SoundEvents.WOOL_PLACE, () -> SoundEvents.WOOL_HIT,
			() -> SoundEvents.WOOL_FALL);
	public static final Map<Direction, EnumProperty<CableConnectionType>> CONNECTION_TYPES = new HashMap<>();
	static {
		for (Direction dir : Direction.values()) {
			CONNECTION_TYPES.put(dir, EnumProperty.create(dir.getName(), CableConnectionType.class));
		}
	}

	public final CableBoundsCache cableBoundsCache;
	public final float coverHoleSize;

	public AbstractCableBlock(CreativeModeTab tab, CableBoundsCache cableBoundsCache, float coverHoleSize) {
		this(tab, null, cableBoundsCache, coverHoleSize);
	}

	/**
	 * 
	 * @param name
	 * @param cableBoundsGenerator
	 * @param coverHoleSize        The size of the hole to render in a cover when
	 *                             this cable passes through a cover.
	 */
	public AbstractCableBlock(CreativeModeTab tab, ResourceLocation tier, CableBoundsCache cableBoundsCache, float coverHoleSize) {
		super(tab, tier, Block.Properties.of(Material.METAL).sound(METAL_CABLE).strength(1.5f).noOcclusion().requiresCorrectToolForDrops());
		this.cableBoundsCache = cableBoundsCache;
		this.coverHoleSize = coverHoleSize;
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.NEVER;
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return cableBoundsCache.getShape(state, worldIn, pos, context, false);
	}

	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return cableBoundsCache.getShape(state, worldIn, pos, context, true);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
		return !state.getValue(BlockStateProperties.WATERLOGGED);
	}

	@Override
	public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type) {
		return false;
	}

	protected boolean canBeWaterlogged() {
		return true;
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	public InteractionResult onStaticPowerBlockActivated(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		// Get the component at the location.
		AbstractCableProviderComponent component = CableUtilities.getCableWrapperComponent(world, pos);
		if (component == null) {
			StaticCore.LOGGER.error(String.format("Encountered invalid cable provider component at position: %1$s when attempting to open the Attachment Gui.", pos));
			return InteractionResult.FAIL;
		}

		// Get the attachment side that is hovered (if any).
		CableBoundsHoverResult hoverResult = cableBoundsCache.getHoveredAttachmentOrCover(pos, player);
		if (!hoverResult.isEmpty()) {
			Direction hoveredDirection = cableBoundsCache.getHoveredAttachmentOrCover(pos, player).direction;

			if (hoveredDirection != null && component.hasAttachment(hoveredDirection)) {
				// Get the attachment on the hovered side.
				ItemStack attachment = component.getAttachment(hoveredDirection);

				// Get the attachment's item.
				AbstractCableAttachment attachmentItem = (AbstractCableAttachment) attachment.getItem();

				// If the item requests a GUI, open it.
				if (attachmentItem.hasGui(attachment)) {
					if (!world.isClientSide()) {
						NetworkGUI.openScreen((ServerPlayer) player, attachmentItem.getUIContainerProvider(attachment, component, hoveredDirection), buff -> {
							buff.writeInt(hoveredDirection.ordinal());
							buff.writeBlockPos(pos);
						});
					}
					return InteractionResult.CONSUME;
				}
			}
		}

		// IF we didn't return earlier, go to the super call.
		return super.onStaticPowerBlockActivated(state, world, pos, player, hand, hit);
	}

	@Override
	public InteractionResult sneakWrenchBlock(Player player, SneakWrenchMode mode, ItemStack wrench, Level world, BlockPos pos, Direction facing, boolean returnDrops) {
		if (world.isClientSide()) {
			return super.sneakWrenchBlock(player, mode, wrench, world, pos, facing, returnDrops);
		}

		// Drop the block.
		if (returnDrops) {
			BlockState state = world.getBlockState(pos);
			List<ItemStack> stacks = Block.getDrops(state, (ServerLevel) world, pos, world.getBlockEntity(pos));
			for (ItemStack stack : stacks) {
				WorldUtilities.dropItem(world, pos, stack);
			}
			this.spawnAfterBreak(state, (ServerLevel) world, pos, wrench, true);

			// Perform this on both the client and the server so the client updates any
			// render changes (any conected cables).
			world.setBlock(pos, Blocks.AIR.defaultBlockState(), 1 | 2);
			world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos), world.getBlockState(pos), 1 | 2, 512);
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult wrenchBlock(Player player, RegularWrenchMode mode, ItemStack wrench, Level world, BlockPos pos, Direction facing, boolean returnDrops) {
		if (world.isClientSide()) {
			return super.wrenchBlock(player, mode, wrench, world, pos, facing, returnDrops);
		}

		// Get the cable component and make sure its valid.
		AbstractCableProviderComponent component = CableUtilities.getCableWrapperComponent(world, pos);
		if (component == null) {
			return super.wrenchBlock(player, mode, wrench, world, pos, facing, returnDrops);
		}

		// Check for the hover result.
		CableBoundsHoverResult hoverResult = cableBoundsCache.getHoveredAttachmentOrCover(pos, player);

		// If non null, check for any attached cover or attachment.
		if (!hoverResult.isEmpty()) {
			Direction hoveredDirection = cableBoundsCache.getHoveredAttachmentOrCover(pos, player).direction;

			// Remove the attachment on that side if there is one.
			if (hoverResult.type == CableBoundsHoverType.ATTACHED_ATTACHMENT) {
				ItemStack output = component.removeAttachment(hoveredDirection);
				if (!output.isEmpty()) {
					WorldUtilities.dropItem(world, pos, output, 1);
					return InteractionResult.SUCCESS;
				}
			} else if (hoverResult.type == CableBoundsHoverType.ATTACHED_COVER) {
				// Now also remove the cover if there is one.
				ItemStack output = component.removeCover(hoveredDirection);
				if (!output.isEmpty()) {
					WorldUtilities.dropItem(world, pos, output, 1);
					return InteractionResult.SUCCESS;
				}
			}
		}

		// If we didnt return earlier, we probably hit the cable itseelf, lets see if we
		// can disable or enabled part of it.
		Direction hitSide = !hoverResult.isEmpty() ? hoverResult.direction : facing;
		component.setSideDisabledState(hitSide, !component.isSideDisabled(hitSide));

		// Update the cable opposite from the side we just toggled if a cable exists
		// there.
		AbstractCableProviderComponent oppositeComponent = CableUtilities.getCableWrapperComponent(world, pos.relative(hitSide));
		if (oppositeComponent != null) {
			oppositeComponent.setSideDisabledState(hitSide.getOpposite(), component.isSideDisabled(hitSide));
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
		// Get the attachment side we're hovering.
		CableBoundsHoverResult hoverResult = cableBoundsCache.getHoveredAttachmentOrCover(pos, player);

		if (!hoverResult.isEmpty()) {
			AbstractCableProviderComponent component = CableUtilities.getCableWrapperComponent(world, pos);
			if (hoverResult.type == CableBoundsHoverType.ATTACHED_ATTACHMENT) {
				if (component.hasAttachment(hoverResult.direction)) {
					ItemStack attachment = component.getAttachment(hoverResult.direction);

					if (!attachment.isEmpty()) {
						// Check to see if this attachment should appear as a pick block.
						if (((AbstractCableAttachment) attachment.getItem()).shouldAppearOnPickBlock(attachment)) {
							// Make a clean version of attachment with clear inventory.
							ItemStack cleanAttachment = new ItemStack(attachment.getItem());
							return cleanAttachment.copy();
						}
					}
				}
			} else if (hoverResult.type == CableBoundsHoverType.ATTACHED_COVER) {
				if (component.hasCover(hoverResult.direction)) {
					ItemStack cover = component.getCover(hoverResult.direction);
					if (!cover.isEmpty()) {
						return cover.copy();
					}
				}
			}
		}

		return super.getCloneItemStack(state, target, world, pos, player);
	}

	@Deprecated
	@Override
	public void spawnAfterBreak(BlockState state, ServerLevel level, BlockPos pos, ItemStack stack, boolean brokenByPlayer) {
		// Get the cable provider if present.
		ComponentUtilities.getComponent(AbstractCableProviderComponent.class, level.getBlockEntity(pos)).ifPresent(component -> {
			// Allocate a container for the additional drops.
			List<ItemStack> additionalDrops = new ArrayList<ItemStack>();

			// Check the attachments on all sides, and if there is one, get the additional
			// drops.
			for (Direction dir : Direction.values()) {
				if (component.hasAttachment(dir)) {
					ItemStack attachment = component.getAttachment(dir);
					AbstractCableAttachment attachmentItem = (AbstractCableAttachment) attachment.getItem();
					attachmentItem.getAdditionalDrops(attachment, component, additionalDrops);
				}
			}

			// Drop the additional drops.
			for (ItemStack drop : additionalDrops) {
				WorldUtilities.dropItem(level, pos, drop);
			}
		});
	}

	protected boolean isDisabledOnSide(LevelAccessor world, BlockPos pos, Direction direction) {
		AbstractCableProviderComponent cableComponent = CableUtilities.getCableWrapperComponent(world, pos);
		if (cableComponent != null) {
			return cableComponent.isSideDisabled(direction);
		}
		return true;
	}

	@Override
	protected BlockState getDefaultStateForRegistration() {
		BlockState superCall = super.getDefaultStateForRegistration();
		for (Direction dir : Direction.values()) {
			superCall = superCall.setValue(CONNECTION_TYPES.get(dir), CableConnectionType.NONE);
		}
		return superCall;
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		for (Direction dir : Direction.values()) {
			builder.add(CONNECTION_TYPES.get(dir));
		}
	}
}