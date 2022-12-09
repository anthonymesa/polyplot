# PolyPlot
(Alternate name: Bangon's charm)

Mod for Minecraft Forge with Minecraft v1.12.2.

Curseforge: https://www.curseforge.com/minecraft/mc-mods/polyplot

Youtube Demo: https://www.youtube.com/watch?v=iZ_u5vh_u4w

## Specifications
### Features:

- New Items:
  - Wierd Wand (effectively inert, used for crafting the other three wands)
  - Banagon's Wand
  - Banagon's Wand of Spires
  - Banagon's Wand of Barriers

- New Functionality:
  - Create protected polygonal zones ('plots') in any dimension (plots with spires or barriers are restricted to the overworld).
  - Generate up to three plots using any of the three wands.
  - Remove a plot using any of the three wands.
  - Rejects block placement/removal in/from plots by foreign players.
  - Plots generated by a Wand of Spires generates a 3-block-high pillars at the plot's corners.
  - Plots generated by a Wand of Barriers generates a 3-block-high pillars at the plot's corners, and 3-block-high walls along the plot's edges.
  - Alerts players upon entering/leaving a foreign player's plot.
  - Customize plot entry/exit messages by changing a wand's display name using an anvil.
  
- New server console commands:
    - /pp_ls: list all plots
    - /pp_setPillarConfig: set blockstate configuration for pillars
    - /pp_setWallConfig: set blockstate configuration for walls

### Planned Features

- New Items:
  - 3 lore books, adding some background lore to why the wands exist, that can be found randomly in generated chests.

- New Functionality:
  - Allow plots to have multiple owners.
  - Allow plots with pillars and walls to be created in non-overworld dimensions.
  - Enable rendering of plot borders/area for plot owners and server op's while holding wands.
  - Plot area protects from explosions damage.
  - Plot area protects from interactive modification by foreign players (i.e. opening a chest).

- New Server Console Commands:
  - /pp_deletePlot: forcibly delete a plot from the server

- Fixes:
  - Fix bug where walls generated and protected plot area doesn't line up exactly.
  
## User Manual

(Coming soon)
