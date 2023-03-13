package theking530.staticcore.container;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.IContainerFactory;
import theking530.staticcore.network.NetworkGUI;
import theking530.staticpower.container.StaticPowerContainer;

public class ContainerOpener<T extends StaticPowerContainer> implements MenuProvider {
	private final IContainerFactory<T> containerFactory;
	private final Component name;
	private StaticPowerContainer parent;

	public ContainerOpener(Component name, IContainerFactory<T> containerFactory) {
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
	public AbstractContainerMenu createMenu(int windowId, Inventory playerInv, Player player) {
		T output = containerFactory.create(windowId, playerInv);
		if (parent != null) {
			output.setOpener(this);
		}
		return output;
	}

	@Override
	public Component getDisplayName() {
		return name;
	}

	public void open(ServerPlayer player, Consumer<FriendlyByteBuf> extraDataWriter) {
		NetworkGUI.openScreen(player, this, extraDataWriter);
	}

	public void open(ServerPlayer player) {
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
