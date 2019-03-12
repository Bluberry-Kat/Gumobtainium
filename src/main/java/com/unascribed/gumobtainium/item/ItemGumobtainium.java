package com.unascribed.gumobtainium.item;

import java.util.List;

import javax.annotation.Nullable;

import com.unascribed.gumobtainium.GumData;
import com.unascribed.gumobtainium.Gumobtainium;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemGumobtainium extends ItemFood {

	public ItemGumobtainium() {
		super(1, 20, false);
		setAlwaysEdible();
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
		ItemStack is = super.onItemUseFinish(stack, worldIn, entityLiving);
		int hearts = GumData.getGumHearts(entityLiving);
		hearts++;
		GumData.setGumHearts(entityLiving, hearts);
		entityLiving.playSound(SoundEvents.ENTITY_SLIME_SQUISH, 1.0f, 0.5f);
		return is;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
		if (GumData.getGumHearts(playerIn) >= Gumobtainium.maxGumHearts) {
			if (!worldIn.isRemote) {
				playerIn.sendStatusMessage(new TextComponentTranslation("msg."+Gumobtainium.MODID+".heartLimit"), true);
			}
			return new ActionResult<>(EnumActionResult.FAIL, playerIn.getHeldItem(handIn));
		}
		return super.onItemRightClick(worldIn, playerIn, handIn);
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
	public int getMaxItemUseDuration(ItemStack stack) {
		return 64;
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		return "§d"+super.getItemStackDisplayName(stack);
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if (isInCreativeTab(tab)) {
			items.add(FluidUtil.getFilledBucket(new FluidStack(Gumobtainium.GELATIN_FLUID, 1)));
			items.add(new ItemStack(this));
		}
	}
	
}
