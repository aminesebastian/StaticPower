package theking530.staticpower.blockentities.components.control.sideconfiguration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.base.Joiner;

import net.minecraft.nbt.CompoundTag;
import theking530.staticpower.blockentities.components.control.sideconfiguration.SideConfigurationUtilities.BlockSide;

public class SideConfigurationPreset {
	private Map<BlockSide, MachineSideMode> defaultConfiguration;
	private Map<BlockSide, Set<MachineSideMode>> possibleConfigurations;

	public SideConfigurationPreset() {
		defaultConfiguration = new HashMap<>();
		possibleConfigurations = new HashMap<>();

		for (BlockSide side : BlockSide.values()) {
			defaultConfiguration.put(side, MachineSideMode.Never);
			possibleConfigurations.put(side, new HashSet<>());
		}
	}

	protected SideConfigurationPreset setDefaultOnSide(BlockSide side, MachineSideMode mode) {
		defaultConfiguration.put(side, mode);
		possibleConfigurations.get(side).add(mode);
		return this;
	}

	protected SideConfigurationPreset addPossibleConfiguration(BlockSide side, MachineSideMode mode) {
		possibleConfigurations.get(side).add(mode);
		return this;
	}

	protected SideConfigurationPreset removePossibleConfiguration(BlockSide side, MachineSideMode mode) {
		possibleConfigurations.get(side).remove(mode);
		return this;
	}

	protected SideConfigurationPreset clearPossibleConfigurations(BlockSide side) {
		possibleConfigurations.get(side).clear();
		return this;
	}

	public MachineSideMode getSideDefaultMode(BlockSide side) {
		return defaultConfiguration.get(side);
	}

	public boolean validate(BlockSide side, MachineSideMode newMode) {
		return possibleConfigurations.get(side).contains(newMode);
	}

	public void modifyAfterChange(BlockSide changedSide, Map<BlockSide, MachineSideMode> configuration) {

	}

	public void deserialize(CompoundTag tag) {
		CompoundTag defaultTag = tag.getCompound("default");
		CompoundTag possibilityTag = tag.getCompound("possibilities");

		for (BlockSide side : BlockSide.values()) {
			defaultConfiguration.put(side, MachineSideMode.values()[defaultTag.getByte(side.toString())]);

			possibleConfigurations.get(side).clear();
			byte[] possibleConfigs = possibilityTag.getByteArray(side.toString());
			for (byte possibleMode : possibleConfigs) {
				possibleConfigurations.get(side).add(MachineSideMode.values()[possibleMode]);
			}
		}
	}

	public CompoundTag serialize() {
		CompoundTag defaultTag = new CompoundTag();
		CompoundTag possibilityTag = new CompoundTag();

		for (BlockSide side : BlockSide.values()) {
			defaultTag.putByte(side.toString(), (byte) defaultConfiguration.get(side).ordinal());

			Byte[] possibleConfigs = possibleConfigurations.get(side).stream().map((mode) -> mode.ordinal()).toArray(Byte[]::new);
			possibilityTag.putByteArray(side.toString(), ArrayUtils.toPrimitive(possibleConfigs));
		}

		CompoundTag output = new CompoundTag();
		output.put("default", defaultTag);
		output.put("possibilities", possibilityTag);
		return output;
	}

	@Override
	public String toString() {
		return "SideConfigurationPreset [configuration=" + ", defaultConfiguration=" + Joiner.on(",").withKeyValueSeparator("=").join(defaultConfiguration)
				+ ", possibleConfigurations=" + possibleConfigurations + "]";
	}
}
