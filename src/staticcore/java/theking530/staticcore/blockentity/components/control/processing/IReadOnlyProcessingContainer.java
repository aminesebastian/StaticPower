package theking530.staticcore.blockentity.components.control.processing;

import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import theking530.staticcore.productivity.product.ProductType;

public interface IReadOnlyProcessingContainer {
	public List<ItemStack> getItems();

	public List<FluidStack> getFluids();

	public ItemStack getItem(int index);

	public FluidStack getFluid(int index);

	public double getPower(int index);

	public boolean hasItems();

	public boolean hasFluids();

	public boolean hasPower();

	public <T extends ProductType<K>, K> ProcessingProduct<T, K> getProductOfType(T type, int index);

	public boolean hasProductsOfType(ProductType<?> type);

	public <K, T extends ProductType<K>, W extends ProcessingProduct<T, K>> List<W> getProductsOfType(T type);;

}
