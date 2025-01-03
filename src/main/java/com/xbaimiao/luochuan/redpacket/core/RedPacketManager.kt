package com.xbaimiao.luochuan.redpacket.core

import com.xbaimiao.easylib.util.submit
import com.xbaimiao.luochuan.redpacket.LuoChuanRedPacket
import com.xbaimiao.luochuan.redpacket.core.redpacket.RedPacket

object RedPacketManager {

    private val redPacket = HashMap<Long, RedPacket>()
    private val lock = Any()

    fun load() {
        submit(async = true, period = 200) {
            synchronized(lock) {
                // 删除过期数据
                val deleteList = ArrayList<Long>()
                redPacket.forEach {
                    if (it.key < System.currentTimeMillis()) {
                        delete(it.value)
                        deleteList.add(it.key)
                    }
                }
                deleteList.forEach {
                    redPacket.remove(it)
                }
            }
        }
    }

    fun addRedPacket(redPacket: RedPacket) {
        synchronized(lock) {
            this.redPacket[System.currentTimeMillis() + 1000 * 60 * 30] = redPacket
        }
    }

    fun clear() {
        synchronized(lock) {
            redPacket.forEach { delete(it.value, false) }
        }
    }

    private fun delete(redPacket: RedPacket, async: Boolean = true) {
        LuoChuanRedPacket.redisManager.delete(redPacket.id, async)
    }

}
