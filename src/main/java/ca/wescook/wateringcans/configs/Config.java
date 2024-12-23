package ca.wescook.wateringcans.configs;

import ca.wescook.wateringcans.WateringCans;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.text.WordUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

public class Config {

	public static Map<String, Boolean> recipeBools = new HashMap<>();

	public static float growthMultiplier;

    public static HashMap<String, Float> fluidMultiplierMap = new HashMap<>();
	public static HashMap<String, Integer> fluidColorMap = new HashMap<>();
	public static ArrayList<String> allowedFluids = new ArrayList<>();

	private static final String[] materials = new String[]{"stone", "iron", "golden", "obsidian", "creative"};

	public Configuration configFile;

	public void generateConfig(File file)
	{
		configFile = new Configuration(file);
		registerConfigs();
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
	{
		if(WateringCans.MODID.equals(event.getModID()))
			registerConfigs();
	}

	public void registerConfigs() {
		for (String material : materials) {
			if (!material.equals("creative"))
				recipeBools.put(WordUtils.capitalize(material) + " Watering Can Recipe", configFile.get("recipes", WordUtils.capitalize(material) + " Watering Can Recipe", true).getBoolean());
		}

		recipeBools.put("Growth Solution Recipe", configFile.get("recipes", "Growth Solution Recipe", true).getBoolean());

		growthMultiplier = configFile.getFloat("Watering Can Growth Multiplier", CATEGORY_GENERAL, 1.0F, 0.0F, 10.0F, "Multiply growth ticks from watering cans by this value");

        String[] rawFluidMap = configFile.getStringList("Allowed fluids", CATEGORY_GENERAL, new String[]
                        {"water, 1.0F, 0x3F76E4", "growth_solution, 2.0F, 0x1AFF1A"},
                "The IDs of fluids that can be bucketed into a watering can, succeeded by their growth multiplier (float) and hex color in that order.\n");

		if(rawFluidMap != null) {
            for (String fluidEntry : rawFluidMap) {

                String[] split = fluidEntry.replaceAll("\\s+", "").split(",");

                fluidMultiplierMap.put(split[0], Float.parseFloat(split[1]));
                fluidColorMap.put(split[0], Integer.decode(split[2]));
                allowedFluids.add(split[0]);
            }
		}


		if (configFile.hasChanged())
			configFile.save();
	}
}
