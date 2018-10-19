package com.unascribed.gumobtainium.network;

import com.elytradev.concrete.network.Message;
import com.elytradev.concrete.network.NetworkContext;
import com.elytradev.concrete.network.annotation.field.MarshalledAs;
import com.elytradev.concrete.network.annotation.type.ReceivedOn;
import com.elytradev.concrete.reflect.accessor.Accessor;
import com.elytradev.concrete.reflect.accessor.Accessors;
import com.unascribed.gumobtainium.Gumobtainium;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@ReceivedOn(Side.CLIENT)
public class MessageItemSoaking extends Message {
	@MarshalledAs("i32")
	private int entityId;
	@MarshalledAs("u16")
	private int ticksSoaked;
	
	public MessageItemSoaking(NetworkContext ctx) {
		super(ctx);
	}
	
	public MessageItemSoaking(Entity entity, int ticks) {
		super(Gumobtainium.NETWORK);
		this.entityId = entity.getEntityId();
		this.ticksSoaked = ticks;
	}
	
	private static final Accessor<Integer> age = Accessors.findField(EntityItem.class, "age", "field_70292_b");
	private static final Accessor<Integer> pickupDelay = Accessors.findField(EntityItem.class, "pickupDelay", "field_145804_b");
	
	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityPlayer ep) {
		Entity e = ep.world.getEntityByID(entityId);
		if (e != null && e instanceof EntityItem) {
			/*if (!(e instanceof EntityItemSoaking)) {
				EntityItemSoaking eis = new EntityItemSoaking(e.world, e.posX, e.posY, e.posZ, ((EntityItem)e).getItem());
				eis.lifespan = ((EntityItem)e).lifespan;
				eis.setUniqueId(e.getUniqueID());
				age.set(eis, age.get(e));
				pickupDelay.set(eis, pickupDelay.get(e));
				eis.setVelocity(e.motionX, e.motionY, e.motionZ);
				Minecraft.getMinecraft().world.removeEntityDangerously(e);
				Minecraft.getMinecraft().world.addEntityToWorld(entityId, eis);
				e = eis;
			}*/
			e.getEntityData().setInteger(Gumobtainium.MODID+":ticksInWater", ticksSoaked);
		}
	}

}
