package com.example;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.SpriteID;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class FrozenIconOverlay extends Overlay {

    private final Client client;
    private final SpriteManager spriteManager;
    private final FrozenIconConfig config;
    private int freezeStartTick;
    private int freezeTick;
    private boolean isFrozen = false;

    @Inject
    FrozenIconOverlay(Client client, SpriteManager spriteManager,  FrozenIconConfig config)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.spriteManager = spriteManager;
        this.config = config;

    }

    @Override
    public Dimension render(Graphics2D graphics2D)
    {
        Player player = client.getLocalPlayer();
        if (player == null || player.getPlayerComposition() == null)
        {
            return null;
        }

        final BufferedImage iceBarrageIcon = spriteManager.getSprite(SpriteID.SPELL_ICE_BARRAGE, 0);
        if (iceBarrageIcon == null)
        {
            return null;
        }


        if (!isFrozen)
        {
            isFrozen = freezeDetect(player.getGraphic());
        }
        int currentTick = client.getTickCount();


        if (isFrozen && currentTick <= freezeStartTick + freezeTick)
        {
            int modelHeight = player.getLogicalHeight();
            Point canvasPoint = Perspective.getCanvasImageLocation(client, player.getLocalLocation(), iceBarrageIcon, modelHeight);
            if (canvasPoint != null) {
                graphics2D.drawImage(
                        iceBarrageIcon,
                        canvasPoint.getX() + 25,
                        canvasPoint.getY() - 5,
                        16 + config.size(),
                        16 + config.size(),
                        null
                );
            }
        }
        else
        {
            isFrozen = false;
            freezeStartTick = 0;
            freezeTick = 0;
        }

        return null;
    }


    private boolean freezeDetect(int gfxId)
    {
        switch (gfxId)
        {
            case 369: freezeTick = 33; break; // Ice Barrage
            case 367: freezeTick = 25; break; // Ice Blitz
            case 363: freezeTick = 17; break; // Ice Burst
            case 362: freezeTick = 8; break;  // Ice Rush
            case 181: freezeTick = 8; break;  // Bind
            case 180: freezeTick = 17; break; // Snare
            case 179: freezeTick = 25; break; // Entangle
            default: return false;
        }

        freezeStartTick = client.getTickCount();
        return true;

    }
}
