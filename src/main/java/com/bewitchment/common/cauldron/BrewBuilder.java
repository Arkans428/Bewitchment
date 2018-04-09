package com.bewitchment.common.cauldron;

import java.util.List;
import java.util.Optional;

import com.bewitchment.api.cauldron.IBrewEffect;
import com.bewitchment.api.cauldron.IBrewModifierList;
import com.bewitchment.common.cauldron.BrewData.BrewEntry;
import com.bewitchment.common.crafting.cauldron.CauldronRegistry;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class BrewBuilder {
	
	List<ItemStack> list;
	
	public BrewBuilder(List<ItemStack> list) {
		this.list = list;
	}
	
	public Optional<BrewData> build() {
		BrewData data = new BrewData();
		IBrewEffect currentEffect = null;
		IBrewModifierList modList = null;
		for (ItemStack stack : list) {
			
			if (currentEffect != null) { // Check if the next item is a valid modifier for the current potion
				Optional<IBrewModifierList> newList = CauldronRegistry.getModifierListFromStack(stack, modList, currentEffect);
				if (newList == null) {// Null means potion failed, not present means pass
					return Optional.empty();
				}
				if (newList.isPresent()) {
					modList = newList.get();
					continue;
				}
			}
			
			Optional<IBrewEffect> newBrew = CauldronRegistry.getBrewFromStack(stack);
			if (newBrew.isPresent()) {
				
				if (currentEffect != null) {
					data.addEntry(new BrewEntry(CauldronRegistry.getPotionFromBrew(currentEffect), new BrewModifierListImpl(modList)));
					ResourceLocation newPot = CauldronRegistry.getPotionFromBrew(newBrew.get()).getRegistryName();
					if (data.getEffects().stream().map(be -> be.getPotion().getRegistryName()).anyMatch(p -> p.equals(newPot))) {
						return Optional.empty();
					}
				}
				
				currentEffect = newBrew.get();
				modList = new BrewModifierListImpl();
				continue;
			}
			return Optional.empty();
		}
		if (currentEffect != null) {
			data.addEntry(new BrewEntry(CauldronRegistry.getPotionFromBrew(currentEffect), new BrewModifierListImpl(modList)));
		}
		if (data.getEffects().isEmpty()) {
			return Optional.empty();
		}
		return Optional.of(data);
	}
}