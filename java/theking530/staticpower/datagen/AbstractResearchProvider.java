package theking530.staticpower.datagen;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import theking530.staticpower.data.research.Research;
import theking530.staticpower.data.research.Research.ResearchBuilder;

public abstract class AbstractResearchProvider implements DataProvider {
	private final static String NAME = "staticpower:research_provider";
	public static final Logger LOGGER = LogManager.getLogger("Static Power Research Datagen");
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

	private final DataGenerator generator;
	private final HashMap<ResourceLocation, Research> research;

	public AbstractResearchProvider(DataGenerator dataGeneratorIn) {
		this.generator = dataGeneratorIn;
		research = new HashMap<ResourceLocation, Research>();
	}

	public Research register(ResearchBuilder builder) {
		Research output = builder.build();
		research.put(builder.getName(), output);
		return output;
	}

	@Override
	public void run(HashCache cache) throws IOException {
		Path outputFolder = this.generator.getOutputFolder();
		research.forEach((key, research) -> {
			Path path = outputFolder.resolve("data/" + key.getNamespace() + "/research_test/" + key.getPath() + ".json");
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(path.toString()));
				writer.write(GSON.toJson(research));
				writer.close();
			} catch (IOException e) {
				LOGGER.error(String.format("Couldn't write Research: %1$s.", key.toString()), e);
			}
		});
	}

	@Override
	public String getName() {
		return NAME;
	}
}
