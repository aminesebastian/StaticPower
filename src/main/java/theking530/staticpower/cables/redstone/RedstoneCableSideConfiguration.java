package theking530.staticpower.cables.redstone;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class RedstoneCableSideConfiguration implements INBTSerializable<CompoundNBT> {
	public enum RedstoneCableSignalLogic {
		INPUT, OUTPUT, INOUT
	}

	private RedstoneCableSignalLogic logicType;
	private String selector;

	public RedstoneCableSideConfiguration() {
		logicType = RedstoneCableSignalLogic.INPUT;
	}

	public boolean isOutputSide() {
		return logicType == RedstoneCableSignalLogic.OUTPUT;
	}

	public boolean isInputSide() {
		return logicType == RedstoneCableSignalLogic.INPUT;
	}

	public boolean isInputOutputSide() {
		return logicType == RedstoneCableSignalLogic.INOUT;
	}

	public String getSelector() {
		return selector;
	}

	public RedstoneCableSideConfiguration setSelector(String selector) {
		this.selector = selector;
		return this;
	}

	public RedstoneCableSideConfiguration setLogicType(RedstoneCableSignalLogic newType) {
		logicType = newType;
		return this;
	}

	public RedstoneCableSideConfiguration incrementLogicType() {
		int newValue = ((logicType.ordinal() + 1) % RedstoneCableSignalLogic.values().length);
		logicType = RedstoneCableSignalLogic.values()[newValue];
		return this;
	}

	@Override
	public CompoundNBT serializeNBT() {
		CompoundNBT output = new CompoundNBT();
		output.putInt("logic_type", logicType.ordinal());
		output.putString("selector", selector);
		return output;
	}

	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		logicType = RedstoneCableSignalLogic.values()[nbt.getInt("logic_type")];
		selector = nbt.getString("selector");
	}
}
