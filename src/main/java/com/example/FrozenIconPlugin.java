package com.example;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Frozen Icon"
)
public class FrozenIconPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private FrozenIconConfig config;

	@Inject
	FrozenIconOverlay equipmentOverlay;

	@Inject
	OverlayManager overlayManager;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(equipmentOverlay);
		log.info("Frozen icon plug in started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(equipmentOverlay);
		log.info("Frozen icon plug in stopped!");
	}

	@Provides
	FrozenIconConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(FrozenIconConfig.class);
	}
}
