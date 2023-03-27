package theking530.staticpower.cables.attachments.digistore.terminalbase.network;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticcore.network.NetworkMessage;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractContainerDigistoreTerminal;

public class PacketDigistoreFakeSlotClicked extends NetworkMessage {
	protected int windowId;
	private int slot;
	private MouseButton button;
	private boolean shiftHeld;
	private boolean controlHeld;
	private boolean altHeld;

	public PacketDigistoreFakeSlotClicked() {

	}

	public PacketDigistoreFakeSlotClicked(int windowId, int slot, MouseButton button, boolean shiftHeld, boolean controlHeld, boolean altHeld) {
		this.windowId = windowId;
		this.slot = slot;
		this.button = button;
		this.shiftHeld = shiftHeld;
		this.controlHeld = controlHeld;
		this.altHeld = altHeld;
	}

	@Override
	public void encode(FriendlyByteBuf buffer) {
		buffer.writeInt(windowId);
		buffer.writeBoolean(shiftHeld);
		buffer.writeBoolean(controlHeld);
		buffer.writeBoolean(altHeld);
		buffer.writeByte(button.ordinal());
		buffer.writeInt(slot);
	}

	@Override
	public void decode(FriendlyByteBuf buffer) {
		windowId = buffer.readInt();
		shiftHeld = buffer.readBoolean();
		controlHeld = buffer.readBoolean();
		altHeld = buffer.readBoolean();
		button = MouseButton.values()[buffer.readByte()];
		slot = buffer.readInt();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void handle(Supplier<Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Player player = ctx.get().getSender();
			if (player.containerMenu.containerId == windowId && player.containerMenu instanceof AbstractContainerDigistoreTerminal) {
				((AbstractContainerDigistoreTerminal) player.containerMenu).digistoreFakeSlotClickedOnServer(slot, button, shiftHeld, controlHeld, altHeld);
			}
		});
	}
}