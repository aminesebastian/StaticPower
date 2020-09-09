package theking530.staticcore.gui;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.IContainerFactory;
import theking530.staticcore.network.NetworkGUI;
import theking530.staticpower.container.StaticPowerContainer;

public class ContainerOpener<T extends StaticPowerContainer> implements INamedContainerProvider {
	private final IContainerFactory<T> containerFactory;
	private final ITextComponent name;
	private StaticPowerContainer parent;

	public ContainerOpener(ITextComponent name, IContainerFactory<T> containerFactory) {
		this.containerFactory = containerFactory;
		this.name = name;
	}

	public ContainerOpener<T> fromParent(StaticPowerContainer parent) {
		this.parent = parent;
		return this;
	}

	public StaticPowerContainer getParent() {
		return parent;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player) {
		T output = containerFactory.create(windowId, playerInv);
		if (parent != null) {
			output.setOpener(this);
		}
		return output;
	}

	@Override
	public ITextComponent getDisplayName() {
		return name;
	}

	public void open(ServerPlayerEntity player, Consumer<PacketBuffer> extraDataWriter) {
		NetworkGUI.openGui(player, this, extraDataWriter);
	}

	public void open(ServerPlayerEntity player) {
		open(player, (buff) -> {

		});
	}

	public static @FunctionalInterface interface TriFunction<A, B, C, R> {
		R apply(A a, B b, C c);

		default <V> TriFunction<A, B, C, V> andThen(Function<? super R, ? extends V> after) {
			Objects.requireNonNull(after);
			return (A a, B b, C c) -> after.apply(apply(a, b, c));
		}
	}
}
