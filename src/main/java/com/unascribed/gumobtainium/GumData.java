package com.unascribed.gumobtainium;

import com.unascribed.gumobtainium.network.MessageHUDUpdate;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public class GumData {

	public static int getGumHearts(Entity en) {
		return en.getEntityData().getInteger(Gumobtainium.MODID+":gumHearts");
	}
	
	public static int getFilledGumHearts(Entity en) {
		return en.getEntityData().getInteger(Gumobtainium.MODID+":filledGumHearts");
	}
	
	public static boolean hasGumbium(Entity en) {
		return en.getEntityData().getBoolean(Gumobtainium.MODID+":hasGumbium");
	}
	
	public static int getTicksSinceLastDamage(Entity en) {
		return en.getEntityData().getInteger(Gumobtainium.MODID+":ticksSinceLastDamage");
	}
	
	
	public static void setGumHearts(Entity en, int gumHearts) {
		en.getEntityData().setInteger(Gumobtainium.MODID+":gumHearts", gumHearts);
		update(en);
	}
	
	public static void setFilledGumHearts(Entity en, int filledGumHearts) {
		en.getEntityData().setInteger(Gumobtainium.MODID+":filledGumHearts", filledGumHearts);
		update(en);
	}
	
	public static void setHasGumbium(Entity en, boolean hasGumbium) {
		en.getEntityData().setBoolean(Gumobtainium.MODID+":hasGumbium", hasGumbium);
		update(en);
	}
	
	public static void setTicksSinceLastDamage(Entity en, int ticksSinceLastDamage) {
		en.getEntityData().setInteger(Gumobtainium.MODID+":ticksSinceLastDamage", ticksSinceLastDamage);
	}
	

	public static void update(Entity en) {
		if (en instanceof EntityPlayer) {
			new MessageHUDUpdate(getFilledGumHearts(en), getGumHearts(en), hasGumbium(en)).sendTo((EntityPlayer)en);
		}
	}
}
