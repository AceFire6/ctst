# Added Features

## Features Added

1. Levels are generated based on the Tiled Map file. The collision objects are loaded from the 
obstacle layer, the entities are loaded from the entities layer and the two versions of the map 
are stored so they can be switched on and off to simulate turning the light on and off.

2. Menu systems were added and can be navigated with the arrows keys and Enter.

3. Basic AI was added with enemies moving by themselves when they can't see the player and then 
heading towards the player when they can either hear or see the player. If they can see the 
player, their vision range increase because they are more alert. Leaving their vision range or 
entering the shadows causes them to lose sight of you and continue roaming.

4. Every time the player moves and is out of sneak mode (push SHIFT to toggle) they emit a sounds
 that is visualised by the grey circle that is drawn. (It is also possible to visualise all the 
 other shapes used in the game by holding down SPACE).

5. There is music that is played across all levels and a single track that is used specifically 
for the menus.
