package theking530.staticpower.cables.attachments.digistore.terminalbase.network;

import java.util.function.Supplier;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import theking530.staticcore.gui.widgets.button.StandardButton.MouseButton;
import theking530.staticpower.cables.attachments.digistore.terminalbase.AbstractContainerDigistoreTerminal;
import theking530.staticpower.network.NetworkMessage;

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
	public void encode(PacketBuffer buffer) {
		buffer.writeInt(windowId);
		buffer.writeBoolean(shiftHeld);
		buffer.writeBoolean(controlHeld);
		buffer.writeBoolean(altHeld);
		buffer.writeByte(button.ordinal());
		buffer.writeInt(slot);
	}

	@Override
	public void decode(PacketBuffer buffer) {
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
			PlayerEntity player = ctx.get().getSender();
			if (player.openContainer.windowId == windowId && player.openContainer instanceof AbstractContainerDigistoreTerminal) {
				((AbstractContainerDigistoreTerminal) player.openContainer).digistoreFakeSlotClickedOnServer(slot, button, shiftHeld, controlHeld, altHeld);
			}
		});
	}
}