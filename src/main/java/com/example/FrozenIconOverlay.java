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
import java.util.ArrayList;
import java.util.List;

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

        if (plugin.isFrozen() || (plugin.isTb() && config.showTeleBlock())
            || (plugin.isShield() && config.showImmunity()))
        {
            int adjustIcon = 5;
            List<Integer> icons = new ArrayList<>();
            int frozenIcon = plugin.isFrozen() ? plugin.getSpriteId():
                    (plugin.isShield() ? plugin.SHIELD_IMMUNITY:0);

            if (plugin.isTb()) icons.add(plugin.TELE_BLOCK_ID);

            if (frozenIcon != 0) icons.add(0,frozenIcon);

            for (int icon : icons)
            {
                BufferedImage iconImg = spriteManager.getSprite(icon, 0);
                int shieldAdj = icon == plugin.SHIELD_IMMUNITY ? 5:0;

                if (icon != 0 && iconImg != null)
                {
                    int modelHeight = player.getLogicalHeight();
                    Point canvasPoint = Perspective.getCanvasImageLocation(
                            client,
                            player.getLocalLocation(),
                            iconImg,
                            modelHeight);

                    int yOffset = plugin.isLengthOffset() ? 8:16;
                    int xOffset = plugin.isWidthOffset() ? 8:16;
                    if (canvasPoint != null)
                    {
                        int iconOffset = config.iconOrientation() == IconOrientation.RIGHT ? 25:-25;
                        graphics2D.drawImage(
                                iconImg,
                                canvasPoint.getX() + iconOffset + shieldAdj + (icon == plugin.ICE_BURST_ID ? 6:0),
                                canvasPoint.getY() - adjustIcon + shieldAdj,
                                config.size() + yOffset - shieldAdj,
                                config.size() + xOffset - shieldAdj,
                                null
                        );

                        if (config.freezeTimer() && icon != plugin.SHIELD_IMMUNITY
                            && icon != plugin.TELE_BLOCK_ID)
                        {
                            if (plugin.getFreezeTime() <= 5)
                            {
                                graphics2D.setColor(Color.RED);
                            }

                            graphics2D.setFont(graphics2D.getFont().deriveFont(
                                    graphics2D.getFont().getStyle(),
                                    graphics2D.getFont().getSize() + config.size()
                            ));

                            int timerOffset = config.iconOrientation() == IconOrientation.RIGHT ? 45:-45;
                            graphics2D.drawString(
                                    String.valueOf(plugin.getFreezeTime()),
                                    canvasPoint.getX() + config.size() + timerOffset,
                                    canvasPoint.getY() + config.size() + 11 - adjustIcon
                            );
                        }
                    }
                    adjustIcon += config.size() + yOffset;
                }
            }
        }
        return null;
    }
}
