package me.zaksen.deathLabyrinth.item.ability.stuff

import me.zaksen.deathLabyrinth.damage.DamageType
import me.zaksen.deathLabyrinth.event.EventManager
import me.zaksen.deathLabyrinth.event.item.ItemUseEvent
import me.zaksen.deathLabyrinth.item.ability.ItemAbility
import net.kyori.adventure.text.Component
import org.bukkit.event.Event

class ExplosionCastTierTwo: ItemAbility(
    Component.translatable("ability.explosion_cast_tier_two.name"),
    Component.translatable("ability.explosion_cast_tier_two.description"),
    10.0,
    2.5,
    damageType = DamageType.EXPLODE
) {
    override fun invoke(event: Event) {
        if(event !is ItemUseEvent) return

        val stack = event.stack!!
        val item = event.item!!

        var rayCast = event.player.rayTraceEntities(24)

        if(rayCast == null) {
            rayCast = event.player.rayTraceBlocks(24.0)
        }

        if(rayCast == null) {
            return
        }

        if(item.checkCooldown(stack)) {
            val pos = rayCast.hitPosition
            EventManager.callPlayerSummonExplosionEvent(event.player, pos.toLocation(event.player.world).subtract(0.0, 1.0, 0.0), 2.5, 10.0)
        }
    }

    override fun getUpdateAbility(): String {
        return "explosion_cast_tier_three"
    }

    override fun getConflictAbilities(): List<String> {
        return listOf("explosion_cast")
    }
}