package ca.wescook.wateringcans.items;

import ca.wescook.wateringcans.ModContent;
import ca.wescook.wateringcans.configs.Config;
import ca.wescook.wateringcans.particles.ParticleGrowthSolution;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

import static ca.wescook.wateringcans.WateringCans.MODID;
import static net.minecraft.block.BlockFarmland.MOISTURE;

public class ItemWateringCan extends Item {

	private final short fluidCapacity;
	private final float innateGrowthMultiplier;
	private final int reach;
	private final boolean oneFill;
	private final boolean heavy;

	public ItemWateringCan(String material, short fluidCapacity, float innateGrowthMultiplier, int reach, boolean oneFill, boolean heavy) {
		setRegistryName( material + "_watering_can");
		setUnlocalizedName(getRegistryName().toString());
		setCreativeTab(CreativeTabs.TOOLS);
		setMaxStackSize(1);

		// Variables
		this.fluidCapacity = fluidCapacity;
		this.innateGrowthMultiplier = innateGrowthMultiplier;
		this.reach = reach;
		this.oneFill = oneFill;
		this.heavy = heavy;

		// Get the percentage of fluid in the can for use in switching the model JSONs
		addPropertyOverride(new ResourceLocation(MODID,"fluid_percentage"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			@Override
			public float apply(ItemStack itemStack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				FluidStack fluidStack = FluidUtil.getFluidContained(itemStack); // Get fluidstack
				return fluidStack != null ? (float) fluidStack.amount / fluidCapacity : 0F; // Calculate percentage if fluidstack exists. Else return empty
			}
		});
	}

	// Add tooltips
	@Override
	public void addInformation(ItemStack itemStack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		FluidStack fluidStack = FluidUtil.getFluidContained(itemStack);
		if (fluidStack != null) {
			tooltip.add(I18n.format("tooltip.wateringcans:contains") + ": " + fluidStack.getLocalizedName());
			tooltip.add(fluidStack.amount + "/" + fluidCapacity + "mB");
		}
		else
			tooltip.add(I18n.format("tooltip." + itemStack.getItem().getRegistryName().toString(), TextFormatting.DARK_GRAY)); // Display material tooltip
	}

	// On right click
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		ItemStack itemStack = playerIn.getHeldItem(handIn);
		FluidStack fluidStack = FluidUtil.getFluidContained(itemStack);
		NBTTagCompound nbtCompound = itemStack.getTagCompound();
		IFluidHandler fluidHandler = FluidUtil.getFluidHandler(itemStack);
		RayTraceResult rayTraceResult = this.rayTrace(worldIn, playerIn, true);

		// If no NBT present
		if (nbtCompound == null) {
			nbtCompound = new NBTTagCompound(); // Create new compound
			nbtCompound.setBoolean("filledAtLeastOnce", false); // Hasn't been filled yet
			itemStack.setTagCompound(nbtCompound); // Attach to itemstack
		}

		// Break check
		if (oneFill && nbtCompound.getBoolean("filledAtLeastOnce") && fluidStack == null) {
			playerIn.renderBrokenItemStack(itemStack);
			itemStack.shrink(1);
		}

		// See if ray trace it hit a block
		if (rayTraceResult != null && rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {
			BlockPos blockPos = rayTraceResult.getBlockPos(); // Get block position from ray trace
			Block block = worldIn.getBlockState(blockPos).getBlock(); // Get block

			// Check if it's a fluid block
			if (block instanceof IFluidBlock || block instanceof BlockLiquid) {
				// Check if the one fill boolean is false
				if (!oneFill || !nbtCompound.getBoolean("filledAtLeastOnce")) {
					fluidHandler.drain(fluidCapacity, true); // Pre-emptively drain the can to replace the fluid inside if it exists
					FluidStack targetFluidStack = FluidRegistry.getFluidStack(FluidRegistry.getFluidName(FluidRegistry.lookupFluidForBlock(block)), 1000); // Get a fluidstack of the block's fluid
					fluidHandler.fill(targetFluidStack, true); // Fill the can with fluid

					worldIn.playSound(playerIn, playerIn.posX, playerIn.posY, playerIn.posZ, targetFluidStack.getFluid().getFillSound(), SoundCategory.BLOCKS, 1.0F, 1.0F); // Play fluid's bucketing sound

					worldIn.setBlockToAir(blockPos); // Remove the fluid block

					nbtCompound.setBoolean("filledAtLeastOnce", true);
				}
			}
			else
				commenceWatering(fluidStack, playerIn, worldIn, fluidHandler, rayTraceResult);
		}
		return new ActionResult<>(EnumActionResult.PASS, itemStack);
	}

	public void commenceWatering(FluidStack fluidStack, EntityPlayer playerIn, World worldIn, IFluidHandler fluidHandler, RayTraceResult rayTraceResult) {
		if (fluidStack != null) {
			BlockPos rayTraceBlockPos = rayTraceResult.getBlockPos();
			Vec3d rayTraceVector = rayTraceResult.hitVec;

			// Slow player if heavy
			if (heavy) {
				playerIn.addPotionEffect(new PotionEffect(ModContent.SLOW_PLAYER, 5, 5, false, false)); // Slow player
				playerIn.addPotionEffect(new PotionEffect(ModContent.INHIBIT_FOV, 10, 0, false, false)); // Apply secondary, slightly longer potion effect to inhibit FOV changes from slowness
			}

			// Play watering sound
			worldIn.playSound(playerIn, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.WEATHER_RAIN, SoundCategory.BLOCKS, 0.12F, 1.85F);

			// Create water particles
			if (worldIn.isRemote) { // Client only
				for (int i = 0; i < 25; i++) {
					if (fluidStack.isFluidEqual(FluidRegistry.getFluidStack("water", 0)))
						worldIn.spawnParticle(EnumParticleTypes.WATER_SPLASH, rayTraceVector.x + (worldIn.rand.nextGaussian() * 0.18D), rayTraceVector.y, rayTraceVector.z + (worldIn.rand.nextGaussian() * 0.18D), 0.0D, 0.0D, 0.0D);
					else if (fluidStack.isFluidEqual(FluidRegistry.getFluidStack("growth_solution", 0)))
						ParticleGrowthSolution.spawn(worldIn, rayTraceVector.x + (worldIn.rand.nextGaussian() * 0.18D), rayTraceVector.y, rayTraceVector.z + (worldIn.rand.nextGaussian() * 0.18D), 0.0D, 0.0D, 0.0D);
				}
			}

			int reach = this.reach; // Calculate watering can reach
			int halfReach = (int) Math.floor(reach / 2); // Used to calculate offset in each direction
			float growthSpeed; // Calculate growth speed

			if (Config.growthMultiplier != 0.0F) { // Avoid dividing by zero
				growthSpeed = 6F; // Initial speed
				if (fluidStack.isFluidEqual(FluidRegistry.getFluidStack("growth_solution", 0))) // Fluid multiplier
					growthSpeed *= 2F;
				growthSpeed *= innateGrowthMultiplier;
				growthSpeed = Math.max(0, 30F - growthSpeed); // Lower is actually faster, so invert
				growthSpeed = (float) Math.ceil(growthSpeed / Config.growthMultiplier); // Divide by config setting (0-10) as multiplier
			} else {
				growthSpeed = 0.0F;
			}

			// Put out entity fires
			List<EntityLivingBase> affectedMobs = worldIn.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(rayTraceBlockPos.add(-halfReach, -1, -halfReach), rayTraceBlockPos.add(halfReach + 1, 2, halfReach + 1))); // Find mobs
			for (EntityLivingBase mob : affectedMobs) // Loop through found mobs/players
				mob.extinguish(); // Extinguish fire

			// Iterate through total reach
			for (int i = 0; i < reach; i++) {
				for (int j = 0; j < reach; j++) {
					for (int k = -1; k < 2; k++) { // Go down one layer, up two layers
						// Calculate new block position from reach and current Y level
						BlockPos newBlockPos = rayTraceBlockPos.add(i - halfReach, k, j - halfReach);
						Block newBlockObj = worldIn.getBlockState(newBlockPos).getBlock();
						ResourceLocation blockResourceLocation = newBlockObj.getRegistryName();

						// Ensure RL is valid
						if (blockResourceLocation == null)
							continue;

						// Put out block fires
						if (blockResourceLocation.toString().equals("minecraft:fire")) { // If fire
							worldIn.setBlockToAir(newBlockPos); // Remove it
							worldIn.playSound(playerIn, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 1.0F); // Fire extinguish sound
						}

						// Moisten soil/Tick Updates
						if (blockResourceLocation.toString().equals("minecraft:farmland")) // If farmland
							worldIn.setBlockState(newBlockPos, Blocks.FARMLAND.getDefaultState().withProperty(MOISTURE, 7)); // Moisten it
						else // If not farmland, to avoid immediately untilling
							worldIn.updateBlockTick(newBlockPos, newBlockObj, (int) growthSpeed, 0); // Do tick updates
					}
				}
			}
			// Decrease fluid amount
			if (fluidStack.amount > 0 && !playerIn.isCreative())
				fluidHandler.drain(2, true);
		}
	}

	// Fluid item capability
	@Override
	public ICapabilityProvider initCapabilities(ItemStack itemStack, NBTTagCompound nbtCompound) {
		return new FluidHandlerItemStack(itemStack, fluidCapacity);
	}

	// Only animate item re-equip animation if the slot has changed
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		return slotChanged;
	}
}
