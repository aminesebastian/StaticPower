package theking530.staticpower.init;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;

import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import theking530.staticcore.init.StaticCoreKeyBindings;
import theking530.staticcore.init.StaticCoreKeyBindings.KeyBinding;

public class ModKeyBindings {
	private static final List<KeyBinding> BINDINGS = new ArrayList<>();
	/**
	 * If the player has magnets in their inventory, this toggles the state.
	 */
	public static final KeyBinding TOGGLE_MAGNET = StaticCoreKeyBindings.createKeyBinding("toggle_magnet", GLFW.GLFW_KEY_M);
	/**
	 * If the player has a wireless digistore terminal in their inventory, attempts
	 * to open it.
	 */
	public static final KeyBinding OPEN_PORTABLE_DIGISTORE = StaticCoreKeyBindings.createKeyBinding("open_portable_digistore", GLFW.GLFW_KEY_TAB);

	/**
	 * This event is called during the client side setup.
	 * 
	 * @param event
	 */
	public static void registerBindings(RegisterKeyMappingsEvent event) {
		for (KeyBinding binding : BINDINGS) {
			event.register(binding.getMapping());
		}
	}

	public static KeyBinding createKeyBinding(String name, int key) {
		KeyBinding output = new KeyBinding(name, key);
		BINDINGS.add(output);
		return output;
	}

}
