package com.mucheng.mucute.client.game.module.combat

import com.mucheng.mucute.client.game.InterceptablePacket
import com.mucheng.mucute.client.game.Module
import com.mucheng.mucute.client.game.ModuleCategory
import org.cloudburstmc.protocol.bedrock.packet.PlayerAuthInputPacket

class AutoTotemModule : Module("auto_totem", ModuleCategory.Combat) {

    private var slotDelay by intValue("delay_ms", 50, 0..500)
    private var lastSwapTime = 0L

    override fun beforePacketBound(interceptablePacket: InterceptablePacket) {
        if (!isEnabled) return

        val packet = interceptablePacket.packet
        if (packet is PlayerAuthInputPacket) {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastSwapTime < slotDelay) return

            val localPlayer = session.localPlayer ?: return
            
            // ID 450 (or "totem_of_undying") is Bedrock's Totem of Undying
            val offhandItem = localPlayer.offhandItem
            if (offhandItem?.id != "totem_of_undying" && offhandItem?.id != "450") {
                
                // Find totem in player inventory
                val totemSlot = localPlayer.inventory.indexOfFirst { item ->
                    item.id == "totem_of_undying" || item.id == "450"
                }

                if (totemSlot != -1) {
                    // Swap totem to offhand via local player session controller
                    localPlayer.equipToOffhand(totemSlot)
                    lastSwapTime = currentTime
                }
            }
        }
    }
}
