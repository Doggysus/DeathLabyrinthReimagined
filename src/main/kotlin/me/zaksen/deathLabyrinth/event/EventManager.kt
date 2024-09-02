package me.zaksen.deathLabyrinth.event

import me.zaksen.deathLabyrinth.event.custom.PlayerReadyEvent
import me.zaksen.deathLabyrinth.event.custom.game.*
import me.zaksen.deathLabyrinth.game.GameController
import me.zaksen.deathLabyrinth.game.room.Room
import me.zaksen.deathLabyrinth.game.room.RoomController
import me.zaksen.deathLabyrinth.util.tryAddEntity
import net.minecraft.world.entity.Entity
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.craftbukkit.entity.CraftLivingEntity
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.damage.DamageSource
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent.DamageCause
import org.bukkit.inventory.ItemStack


object EventManager {

    fun callReadyEvent(player: Player) {
        val coolEvent = PlayerReadyEvent(player)
        coolEvent.callEvent()
        if (!coolEvent.isCancelled) {
            GameController.toggleReadyState(coolEvent.player)
        }
    }

    fun callPlayerDeathEvent(player: Player, damageCause: DamageCause, damageSource: DamageSource, damage: Double) {
        val coolEvent = PlayerDeathEvent(player, damageCause, damageSource, damage)
        coolEvent.callEvent()
        if (!coolEvent.isCancelled) {
            GameController.processPlayerDeath(coolEvent.entity as Player)
        }
    }

    fun callEntityHitEvent(entity: LivingEntity, damageCause: DamageCause, damageSource: DamageSource, damage: Double) {
        val coolEvent = PlayerDamageEntityEvent(entity, damageCause, damageSource, damage)
        coolEvent.callEvent()
        if (!coolEvent.isCancelled) {
            GameController.processEntityHit(coolEvent.entity as LivingEntity)
        }
    }

    fun callPlayerKillEntityEvent(entity: LivingEntity, damageSource: DamageSource, drops: List<ItemStack>) {
        val coolEvent = PlayerKillEntityEvent(entity, damageSource, drops)
        coolEvent.callEvent()
        if (!coolEvent.isCancelled) {
            RoomController.processEntityRoomDeath(coolEvent)
        }
    }

    fun callPlayerDamagedByEntityEvent(event: EntityDamageByEntityEvent) {
        val coolEvent = PlayerDamagedByEntityEvent(event.damager, event.entity as Player, event.cause, event.damageSource, event.damage)
        coolEvent.callEvent()
        if (coolEvent.isCancelled) {
            event.isCancelled = true
        }
    }

    fun callRoomCompleteEvent(roomNumber: Int, room: Room, reward: Int) {
        val coolEvent = RoomCompleteEvent(roomNumber, room, reward)
        coolEvent.callEvent()
        RoomController.processRoomCompletion(coolEvent.reward)
    }

    fun callEntitySpawnEvent(world: World, entity: Entity, requireKill: Boolean, debug: Boolean = false) {
        val coolEvent = EntitySpawnEvent(world, entity, requireKill, debug)
        coolEvent.callEvent()
        if(!coolEvent.isCancelled) {
            RoomController.processEntitySpawn(coolEvent.world, coolEvent.entity, coolEvent.requireKill, coolEvent.debug)
        }
    }

    fun callBreakPotEvent(player: Player, pot: Block) {
        val coolEvent = PlayerBreakPotEvent(player, pot)
        coolEvent.callEvent()
        GameController.processPotBreaking(coolEvent)
    }

    fun callPlayerSummonFriendlyEntityEvent(player: Player, entity: net.minecraft.world.entity.LivingEntity) {
        val coolEvent = PlayerSummonFriendlyEntityEvent(player, entity)
        coolEvent.callEvent()
        if(!coolEvent.isCancelled) {
            coolEvent.player.world.tryAddEntity(entity)
        }
    }

    fun callPlayerSpellEntityDamageEvent(player: Player, entity: net.minecraft.world.entity.LivingEntity, damage: Double) {
        val coolEvent = PlayerSpellEntityDamageEvent(player, entity, damage)
        coolEvent.callEvent()
        if(!coolEvent.isCancelled) {
            coolEvent.entity.hurt(entity.damageSources().playerAttack((player as CraftPlayer).handle), coolEvent.damage.toFloat())
        }
    }

    fun callPlayerSpellEntityDamageEvent(player: Player, entity: LivingEntity, damage: Double) {
        this.callPlayerSpellEntityDamageEvent(player, (entity as CraftLivingEntity).handle, damage)
    }

    fun callSpellEntityDamageEvent(entity: net.minecraft.world.entity.LivingEntity, damage: Double) {
        val coolEvent = SpellEntityDamageEvent(entity, damage)
        coolEvent.callEvent()
        if(!coolEvent.isCancelled) {
            coolEvent.entity.hurt(entity.damageSources().generic(), coolEvent.damage.toFloat())
        }
    }

    fun callSpellEntityDamageEvent(entity: LivingEntity, damage: Double) {
        this.callSpellEntityDamageEvent((entity as CraftLivingEntity).handle, damage)
    }

    fun callPlayerSummonSpellEvent(player: Player, entity: Entity) {
        val coolEvent = PlayerSummonSpellEvent(player, entity)
        coolEvent.callEvent()
        if(!coolEvent.isCancelled) {
            coolEvent.player.world.tryAddEntity(coolEvent.spell)
        }
    }

}