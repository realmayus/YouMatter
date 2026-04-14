**YouMatter v3.0.0 is currently in Beta for 1.21.1. Expect bugs.**

# Changelog

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

[v3.0.0-beta.1] - April 13th, 2026

### Added
- v3.0.0-beta.1 Chinese Translation file provided to me by nvzy. (April 13th, 2026)
- v3.0.0-beta.1 Added toggle for U-Matter Creator's auto-output mode which is disabled by default. (April 13th, 2026)
- v3.0.0-alpha.4 Added WIP Config Screen. (June 10th, 2025)
- v3.0.0-alpha.4 Added clearer documentation on how to use each config option. (June 10th, 2025)
- v3.0.0-alpha.4 Added shapeless recipe for black hole duplication. (June 10th, 2025)
- v3.0.0-alpha.3 Added startup config. - WIP (June 8th, 2025)
- v3.0.0-alpha.3 Properly added black hole to end city treasure chests. (June 8th, 2025)
- v3.0.0-alpha.3 Added "youmatter:matter" tag. (June 8th, 2025)
- v3.0.0-alpha.1 Added support for wildcards to config. (June 2nd, 2025)

### Changed
- v3.0.0-beta.1 Changed GUIs to be a bit more modernized. (April 12th, 2026)
- v3.0.0-beta.1 Creator is now off by default like the Replicator. (April 13th, 2026)
- v3.0.0-beta.1 Reverted syntax change of "overrides" in config back to the original format. (April 11th, 2026)
- v3.0.0-beta.1 Reverted pattern-matching configuration change. (April 11th, 2026)
- v3.0.0-beta.1 Changed .toml config back to .json format due to issues with NightConfig present in NeoForge/Forge. (April 11th, 2026)
- v3.0.0-beta.1 Reverted changes to Energy usage.
- v3.0.0-alpha.4 Energy usage now takes a string expression to allow for custom energy calculations. (June 10th, 2025)
- v3.0.0-alpha.4 Added more configuration options in youmatter/common.toml. (June 10th, 2025)
- v3.0.0-alpha.3 Reverted scanner functionality. (June 8th, 2025)
- v3.0.0-alpha.3 Changed syntax of "overrides" in config. New syntax is: "{&lt;modid:item&gt;, amount}"
- v3.0.0-alpha.3 Refactored configuration file. (June 8th, 2025)
- v3.0.0-alpha.2 Refactored project. (June 3rd, 2025)
- v3.0.0-alpha.1 Scanner mechanics have been reworked. (June 2nd, 2025)
- v3.0.0-alpha.1 Now supports NeoForge as of 1.21.1. (June 2nd, 2025)
- v3.0.0-alpha.1 Dropped "old" Forge support as of 1.21.1. (June 2nd, 2025)
- v3.0.0-alpha.1 New textures by xDonnerVogelx and tigerlily83. (June 2nd, 2025)
- v3.0.0-alpha.1 Replicator and creator can now be toggled on/off with redstone. (June 2nd, 2025)
- v3.0.0-alpha.1 Alternative stabilizer can now be defined with the "youmatter:stabilizer" tag. (June 2nd, 2025)
- v3.0.0-alpha.1 Portable black holes now spawn in End Cities again. (June 2nd, 2025)

### Removed
- v3.0.0-beta.1 Removed "buggy" redstone mechanics. (April 10th, 2026)
- v3.0.0-beta.1 Removed startup config. (April 11th, 2026)
- v3.0.0-beta.1 Removed unused "youmatter:matter" tag (April 12th, 2026)
- v3.0.0-alpha.3 Removed old way of adding black hole to end city loot table. (June 8th, 2025)
- v3.0.0-alpha.1 Alternative stabilizer config option. (June 2nd, 2025)

### Fixed
- v3.0.0-alpha.4 Fixed encoder encoding full stacks to the thumb drive. (June 11th, 2025)
- v3.0.0-alpha.3 Scanner now properly transmits the data to the encoder. (June 8th, 2025)
- v3.0.0-alpha.2 Fixed recipe for Machine Casing. (June 3rd, 2025)
- v3.0.0-alpha.2 Fixed bug where Creator and Replicator update whenever any block is placed/removed next to them. (June 3rd, 2025)
- v3.0.0-alpha.2 Fixed thumb drive tooltips. (June 3rd, 2025)
- v3.0.0-alpha.2 Fixed color tint of fluid when using water in machines as a stabilizer. (June 3rd, 2025)
- v3.0.0-alpha.1 Can now properly pipe in fluids from other mods defined as stabilizer into the creator. (June 2nd, 2025)