package theking530.staticpower.cables.fluid;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import theking530.staticcore.utilities.NBTUtilities;

public class ExtendedFluidTank {
	private final List<ExtendedFluidStack> fluids;
	private FluidStack conatinedFluid;
	private int capacity;

	public ExtendedFluidTank(int capacity) {
		fluids = new LinkedList<>();
		this.capacity = capacity;
		this.conatinedFluid = FluidStack.EMPTY;
	}

	public boolean isEmpty() {
		return fluids.isEmpty();
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public FluidStack getFluid() {
		return conatinedFluid;
	}

	public int getFluidAmount() {
		if (fluids.isEmpty()) {
			return 0;
		}
		return 0;
	}

	public boolean isFluidValid(FluidStack stack) {
		return false;
	}

	public int fill(ExtendedFluidStack resource, FluidAction action) {
		return 0;
	}

	public ExtendedFluidStack drain(int maxDrain, FluidAction action) {
		return null;
	}

	public void deserialize(CompoundTag tag) {
		capacity = tag.getInt("c");
		List<ExtendedFluidStack> deserializedFluids = NBTUtilities.deserialize(tag.getList("f", ListTag.TAG_COMPOUND), (listEntry) -> {
			return ExtendedFluidStack.loadFromNBT((CompoundTag) listEntry);
		});
		fluids.addAll(deserializedFluids);
	}

	public CompoundTag serialize() {
		CompoundTag tag = new CompoundTag();
		tag.putInt("c", capacity);
		tag.put("f", NBTUtilities.serialize(fluids, (fluid) -> {
			return fluid.serialize();
		}));

		return tag;
	}

}
