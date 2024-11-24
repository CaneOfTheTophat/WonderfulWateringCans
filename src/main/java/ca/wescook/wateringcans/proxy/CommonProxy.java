package ca.wescook.wateringcans.proxy;

import ca.wescook.wateringcans.ModContent;
import ca.wescook.wateringcans.configs.Config;
import ca.wescook.wateringcans.fluids.ModFluids;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class CommonProxy {

	public void preInit(FMLPreInitializationEvent event) {
		Config.registerConfigs(event);

		FluidRegistry.registerFluid(ModFluids.growthSolution);
		FluidRegistry.addBucketForFluid(ModFluids.growthSolution);
	}

	public void init(FMLInitializationEvent event) {
	}

	public void postInit(FMLPostInitializationEvent event) {
	}
}
