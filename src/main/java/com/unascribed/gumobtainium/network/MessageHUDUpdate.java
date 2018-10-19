package com.unascribed.gumobtainium.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import com.unascribed.gumobtainium.GumData;
import com.unascribed.gumobtainium.Gumobtainium;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ReceivedOn(Side.CLIENT)
public class MessageHUDUpdate extends Message {

	@MarshalledAs("u8")
	private int filledGumHearts;
	@MarshalledAs("u8")
	private int gumHearts;
	private boolean hasGumbium;
	
	public MessageHUDUpdate(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageHUDUpdate(int filledGumHearts, int gumHearts, boolean hasGumbium) {
		super(Gumobtainium.NETWORK);
		this.filledGumHearts = filledGumHearts;
		this.gumHearts = gumHearts;
		this.hasGumbium = hasGumbium;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityPlayer ep) {
		GumData.setFilledGumHearts(ep, filledGumHearts);
		GumData.setGumHearts(ep, gumHearts);
		GumData.setHasGumbium(ep, hasGumbium);
	}

}
