package me.zaksen.deathLabyrinth.item.weapon.weapons.stuff

import me.zaksen.deathLabyrinth.entity.friendly.skeleton.FriendlySkeletonArcherEntity
import me.zaksen.deathLabyrinth.item.ItemQuality
import me.zaksen.deathLabyrinth.item.settings.ItemSettings
import me.zaksen.deathLabyrinth.item.weapon.WeaponItem
import me.zaksen.deathLabyrinth.item.weapon.WeaponType
import me.zaksen.deathLabyrinth.util.ChatUtil
import me.zaksen.deathLabyrinth.util.asText
import me.zaksen.deathLabyrinth.util.tryAddEntity
import org.bukkit.Material
import org.bukkit.event.player.PlayerInteractEvent

class NecromanticStuff(id: String): WeaponItem(
    WeaponType.MISC_STUFF,
    id,
    ItemSettings(Material.STICK)
        .customModel(108)
        .displayName(ChatUtil.format("<gray>Посох некроманта</gray>"))
        .abilityCooldown(60000)
        .lore(mutableListOf(
            "<dark_purple>Призывает 2х скелетов лучников</dark_purple>".asText()
        )).quality(ItemQuality.RARE)
)
{
    override fun onUse(event: PlayerInteractEvent) {
        val item = event.item!!

        if(checkAndUpdateCooldown(item)) {
            val skeleton = FriendlySkeletonArcherEntity(event.player.location.add(2.0, 1.0, 1.0))
            event.player.world.tryAddEntity(skeleton)
            val skeletonTwo = FriendlySkeletonArcherEntity(event.player.location.add(-2.0, 1.0, -1.0))
            event.player.world.tryAddEntity(skeletonTwo)
        }
    }
}