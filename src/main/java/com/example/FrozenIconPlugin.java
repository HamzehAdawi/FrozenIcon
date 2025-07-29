package com.example;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Frozen Icon"
)
public class FrozenIconPlugin extends Plugin
{
	private static final String FROZEN_MSG = "<col=ef1020>You have been frozen!</col>";

	@Inject
	private Client client;

	@Inject
	private FrozenIconOverlay frozenIconOverlay;

	@Inject
	private OverlayManager overlayManager;

	@Getter(AccessLevel.PACKAGE)
	private int freezeStartTick;

	@Getter(AccessLevel.PACKAGE)
	private int freezeTick;

	@Getter(AccessLevel.PACKAGE)
	private boolean isFrozen;

	@Getter(AccessLevel.PACKAGE)
	private int immunity;

	@Getter(AccessLevel.PACKAGE)
	private int spriteId;

	private boolean freezePending;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(frozenIconOverlay);
		log.info("Frozen icon plug in started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(frozenIconOverlay);
		log.info("Frozen icon plug in stopped!");
	}

	@Provides
	FrozenIconConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(FrozenIconConfig.class);
	}

	@Subscribe
	public void onGraphicChanged(GraphicChanged event)
	{

		Player player = client.getLocalPlayer();
		if (event.getActor() != player)
		{
			return;
		}

		int gfxId = player.getGraphic();
		switch (gfxId)
		{
			case 181: freezeTick = 8;  spriteId = 319; break; // Bind
			case 180: freezeTick = 17; spriteId = 320; break; // Snare
			case 179: freezeTick = 25; spriteId = 321; break; // Entangle
			default: return;
		}
		isFrozen = true;
		freezeStartTick = client.getTickCount();
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if  (event.getType() != ChatMessageType.SPAM && event.getType() != ChatMessageType.GAMEMESSAGE)
		{
			return;
		}

		String message = event.getMessage();
		if (message.contains(FROZEN_MSG))
		{
			freezePending = true;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (freezePending)
		{
			findIceSpell();
			freezePending = false;
		}
		if (isFrozen && client.getTickCount() > freezeStartTick + freezeTick + immunity)
		{
			isFrozen = false;
			freezeStartTick = 0;
			freezeTick = 0;
			spriteId = 0;
			immunity = 0;
		}
	}

	private void findIceSpell()
	{

		Player player = client.getLocalPlayer();
		int gfxId = player.getGraphic();

		switch (gfxId)
		{
			case 369: freezeTick = 33; spriteId = 328; break; // Ice Barrage
			case 367: freezeTick = 25; spriteId = 327; break; // Ice Blitz
			case 363: freezeTick = 17; spriteId = 326; break; // Ice Burst
			case 361: freezeTick = 8;  spriteId = 325; break; // Ice Rush
			default: return;
		}
		isFrozen = true;
		freezeStartTick = client.getTickCount();
		immunity = 5;
	}
}
