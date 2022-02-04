package theking530.staticpower.cables;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import theking530.api.wrench.RegularWrenchMode;
import theking530.api.wrench.SneakWrenchMode;
import theking530.staticcore.item.ICustomModelSupplier;
import theking530.staticcore.network.NetworkGUI;
import theking530.staticpower.blocks.tileentity.StaticPowerTileEntityBlock;
import theking530.staticpower.cables.CableBoundsHoverResult.CableBoundsHoverType;
import theking530.staticpower.cables.attachments.AbstractCableAttachment;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.tileentities.components.ComponentUtilities;
import theking530.staticpower.utilities.WorldUtilities;

public abstract class AbstractCableBlock extends StaticPowerTileEntityBlock implements ICustomModelSupplier {
	public static final Logger LOGGER = LogManager.getLogger(AbstractCableBlock.class);
	public final CableBoundsCache cableBoundsCache;
	public final float coverHoleSize;

	/**
	 * 
	 * @param name
	 * @param cableBoundsGenerator
	 * @param coverHoleSize        The size of the hole to render in a cover when
	 *                             this cable passes through a cover.
	 */
	public AbstractCableBlock(String name, CableBoundsCache cableBoundsGenerator, float coverHoleSize) {
		super(name, Block.Properties.of(Material.METAL).strength(1.5f).noOcclusion().requiresCorrectToolForDrops());
		cableBoundsCache = cableBoundsGenerator;
		this.coverHoleSize = coverHoleSize;
	}

	@Override
	public HasGuiType hasGuiScreen(BlockEntity tileEntity, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		return HasGuiType.NEVER;
	}

	@Deprecated
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return cableBoundsCache.getShape(state, worldIn, pos, context, false);
	}

	@Deprecated
	@Override
	public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return cableBoundsCache.getShape(state, worldIn, pos, context, true);
	}

	@Override
	public boolean hasModelOverride(BlockState state) {
		return true;
	}

	@Override
	public boolean shouldHaveFacingProperty() {
		return false;
	}

	@Override
	public InteractionResult onStaticPowerBlockActivated(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		// Get the component at the location.
		AbstractCableProviderComponent component = CableUtilities.getCableWrapperComponent(world, pos);
		if (component == null) {
			LOGGER.error(String.format("Encountered invalid cable provider component at position: %1$s when attempting to open the Attachment Gui.", pos));
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
					if (!world.isClientSide) {
						NetworkGUI.openGui((ServerPlayer) player, attachmentItem.getUIContainerProvider(attachment, component, hoveredDirection), buff -> {
							buff.writeInt(hoveredDirection.ordinal());
							buff.writeBlockPos(pos);
						});
					}
					return InteractionResult.CONSUME;
				}
			}
		}

		// IF we didn't return earlier, go to the super call.
		//hit.hitInfo = hoverResult;
		return super.onStaticPowerBlockActivated(state, world, pos, player, hand, hit);
	}

	@Override
	public InteractionResult sneakWrenchBlock(Player player, SneakWrenchMode mode, ItemStack wrench, Level world, BlockPos pos, Direction facing, boolean returnDrops) {
		// Drop the block.
		if (returnDrops && !world.isClientSide) {
			BlockState state = world.getBlockState(pos);
			List<ItemStack> stacks = Block.getDrops(state, (ServerLevel) world, pos, world.getBlockEntity(pos));
			for (ItemStack stack : stacks) {
				WorldUtilities.dropItem(world, pos, stack);
			}
			this.spawnAfterBreak(state, (ServerLevel) world, pos, wrench);
		}

		// Perform this on both the client and the server so the client updates any
		// render changes (any conected cables).
		world.setBlock(pos, Blocks.AIR.defaultBlockState(), 1 | 2);
		world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos), world.getBlockState(pos), 1 | 2, 512);

		return InteractionResult.SUCCESS;
	}

	@Override
	public InteractionResult wrenchBlock(Player player, RegularWrenchMode mode, ItemStack wrench, Level world, BlockPos pos, Direction facing, boolean returnDrops) {
		super.wrenchBlock(player, mode, wrench, world, pos, facing, returnDrops);
		// Get the cable component and make sure its valid.
		AbstractCableProviderComponent component = CableUtilities.getCableWrapperComponent(world, pos);
		if (component == null) {
			return InteractionResult.FAIL;
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

		// Refresh the cable on the server.
		if (!world.isClientSide) {
			CableNetworkManager.get(world).refreshCable(CableNetworkManager.get(world).getCable(pos));
		}

		return InteractionResult.SUCCESS;
	}

	@Override
	public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
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

		return super.getPickBlock(state, target, world, pos, player);
	}

	@Deprecated
	@Override
	public void spawnAfterBreak(BlockState state, ServerLevel worldIn, BlockPos pos, ItemStack stack) {
		// Get the cable provider if present.
		ComponentUtilities.getComponent(AbstractCableProviderComponent.class, worldIn.getBlockEntity(pos)).ifPresent(component -> {
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
				WorldUtilities.dropItem(worldIn, pos, drop);
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
}
