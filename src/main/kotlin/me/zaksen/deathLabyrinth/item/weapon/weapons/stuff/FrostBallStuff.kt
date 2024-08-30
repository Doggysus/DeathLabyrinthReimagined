package me.zaksen.deathLabyrinth.item.weapon.weapons.stuff

import me.zaksen.deathLabyrinth.entity.projectile.FrostBallEntity
import me.zaksen.deathLabyrinth.item.settings.ItemSettings
import me.zaksen.deathLabyrinth.item.weapon.WeaponItem
import me.zaksen.deathLabyrinth.item.weapon.WeaponType
import me.zaksen.deathLabyrinth.util.ChatUtil
import me.zaksen.deathLabyrinth.util.asText
import me.zaksen.deathLabyrinth.util.tryAddEntity
import net.minecraft.world.phys.Vec3
import org.bukkit.Material
import org.bukkit.craftbukkit.entity.CraftPlayer
import org.bukkit.event.player.PlayerInteractEvent

class FrostBallStuff(id: String): WeaponItem(
    WeaponType.ATTACK_STUFF,
    id,
    ItemSettings(Material.STICK)
        .customModel(101)
        .displayName(ChatUtil.format("<aqua>Леденящий посох</aqua>"))
        .abilityCooldown(800)
        .lore(mutableListOf(
            "<dark_purple>Выпускает ненадолго замедляющий ледяной шар</dark_purple>".asText(),
            "<green>Урон: 4</green>".asText()
        ))
)
{
    override fun onUse(event: PlayerInteractEvent) {
        val item = event.item!!

        if(checkAndUpdateCooldown(item)) {
            val shotVelocity = event.player.location.direction.multiply(2).normalize()

            val projectile = FrostBallEntity(event.player.location.add(shotVelocity).add(0.0, 1.6, 0.0))
            projectile.deltaMovement = Vec3(shotVelocity.x, shotVelocity.y, shotVelocity.z)
            projectile.setOwner((event.player as CraftPlayer).handle)
            event.player.world.tryAddEntity(projectile)
        }
    }
}