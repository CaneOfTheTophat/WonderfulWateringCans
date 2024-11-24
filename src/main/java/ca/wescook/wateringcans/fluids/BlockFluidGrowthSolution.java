package ca.wescook.wateringcans.fluids;

import net.minecraft.block.material.Material;
import net.minecraftforge.fluids.BlockFluidClassic;

public class BlockFluidGrowthSolution extends BlockFluidClassic {
	BlockFluidGrowthSolution() {
		super(ModFluids.growthSolution, Material.WATER);
		setRegistryName("growth_solution_block");
		setUnlocalizedName(getRegistryName().toString());
	}
}
