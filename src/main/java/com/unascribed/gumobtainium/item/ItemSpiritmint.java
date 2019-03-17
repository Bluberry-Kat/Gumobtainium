package com.unascribed.gumobtainium.item;

import java.util.List;

import javax.annotation.Nullable;

import com.unascribed.gumobtainium.GumData;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.item.ItemFood;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class ItemSpiritmint extends ItemFood {
	
	public ItemSpiritmint() {
		super(9, 1f, false);
		setAlwaysEdible();
	}

	
//	@Override
//	public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
//		return oldStack.getItem() != newStack.getItem() || slotChanged;
//	}
	
	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
		BlockPos pos = entityIn.getPosition();
		if (worldIn.isRemote) return;
		if (worldIn.getLight(pos, true) <= 7) {
			stack.setItemDamage(1);
		} 
		else {
			stack.setItemDamage(0);
		}
		super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected);
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		ItemStack is = super.onItemUseFinish(stack, worldIn, entityLiving);
		entityLiving.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 1.0f, 0.5f);
		entityLiving.playSound(SoundEvents.ENTITY_GHAST_DEATH, 0.2f, 0.6f);
		return is;
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 64;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
		int i = 0;
		while (I18n.hasKey(getTranslationKey()+".tooltip."+i)) {
			tooltip.add("§5§o"+I18n.format(getTranslationKey()+".tooltip."+i));
			i++;
		}
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return "§b"+super.getItemStackDisplayName(stack);
	}

}
