package theking530.staticcore.blockentity.components.multiblock.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.level.BlockEvent;
import theking530.staticcore.StaticCore;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.blockentity.components.ComponentUtilities;
import theking530.staticcore.blockentity.components.multiblock.AbstractMultiblockPattern;
import theking530.staticcore.blockentity.components.multiblock.IMultiblockBlock;
import theking530.staticcore.blockentity.components.multiblock.MultiblockBlockStateProperties;
import theking530.staticcore.blockentity.components.multiblock.MultiblockComponent;
import theking530.staticcore.blockentity.components.multiblock.MultiblockState;
import theking530.staticcore.blockentity.components.multiblock.MultiblockState.MultiblockStateEntry;
import theking530.staticcore.blockentity.components.multiblock.PacketSyncMultiblockStates;
import theking530.staticcore.network.StaticCoreMessageHandler;
import theking530.staticcore.utilities.NBTUtilities;

public class ServerMultiblockManager extends SavedData implements IMultiblockManager {
	private Map<BlockPos, MultiblockState> blocksToMasterMap;
	private Set<MultiblockState> uniqueStates;
	private ServerLevel level;
	private boolean shouldSyncClients;

	public ServerMultiblockManager(ServerLevel level) {
		this.level = level;
		this.blocksToMasterMap = new HashMap<>();
		this.uniqueStates = new HashSet<>();
	}

	public void tick() {
		if (shouldSyncClients) {
			syncToClients();
			StaticCore.LOGGER.debug(String.format("Syncing multiblock states for level: %1$s.",
					level.dimensionType().effectsLocation()));
		}

		List<MultiblockState> statesToRemove = new ArrayList<>();
		for (MultiblockState existingState : uniqueStates) {
			if (level.isLoaded(existingState.getInitialPos())) {
				MultiblockState newState = existingState.getPattern().isStateStillValid(level, existingState);
				if (!newState.equals(existingState)) {
					onMultiblockStateChanged(existingState, newState, level);
				}
				if (!newState.isWellFormed()) {
					statesToRemove.add(existingState);
				}
			}
		}

		for (MultiblockState toRemove : statesToRemove) {
			multiblockRemoved(toRemove);
		}
	}

	@Override
	public boolean containsBlock(BlockPos pos) {
		return this.blocksToMasterMap.containsKey(pos);
	}

	@Override
	public MultiblockState getMultiblockState(BlockPos pos) {
		return this.blocksToMasterMap.getOrDefault(pos, null);
	}

	public void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		for (ResourceLocation id : StaticCoreRegistries.MultiblockTypes().getKeys()) {
			AbstractMultiblockPattern pattern = StaticCoreRegistries.MultiblockTypes().getValue(id);
			if (pattern.isValidBlock(event.getState())) {
				MultiblockState state = pattern.checkWellFormed(event.getEntity().getLevel(), event.getPos());
				if (state.isWellFormed()) {
					multiblockCreated(state);
					break;
				}
			}
		}
	}

	public void onBlockRightClicked(RightClickBlock event) {
		MultiblockState mbState = getMultiblockState(event.getPos());
		if (mbState == null || !mbState.isWellFormed()) {
			return;
		}
		mbState.getPattern().onMemberBlockRightClicked(mbState, event);
	}

	public void onMultiblockMasterBroken(BlockPos masterPos) {
		if (!blocksToMasterMap.containsKey(masterPos)) {
			return;
		}

		multiblockRemoved(blocksToMasterMap.get(masterPos));
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		ListTag serializedStates = NBTUtilities.serialize(uniqueStates, (entry) -> {
			return entry.serialize();
		});
		tag.put("states", serializedStates);

		return tag;
	}

	public static ServerMultiblockManager load(CompoundTag tag, String name, ServerLevel level) {
		ServerMultiblockManager output = new ServerMultiblockManager(level);
		List<MultiblockState> states = NBTUtilities.deserialize(tag.getList("states", Tag.TAG_COMPOUND), (stateTag) -> {
			return MultiblockState.deserialize((CompoundTag) stateTag);
		});

		for (MultiblockState state : states) {
			for (MultiblockStateEntry entry : state.getBlocks()) {
				output.blocksToMasterMap.put(entry.pos(), state);
			}
			output.uniqueStates.add(state);
		}

		return output;
	}

	@Override
	public void setDirty(boolean dirty) {
		super.setDirty(dirty);
		shouldSyncClients = dirty;
	}

	protected void multiblockCreated(MultiblockState state) {
		if (state == null) {
			StaticCore.LOGGER.warn("Attempted to add a null multiblock state to the manager.");
			return;
		}

		uniqueStates.add(state);
		for (MultiblockStateEntry entry : state.getBlocks()) {
			blocksToMasterMap.put(entry.pos(), state);
		}

		for (MultiblockStateEntry entry : state.getBlocks()) {
			// Update the block state.
			BlockState blockState = entry.blockState();
			if (blockState.hasProperty(MultiblockBlockStateProperties.IS_IN_VALID_MULTIBLOCK)) {
				BlockState newState = blockState.setValue(MultiblockBlockStateProperties.IS_IN_VALID_MULTIBLOCK, true);

				if (entry.pos().equals(state.getMasterPos())) {
					newState = newState.setValue(MultiblockBlockStateProperties.IS_MASTER, true);
				}

				newState = state.getPattern().modifyBlockStateOnFormed(newState, entry.pos(), level);
				level.setBlock(entry.pos(), newState, 3);
			}

			if (blockState.getBlock() instanceof IMultiblockBlock) {
				IMultiblockBlock mbBlock = (IMultiblockBlock) blockState.getBlock();
				mbBlock.addedToMultiblock(level, entry.pos(), state);
			}
			getMultiblockComponent(entry.pos()).ifPresent((component) -> {
				component.addedToMultiblock(state);
			});
		}

		state.getPattern().onWellFormedOnPlaceEvent(state, level);
		setDirty(true);
	}

	protected void onMultiblockStateChanged(MultiblockState oldState, MultiblockState newState, Level level) {
		for (MultiblockStateEntry entry : oldState.getBlocks()) {
			BlockState blockState = entry.blockState();
			if (blockState.getBlock() instanceof IMultiblockBlock) {
				IMultiblockBlock mbBlock = (IMultiblockBlock) blockState.getBlock();
				mbBlock.onMultiblockStateChanged(level, entry.pos(), oldState, newState);
			}
			getMultiblockComponent(entry.pos()).ifPresent((component) -> {
				component.onMultiblockStateChanged(oldState, newState);
			});
		}
		oldState.getPattern().onMultiblockStateChanged(oldState, newState, level);
	}

	protected void multiblockRemoved(MultiblockState state) {
		if (state == null) {
			StaticCore.LOGGER.warn("Attempted to remove a null multiblock state from the manager.");
			return;
		}

		uniqueStates.remove(state);
		for (MultiblockStateEntry entry : state.getBlocks()) {
			blocksToMasterMap.remove(entry.pos());
		}

		for (MultiblockStateEntry entry : state.getBlocks()) {
			// Update the block state.
			BlockState existingState = level.getBlockState(entry.pos());
			if (existingState.hasProperty(MultiblockBlockStateProperties.IS_IN_VALID_MULTIBLOCK)) {
				BlockState newState = existingState.setValue(MultiblockBlockStateProperties.IS_IN_VALID_MULTIBLOCK,
						false);

				if (entry.pos().equals(state.getMasterPos())) {
					newState = newState.setValue(MultiblockBlockStateProperties.IS_MASTER, false);
				}

				newState = state.getPattern().modifyBlockStateOnBroken(newState, entry.pos(), level);

				level.setBlock(entry.pos(), newState, 3);
			}

			BlockState blockState = entry.blockState();
			if (blockState.getBlock() instanceof IMultiblockBlock) {
				IMultiblockBlock mbBlock = (IMultiblockBlock) blockState.getBlock();
				mbBlock.onMultiblockStateChanged(level, entry.pos(), state, MultiblockState.FAILED);
			}
			getMultiblockComponent(entry.pos()).ifPresent((component) -> {
				component.onRemovedFromMultiblock(state);
			});
		}

		state.getPattern().onMultiblockBroken(state, level);
		setDirty(true);
	}

	protected Optional<MultiblockComponent> getMultiblockComponent(BlockPos pos) {
		BlockEntity be = level.getBlockEntity(pos);
		if (be == null) {
			return Optional.empty();
		}

		return ComponentUtilities.getComponent(MultiblockComponent.class, be);
	}

	protected void syncToClients() {
		StaticCoreMessageHandler.sendToAllPlayers(StaticCoreMessageHandler.MAIN_PACKET_CHANNEL,
				new PacketSyncMultiblockStates(uniqueStates));
		shouldSyncClients = false;
	}
}
