package theking530.staticcore.blockentity.components.control.processing;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.INBTSerializable;
import theking530.staticcore.utilities.math.SDMath;

public class Timer implements INBTSerializable<CompoundTag> {
	private int maxTime;
	private int currentTime;
	private int rate;

	public Timer(int maxTime) {
		this(0, maxTime, 1);
	}

	public Timer(int currentTime, int maxTime) {
		this(currentTime, maxTime, 1);
	}

	public Timer(int currentTime, int maxTime, int rate) {
		this.maxTime = maxTime;
		this.currentTime = currentTime;
		this.rate = rate;

	}

	public boolean increment() {
		currentTime = SDMath.clamp(currentTime + rate, 0, maxTime);
		return hasElapsed();
	}

	public void reset() {
		currentTime = 0;
	}

	public boolean hasElapsed() {
		return currentTime >= maxTime;
	}

	public int getMaxTime() {
		return maxTime;
	}

	public void setMaxTime(int maxTime) {
		this.maxTime = Math.max(0, maxTime);
		if (currentTime > maxTime) {
			currentTime = maxTime;
		}
	}

	public int getCurrentTime() {
		return currentTime;
	}

	public int getTicksPerIncrement() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = Math.max(0, rate);
	}

	public int getRemainingTime() {
		return maxTime - currentTime;
	}

	public float getCompletionRatio() {
		return currentTime / (float) maxTime;
	}

	public float getCompletionRatioScaled(float scale) {
		return scale * getCompletionRatio();
	}

	public static Timer fromTag(CompoundTag tag) {
		return new Timer(tag.getInt("current"), tag.getInt("max"));
	}

	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(currentTime);
		buffer.writeInt(maxTime);
		buffer.writeByte(rate);
	}

	public static Timer decode(FriendlyByteBuf buffer) {
		return new Timer(buffer.readInt(), buffer.readInt(), buffer.readByte());
	}

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag output = new CompoundTag();
		output.putInt("current", currentTime);
		output.putInt("max", maxTime);
		output.putByte("rate", (byte) rate);
		return output;
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		currentTime = nbt.getInt("current");
		maxTime = nbt.getInt("max");
		rate = nbt.getByte("rate");
	}
}
