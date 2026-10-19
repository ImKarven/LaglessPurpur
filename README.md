# LaglessPurpur
LaglessPurpur is a fork of [Purpur](https://github.com/PurpurMC/Purpur), a Minecraft server software.

LaglessPurpur integrates [Lagless](https://github.com/ImKarven/lagless) into Purpur. Maintaining a fork of the server software is a solution that keeps the project stable and maintainable.

## Why not plugin?
Paper (of which Purpur is a fork) provides a powerful API for developers. However, most of those APIs are not applicable for Lagless. If Lagless were implemented as a plugin, it would have to poke into the game's internal as well as make use of hacky workarounds to achieve the desired behaviors.

This is not the case for mod, as mod can modify the game however it wants. Mods also only have to update when a new Minecraft version releases.

Plugin is supposed to use the API to achieve its goals, poking into Minecraft internals is possible, but this does not provide full control over the software's behavior. Doing so also requires more maintenance as updates from Paper can break the plugin.

> [!NOTE]
> This project is still in development, not ready to use yet.
