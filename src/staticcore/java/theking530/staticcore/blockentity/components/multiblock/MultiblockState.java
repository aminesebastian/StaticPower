package theking530.staticcore.blockentity.components.multiblock;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import theking530.staticcore.StaticCoreRegistries;
import theking530.staticcore.utilities.NBTUtilities;

public class MultiblockState {
	public record MultiblockStateEntry(BlockPos pos, BlockState blockState, char patternKey) {
		public CompoundTag serialize() {
			CompoundTag output = new CompoundTag();
			output.putLong("pos", pos.asLong());
			output.put("state", NbtUtils.writeBlockState(blockState));
			output.putByte("key", (byte) patternKey);
			return output;
		}

		public static MultiblockStateEntry fromNBT(CompoundTag nbt) {
			BlockPos pos = BlockPos.of(nbt.getLong("pos"));
			BlockState state = NbtUtils.readBlockState(nbt.getCompound("state"));
			char key = (char) nbt.getByte("key");
			return new MultiblockStateEntry(pos, state, key);
		}
	}

	public static final MultiblockState FAILED = new MultiblockState(null, BlockPos.ZERO, null,
			MultiBlockFormationStatus.FAILED);
	private final AbstractMultiblockPattern pattern;
	private final BlockPos initialPos;
	private final Direction facingDirection;
	private final MultiBlockFormationStatus status;
	private final TreeMap<BlockPos, MultiblockStateEntry> blocks;
	private BlockPos masterPos;

	public MultiblockState(AbstractMultiblockPattern pattern, BlockPos initialPos, Direction facingDirection,
			MultiBlockFormationStatus status) {
		this.pattern = pattern;
		this.facingDirection = facingDirection;
		this.initialPos = initialPos;
		this.masterPos = null;
		this.blocks = new TreeMap<>();
		this.status = status;
	}

	public AbstractMultiblockPattern getPattern() {
		return pattern;
	}

	public Collection<MultiblockStateEntry> getBlocks() {
		return blocks.values();
	}

	public boolean isWellFormed() {
		return pattern != null && status.isSuccessful() && facingDirection != null && !blocks.isEmpty();
	}

	public MultiBlockFormationStatus getStatus() {
		return status;
	}

	public BlockPos getInitialPos() {
		return initialPos;
	}

	public BlockPos getMasterPos() {
		return masterPos;
	}

	public Direction getFacingDirection() {
		return facingDirection;
	}

	public void addEntry(BlockPos pos, BlockState state, char patternKey, boolean canBeMaster) {
		blocks.put(pos, new MultiblockStateEntry(pos, state, patternKey));
		if (canBeMaster && masterPos == null) {
			masterPos = pos;
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(blocks, facingDirection, initialPos, masterPos, status);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MultiblockState other = (MultiblockState) obj;
		return Objects.equals(blocks, other.blocks) && facingDirection == other.facingDirection
				&& Objects.equals(initialPos, other.initialPos) && Objects.equals(masterPos, other.masterPos)
				&& Objects.equals(status, other.status);
	}

	@Override
	public String toString() {
		return "MultiblockState [initialPos=" + initialPos + ", facingDirection=" + facingDirection + ", status="
				+ status + ", blocks=" + blocks + ", masterPos=" + masterPos + "]";
	}

	public CompoundTag serialize() {
		CompoundTag output = new CompoundTag();
		if (!isWellFormed() || this == FAILED) {
			return output;
		}

		output.putString("pattern", StaticCoreRegistries.MultiblockTypes().getKey(pattern).toString());
		output.putLong("initialPos", initialPos.asLong());
		output.putLong("masterPos", masterPos.asLong());
		output.putByte("facing", (byte) facingDirection.ordinal());
		output.put("status", status.serialize());
		ListTag serializedBlocks = NBTUtilities.serialize(blocks.values(), (entry) -> {
			return entry.serialize();
		});
		output.put("blocks", serializedBlocks);
		return output;
	}

	public static MultiblockState deserialize(CompoundTag nbt) {
		if (nbt.isEmpty()) {
			return FAILED;
		}

		ResourceLocation patternKey = new ResourceLocation(nbt.getString("pattern"));
		AbstractMultiblockPattern pattern = StaticCoreRegistries.MultiblockTypes().getValue(patternKey);
		if (pattern == null) {
			return FAILED;
		}

		MultiBlockFormationStatus status = MultiBlockFormationStatus.deserialize(nbt.getCompound("status"));
		BlockPos initialPos = BlockPos.of(nbt.getLong("initialPos"));
		BlockPos masterPos = BlockPos.of(nbt.getLong("masterPos"));
		Direction facingDirection = Direction.values()[nbt.getByte("facing")];
		List<MultiblockStateEntry> entries = NBTUtilities.deserialize(nbt.getList("blocks", Tag.TAG_COMPOUND),
				(tag) -> {
					return MultiblockStateEntry.fromNBT((CompoundTag) tag);
				});

		MultiblockState newState = new MultiblockState(pattern, initialPos, facingDirection, status);
		for (MultiblockStateEntry entry : entries) {
			newState.addEntry(entry.pos(), entry.blockState(), entry.patternKey(), entry.pos().equals(masterPos));
		}
		return newState;
	}
}
