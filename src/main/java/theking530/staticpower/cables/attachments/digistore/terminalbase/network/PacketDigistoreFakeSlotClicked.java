package theking530.staticpower.cables.attachments.digistore.terminalbase.network;

import java.util.function.Supplier;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractContainerDigistoreTerminal;
import theking530.staticpower.network.NetworkMessage;
import theking530.staticpower.utilities.ItemUtilities;

public class PacketDigistoreFakeSlotClicked extends NetworkMessage {
	protected int windowId;
	private ItemStack stack;
	private MouseButton button;
	private boolean shiftHeld;
	private boolean controlHeld;
	private boolean altHeld;

	public PacketDigistoreFakeSlotClicked() {

	}

	public PacketDigistoreFakeSlotClicked(int windowId, ItemStack stack, MouseButton button, boolean shiftHeld, boolean controlHeld, boolean altHeld) {
		this.windowId = windowId;
		this.stack = stack;
		this.button = button;
		this.shiftHeld = shiftHeld;
		this.controlHeld = controlHeld;
		this.altHeld = altHeld;
	}

	@Override
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(windowId);
		buffer.writeBoolean(shiftHeld);
		buffer.writeBoolean(controlHeld);
		buffer.writeBoolean(altHeld);
		buffer.writeByte(button.ordinal());
		ItemUtilities.writeLargeStackItemToBuffer(stack, false, buffer);
	}

	@Override
	public void decode(PacketBuffer buffer) {
		windowId = buffer.readInt();
		shiftHeld = buffer.readBoolean();
		controlHeld = buffer.readBoolean();
		altHeld = buffer.readBoolean();
		button = MouseButton.values()[buffer.readByte()];
		stack = ItemUtilities.readLargeStackItemFromBuffer(buffer);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			PlayerEntity player = ctx.get().getSender();
			if (player.openContainer.windowId == windowId && player.openContainer instanceof AbstractContainerDigistoreTerminal) {
				((AbstractContainerDigistoreTerminal) player.openContainer).digistoreFakeSlotClickedOnServer(stack, button, shiftHeld, controlHeld, altHeld);
			}
		});
	}
}