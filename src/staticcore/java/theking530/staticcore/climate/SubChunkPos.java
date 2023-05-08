package theking530.staticcore.climate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.ChunkPos;

public record SubChunkPos(byte x, byte y, byte z) {

	public static final int SUB_CHUNK_SIZE = 8;

	public SubChunkPos(byte x, byte y, byte z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public SubChunkPos(int x, int y, int z) {
		this((byte) x, (byte) y, (byte) z);
	}

	public byte getX() {
		return x;
	}

	public byte getY() {
		return y;
	}

	public byte getZ() {
		return z;
	}

	public SubChunkPos relative(Direction p_175592_) {
		return this.relative(p_175592_, 1);
	}

	public SubChunkPos relative(Direction direction, int distance) {
		return distance == 0 ? this
				: new SubChunkPos(getX() + direction.getStepX() * distance, getY() + direction.getStepY() * distance,
						getZ() + direction.getStepZ() * distance);
	}

	public BlockPos getCenterBlockPosRelativeTo(ChunkPos chunk) {
		int centerOffset = SUB_CHUNK_SIZE / 2;
		int centerX = (chunk.x * SUB_CHUNK_SIZE) + chunk.getBlockX(centerOffset);
		int centerZ = (chunk.z * SUB_CHUNK_SIZE) + chunk.getBlockZ(centerOffset);
		int centerY = y * SUB_CHUNK_SIZE;

		return new BlockPos(centerX, centerY, centerZ);
	}

	public int asInt() {
		return (x & 0xff) << 16 | (y & 0xff) << 8 | (z & 0xff);
	}

	public static SubChunkPos of(int packedPos) {
		byte x = (byte) (packedPos >> 16);
		byte y = (byte) (packedPos >> 8);
		byte z = (byte) (packedPos);
		return new SubChunkPos(x, y, z);
	}

	public static SubChunkPos of(ChunkPos chunk, BlockPos pos) {
		int chunkX = chunk.getBlockX(0);
		int chunkZ = chunk.getBlockZ(0);

		int blockXOffset = pos.getX() - chunkX;
		int blockZOffset = pos.getZ() - chunkZ;

		return new SubChunkPos(blockXOffset / SUB_CHUNK_SIZE, blockZOffset / SUB_CHUNK_SIZE,
				pos.getY() / SUB_CHUNK_SIZE);
	}

}
