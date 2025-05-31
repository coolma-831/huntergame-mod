package com.example.huntergame;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.MinecraftServer;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import java.util.*;
import java.util.stream.Collectors;

public class HunterGameManager {
    public static boolean isGameActive = false;
    public static Set<UUID> survivors = new HashSet<>();
    public static Set<UUID> hunters = new HashSet<>();
    public static MinecraftServer server;
    
    public static void startGame(List<UUID> selectedSurvivors, MinecraftServer server) {
        HunterGameManager.server = server;
        isGameActive = true;
        survivors.clear();
        hunters.clear();
        
        // 添加选中的逃生者
        survivors.addAll(selectedSurvivors);
        
        // 所有其他玩家作为猎人
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            UUID playerId = player.getUUID();
            if (!survivors.contains(playerId)) {
                hunters.add(playerId);
            }
        }
        
        // 通知玩家角色分配
        broadcastMessage("猎人游戏开始!", ChatFormatting.GOLD);
        broadcastMessage("逃生者数量: " + survivors.size(), ChatFormatting.GREEN);
        broadcastMessage("猎人数量: " + hunters.size(), ChatFormatting.RED);
        
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (survivors.contains(player.getUUID())) {
                player.sendSystemMessage(Component.literal("你是逃生者! 努力生存下去!").withStyle(ChatFormatting.GREEN));
            } else {
                player.sendSystemMessage(Component.literal("你是猎人! 追捕逃生者!").withStyle(ChatFormatting.RED));
                player.sendSystemMessage(Component.literal("你可以无限次重生!").withStyle(ChatFormatting.YELLOW));
            }
        }
    }
    
    public static void endGame(boolean survivorsWin) {
        if (!isGameActive) return;
        
        isGameActive = false;
        String message = survivorsWin ? 
            "逃生者胜利!" : "猎人胜利!";
        ChatFormatting color = survivorsWin ? 
            ChatFormatting.GREEN : ChatFormatting.RED;
        
        broadcastMessage("游戏结束! " + message, color);
        
        // 重置所有玩家状态
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            if (player.isSpectator()) {
                player.setGameMode(net.minecraft.world.level.GameType.SURVIVAL);
            }
        }
        
        survivors.clear();
        hunters.clear();
    }
    
    public static void handlePlayerDeath(Player player) {
        if (!isGameActive || server == null) return;
        
        UUID playerId = player.getUUID();
        
        // 处理逃生者死亡
        if (survivors.contains(playerId)) {
            survivors.remove(playerId);
            player.sendSystemMessage(Component.literal("你已死亡!").withStyle(ChatFormatting.RED));
            
            if (survivors.isEmpty()) {
                // 所有逃生者死亡，猎人胜利
                endGame(false);
            } else if (survivors.size() >= 2) {
                // 逃生者人数>=2，切换旁观者模式
                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.setGameMode(net.minecraft.world.level.GameType.SPECTATOR);
                    player.sendSystemMessage(Component.literal("你已成为旁观者").withStyle(ChatFormatting.GRAY));
                }
            } else if (survivors.size() == 1) {
                // 只剩一个逃生者，猎人胜利
                endGame(false);
            }
        }
        // 猎人死亡不做处理（无限重生）
    }
    
    public static void declareSurvivorVictory(Player commander) {
        if (!isGameActive) return;
        
        // 检查命令执行者是否为猎人
        if (hunters.contains(commander.getUUID())) {
            endGame(true);
        } else {
            commander.sendSystemMessage(
                Component.literal("只有猎人才能宣布逃生者胜利!").withStyle(ChatFormatting.RED)
            );
        }
    }
    
    public static boolean isSurvivor(UUID playerId) {
        return survivors.contains(playerId);
    }
    
    public static boolean isHunter(UUID playerId) {
        return hunters.contains(playerId);
    }
    
    private static void broadcastMessage(String message, ChatFormatting color) {
        if (server == null) return;
        
        Component component = Component.literal("[猎人游戏] " + message).withStyle(color);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            player.sendSystemMessage(component);
        }
    }
}
