package me.zaksen.deathLabyrinth.item.accessory.accessories

import me.zaksen.deathLabyrinth.item.ItemQuality
import me.zaksen.deathLabyrinth.item.accessory.AccessoryItem
import me.zaksen.deathLabyrinth.item.accessory.AccessorySlot
import me.zaksen.deathLabyrinth.item.settings.ItemSettings
import me.zaksen.deathLabyrinth.util.asTranslate
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Material

class AmuletOfGreed: AccessoryItem("amulet_of_greed", AccessorySlot.AMULET, ItemSettings(Material.LEAD)
    .customModel(100)
    .displayName("item.amulet_of_greed.name".asTranslate().color(TextColor.color(128, 128, 128)))
    .loreLine("text.accessory.on_equip".asTranslate().color(TextColor.color(169,169,169)))
    .loreLine("item.amulet_of_greed.effect".asTranslate().color(TextColor.color(65,105,225)))
    .quality(ItemQuality.RARE)
    .tradePrice(180)
)