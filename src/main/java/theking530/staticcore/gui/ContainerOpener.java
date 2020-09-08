package theking530.staticcore.gui;

import java.util.Objects;
import java.util.function.Function;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;

public class ContainerOpener implements INamedContainerProvider {
	private final TriFunction<Integer, PlayerInventory, PlayerEntity, Container> container;
	private final ITextComponent name;

	public ContainerOpener(ITextComponent name, TriFunction<Integer, PlayerInventory, PlayerEntity, Container> container) {
		this.container = container;
		this.name = name;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity player) {
		return container.apply(windowId, playerInv, player);
	}

	@Override
	public ITextComponent getDisplayName() {
		return name;
	}

	public static @FunctionalInterface interface TriFunction<A, B, C, R> {
		R apply(A a, B b, C c);

		default <V> TriFunction<A, B, C, V> andThen(Function<? super R, ? extends V> after) {
			Objects.requireNonNull(after);
			return (A a, B b, C c) -> after.apply(apply(a, b, c));
		}
	}
}
