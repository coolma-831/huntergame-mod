package com.example.huntergame.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.example.huntergame.HunterGameMod;
import com.example.huntergame.HunterGameScreen;
import net.minecraft.client.Minecraft;

@Mod.EventBusSubscriber(modid = HunterGameMod.MODID, value = Dist.CLIENT)
public class WorldJoinHandler {
    
    @SubscribeEvent
    public static void onPlayerJoinWorld(PlayerEvent.PlayerLoggedInEvent event) {
        // 只在客户端且是单机世界时显示界面
        if (event.getEntity().level().isClientSide) {
            Minecraft.getInstance().tell(() -> {
                Minecraft.getInstance().setScreen(new HunterGameScreen());
            });
        }
    }
}