package com.example;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class FrozenIconOverlay extends Overlay
{

    private final Client client;
    private final SpriteManager spriteManager;
    private final FrozenIconConfig config;
    private final FrozenIconPlugin plugin;

    @Inject
    FrozenIconOverlay(Client client, SpriteManager spriteManager, FrozenIconConfig config, FrozenIconPlugin plugin)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.spriteManager = spriteManager;
        this.config = config;
        this.plugin = plugin;

    }

    @Override
    public Dimension render(Graphics2D graphics2D)
    {
        Player player = client.getLocalPlayer();
        if (player == null || player.getPlayerComposition() == null)
        {
            return null;
        }

        int currentTick = client.getTickCount();
        int freezeDuration =  plugin.getFreezeStartTick() + plugin.getFreezeTick() + plugin.getImmunity();

        if (plugin.isFrozen() && currentTick < freezeDuration)
        {
            BufferedImage iceBarrageIcon = spriteManager.getSprite(plugin.getSpriteId(), 0);

            if (iceBarrageIcon == null || currentTick > freezeDuration - plugin.getImmunity())
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
            plugin.freezeFinished();
        }

        return null;
    }
}
