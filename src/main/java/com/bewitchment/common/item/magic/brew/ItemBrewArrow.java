package com.bewitchment.common.item.magic.brew;

import java.util.List;
import java.util.Optional;

import com.bewitchment.api.cauldron.IBrewModifierList;
import com.bewitchment.api.cauldron.modifiers.BewitchmentModifiers;
import com.bewitchment.client.core.IModelRegister;
import com.bewitchment.common.cauldron.BrewData;
import com.bewitchment.common.cauldron.BrewData.BrewEntry;
import com.bewitchment.common.cauldron.BrewModifierListImpl;
import com.bewitchment.common.core.ModCreativeTabs;
import com.bewitchment.common.crafting.cauldron.CauldronRegistry;
import com.bewitchment.common.lib.LibItemName;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBrewArrow extends ItemArrow implements IModelRegister {
	
	public ItemBrewArrow() {
		super();
		setRegistryName(LibItemName.BREW_ARROW);
		setUnlocalizedName(LibItemName.BREW_ARROW);
		setCreativeTab(ModCreativeTabs.BREW_CREATIVE_TAB);
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (this.isInCreativeTab(tab)) {
			CauldronRegistry.BREW_POTION_MAP.values().forEach(p -> addPotionType(items, p));
		}
	}
	
	private void addPotionType(NonNullList<ItemStack> items, Potion p) {
		BrewData data = new BrewData();
		BrewModifierListImpl list = new BrewModifierListImpl();
		data.addEntry(new BrewEntry(p, list));
		ItemStack stack = new ItemStack(this);
		data.saveToStack(stack);
		items.add(stack);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		BrewData.fromStack(stack).getEffects().forEach(brewEntry -> {
			TextFormatting color = brewEntry.getPotion().isBadEffect() ? TextFormatting.RED : TextFormatting.DARK_AQUA;
			IBrewModifierList list = brewEntry.getModifierList();
			if (GuiScreen.isShiftKeyDown()) {
				tooltip.add(color + I18n.format("effect." + brewEntry.getPotion().getRegistryName().getResourcePath() + ".tooltip"));
				list.getModifiers().stream().filter(modifier -> list.getLevel(modifier).isPresent()).forEach(bm -> {
					tooltip.add(I18n.format("brew.parameters.formatting", bm.getTooltipString(brewEntry.getModifierList().getLevel(bm).get())));
				});
			} else {
				Optional<Integer> lvl = list.getLevel(BewitchmentModifiers.POWER);
				if (lvl.isPresent() && lvl.get() > 1) {
					tooltip.add(color + I18n.format("effect." + brewEntry.getPotion().getRegistryName().getResourcePath() + ".tooltip") + " " + lvl.get()); // TODO fix roman
				} else {
					tooltip.add(color + I18n.format("effect." + brewEntry.getPotion().getRegistryName().getResourcePath() + ".tooltip"));
				}
				
				String ref = TextFormatting.DARK_GRAY + I18n.format("effect." + brewEntry.getPotion().getRegistryName().getResourcePath() + ".desc");
				tooltip.add(I18n.format("brew.description.formatting", ref));
			}
		});
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerModel() {
		ModelResourceLocation modelResourceLocation = new ModelResourceLocation(Items.TIPPED_ARROW.getRegistryName(), "inventory");
		ModelLoader.setCustomModelResourceLocation(this, 0, modelResourceLocation);
	}
	
}
