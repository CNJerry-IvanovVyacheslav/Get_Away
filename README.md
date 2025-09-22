# Wappo-Style Puzzle Game (Jetpack Compose)

A puzzle game inspired by Wappo, built entirely with **Jetpack Compose** in Kotlin.  
Navigate the player to the exit while avoiding the enemy and using traps strategically.

## Features

- **6x6 grid** with player, enemy, traps, and exit
- **Enemy AI**: prioritizes horizontal movement, respects walls, and reacts to traps
- **Smooth animations** for player and enemy movement
- **Trap mechanics**: freeze enemy for 3 turns or eliminate player if triggered
- **Walls** drawn precisely between tiles
- **Move counter** and game result displayed above the board
- **Interactive Map Editor**: design custom levels with walls, traps, and exits, then playtest immediately
- **Map Preview in Menu**: see a mini-version of the current or last designed map, including walls, traps, exit, and player/enemy positions
- **Responsive UI**: tiles and characters scale nicely to screen size

## Technologies

- Kotlin + Jetpack Compose
- StateFlow for reactive game state
- Compose animations for smooth transitions
- MVVM pattern

## Latest Updates

- The list of saved maps now displays a preview of each map instead of a static size.  
- Map names in the list and in the main menu are larger and visually enhanced.  
- Improved element alignment: map preview, name, and delete button.  
- Users can see the name of the selected map directly in the main menu along with its preview.

## How to Play

1. Start the game from the **Menu**.
2. Use swipe gestures to move the player.
3. Avoid the enemy and strategically use traps to freeze it.
4. Reach the **exit** to win.
5. Optionally, open the **Editor** to create and save your own levels.
6. Preview your custom map in the menu before starting a game.

*(The game is inspired by the "Wappo Game" and is made solely to explore a technology that is new to me)*

<img src="https://github.com/CNJerry-IvanovVyacheslav/Wappo_game/blob/master/photo_3_2025-09-19_12-43-47.jpg" width="200">    <img src="https://github.com/CNJerry-IvanovVyacheslav/Wappo_game/blob/master/photo_2_2025-09-19_12-43-47.jpg" width="200">        <img src="https://github.com/CNJerry-IvanovVyacheslav/Wappo_game/blob/master/photo_1_2025-09-19_12-43-47.jpg" width="200"> <img src="https://github.com/CNJerry-IvanovVyacheslav/Wappo_game/blob/master/photo_2025-09-22_11-01-37.jpg" width="200">
