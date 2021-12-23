package theking530.staticpower.cables.attachments.digistore.terminalbase.autocrafting;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.cables.digistore.DigistoreNetworkModule;
import theking530.staticpower.cables.digistore.crafting.network.PacketRequestDigistoreCraftRecalculation;
import theking530.staticpower.cables.digistore.crafting.network.PacketSimulateDigistoreCraftingRequestResponse;
import theking530.staticpower.cables.digistore.crafting.recipes.CraftingStepsBundle;
import theking530.staticpower.cables.digistore.crafting.recipes.CraftingStepsBundle.CraftingStepsBundleContainer;
import theking530.staticpower.cables.network.CableNetwork;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.container.StaticPowerContainer;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class ContainerCraftingAmount extends StaticPowerContainer {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerCraftingAmount, GuiCraftingAmount> TYPE = new ContainerTypeAllocator<>("crafting_request", ContainerCraftingAmount::new);
	static {
		if (FMLEnvironment.dist == Dist.CLIENT) {
			TYPE.setScreenFactory(GuiCraftingAmount::new);
		}
	}

	private CraftingStepsBundleContainer bundles;
	private long networkId;

	public ContainerCraftingAmount(int windowId, Inventory playerInventory, FriendlyByteBuf data) {
		this(windowId, playerInventory, CraftingStepsBundleContainer.readFromCompressedString(data.readUtf()), data.readLong());
	}

	public ContainerCraftingAmount(int windowId, Inventory playerInventory, CraftingStepsBundleContainer bundles, long networkId) {
		super(TYPE, windowId, playerInventory);
		this.bundles = bundles;
		this.networkId = networkId;
	}

	public CraftingStepsBundleContainer getBundleContainer() {
		return bundles;
	}

	public void updateCraftingResponse(ItemStack target, int amount) {
		// If on the server, perform the recalculation and send the result to the
		// client. If on the client, send a packet to trigger the recalculation.
		if (!getPlayerInventory().player.level.isClientSide) {
			CableNetwork network = CableNetworkManager.get(getPlayerInventory().player.level).getNetworkById(networkId);
			DigistoreNetworkModule digistoreModule = network.getModule(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE);
			if (digistoreModule != null && digistoreModule.isManagerPresent()) {
				CraftingStepsBundleContainer newBundles = digistoreModule.getCraftingManager().calculateAllPossibleCraftingTrees(target, amount);
				PacketSimulateDigistoreCraftingRequestResponse newCraftingRequest = new PacketSimulateDigistoreCraftingRequestResponse(containerId, newBundles);
				StaticPowerMessageHandler.sendMessageToPlayer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, (ServerPlayer) getPlayerInventory().player, newCraftingRequest);
			}
		} else {
			PacketRequestDigistoreCraftRecalculation newCraftingRequest = new PacketRequestDigistoreCraftRecalculation(containerId, target, amount);
			StaticPowerMessageHandler.sendToServer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, newCraftingRequest);
		}
	}

	public void makeRequest(CraftingStepsBundle bundle) {
		// If on the server.
		if (!getPlayerInventory().player.level.isClientSide) {
			// Get the network and the digistore module.
			CableNetwork network = CableNetworkManager.get(getPlayerInventory().player.level).getNetworkById(networkId);
			DigistoreNetworkModule digistoreModule = network.getModule(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE);

			// If the module is valid and we still have the manager present, add the
			// request.
			if (digistoreModule != null && digistoreModule.isManagerPresent()) {
				digistoreModule.getCraftingManager().addCraftingRequest(bundle);
				revertToParent();
			}
		} else {
			throw new RuntimeException("This should only be called on the server!");
		}
	}

	public void onCraftingResponseUpdated(CraftingStepsBundleContainer bundles) {
		this.bundles = bundles;
	}

	@Override
	public void initializeContainer() {

	}
}