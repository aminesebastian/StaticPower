package theking530.staticpower.init;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;

public class ModKeyBindings {
	private static final List<KeyBinding> BINDINGS = new ArrayList<>();
	private static final HashMap<KeyBinding, List<Consumer<KeyBinding>>> CALLBACKS = new HashMap<>();

	/**
	 * When this binding is held and slot is clicked, if the slot supports it, it
	 * will toggle it's locked state.
	 */
	public static final KeyBinding SLOT_LOCK = create("lock_slot", GLFW.GLFW_KEY_LEFT_CONTROL);
	/**
	 * If the player has magnets in their inventory, this toggles the state.
	 */
	public static final KeyBinding TOGGLE_MAGNET = create("toggle_magnet", GLFW.GLFW_KEY_M);
	/**
	 * If the player has a wireless digistore terminal in their inventory, attempts
	 * to open it.
	 */
	public static final KeyBinding OPEN_PORTABLE_DIGISTORE = create("open_portable_digistore", GLFW.GLFW_KEY_TAB);
	/**
	 * Opens the research window.
	 */
	public static final KeyBinding OPEN_RESEARCH = create("open_research", GLFW.GLFW_KEY_R);
	/**
	 * Opens the production window.
	 */
	public static final KeyBinding OPEN_PRODUCTION = create("open_production", GLFW.GLFW_KEY_P);
	/**
	 * If held while clicking inside the side config tab, reset the config to
	 * default.
	 */
	public static final KeyBinding RESET_SIDE_CONFIGURATION = create("reset_side_configuration", GLFW.GLFW_KEY_R);

	/**
	 * Passing this function a binding and a callback will ensure the callback is
	 * called when the binding's state changes.
	 * 
	 * @param binding
	 * @param callback
	 */
	public static void addCallback(KeyBinding binding, Consumer<KeyBinding> callback) {
		if (!CALLBACKS.containsKey(binding)) {
			CALLBACKS.put(binding, new ArrayList<Consumer<KeyBinding>>());
		}
		CALLBACKS.get(binding).add(callback);
	}

	/**
	 * This event is called during the client side setup.
	 * 
	 * @param event
	 */
	public static void registerBindings(RegisterKeyMappingsEvent event) {
		for (KeyBinding binding : BINDINGS) {
			event.register(binding.mapping);
		}
	}

	/**
	 * This method is called by the event handler.
	 * 
	 * @param event
	 */
	public static void onKeyEvent(InputEvent.Key event) {
		for (KeyBinding binding : BINDINGS) {
			if (binding.mapping.getKey().getValue() == event.getKey() && binding.handle(event)) {
				if (CALLBACKS.containsKey(binding)) {
					for (Consumer<KeyBinding> callback : CALLBACKS.get(binding)) {
						callback.accept(binding);
					}
				}
			}
		}
	}

	private static KeyBinding create(String name, int key) {
		KeyBinding output = new KeyBinding(name, key);
		BINDINGS.add(output);
		return output;
	}

	public static class KeyBinding {
		private final static long MAX_LATENCY = 50;
		private final KeyMapping mapping;
		private boolean isDown;
		private boolean justPressed;
		private boolean lastCallbackValue;
		private long lastEventTime;

		public KeyBinding(String name, int key) {
			mapping = new KeyMapping("key.staticpower." + name, InputConstants.Type.KEYSYM, key, "key.categories.staticpower");
		}

		public KeyMapping getMapping() {
			return mapping;
		}

		public boolean isDown() {
			return isDown;
		}

		public boolean wasJustPressed() {
			// Handle edge cases where whatever is querying this is running on a faster tick
			// than the event bus.
			if (new Date().getTime() - lastEventTime >= MAX_LATENCY) {
				justPressed = false;
			}
			return justPressed;
		}

		/**
		 * Handles the incoming return. If this method returns true, that indicates that
		 * the state of the binding has changed (down to up/up to down).
		 * 
		 * @param event
		 * @return
		 */
		private boolean handle(InputEvent.Key event) {
			// If this was a press event, capture that.
			boolean tempIsDown = event.getAction() == GLFW.GLFW_PRESS || event.getAction() == GLFW.GLFW_REPEAT;
			justPressed = !isDown && tempIsDown;
			isDown = tempIsDown;
			lastEventTime = new Date().getTime();

			// Update the just pressed state.
			if (lastCallbackValue != isDown) {
				lastCallbackValue = isDown;
				return true;
			}
			return false;
		}
	}
}
