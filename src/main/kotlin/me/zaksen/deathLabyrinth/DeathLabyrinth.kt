package me.zaksen.deathLabyrinth

import me.zaksen.deathLabyrinth.command.*
import me.zaksen.deathLabyrinth.config.*
import me.zaksen.deathLabyrinth.event.CustomItemEvents
import me.zaksen.deathLabyrinth.event.GameEvents
import me.zaksen.deathLabyrinth.event.MenuEvents
import me.zaksen.deathLabyrinth.game.GameController
import me.zaksen.deathLabyrinth.game.room.RoomController
import me.zaksen.deathLabyrinth.keys.PluginKeys
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

// TODO - Удалить Singleton и переделать на обычных классах
class DeathLabyrinth : JavaPlugin(), ConfigContainer {

    private val roomDirectory = File(dataFolder, "rooms")
    private lateinit var mainConfig: MainConfig
    private lateinit var langConfig: LangConfig
    private lateinit var generationConfig: GenerationConfig

    override fun onEnable() {
        reloadConfigs()
        PluginKeys.setup(this)
        RoomController.reloadRooms(roomDirectory)
        GameController.setup(this, this)
        RoomController.setup(this)
        registerEvents()
        registerCommands()
    }

    override fun onDisable() {

    }

    private fun registerEvents() {
        server.pluginManager.registerEvents(MenuEvents(), this)
        server.pluginManager.registerEvents(CustomItemEvents(), this)
        server.pluginManager.registerEvents(GameEvents(mainConfig), this)
    }

    private fun registerCommands() {
        getCommand("game_status")?.setExecutor(GameStatusCommand())
        getCommand("class")?.setExecutor(ClassCommand(this))
        getCommand("give_item")?.setExecutor(GiveItemCommand())
        getCommand("give_item")?.tabCompleter = GiveItemCommand()
        getCommand("reload_game")?.setExecutor(GameReloadCommand(this, roomDirectory))
        getCommand("reload_game")?.tabCompleter = GameReloadCommand(this, roomDirectory)
        getCommand("item_tab")?.setExecutor(ItemTabCommand())
        getCommand("summon_custom")?.setExecutor(CustomSummonCommand())
        getCommand("summon_custom")?.tabCompleter = CustomSummonCommand()
        getCommand("build_room")?.setExecutor(BuildRoomCommand())
        getCommand("build_room")?.tabCompleter = BuildRoomCommand()
        getCommand("start_generation")?.setExecutor(StartGenerationCommand())
    }

    override fun reloadConfigs() {
        mainConfig = loadConfig(dataFolder, "main-config.yml")
        langConfig = loadConfig(dataFolder, "lang-config.yml")
        generationConfig = loadConfig(dataFolder, "generation-config.yml")
    }

    override fun mainConfig(): MainConfig {
        return mainConfig
    }

    override fun langConfig(): LangConfig {
        return langConfig
    }

    override fun generatioConfig(): GenerationConfig {
        return generationConfig
    }
}
