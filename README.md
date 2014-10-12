# Climbing Towers, Stealing Treasure

<h3>Jethro Muller<br>
MLLJET001</h3>

### Description

My second year game development project. A 2D top-down stealth based game.
See [here](jethromuller.co.za/projects/game-development/second-year/ctst/) for more information.

Each level is generated based on the given tmx file.

The required layer are called:

* obstacles
* shadows
* entities

In the obstacles layer, there are two required objects.  
A light source labelled "fire" and a staircase labelled "staircase".

In the entities layer, there are 3 types of possible entities that are handled:

* Enemy
* Player
* Treasure

The appropriate entity class with the given coordinate will be spawned at the locations given by 
the map.

The map must be in the levels folder, in a folder with the same name as the map.  
For example, tutorialLevel is located in `/levels/tutorialLevel/tutorialLevel.tmx`

### Instructions

1. Navigate to the root of the cloned directory.
2. Run `./gradlew run`.

### Acknowledgements

1. The music in the main menu was made by Quintin Clarkson and I using [inudge.net](http://inudge.net/).
2. The sound effects were made using [bfxr.net](http://www.bfxr.net/).
3. Music used in game all comes from [freemusicarchive.org](freemusicarchive.org)

#### Artist Attributions:

[Submerged](http://freemusicarchive.org/music/Edward_Shallow/Dodecaheadroom/Edward_Shallow_-_Dodecaheadroom_-_03_Submerged) by [Edward Shallow](http://freemusicarchive.org/music/Edward_Shallow/)  
Under CC BY-NC-SA license [creativecommons.org/licenses/by-nc-sa/3.0/us/](http://creativecommons.org/licenses/by-nc-sa/3.0/us/)

[Breaking In](http://freemusicarchive.org/music/BoxCat_Games/Nameless_the_Hackers_RPG_Soundtrack/BoxCat_Games_-_Nameless-_the_Hackers_RPG_Soundtrack_-_01_Breaking_In) by [BoxCat Games](http://freemusicarchive.org/music/BoxCat_Games/)  
Under CC BY license [creativecommons.org/licenses/by/3.0/](http://creativecommons.org/licenses/by/3.0/)

[OHC3](http://freemusicarchive.org/music/Kris_Keyser/Kris_Keyser/8bp130-04-kris_keyser-ohc3) by [Kris Keyser](http://freemusicarchive.org/music/Kris_Keyser/Kris_Keyser/)  
Under CC BY-NC-ND license [creativecommons.org/licenses/by-nc-nd/3.0/](http://creativecommons.org/licenses/by-nc-nd/3.0/)

[Sea Battles in Space](http://freemusicarchive.org/music/RoccoW/~/RoccoW_-_Sea_Battles_in_Space) by [RoccoW](http://freemusicarchive.org/music/RoccoW/)  
Under CC BY-NC-SA license [creativecommons.org/licenses/by-nc-sa/4.0/](http://creativecommons.org/licenses/by-nc-sa/4.0/)

[Fuckaboing](http://freemusicarchive.org/music/RoccoW/~/RoccoW_-_Fuckaboing) by [RoccoW](http://freemusicarchive.org/music/RoccoW/)  
Under CC BY-SA license [creativecommons.org/licenses/by-sa/4.0/](http://creativecommons.org/licenses/by-sa/4.0/)

[Enthalpy](http://freemusicarchive.org/music/Rolemusic/~/03_rolemusic_-_enthalpy) by [Rolemusic](http://freemusicarchive.org/music/Rolemusic/)  
Under CC BY license [creativecommons.org/licenses/by/4.0/](http://creativecommons.org/licenses/by/4.0/)
