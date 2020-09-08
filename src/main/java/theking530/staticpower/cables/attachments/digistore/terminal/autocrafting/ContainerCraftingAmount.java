package theking530.staticpower.cables.attachments.digistore.terminal.autocrafting;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import theking530.staticcore.initialization.container.ContainerTypeAllocator;
import theking530.staticcore.initialization.container.ContainerTypePopulator;
import theking530.staticpower.cables.digistore.DigistoreNetworkModule;
import theking530.staticpower.cables.digistore.crafting.CraftingRequestResponse;
import theking530.staticpower.cables.digistore.crafting.network.PacketRequestDigistoreCraftRecalculation;
import theking530.staticpower.cables.digistore.crafting.network.PacketSimulateDigistoreCraftingRequestResponse;
import theking530.staticpower.cables.network.CableNetwork;
import theking530.staticpower.cables.network.CableNetworkManager;
import theking530.staticpower.cables.network.CableNetworkModuleTypes;
import theking530.staticpower.container.StaticPowerContainer;
import theking530.staticpower.network.PacketCloseCurrentContainer;
import theking530.staticpower.network.StaticPowerMessageHandler;

public class ContainerCraftingAmount extends StaticPowerContainer {
	@ContainerTypePopulator
	public static final ContainerTypeAllocator<ContainerCraftingAmount, GuiCraftingAmount> TYPE = new ContainerTypeAllocator<>("crafting_request", ContainerCraftingAmount::new,
			GuiCraftingAmount::new);

	private final int maximumToCraft;
	private CraftingRequestResponse craftingResponse;
	private long networkId;

	public ContainerCraftingAmount(int windowId, PlayerInventory playerInventory, PacketBuffer data) {
		this(windowId, playerInventory, CraftingRequestResponse.read(data.readCompoundTag()), data.readLong());
	}

	public ContainerCraftingAmount(int windowId, PlayerInventory playerInventory, CraftingRequestResponse craftingResponse, long networkId) {
		super(TYPE, windowId, playerInventory);
		this.craftingResponse = craftingResponse;
		this.maximumToCraft = craftingResponse.getCraftableAmount();
		this.networkId = networkId;
	}

	public CraftingRequestResponse getCraftingResponse() {
		return craftingResponse;
	}

	public int getMaxCraftableAmount() {
		return maximumToCraft;
	}

	public void updateCraftingResponse(ItemStack target, int amount) {
		// If on the server, perform the recalculation and send the result to the
		// client. If on the client, send a packet to trigger the recalculation.
		if (!getPlayerInventory().player.world.isRemote) {
			CableNetwork network = CableNetworkManager.get(getPlayerInventory().player.world).getNetworkById(networkId);
			DigistoreNetworkModule digistoreModule = network.getModule(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE);
			if (digistoreModule != null && digistoreModule.isManagerPresent()) {
				CraftingRequestResponse newResponse = digistoreModule.getCraftingManager().addCraftingRequest(target, amount, true);
				PacketSimulateDigistoreCraftingRequestResponse newCraftingRequest = new PacketSimulateDigistoreCraftingRequestResponse(windowId, newResponse);
				StaticPowerMessageHandler.sendMessageToPlayer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, (ServerPlayerEntity) getPlayerInventory().player, newCraftingRequest);
			}
		} else {
			PacketRequestDigistoreCraftRecalculation newCraftingRequest = new PacketRequestDigistoreCraftRecalculation(windowId, target, amount);
			StaticPowerMessageHandler.sendToServer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, newCraftingRequest);
		}
	}

	public void makeRequest(ItemStack target, int amount) {
		// If on the server.
		if (!getPlayerInventory().player.world.isRemote) {
			// Get the network and the digistore module.
			CableNetwork network = CableNetworkManager.get(getPlayerInventory().player.world).getNetworkById(networkId);
			DigistoreNetworkModule digistoreModule = network.getModule(CableNetworkModuleTypes.DIGISTORE_NETWORK_MODULE);

			// If the module is valid and we still have the manager present, add the
			// request.
			if (digistoreModule != null && digistoreModule.isManagerPresent()) {
				digistoreModule.getCraftingManager().addCraftingRequest(target, amount, false);
				StaticPowerMessageHandler.sendMessageToPlayer(StaticPowerMessageHandler.MAIN_PACKET_CHANNEL, (ServerPlayerEntity) getPlayerInventory().player,
						new PacketCloseCurrentContainer(windowId));
			}
		} else {
			throw new RuntimeException("This should only be called on the server!");
		}
	}

	public void onCraftingResponseUpdated(CraftingRequestResponse response) {
		this.craftingResponse = response;
	}

	@Override
	public void initializeContainer() {

	}
}
