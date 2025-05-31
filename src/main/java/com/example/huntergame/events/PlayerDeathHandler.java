package com.example.huntergame.events;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.example.huntergame.HunterGameMod;
import com.example.huntergame.HunterGameManager;

@Mod.EventBusSubscriber(modid = HunterGameMod.MODID)
public class PlayerDeathHandler {
    
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            // 延迟处理以确保死亡事件完成
            if (player.getServer() != null) {
                player.getServer().execute(() -> {
                    HunterGameManager.handlePlayerDeath(player);
                });
            }
        }
    }
}