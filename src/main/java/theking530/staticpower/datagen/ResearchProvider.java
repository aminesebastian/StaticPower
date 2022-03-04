package theking530.staticpower.datagen;

import java.io.IOException;

import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;

public class ResearchProvider implements DataProvider {
	private final static String NAME = "staticpower:research_provider";

	@Override
	public void run(HashCache cache) throws IOException {
	}

	@Override
	public String getName() {
		return NAME;
	}

}
