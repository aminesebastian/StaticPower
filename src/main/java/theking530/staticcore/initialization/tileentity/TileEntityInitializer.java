package theking530.staticcore.initialization.tileentity;

import java.util.function.Supplier;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

/**
 * Wrapper class used to help in the initialization of tile entites.
 * 
 * @author amine
 *
 * @param <T>
 */
public class TileEntityInitializer<T extends TileEntity> implements Supplier<T> {
	private TileEntityTypeAllocator<T> allocator;

	public TileEntityInitializer(TileEntityTypeAllocator<T> allocator) {
		this.allocator = allocator;
	}

	public void setType(TileEntityType<T> type) {
		allocator.type = type;
	}

	@Override
	public T get() {
		return allocator.factory.apply(allocator);
	}
}
