package com.example.huntergame;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import java.util.ArrayList;
import java.util.List;

public class HunterGameScreen extends Screen {
    private final List<PlayerSelectionEntry> playerEntries = new ArrayList<>();
    private EditBox survivorCountInput;
    private Button startButton;
    private String errorMessage = "";
    private final Minecraft minecraft;
    
    public HunterGameScreen() {
        super(Component.literal("猎人游戏设置"));
        this.minecraft = Minecraft.getInstance();
    }
    
    @Override
    protected void init() {
        super.init();
        playerEntries.clear();
        
        // 获取所有在线玩家
        List<Player> players = new ArrayList<>(minecraft.level.players());
        
        // 创建玩家选择条目
        int yPos = 70;
        for (Player player : players) {
            playerEntries.add(new PlayerSelectionEntry(
                this, player, 
                width / 4, yPos, 
                150, 20
            ));
            yPos += 25;
        }
        
        // 创建逃生者人数输入框
        survivorCountInput = new EditBox(
            minecraft.font, 
            width / 2 - 75, height - 100, 
            50, 20, 
            Component.literal("逃生者人数")
        );
        survivorCountInput.setMaxLength(2);
        survivorCountInput.setValue("1"); // 默认值
        addRenderableWidget(survivorCountInput);
        
        // 创建开始按钮
        startButton = Button.builder(
                Component.literal("开始游戏"), 
                button -> startGame()
            )
            .pos(width / 2 + 10, height - 100)
            .size(100, 20)
            .build();
        addRenderableWidget(startButton);
        
        // 添加所有玩家选择条目到界面
        playerEntries.forEach(this::addRenderableWidget);
    }
    
    private void startGame() {
        // 验证输入
        String input = survivorCountInput.getValue();
        if (input.isEmpty()) {
            errorMessage = "请输入逃生者人数!";
            return;
        }
        
        try {
            int survivorCount = Integer.parseInt(input);
            if (survivorCount <= 0) {
                errorMessage = "逃生者人数必须大于0!";
                return;
            }
            
            // 获取选中的玩家
            List<UUID> selectedPlayers = new ArrayList<>();
            for (PlayerSelectionEntry entry : playerEntries) {
                if (entry.isSelected()) {
                    selectedPlayers.add(entry.getPlayerId());
                }
            }
            
            if (selectedPlayers.size() < survivorCount) {
                errorMessage = "选择的玩家不足! 需要: " + survivorCount + " 已选: " + selectedPlayers.size();
                return;
            }
            
            // 开始游戏
            HunterGameManager.startGame(
                selectedPlayers.subList(0, survivorCount), 
                minecraft.getSingleplayerServer()
            );
            
            // 关闭界面
            onClose();
        } catch (NumberFormatException e) {
            errorMessage = "请输入有效的数字!";
        }
    }
    
    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        // 绘制半透明背景
        renderBackground(guiGraphics);
        
        // 绘制标题
        guiGraphics.drawCenteredString(
            minecraft.font, 
            Component.literal("猎人游戏设置").withStyle(net.minecraft.ChatFormatting.BOLD, net.minecraft.ChatFormatting.GOLD), 
            width / 2, 20, 
            0xFFFFFF
        );
        
        // 绘制玩家列表标题
        guiGraphics.drawString(
            minecraft.font, 
            "选择玩家 (点击切换选择):", 
            width / 4, 50, 
            0xFFFFFF
        );
        
        // 绘制逃生者人数标签
        guiGraphics.drawString(
            minecraft.font, 
            "逃生者人数:", 
            width / 2 - 75, height - 120, 
            0xFFFFFF
        );
        
        // 绘制错误消息
        if (!errorMessage.isEmpty()) {
            guiGraphics.drawCenteredString(
                minecraft.font, 
                Component.literal(errorMessage).withStyle(net.minecraft.ChatFormatting.RED), 
                width / 2, height - 70, 
                0xFF0000
            );
        }
        
        // 绘制游戏说明
        guiGraphics.drawString(
            minecraft.font, 
            "游戏规则:", 
            width * 3 / 4 - 50, 50, 
            0xFFFFFF
        );
        guiGraphics.drawString(
            minecraft.font, 
            "- 被选中的玩家成为逃生者", 
            width * 3 / 4 - 50, 70, 
            0xAAAAAA
        );
        guiGraphics.drawString(
            minecraft.font, 
            "- 其他玩家成为猎人", 
            width * 3 / 4 - 50, 85, 
            0xAAAAAA
        );
        guiGraphics.drawString(
            minecraft.font, 
            "- 猎人可以无限次重生", 
            width * 3 / 4 - 50, 100, 
            0xAAAAAA
        );
        guiGraphics.drawString(
            minecraft.font, 
            "- 逃生者死亡后:", 
            width * 3 / 4 - 50, 115, 
            0xAAAAAA
        );
        guiGraphics.drawString(
            minecraft.font, 
            "  • 如果剩余逃生者≥2: 成为旁观者", 
            width * 3 / 4 - 40, 130, 
            0xAAAAAA
        );
        guiGraphics.drawString(
            minecraft.font, 
            "  • 否则: 猎人胜利", 
            width * 3 / 4 - 40, 145, 
            0xAAAAAA
        );
        guiGraphics.drawString(
            minecraft.font, 
            "- 猎人可以通过命令", 
            width * 3 / 4 - 50, 160, 
            0xAAAAAA
        );
        guiGraphics.drawString(
            minecraft.font, 
            "  /escapeeswin 宣布逃生者胜利", 
            width * 3 / 4 - 40, 175, 
            0xAAAAAA
        );
        
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
    }
    
    @Override
    public void onClose() {
        super.onClose();
        minecraft.setScreen(null);
    }
    
    @Override
    public boolean isPauseScreen() {
        return false;
    }
}