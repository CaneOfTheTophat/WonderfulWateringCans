package ca.wescook.wateringcans;

import ca.wescook.wateringcans.proxy.CommonProxy;
import com.google.common.collect.ImmutableMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.Map;

@Mod(modid = WateringCans.MODID, useMetadata = true, dependencies = "after:jei@[3.7.2.220,)")
public class WateringCans {

	public static final String MODID = "wateringcans"; // FG5 doesn't have the innate ability to have one source of truth for the modid using Gradle :(

	public static final Map<String, String> fluids = ImmutableMap.of( // Fluid IDs and their localization strings
		"water", "tile.water.name",
		"growth_solution", "fluid.growth_solution"
	);

	@SidedProxy(clientSide="ca.wescook.wateringcans.proxy.ClientProxy", serverSide="ca.wescook.wateringcans.proxy.CommonProxy")
	static private CommonProxy proxy;

	static {
		FluidRegistry.enableUniversalBucket();
	}

	// Define growth solution here
	public static final Fluid growthSolution = new Fluid("growth_solution", new ResourceLocation(WateringCans.MODID, "fluids/growth_solution_still"), new ResourceLocation(WateringCans.MODID, "fluids/growth_solution_flow"));

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		WateringCans.proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		WateringCans.proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		WateringCans.proxy.postInit(event);
	}
}
