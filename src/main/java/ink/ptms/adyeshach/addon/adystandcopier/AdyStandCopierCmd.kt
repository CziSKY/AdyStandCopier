package ink.ptms.adyeshach.addon.adystandcopier

import ink.ptms.adyeshach.api.AdyeshachAPI
import ink.ptms.adyeshach.common.bukkit.BukkitRotation
import ink.ptms.adyeshach.common.entity.type.AdyArmorStand
import io.izzel.taboolib.module.command.base.BaseCommand
import io.izzel.taboolib.module.command.base.BaseMainCommand
import io.izzel.taboolib.module.command.base.BaseSubCommand
import io.izzel.taboolib.module.command.base.SubCommand
import io.izzel.taboolib.module.locale.TLocale.Translate.setColored
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols


@BaseCommand(name = "AdyStandCopier", aliases = ["asc"], permission = "*")
class AdyStandCopierCmd : BaseMainCommand() {

    @SubCommand(description = "将玩家视角内的实体盔甲架数据赋予给生物类型为盔甲架的 Adyeshach NPC.")
    val pasteData: BaseSubCommand = object : BaseSubCommand() {
        override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>) {
            if (args.isEmpty() || sender is ConsoleCommandSender) {
                return sender.sendMessage(
                    when (sender is ConsoleCommandSender) {
                        true -> setColored("&c[AdyStandCopier] &7执行者必须为玩家.")
                        else -> setColored("&c[AdyStandCopier] &7错误的指令使用方法.")
                    }
                )
            }

            val player = sender as? Player ?: return

            if (AdyeshachAPI.getEntityFromId(args[0], player) == null) {
                return sender.sendMessage(setColored("&c[AdyStandCopier] &7这个 NPC 不存在."))
            }

            val adyArmorStand = AdyeshachAPI.getEntityFromId(args[0]) as? AdyArmorStand
                ?: return sender.sendMessage(setColored("&c[AdyStandCopier] &7这个 NPC 的类型必须为盔甲架."))

            val targetedArmorStand: ArmorStand? = (player.getNearbyEntities(3.0, 3.0, 3.0)
                .asSequence()
                .filter { entity -> entity.type == EntityType.ARMOR_STAND }
                .iterator().next()
                ?: return sender.sendMessage(setColored("&c[AdyStandCopier] &7您的周围没有任何盔甲架实体."))) as? ArmorStand

            adyArmorStand.setCustomNameVisible(targetedArmorStand!!.isCustomNameVisible)
            targetedArmorStand.customName?.let { adyArmorStand.setCustomName(it) }
            adyArmorStand.setArms(targetedArmorStand.hasArms())
            adyArmorStand.setBasePlate(targetedArmorStand.hasBasePlate())
            adyArmorStand.setSmall(targetedArmorStand.isSmall)
            adyArmorStand.setMarker(targetedArmorStand.isMarker)
            adyArmorStand.setGlowing(targetedArmorStand.isGlowing)

            adyArmorStand.setHelmet(targetedArmorStand.helmet)
            adyArmorStand.setChestplate(targetedArmorStand.chestplate)
            adyArmorStand.setLeggings(targetedArmorStand.leggings)
            adyArmorStand.setBoots(targetedArmorStand.boots)
            adyArmorStand.setItemInMainHand(targetedArmorStand.itemInHand)
            targetedArmorStand.equipment?.itemInOffHand?.let { adyArmorStand.setItemInOffHand(it) }

            adyArmorStand.setRotation(
                BukkitRotation.HEAD, adyArmorStand.getRotation(BukkitRotation.HEAD)
                    .setX(angle(targetedArmorStand.headPose.x))
                    .setY(angle(targetedArmorStand.headPose.y))
                    .setZ(angle(targetedArmorStand.headPose.z))
            )

            adyArmorStand.setRotation(
                BukkitRotation.BODY, adyArmorStand.getRotation(BukkitRotation.BODY)
                    .setX(angle(targetedArmorStand.bodyPose.x))
                    .setY(angle(targetedArmorStand.bodyPose.y))
                    .setZ(angle(targetedArmorStand.bodyPose.z))
            )

            adyArmorStand.setRotation(
                BukkitRotation.LEFT_ARM, adyArmorStand.getRotation(BukkitRotation.LEFT_ARM)
                    .setX(angle(targetedArmorStand.leftArmPose.x))
                    .setY(angle(targetedArmorStand.leftArmPose.y))
                    .setZ(angle(targetedArmorStand.leftArmPose.z))
            )

            adyArmorStand.setRotation(
                BukkitRotation.LEFT_LEG, adyArmorStand.getRotation(BukkitRotation.LEFT_LEG)
                    .setX(angle(targetedArmorStand.leftLegPose.x))
                    .setY(angle(targetedArmorStand.leftLegPose.y))
                    .setZ(angle(targetedArmorStand.leftLegPose.z))
            )

            adyArmorStand.setRotation(
                BukkitRotation.RIGHT_ARM, adyArmorStand.getRotation(BukkitRotation.RIGHT_ARM)
                    .setX(angle(targetedArmorStand.rightArmPose.x))
                    .setY(angle(targetedArmorStand.rightArmPose.y))
                    .setZ(angle(targetedArmorStand.rightArmPose.z))
            )

            adyArmorStand.setRotation(
                BukkitRotation.RIGHT_LEG, adyArmorStand.getRotation(BukkitRotation.RIGHT_LEG)
                    .setX(angle(targetedArmorStand.rightLegPose.x))
                    .setY(angle(targetedArmorStand.rightLegPose.y))
                    .setZ(angle(targetedArmorStand.rightLegPose.z))
            )


            val location = targetedArmorStand.location
            val yawAndPitch = listOf(location.yaw, location.pitch)

            targetedArmorStand.remove()
            sender.sendMessage(setColored("&c[AdyStandCopier] &7已自动移除原盔甲架."))

            adyArmorStand.teleport(location)
            adyArmorStand.setHeadRotation(yawAndPitch[0], yawAndPitch[1])

            sender.sendMessage(setColored("&c[AdyStandCopier] &7操作完成."))
        }
    }

    private fun angle(d: Double): Double {
        return twoDec(d * 180.0 / Math.PI)!!.toDouble()
    }

    private fun twoDec(d: Double): String? {
        val twoDec = DecimalFormat("0.0#")
        val symbols = DecimalFormatSymbols()
        symbols.decimalSeparator = '.'
        twoDec.decimalFormatSymbols = symbols
        return twoDec.format(d)
    }
}