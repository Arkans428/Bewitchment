package com.bewitchment.common.divination.fortunes;

import com.bewitchment.api.divination.Fortune;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;

public class FortuneMeetDog extends Fortune {

	public FortuneMeetDog(int weight, String name, String modid) {
		super(weight, name, modid);
	}

	@Override
	public boolean canBeUsedFor(EntityPlayer player) {
		return true;
	}

	@Override
	public boolean canShouldBeAppliedNow(EntityPlayer player) {
		return player.dimension != 1 && player.dimension != -1 && player.getRNG().nextDouble() < 0.0001;
	}

	@Override
	public boolean apply(EntityPlayer player) {
		for (int i = 0; i < 10; i++) {
			BlockPos pos = new BlockPos(player.posX + player.getRNG().nextGaussian() * 4, player.posY, player.posZ + player.getRNG().nextGaussian() * 4);
			EntityWolf wolf = new EntityWolf(player.world);
			if (player.world.isAirBlock(pos) && player.world.isAirBlock(pos.up()) && player.world.getBlockState(pos.down()).canEntitySpawn(wolf)) {
				wolf.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
				wolf.onInitialSpawn(player.world.getDifficultyForLocation(pos), null);
				wolf.setTamedBy(player);
				player.world.spawnEntity(wolf);
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isNegative() {
		return false;
	}

}
