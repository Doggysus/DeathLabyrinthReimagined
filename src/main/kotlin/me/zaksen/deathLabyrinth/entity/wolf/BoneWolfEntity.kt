package me.zaksen.deathLabyrinth.entity.wolf

import net.kyori.adventure.text.format.TextColor
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.animal.Wolf
import net.minecraft.world.entity.player.Player
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.craftbukkit.CraftWorld

class BoneWolfEntity(location: Location): Wolf(EntityType.WOLF, (location.getWorld() as CraftWorld).handle) {

    init {
        this.getAttribute(Attributes.MAX_HEALTH)?.baseValue = 16.0
        this.health = 16.0f
        this.customName = Component.literal("Волк").withColor(TextColor.color(124, 242, 81).value())
        this.isCustomNameVisible = true
        this.setPos(location.x, location.y, location.z)
    }

    override fun registerGoals() {
        goalSelector.addGoal(1, LeapAtTargetGoal(this, 0.4f))
        goalSelector.addGoal(2, MeleeAttackGoal(this, 1.0, false))
        goalSelector.addGoal(3, WaterAvoidingRandomStrollGoal(this, 1.0))

        targetSelector.addGoal(
            1,
            HurtByTargetGoal(
                this,
                *arrayOfNulls(0)
            )
        )
        targetSelector.addGoal(
            2, NearestAttackableTargetGoal(
                this,
                Player::class.java, true
            )
        )
    }

    override fun checkDespawn() { }

    override fun dropExperience(attacker: Entity?) { }

    override fun dropEquipment() { }

    override fun shouldDropLoot(): Boolean {
        return false
    }
}