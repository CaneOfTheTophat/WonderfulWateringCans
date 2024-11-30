package ca.wescook.wateringcans.configs;

import ca.wescook.wateringcans.WateringCans;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;

public class Config {

	public static float growthMultiplier;
    public static HashMap<String, Float> fluidMultiplierMap = new HashMap<>();
	public static HashMap<String, Integer> fluidColorMap = new HashMap<>();
	public static ArrayList<String> allowedFluids = new ArrayList<>();

	public static void registerConfigs(FMLPreInitializationEvent event) {
		// Create or load from file
		Configuration configFile = new Configuration(new File(event.getModConfigurationDirectory().getPath(), WateringCans.MODID + ".cfg"));
		configFile.load();

		// Get Values
		growthMultiplier = configFile.getFloat("growthMultiplier", CATEGORY_GENERAL, 1.0F, 0.0F, 10.0F, "Multiply growth ticks from watering cans by this value");

        String[] rawFluidMap = configFile.getStringList("fluidMap", CATEGORY_GENERAL, new String[]
                        {"water", "1.0F", "0x3F76E4",
                                "growth_solution", "2.0F", "0x1AFF1A"},
                "The IDs of fluids that can be bucketed into a watering can, succeeded by their growth multiplier (float) and hex color in that order.");

		for (int i = 0; i < rawFluidMap.length; i += 3) {
			fluidMultiplierMap.put(rawFluidMap[i], Float.parseFloat(rawFluidMap[i + 1]));
			fluidColorMap.put(rawFluidMap[i], Integer.decode(rawFluidMap[i + 2]));
			allowedFluids.add(rawFluidMap[i]);
		}

		// Update file
		if (configFile.hasChanged())
			configFile.save();
	}
}
