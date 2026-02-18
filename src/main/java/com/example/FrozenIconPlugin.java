package com.example;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ActorDeath;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.GraphicChanged;
import net.runelite.api.gameval.SpotanimID;
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
    protected final String FROZEN_MSG = "<col=ef1020>You have been frozen!</col>";
    protected final int BIND_ID = 319;
    protected final int BIND_TICKS = 8;
    protected final int SNARE_ID = 320;
    protected final int SNARE_TICKS = 17;
    protected final int ENTANGLE_ID = 321;
    protected final int ENTANGLE_TICKS = 25;
    protected final int ICE_BARRAGE_ID = 328;
    protected final int ICE_BARRAGE_TICKS = 33;
    protected final int ICE_BLITZ_ID = 326;
    protected final int ICE_BLITZ_TICKS = 17;
    protected final int ICE_BURST_ID = 327;
    protected final int ICE_BURST_TICKS = 25;
    protected final int ICE_RUSH_ID = 325;
    protected final int ICE_RUSH_TICKS = 8;
    protected final int TELE_BLOCK_TICKS = 500;
    protected final int TELE_BLOCK_ID = 352;
    protected final int SHIELD_IMMUNITY = 760;

    @Inject
	private Client client;

    @Inject
    private FrozenIconConfig config;

	@Inject
	private FrozenIconOverlay frozenIconOverlay;

	@Inject
	private OverlayManager overlayManager;

	@Getter
	private int stopFreezeTick;

	@Getter
	private boolean isFrozen;

	@Getter
	private int spriteId;

	@Getter
	private int freezeTime;

    @Getter
    private WorldPoint frozenLocation;

    @Getter
    private boolean widthOffset;

    @Getter
    private boolean lengthOffset;

    @Getter
    private boolean isTb;

    @Getter
    private int stopTbTick;

    @Getter
    private int stopImmunityTick;

    @Getter
    private boolean isShield;

    private int freezeTick;

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
        Actor actor = event.getActor();
		if (actor != player)
		{
			return;
		}

        int gfxId = actor.getGraphic();
        switch (gfxId)
		{
            case SpotanimID.BIND_IMPACT: freezeTick = BIND_TICKS; spriteId = BIND_ID; break;
            case SpotanimID.SNARE_IMPACT: freezeTick = SNARE_TICKS; spriteId = SNARE_ID; break;
            case SpotanimID.ENTANGLE_IMPACT: freezeTick = ENTANGLE_TICKS; spriteId = ENTANGLE_ID; break;
            case SpotanimID.TELE_BLOCK_IMPACT:
                if (isTb || !config.showTeleBlock()) return;
                stopTbTick = (player.getOverheadIcon() != HeadIcon.MAGIC ?
                            TELE_BLOCK_TICKS:(TELE_BLOCK_TICKS/2)) + client.getTickCount();
                isTb = true;
                return;
			default: return;
		}
		isFrozen = true;
		freezeTime = freezeTick;
        stopFreezeTick = freezeTick + client.getTickCount();
        frozenLocation = player.getWorldLocation();
    }

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.SPAM && event.getType() != ChatMessageType.GAMEMESSAGE)
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
        Player player = client.getLocalPlayer();
        WorldPoint currentWorldPoint = player.getWorldLocation();

        if (freezePending)
        {
            findIceSpell();
            frozenLocation = player.getWorldLocation();
            freezePending = false;
        }

        int currTick = client.getTickCount();

        if (isTb && (currTick >= stopTbTick || (player.getInteracting() != null && player.getInteracting().isDead())))
        {
            resetTb();
        }

        if (isShield && currTick >= stopImmunityTick) resetImmunity();

        if (isFrozen && (currTick >= stopFreezeTick || !currentWorldPoint.equals(frozenLocation)))
        {
            resetFreeze();
            if (config.showImmunity())
            {
                isShield = true;
                stopImmunityTick = 5 + client.getTickCount();
            }
        }
        freezeTime = freezeTime > 0 ? freezeTime-1:freezeTime;
    }

    @Subscribe
    public void onActorDeath(ActorDeath actorDeath) {
        Player player = client.getLocalPlayer();
        if (actorDeath.getActor() == player)
        {
            resetFreeze();
            resetTb();
            resetImmunity();
        }
    }

	private void findIceSpell()
	{
		Player player = client.getLocalPlayer();

        int gfxId = player.getGraphic();
        switch (gfxId)
        {
            case SpotanimID.ICE_BARRAGE_IMPACT: freezeTick = ICE_BARRAGE_TICKS; spriteId = ICE_BARRAGE_ID; break;
            case SpotanimID.ICE_BURST_IMPACT: freezeTick = ICE_BURST_TICKS; spriteId = ICE_BURST_ID; lengthOffset = true; break;
            case SpotanimID.ICE_BLITZ_IMPACT: freezeTick = ICE_BLITZ_TICKS; spriteId = ICE_BLITZ_ID; break;
            case SpotanimID.ICE_RUSH_IMPACT: freezeTick = ICE_RUSH_TICKS;  spriteId = ICE_RUSH_ID; widthOffset = true; break;
            default: return;
        }

		isFrozen = true;
		freezeTime = freezeTick;
        stopFreezeTick = freezeTick + client.getTickCount();
	}

    private void resetFreeze()
    {
        spriteId = 0;
        freezeTick = 0;
        stopFreezeTick = 0;
        freezeTime = 0;
        widthOffset = false;
        lengthOffset = false;
        isFrozen = false;
    }

    private void resetTb()
    {
        isTb = false;
        stopTbTick = 0;
    }

    private void resetImmunity()
    {
        isShield = false;
        stopImmunityTick = 0;
    }
}
