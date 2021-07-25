package theking530.staticpower.cables.network;

import java.util.List;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public abstract class AbstractCableNetworkModule {
	protected CableNetwork Network;
	private final ResourceLocation Type;

	public AbstractCableNetworkModule(ResourceLocation type) {
		Type = type;
	}

	public void onNetworkGraphUpdated(NetworkMapper mapper, BlockPos startingPosition) {

	}

	public void onNetworksSplitOff(List<CableNetwork> newNetworks) {

	}

	public void onAddedToNetwork(CableNetwork network) {
		Network = network;
	}

	public ResourceLocation getType() {
		return Type;
	}

	public CableNetwork getNetwork() {
		return Network;
	}

	public void onNetworkUpdatesDisabled() {

	}

	public void onNetworkUpdatesEnabled() {

	}

	public abstract void getReaderOutput(List<ITextComponent> components);

	public abstract void tick(World world);

	public void readFromNbt(CompoundNBT tag) {
	}

	public CompoundNBT writeToNbt(CompoundNBT tag) {
		return tag;
	}
}
