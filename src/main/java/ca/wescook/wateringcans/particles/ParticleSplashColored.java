package ca.wescook.wateringcans.particles;

import ca.wescook.wateringcans.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleSplash;
import net.minecraft.world.World;

public class ParticleSplashColored extends ParticleSplash {

	// Forwarding all the same arguments
	private ParticleSplashColored(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int color) {
		super(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
		this.setRBGColorF(Util.colorFloatFromDecimal(color, 0), Util.colorFloatFromDecimal(color, 2), Util.colorFloatFromDecimal(color, 4)); // Set color
		this.particleAlpha = 0.7F; // Set alpha
	}

	// Method called directly to spawn particles
	public static void spawn(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int color) {
		Minecraft.getMinecraft().effectRenderer.addEffect(new ParticleSplashColored(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn, color));
	}
}