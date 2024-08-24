package me.zaksen.deathLabyrinth.game.room

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitWorld
import com.sk89q.worldedit.extent.clipboard.Clipboard
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats
import com.sk89q.worldedit.function.operation.Operation
import com.sk89q.worldedit.function.operation.Operations
import com.sk89q.worldedit.math.BlockVector3
import com.sk89q.worldedit.session.ClipboardHolder
import com.sk89q.worldedit.world.AbstractWorld
import me.zaksen.deathLabyrinth.config.*
import me.zaksen.deathLabyrinth.config.data.Position
import me.zaksen.deathLabyrinth.entity.EntityController
import me.zaksen.deathLabyrinth.util.tryAddEntity
import net.minecraft.world.entity.Entity
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.entity.CraftEntity
import org.bukkit.event.entity.EntityDeathEvent
import java.io.File
import java.io.FileInputStream
import kotlin.random.Random

object RoomController {

    private lateinit var configs: ConfigContainer
    private lateinit var world: AbstractWorld

    private val clearZones: MutableList<Pair<Position, Position>> = mutableListOf()

    val rooms: MutableMap<String, Room> = mutableMapOf()

    private val generationQuery: MutableList<Room> = mutableListOf()
    private var actualQueryRoom: Room? = null

    private val actualRoomEntities: MutableList<Entity> = mutableListOf()

    private var roomGenerated: Int = 0

    private var nextRoomX: Int = 0
    private var nextRoomY: Int = 0
    private var nextRoomZ: Int = 0

    fun setup(configs: ConfigContainer) {
        this.configs = configs
        val loadedWorld = Bukkit.getWorld(configs.generationConfig().firstRoomEntry.world)

        if(loadedWorld != null) {
            this.world = BukkitWorld(loadedWorld)
        } else {
            println("Unable to init room controller - world is null")
        }
    }

    fun reloadRooms(directory: File) {
        rooms.clear()

        if(!directory.exists()) {
            directory.mkdirs()
        }

        val files = directory.list() ?: return

        for(fileName in files) {
            if(fileName.endsWith(".yml")) {
                val config = loadConfig<RoomConfig>(directory, fileName)
                val id = fileName.replace(".yml", "")
                val schemFileName = fileName.replace(".yml", ".schem")
                val schemFile = File(directory, schemFileName)

                if(schemFile.exists()) {
                    rooms[id] = Room(config, schemFile)
                }
            } else if(fileName.endsWith(".schem")) {
                val configFile = fileName.replace(".schem", ".yml")
                if(!File(directory, configFile).exists()) {
                    saveConfig(directory, configFile, RoomConfig())
                    println("config for room $fileName was generated! please reload plugin using /reload_game!")
                }
            }
        }
    }

    private fun getRandomRoom(type: RoomType? = null): Room? {
        val availableRooms = rooms.values.toMutableList()

        if(type == null) {
            return availableRooms.random()
        } else {
            availableRooms.removeIf {
                it.roomConfig.roomType != type
            }

            if(availableRooms.isEmpty()) {
                println("Unable to generate room with type $type, rooms with this type isn't exists")
                return null
            }

            return availableRooms.random()
        }
    }

    fun processEntityRoomDeath(event: EntityDeathEvent) {
        val entity = (event.entity as CraftEntity).handle

        if(actualRoomEntities.contains(entity)) {
            actualRoomEntities.remove(entity)
            checkRoomCompletion()
        }
    }

    private fun checkRoomCompletion() {
        if(actualRoomEntities.isEmpty()) {
            // TODO - Open door
            generateAndUpdateQueryRoom()
            println("room completed")
        }
    }

    fun startGeneration() {
        for(i in 0..<configs.generationConfig().roomLimit) {
            if(i in configs.generationConfig().shopRooms) {
                val room = getRandomRoom(RoomType.SHOP)
                if(room != null) {
                    generationQuery.add(room)
                }
                continue
            }

            val room = getRandomRoom(RoomType.NORMAL)
            if(room != null) {
                generationQuery.add(room)
            }
        }

        nextRoomX = configs.generationConfig().firstRoomEntry.x.toInt()
        nextRoomY = configs.generationConfig().firstRoomEntry.y.toInt()
        nextRoomZ = configs.generationConfig().firstRoomEntry.z.toInt()

        generateAndUpdateQueryRoom()
    }

    private fun generateAndUpdateQueryRoom() {
        actualRoomEntities.clear()

        if(generationQuery.isEmpty()) {
            println("Generation query end")
            return
        }

        val queryRoom: Room = generationQuery.first()
        generationQuery.remove(queryRoom)
        actualQueryRoom = queryRoom

        val spawnX = nextRoomX - queryRoom.roomConfig.spawnEntryOffset.x
        val spawnY = nextRoomY + queryRoom.roomConfig.spawnEntryOffset.y
        val spawnZ = nextRoomZ - queryRoom.roomConfig.spawnEntryOffset.z

        buildRoom(
            queryRoom,
            spawnX.toInt(),
            spawnY.toInt(),
            spawnZ.toInt()
        )

        clearZones.add(Pair(
            Position(
                configs.generationConfig().firstRoomEntry.world,
                spawnX - queryRoom.roomConfig.roomSize.x.toInt(),
                spawnY + queryRoom.roomConfig.roomSize.y.toInt(),
                spawnZ - queryRoom.roomConfig.roomSize.z.toInt()
            ),
            Position(configs.generationConfig().firstRoomEntry.world, spawnX, spawnY, spawnZ)
        ))

        roomGenerated++

        nextRoomX -= queryRoom.roomConfig.spawnExitOffset.x.toInt()
        nextRoomY += queryRoom.roomConfig.spawnExitOffset.y.toInt()
        nextRoomZ -= queryRoom.roomConfig.spawnExitOffset.z.toInt()
    }

    fun clearGeneration() {
        clearCachedData()

        val roomWorld = Bukkit.getWorld(configs.generationConfig().firstRoomEntry.world) ?: return

        for(zone in clearZones) {
            for(y in zone.second.y.toInt()..zone.first.y.toInt()) {
                for (x in zone.first.x.toInt()..zone.second.x.toInt()) {
                    for (z in zone.first.z.toInt()..zone.second.z.toInt()) {
                        roomWorld.setType(x, y, z, Material.AIR)
                    }
                }
            }
        }

        clearZones.clear()
    }

    fun clearCachedData() {
        generationQuery.clear()
        actualQueryRoom = null
        actualRoomEntities.clear()

        roomGenerated = 0

        nextRoomX = configs.generationConfig().firstRoomEntry.x.toInt()
        nextRoomY = configs.generationConfig().firstRoomEntry.y.toInt()
        nextRoomZ = configs.generationConfig().firstRoomEntry.z.toInt()
    }

    fun buildRoom(room: Room, x: Int, y: Int, z: Int, debug: Boolean = false) {
        val roomWorld = Bukkit.getWorld(configs.generationConfig().firstRoomEntry.world) ?: return
        var clipboard: Clipboard

        val format = ClipboardFormats.findByFile(room.schematic)
        format!!.getReader(FileInputStream(room.schematic)).use { reader ->
            clipboard = reader.read()
        }

        WorldEdit.getInstance().newEditSession(world).use { editSession ->
            val operation: Operation = ClipboardHolder(clipboard)
                .createPaste(editSession)
                .to(BlockVector3.at(x, y, z))
                .build()
            Operations.complete(operation)
        }

        for(entityEntry in room.roomConfig.roomEntities.random()) {
            val entity = EntityController.entities[entityEntry.entityName]

            if(entity != null) {
                val toSpawn = entity.getDeclaredConstructor(Location::class.java).newInstance(Location(roomWorld,
                    x - entityEntry.spawnPosition.x,
                    y + entityEntry.spawnPosition.y,
                    z - entityEntry.spawnPosition.z
                ))
                roomWorld.tryAddEntity(toSpawn)
                if(!debug) {
                    if(entityEntry.requireKill) {
                        actualRoomEntities.add(toSpawn)
                    }
                }
            } else {
                println("Unable to found entity with id ${entityEntry.entityName}")
            }
        }

        val potSpawns = room.roomConfig.potSpawns.toMutableList()
        val numOfPots = Random.Default.nextInt(0, 3)

        if(potSpawns.size < numOfPots) {
            return
        }

        if(numOfPots > 0) {
            for (i in 1..numOfPots) {
                val pot = potSpawns.random()
                potSpawns.remove(pot)
                roomWorld.setType(x - pot.x.toInt(), y + pot.y.toInt(), z - pot.z.toInt(), Material.DECORATED_POT)
            }
        }
    }
}