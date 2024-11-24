package ca.wescook.wateringcans.fluids;

import ca.wescook.wateringcans.WateringCans;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class ModFluids {
	public static final Fluid growthSolution = new Fluid("growth_solution", new ResourceLocation(WateringCans.MODID, "fluids/growth_solution_still"), new ResourceLocation(WateringCans.MODID, "fluids/growth_solution_flow"));

	@SubscribeEvent
	public static void registerFluidBlock(RegistryEvent.Register<Block> register) {
		register.getRegistry().register(new BlockFluidGrowthSolution());
	}
}
