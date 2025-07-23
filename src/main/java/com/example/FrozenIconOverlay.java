package com.example;

import net.runelite.api.*;
import net.runelite.api.Point;
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
    private int spriteId;

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




        if (!isFrozen)
        {
            isFrozen = freezeDetect(player.getGraphic());
        }
        int currentTick = client.getTickCount();


        if (isFrozen && currentTick <= freezeStartTick + freezeTick)
        {
            final BufferedImage iceBarrageIcon = spriteManager.getSprite(spriteId, 0);
            if (iceBarrageIcon == null)
            {
                return null;
            }
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
            spriteId = 0;
        }

        return null;
    }


    private boolean freezeDetect(int gfxId)
    {
        switch (gfxId)
        {
            case 369: freezeTick = 33; spriteId = 328; break; // Ice Barrage
            case 367: freezeTick = 25; spriteId = 327; break; // Ice Blitz
            case 363: freezeTick = 17; spriteId = 326; break; // Ice Burst
            case 362: freezeTick = 8; spriteId = 325; break;  // Ice Rush
            case 181: freezeTick = 8; spriteId = 319; break;  // Bind
            case 180: freezeTick = 17; spriteId = 320; break; // Snare
            case 179: freezeTick = 25; spriteId = 321; break; // Entangle
            default: return false;
        }

        freezeStartTick = client.getTickCount();
        return true;

    }
}
