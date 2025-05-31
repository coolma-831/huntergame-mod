package com.example.huntergame.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.example.huntergame.HunterGameManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;

public class EscapeWinCommand {
    
    public static void register() {
        if (HunterGameManager.server == null) return;
        
        CommandDispatcher<CommandSourceStack> dispatcher = HunterGameManager.server.getCommands().getDispatcher();
        dispatcher.register(
            Commands.literal("escapeeswin")
                .executes(context -> {
                    CommandSourceStack source = context.getSource();
                    if (source.getEntity() instanceof Player player) {
                        HunterGameManager.declareSurvivorVictory(player);
                    }
                    return 1;
                })
        );
    }
}
