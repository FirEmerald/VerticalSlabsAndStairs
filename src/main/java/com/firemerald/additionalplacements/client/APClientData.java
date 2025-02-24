package com.firemerald.additionalplacements.client;

import com.firemerald.additionalplacements.config.APConfigs;
import com.firemerald.additionalplacements.network.server.SetPlacementTogglePacket;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class APClientData
{
	public static final KeyMapping AP_PLACEMENT_KEY = new KeyMapping("key.additionalplacements.placement_toggle", InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), "key.categories.additionalplacements");

	private static boolean placementEnabled = true;
	public static long placementKeyPressTime, lastSynchronizedTime;
	public static boolean placementKeyDown = false;

	public static boolean placementEnabled()
	{
		return placementEnabled;
	}

	public static void setPlacementEnabled(boolean state)
	{
		if (state != placementEnabled) togglePlacementEnabled();
	}

	public static void togglePlacementEnabled()
	{
		setPlacementEnabledAndSynchronize(!placementEnabled, APConfigs.client().togglePlacementLogicStateMessage.get());
	}

	@SuppressWarnings("resource")
	public static void setPlacementEnabledAndSynchronize(boolean state, boolean showMessage)
	{
		placementEnabled = state;
		LocalPlayer player = Minecraft.getInstance().player;
		if (player != null)
		{
			synchronizePlacementEnabled();
			if (showMessage) player.displayClientMessage(Component.translatable(placementEnabled ? "msg.additionalplacements.placement_enable" : "msg.additionalplacements.placement_disable"), true);
		}
	}

	public static void synchronizePlacementEnabled()
	{
		new SetPlacementTogglePacket(placementEnabled).sendToServer();
	}
}