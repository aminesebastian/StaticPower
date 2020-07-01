package theking530.staticpower.initialization;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileEntityInitializer<T extends TileEntity> implements Supplier<T> {
	private TileEntityType<T> type;
	private Function<TileEntityType<T>, T> factory;

	public TileEntityInitializer(Function<TileEntityType<T>, T> factory) {
		this.factory = factory;
	}

	public void setType(TileEntityType<T> type) {
		this.type = type;
	}

	@Override
	public T get() {
		return factory.apply(type);
	}
}
