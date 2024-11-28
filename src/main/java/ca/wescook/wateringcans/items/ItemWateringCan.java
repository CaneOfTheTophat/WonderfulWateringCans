package ca.wescook.wateringcans.items;

import ca.wescook.wateringcans.ModContent;
import ca.wescook.wateringcans.configs.Config;
import ca.wescook.wateringcans.particles.ParticleGrowthSolution;
import net.minecraft.block.Block;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

import static ca.wescook.wateringcans.WateringCans.*;
import static java.util.Arrays.asList;
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

		this.fluidCapacity = fluidCapacity;
		this.innateGrowthMultiplier = innateGrowthMultiplier;
		this.reach = reach;
		this.oneFill = oneFill;
		this.heavy = heavy;

		addPropertyOverride(new ResourceLocation(MODID,"petal"), new IItemPropertyGetter() {
			@SideOnly(Side.CLIENT)
			@Override
			public float apply(ItemStack stack, @Nullable World worldIn, @Nullable EntityLivingBase entityIn) {
				// Get NBT data
				NBTTagCompound nbtCompound = stack.getTagCompound();
				if (nbtCompound != null) {
					short amountRemaining = nbtCompound.getShort("amount");

					return (float) amountRemaining / fluidCapacity;
				}
				return 0F;
			}
		});
	}

	// Don't animate re-equipping item
	@Override
	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
		// Grab NBT data
		NBTTagCompound oldNBT = oldStack.getTagCompound();
		NBTTagCompound newNBT = newStack.getTagCompound();

		// If fluid type match, don't reanimate
		if (oldNBT != null && newNBT != null) { // NBT exists
			if (oldNBT.getString("fluid").equals(newNBT.getString("fluid")))
				return false; // Only fluid amount changed, don't animate
		}
		return slotChanged;
	}

	// Add tooltips
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		NBTTagCompound compound = stack.getTagCompound();
		if (compound == null || compound.getShort("amount") <= 0) {
				tooltip.add(I18n.format("tooltip." + stack.getItem().getRegistryName().toString() + "", TextFormatting.DARK_GRAY)); // Display material tooltip
			} else {
			String fluid = compound.getString("fluid"); // Get fluid type
			tooltip.add(I18n.format("tooltip.wateringcans:contains") + ": " + I18n.format(fluids.get(fluid))); // Get localization string of fluid and add to tooltip
			tooltip.add(I18n.format("tooltip.wateringcans:remaining") + ": " + compound.getShort("amount"));
		}
	}


	// On right click
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		// List of valid fluid blocks
		String[] validBlocks = new String[]{"water", MODID + ":growth_solution_block"};

		// Ray trace - find block we're looking at
		RayTraceResult rayTraceResult = this.rayTrace(worldIn, playerIn, true);

		// Check for/create NBT tag
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		NBTTagCompound nbtCompound = itemstack.getTagCompound(); // Check if exists
		if (nbtCompound == null) { // If not
			nbtCompound = new NBTTagCompound(); // Create new compound
			itemstack.setTagCompound(nbtCompound); // Attach to itemstack
		}

		// If sky, ignore
		if (rayTraceResult != null) {
			// If a block is found (includes fluids)
			if (rayTraceResult.typeOfHit == RayTraceResult.Type.BLOCK) {

				// Get exact vector to later spawn particles there
				Vec3d rayTraceVector = rayTraceResult.hitVec;

				// Collect information about block
				BlockPos blockPos = rayTraceResult.getBlockPos(); // Get block position from ray trace
				Block blockObj = worldIn.getBlockState(blockPos).getBlock(); // Get block object
				String blockNameRaw = blockObj.getUnlocalizedName(); // Get block name
				String blockName = blockNameRaw.substring(5); // Clean .tile prefix

				// If found block is in fluid list, refill watering can
				if (asList(validBlocks).contains(blockName))
					refillWateringCan(worldIn, playerIn, nbtCompound, blockName, blockPos);
				else // Water that block
					commenceWatering(worldIn, playerIn, itemstack, nbtCompound, rayTraceVector, blockPos);
			}
		}
		return new ActionResult<ItemStack>(EnumActionResult.PASS, itemstack); // PASS instead of SUCCESS so we can dual wield watering cans
	}

	private void refillWateringCan(World worldIn, EntityPlayer playerIn, NBTTagCompound nbtCompound, String blockName, BlockPos blockPos) {
		if (oneFill) {
			if (!nbtCompound.getBoolean("filledOnce"))
				nbtCompound.setBoolean("filledOnce", true); // Set flag once and continue
			else {
				worldIn.playSound(playerIn, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F); // Tool break sound
				return; // Exit method
			}
		}

		// Water refill sound
		worldIn.playSound(playerIn, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);

		// Destroy source block
		if (!playerIn.isCreative())
			worldIn.setBlockToAir(blockPos);

		// Create bubbles
		if (worldIn.isRemote) {
			for (int i = 0; i < 15; i++)
				worldIn.spawnParticle(EnumParticleTypes.WATER_BUBBLE, blockPos.getX() + 0.5 + (worldIn.rand.nextGaussian() * 0.3D), blockPos.getY() + 1, blockPos.getZ() + 0.5 + (worldIn.rand.nextGaussian() * 0.3D), 0.0D, 0.0D, 0.0D);
		}

		// Assign fluid based on block
		if (blockName.equals("water"))
			nbtCompound.setString("fluid", "water");
		else if (blockName.equals(MODID + ":growth_solution_block"))
			nbtCompound.setString("fluid", "growth_solution");

		// Refill watering can
		nbtCompound.setShort("amount", fluidCapacity);
	}

	private void commenceWatering(World worldIn, EntityPlayer playerIn, ItemStack itemStackIn, NBTTagCompound nbtCompound, Vec3d rayTraceVector, BlockPos rayTraceBlockPos) {
		// Get info
		String fluid = nbtCompound.getString("fluid");
		short amountRemaining = nbtCompound.getShort("amount");

		// If water remains in can
		if (amountRemaining > 0) {
			// Set player as currently watering (via potions because onItemUseFinish is too limiting)
			playerIn.addPotionEffect(new PotionEffect(ModContent.USING_WATERING_CAN, 6, 0, false, false)); // Set player to "using can"

			// Slow player
			if (heavy) {
				playerIn.addPotionEffect(new PotionEffect(ModContent.SLOW_PLAYER, 5, 5, false, false)); // Slow player
				playerIn.addPotionEffect(new PotionEffect(ModContent.INHIBIT_FOV, 10, 0, false, false)); // Apply secondary, slightly longer potion effect to inhibit FOV changes from slowness
			}

			// Play watering sound
			worldIn.playSound(playerIn, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.WEATHER_RAIN, SoundCategory.BLOCKS, 0.12F, 1.85F);

			// Create water particles
			if (worldIn.isRemote) { // Client only
				for (int i = 0; i < 25; i++) {
					if (fluid.equals("water"))
						worldIn.spawnParticle(EnumParticleTypes.WATER_SPLASH, rayTraceVector.x + (worldIn.rand.nextGaussian() * 0.18D), rayTraceVector.y, rayTraceVector.z + (worldIn.rand.nextGaussian() * 0.18D), 0.0D, 0.0D, 0.0D);
					else if (fluid.equals("growth_solution"))
						ParticleGrowthSolution.spawn(worldIn, rayTraceVector.x + (worldIn.rand.nextGaussian() * 0.18D), rayTraceVector.y, rayTraceVector.z + (worldIn.rand.nextGaussian() * 0.18D), 0.0D, 0.0D, 0.0D);
				}
			}

			// Calculate watering can reach
			int reach = this.reach;

			// Used to calculate offset in each direction
			int halfReach = (int) Math.floor(reach / 2);

			// Calculate growth speed
			float growthSpeed;

			if (Config.growthMultiplier != 0.0F) { // Avoid dividing by zero
				growthSpeed = 6F; // Initial speed
				if (fluid.equals("growth_solution")) // Fluid multiplier
					growthSpeed *= 2F;
				growthSpeed *= innateGrowthMultiplier;
				growthSpeed = Math.max(0, 30F - growthSpeed); // Lower is actually faster, so invert
				growthSpeed = (float) Math.ceil(growthSpeed / Config.growthMultiplier); // Divide by config setting (0-10) as multiplier
			}
			else {
				growthSpeed = 0.0F;
			}

			// Put out entity fires
			List<EntityLivingBase> affectedMobs = worldIn.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(rayTraceBlockPos.add(-halfReach, -1, -halfReach), rayTraceBlockPos.add(halfReach + 1, 2, halfReach + 1))); // Find mobs
			for (EntityLivingBase mob : affectedMobs) // Loop through found mobs/players
				mob.extinguish(); // Extinguish fire

			// Iterate through total reach
			for (int i=0; i<reach; i++) {
				for (int j=0; j<reach; j++) {
					for (int k=-1; k<2; k++) { // Go down one layer, up two layers
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
			if (amountRemaining > 0 && !playerIn.isCreative()) {
					nbtCompound.setShort("amount", (short) (amountRemaining - 1));
			}
		}
		else {
			// If gold can is empty, destroy it
			if (oneFill && nbtCompound.getBoolean("filledOnce")) {
				// Get slot of active watering can (hand or hotbar)
				int slot = playerIn.inventory.getSlotFor(itemStackIn); // Get ID from itemstack (returns -1 in offhand)
				final int handSlot = 40; // Offhand slot

				if (slot == -1) { // Probably in offhand, but let's verify
					if (playerIn.inventory.getStackInSlot(handSlot) == itemStackIn) // Checking offhand
						slot = handSlot; // It's in the offhand
				}

				playerIn.inventory.setInventorySlotContents(slot, ItemStack.EMPTY); // Delete item
				worldIn.playSound(playerIn, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 1.0F, 1.0F); // Tool break sound
			}
		}
	}
}
