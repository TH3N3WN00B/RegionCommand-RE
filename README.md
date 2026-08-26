# RegionCommand 2.0.0

Execute commands when a player enters or leaves a WorldGuard region.

## About

This is a port of **[RegionCommand](https://www.spigotmc.org/resources/free-regioncommand.22012/)** by **[Klemms](https://www.spigotmc.org/resources/authors/klemms.174298/)**, updated to work on modern Paper/UniverseSpigot 1.21+ servers with Java 25.

The original plugin works great, but it was abandoned and its update checker connects to a dead server, causing repeated `Connection refused` errors. This fork fixes that while keeping all the original functionality.

### Changes from v1.5.0

- Removed broken update checker (the server at `klemms.ovh` is down)
- Migrated from Spigot to Paper API
- Migrated from BungeeCord chat API to Adventure API (Kyori)
- Added `$uuid` variable for commands
- Commands now run via `BukkitScheduler` instead of raw `Thread`
- Automatic config migration from v1.5.0 (backups old configs before upgrading)
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

## Example

```
/addregioncommand veryregion enter effect $player minecraft:speed 5 0 true
```

This gives Speed V for 5 seconds to any player entering the `veryregion` WorldGuard region.

## Migration from v1.5.0

Drop `RegionCommand-2.0.0.jar` into your `plugins/` folder, remove the old JAR, and restart the server. Your existing `config.yml` will be detected and backed up automatically. No manual changes needed.

## Building

```bash
./gradlew shadowJar
```

Output: `build/libs/RegionCommand-2.0.0.jar`

## Credits

- **[Klemms](https://www.spigotmc.org/resources/authors/klemms.174298/)** - Original [RegionCommand](https://www.spigotmc.org/resources/free-regioncommand.22012/) plugin author
- **WorldGuardEvents** by [Raidstone](https://www.spigotmc.org/resources/worldguard-events.65176/)

## License

This project is a derivative work of the original RegionCommand plugin. Please respect the original author's work.
