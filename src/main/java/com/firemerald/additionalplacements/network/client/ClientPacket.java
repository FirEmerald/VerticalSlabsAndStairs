package com.firemerald.additionalplacements.network.client;

import com.firemerald.additionalplacements.network.APNetwork;
import com.firemerald.additionalplacements.network.APPacket;

import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public abstract class ClientPacket<T extends IPayloadContext> extends APPacket<T>
{
	@Override
	public PacketFlow getDirection() {
		return PacketFlow.CLIENTBOUND;
	}
	
	@OnlyIn(Dist.CLIENT)
	public abstract void handleClient(T context);
	
	@Override
	public void handleInternal(T context)
	{
		handleClient(context);
	}
	
    public void sendToClient(ServerPlayer player)
    {
    	APNetwork.sendToClient(this, player);
    }

    public void sendToAllClients()
    {
    	APNetwork.sendToAllClients(this);
    }
}