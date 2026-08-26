# RegionCommand 2.1.0

Execute commands when a player enters or leaves a WorldGuard region.

## About

This is a port of **[RegionCommand](https://www.spigotmc.org/resources/free-regioncommand.22012/)** by **[Klemms](https://www.spigotmc.org/resources/authors/klemms.174298/)**, updated to work on modern Paper servers.

The original plugin works great, but it was abandoned and its update checker connects to a dead server, causing repeated `Connection refused` errors. This fork fixes that while keeping all the original functionality.

### Compatibility

| Paper Version | Java | Status |
|---|---|---|
| 1.21.11 | Java 21 | Supported |
| 26.1.2 | Java 25 | Supported |
| 26.2 | Java 25 | Supported |

Single jar works on all versions - no need to download different builds.

### Changes from v1.5.0

- Removed broken update checker (the server at `klemms.ovh` is down)
- Migrated from Spigot to Paper API
- Migrated from BungeeCord chat API to Adventure API (Kyori)
- Added `$uuid` variable for commands
- Commands now run via `BukkitScheduler` instead of raw `Thread`
- Automatic config migration from v1.5.0 (backups old configs before upgrading)
- Auto-updater: checks GitHub releases and downloads updates with `/regioncommandupdate`
- bstats moved to Gradle dependency

## Dependencies

- [WorldGuard](https://dev.bukkit.org/projects/worldguard) (with WorldEdit)
- [WorldGuardEvents](https://www.spigotmc.org/resources/worldguard-events.65176/)

## Commands

| Command | Description |
|---|---|
| `/addregioncommand <region> <enter/leave> <command>` | Add a command to a region |
| `/removeregioncommand <id>` | Remove a command by ID |
| `/regioncommandlist` | List all region commands (with clickable buttons) |
| `/changeregioncommand <id> <region> <enter/leave> <command>` | Edit an existing command |
| `/regioncommandupdate` | Update plugin to latest version |

### Variables

- `$player` - Player's username
- `$region` - Region name
- `$uuid` - Player's UUID

## Permissions

| Permission | Description |
|---|---|
| `regioncommand.addregioncommand` | Use `/addregioncommand` |
| `regioncommand.removeregioncommand` | Use `/removeregioncommand` and `/changeregioncommand` |
| `regioncommand.regioncommandlist` | Use `/regioncommandlist` |
| `regioncommand.update` | Use `/regioncommandupdate` |

## Example

```
/addregioncommand veryregion enter effect $player minecraft:speed 5 0 true
```

This gives Speed V for 5 seconds to any player entering the `veryregion` WorldGuard region.

## Auto-Update

The plugin automatically checks for new versions on startup. When an update is available:
- OPs are notified on join and can click to update
- Run `/regioncommandupdate` to download and restart the server

## Migration from v1.5.0

Drop `RegionCommand-2.1.0.jar` into your `plugins/` folder, remove the old JAR, and restart the server. Your existing `config.yml` will be detected and backed up automatically. No manual changes needed.

## Building

```bash
./gradlew shadowJar
```

Output: `build/libs/RegionCommand-2.1.0.jar`

## Credits

- **[Klemms](https://www.spigotmc.org/resources/authors/klemms.174298/)** - Original [RegionCommand](https://www.spigotmc.org/resources/free-regioncommand.22012/) plugin author
- **WorldGuardEvents** by [Raidstone](https://www.spigotmc.org/resources/worldguard-events.65176/)

## License

This project is a derivative work of the original RegionCommand plugin. Please respect the original author's work.
