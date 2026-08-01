# Practical 08 — Options Menu, Context Menu, and Pop Up Menu

**Unit:** UI Design and Event Handling | **CO:** 2 | **Hrs:** 2

## Aim
To design an Android activity Options Menu, Context Menu, and Pop Up Menu for the Activity.

## Theory
- **Options Menu:** Appears in the app bar / overflow menu — `onCreateOptionsMenu()`.
- **Context Menu:** Appears on long-press of a view — `registerForContextMenu()`.
- **Popup Menu:** Appears anchored to a view on click — `PopupMenu` class.

## Code
See [res-menu/options_menu.xml](./res-menu/options_menu.xml) and [src/MenuActivity.java](./src/MenuActivity.java)

## Output
- Tapping the overflow (⋮) icon shows Settings/About options.
- Long-pressing the TextView shows a Context Menu (Copy/Share).
- Clicking the Button shows a PopupMenu (Edit/Delete).
