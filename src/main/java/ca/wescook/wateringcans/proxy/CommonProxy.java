package ca.wescook.wateringcans.proxy;

import ca.wescook.wateringcans.WateringCans;
import ca.wescook.wateringcans.configs.Config;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class CommonProxy {

	Config config = new Config();

	public void preInit(FMLPreInitializationEvent event) {
		config.generateConfig(new File(event.getModConfigurationDirectory().getPath(), WateringCans.MODID + ".cfg"));

		FluidRegistry.registerFluid(WateringCans.growthSolution);
		FluidRegistry.addBucketForFluid(WateringCans.growthSolution);
	}

	public void init(FMLInitializationEvent event) {
	}

	public void postInit(FMLPostInitializationEvent event) {
	}
}
