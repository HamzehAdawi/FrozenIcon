package com.example;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("frozenIconPlugin")
public interface FrozenIconConfig extends Config
{
    @ConfigItem(
            keyName = "size",
            name = "Size",
            description = "Adjust the size of the icon."
    )
    default int size()
    {
        return 3;
    }

    @ConfigItem(
            keyName = "freezeTimer",
            name = "Freeze timer",
            description = "Show freeze timer next to icon."
    )
    default boolean freezeTimer()
    {
        return true;
    }

    @ConfigItem(
            keyName = "position",
            name = "Icon position",
            description = "Change what side of the HP bar icon appears."
    )
    default IconOrientation iconOrientation()
    {
        return IconOrientation.RIGHT;
    }

    @ConfigItem(
            keyName = "immunity",
            name = "Show re-freeze immunity",
            description = "Display a shield icon when immune from re-freeze."
    )
    default boolean showImmunity()
    {
        return false;
    }

    @ConfigItem(
            keyName = "teleBlock",
            name = "Show Tele Block",
            description = "Display a tele block icon when you are tele blocked."
    )
    default boolean showTeleBlock()
    {
        return false;
    }
}
