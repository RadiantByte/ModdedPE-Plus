# ModdedPE

A modern, enhanced fork of the original ModdedPE launcher with significant improvements and new features.

## What is ModdedPE?

ModdedPE is an advanced Minecraft Pocket Edition launcher that allows you to load NMods and shared object (.so) modifications. This fork has been completely rewritten with modern Android development practices, featuring a brand new Jetpack Compose UI and expanded modding capabilities.

## Key Features

### Modern Interface
- **Complete UI Rewrite**: Migrated from XML views to modern Jetpack Compose
- **Material Design 3**: Clean, modern interface with proper theming
- **Enhanced User Experience**: Intuitive navigation and improved usability

### Enhanced Modding Support
- **NMod Support**: Load traditional NMods for Minecraft PE modifications
- **Shared Object (.so) Mods**: NEW - Support for native shared library mods
- **Mod Management**: Easy enable/disable and organization of modifications

## Supported Shared Object Mods

The following .so mods are compatible with ModdedPE:

### Core Mods
- **[ForceCloseOreUI](https://github.com/QYCottage/ForceCloseOreUI)** - Disables Minecraft's new OreUI and restores the classic JSON-based UI
- **[BetterResourcePackManager](https://github.com/QYCottage/BetterResourcePackManager-Release)** - Enables in-game resource pack management with support for:
  - 3D and 4D skins
  - Animated skins and capes
  - Render Dragon shaders
- **[BetterRenderDragon](https://github.com/QYCottage/BetterRenderDragon)** - Provides shader-related enhancements, currently force enables vibrant visuals in ModdedPE
- **[MaterialBinLoader](https://github.com/ddf8196/MaterialBinLoader)** - Adds Render Dragon shader support

### Installation
1. Download the desired .so mod files
2. Open ModdedPE
3. Navigate to the Manage NMods tab
4. Tap "Add SoMod" and select your .so files
5. Enable the mods you want to use
6. Launch Minecraft

## Development Resources

### NMod Development
- **[NMOD Examples](https://github.com/TimScriptov/NMOD-Examples)** - Sample code and tutorials for NMod development
- **[NModAPI](https://github.com/TimScriptov/NModAPI)** - Open source API for developing MCPE launchers with NMod support

### Using ModdedPE as a Library
ModdedPE can be integrated into your own projects as a library, providing NMod loading capabilities to other applications.

## TODO

- [ ] Fix [mtbinloader2](https://github.com/mcbegamerxx954/mtbinloader2) loading compatibility and related mods
- [ ] Add either Dobby or Gloss

## Fork Information

This is an enhanced community fork of the original ModdedPE project by TimScriptov. This fork focuses on modernization, improved user experience, and expanded modding capabilities.

### Original Project
- **Original Developer**: [TimScriptov](https://github.com/TimScriptov)
- **Collaborators**: [Listerily](https://github.com/listerily)
- **Original Repository**: [TimScriptov/ModdedPE](https://github.com/TimScriptov/ModdedPE)

### Fork Maintainer
- **Enhanced Version**: [RadiantByte](https://github.com/RadiantByte)
- **This Repository**: [RadiantByte/ModdedPE](https://github.com/RadiantByte/ModdedPE)

## Building

1. Clone this repository
2. Open in Android Studio
3. Build and install

## Requirements

- Android 7.0+ (API level 24+)
- Minecraft Pocket Edition installed
- Storage permissions for mod file access

## License

This project is licensed under the GNU General Public License v3.0 - see the original project for details.

## Contributing

Contributions are welcome! Please feel free to submit issues, feature requests, or pull requests to help improve ModdedPE.

## Disclaimer

This software is not affiliated with or endorsed by Mojang Studios or Microsoft. Minecraft is a trademark of Mojang Studios. Use mods at your own discretion and ensure they comply with Minecraft's Terms of Service.
