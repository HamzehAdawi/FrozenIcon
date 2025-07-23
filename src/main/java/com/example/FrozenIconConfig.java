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
        return 1;
    }

}
