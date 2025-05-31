package com.example.huntergame;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;
import java.util.UUID;

public class PlayerSelectionEntry extends AbstractButton {
    private final Player player;
    private final HunterGameScreen screen;
    private boolean selected;
    
    public PlayerSelectionEntry(HunterGameScreen screen, Player player, int x, int y, int width, int height) {
        super(x, y, width, height, Component.literal(player.getScoreboardName()));
        this.screen = screen;
        this.player = player;
        this.selected = false;
    }
    
    @Override
    public void onPress() {
        this.selected = !this.selected;
    }
    
    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        
        // 绘制按钮背景
        int color = selected ? 0xFF00FF00 : 0x80303030; // 绿色表示选中
        guiGraphics.fill(getX(), getY(), getX() + width, getY() + height, color);
        
        // 绘制玩家名称
        int nameColor = getPlayerNameColor();
        guiGraphics.drawString(
            font, 
            player.getScoreboardName(), 
            getX() + 5, getY() + (height - 8) / 2, 
            nameColor
        );
        
        // 绘制选中状态指示器
        if (selected) {
            guiGraphics.drawString(
                font, 
                "✓", 
                getX() + width - 15, getY() + (height - 8) / 2, 
                0xFFFFFF
            );
        }
    }
    
    private int getPlayerNameColor() {
        Team team = player.getTeam();
        if (team != null && team.getColor().getColor() != null) {
            return team.getColor().getColor();
        }
        return 0xFFFFFF;
    }
    
    public boolean isSelected() {
        return selected;
    }
    
    public UUID getPlayerId() {
        return player.getUUID();
    }
    
    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        // 无障碍功能支持
    }
}