package com.example;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.SpriteID;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;

public class FrozenIconOverlay extends Overlay {

    Client client;
    FrozenIconConfig equipmentTotalConfig;
    SpriteManager spriteManager;
    InfoBoxManager infoBoxManager;

    @Inject
    FrozenIconOverlay(Client client,
                      FrozenIconConfig equipmentTotalConfig,
                      SpriteManager spriteManager, InfoBoxManager infoBoxManager)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.equipmentTotalConfig = equipmentTotalConfig;
        this.spriteManager = spriteManager;
        this.infoBoxManager = infoBoxManager;

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

        Player local = client.getLocalPlayer();
        if (iceBarrageIcon == null || local == null)
        {
            return null;
        }


        BufferedImage frozenIcon = null;
        for (InfoBox box : infoBoxManager.getInfoBoxes()) {

            if (box.getImage() == iceBarrageIcon)
            {
                frozenIcon = box.getImage();
            }

        }

        if (frozenIcon == null)
        {
            return null;
        }

        boolean hasIceBarrageInfoBox = infoBoxManager.getInfoBoxes().stream()
                .anyMatch(box -> box.getImage() == iceBarrageIcon);

        if (!hasIceBarrageInfoBox)
        {
            return null;
        }

        int modelHeight = local.getLogicalHeight();
        Point canvasPoint = Perspective.getCanvasImageLocation(client, local.getLocalLocation(), iceBarrageIcon, modelHeight);
        if (canvasPoint == null)
        {
            return null;
        }


        graphics2D.drawImage(
                iceBarrageIcon,
                canvasPoint.getX() + 25,
                canvasPoint.getY() - 5,
                16,
                16,
                null
        );

        return null;
    }


}
