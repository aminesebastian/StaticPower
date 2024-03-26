package theking530.staticcore.utilities;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class FluidUtilities {
	public static int getFluidTemperature(FluidStack fluid) {
		return fluid.getFluid().getFluidType().getTemperature(fluid);
	}

	public static int getFluidStackHash(FluidStack stack) {
		CompoundTag serialized = new CompoundTag();
		stack.writeToNBT(serialized);
		serialized.remove("Amount");
		return Objects.hash(serialized);
	}

	public record FluidContainerFillResult(boolean success, ItemStack filledContainer, FluidStack transferedFluid) {

		public static final FluidContainerFillResult FAILURE = new FluidContainerFillResult(false, ItemStack.EMPTY,
				FluidStack.EMPTY);

		public boolean isSuccess() {
			return success;
		}
	}

	/**
	 * Fill a container from the given fluidSource.
	 *
	 * @param container   The container to be filled. Will not be modified. Separate
	 *                    handling must be done to reduce the stack size, stow
	 *                    containers, etc, on success. See
	 *                    {@link #tryFillContainerAndStow(ItemStack, IFluidHandler, IItemHandler, int, Player, boolean)}.
	 * @param fluidSource The fluid handler to be drained.
	 * @param maxAmount   The largest amount of fluid that should be transferred.
	 * @param player      The player to make the filling noise. Pass null for no
	 *                    noise.
	 * @param doFill      true if the container should actually be filled, false if
	 *                    it should be simulated.
	 * @return a {@link FluidContainerFillResult} holding the filled container if
	 *         successful and the transfered fluid.
	 */
	@NotNull
	public static FluidContainerFillResult tryFillContainer(@NotNull ItemStack container, IFluidHandler fluidSource,
			int maxAmount, @Nullable Player player, boolean doFill) {
		ItemStack containerCopy = ItemHandlerHelper.copyStackWithSize(container, 1); // do not modify the input
		IFluidHandlerItem containerFluidHandler = FluidUtil.getFluidHandler(containerCopy).orElse(null);
		if (containerFluidHandler == null) {
			return FluidContainerFillResult.FAILURE;
		}

		FluidStack simulatedTransfer = FluidUtil.tryFluidTransfer(containerFluidHandler, fluidSource, maxAmount, false);
		if (!simulatedTransfer.isEmpty()) {
			if (doFill) {
				FluidUtil.tryFluidTransfer(containerFluidHandler, fluidSource, maxAmount, true);
				if (player != null) {
					SoundEvent soundevent = simulatedTransfer.getFluid().getFluidType().getSound(simulatedTransfer,
							SoundActions.BUCKET_FILL);
					player.level.playSound(null, player.getX(), player.getY() + 0.5, player.getZ(), soundevent,
							SoundSource.BLOCKS, 1.0F, 1.0F);
				}
			} else {
				containerFluidHandler.fill(simulatedTransfer, IFluidHandler.FluidAction.SIMULATE);
			}

			ItemStack resultContainer = containerFluidHandler.getContainer();
			return new FluidContainerFillResult(true, resultContainer, simulatedTransfer);
		}

		return FluidContainerFillResult.FAILURE;
	}
}
