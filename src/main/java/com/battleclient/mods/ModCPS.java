package com.battleclient.mods;

import com.battleclient.core.settings.BooleanSetting;
import com.battleclient.core.settings.ColorSetting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;
import java.util.List;

public class ModCPS extends BaseMod {
    private final List<Long> leftClicks = new ArrayList<>();
    private final List<Long> rightClicks = new ArrayList<>();

    private final BooleanSetting showRightClick;
    private final BooleanSetting showBackground;
    private final ColorSetting textColor;

    public ModCPS() {
        super("cps", "CPS Mod", "Mostra a quantidade de cliques por segundo (LMB e RMB)", "HUD");
        this.x = 10;
        this.y = 28;
        this.width = 65;
        this.height = 14;

        showRightClick = new BooleanSetting("showRight", "Mostrar Botão Direito", true);
        showBackground = new BooleanSetting("bg", "Fundo Escuro", true);
        textColor = new ColorSetting("color", "Cor do Texto", 0xFF0055FF);

        addSetting(showRightClick);
        addSetting(showBackground);
        addSetting(textColor);
    }

    @Override
    public void onMouse(InputEvent.MouseInputEvent event) {
        if (Mouse.getEventButtonState()) {
            int button = Mouse.getEventButton();
            long now = System.currentTimeMillis();
            if (button == 0) {
                leftClicks.add(now);
            } else if (button == 1) {
                rightClicks.add(now);
            }
        }
    }

    private int getCPS(List<Long> clicks) {
        long now = System.currentTimeMillis();
        clicks.removeIf(time -> now - time > 1000L);
        return clicks.size();
    }

    @Override
    public void onRender(RenderGameOverlayEvent.Post event) {
        int left = getCPS(leftClicks);
        int right = getCPS(rightClicks);

        String text = showRightClick.isEnabled() ? (left + " | " + right + " CPS") : (left + " CPS");

        this.width = mc.fontRendererObj.getStringWidth(text) + 8;
        this.height = 14;

        if (showBackground.isEnabled()) {
            drawHUDBox(x, y, width, height);
        }

        mc.fontRendererObj.drawStringWithShadow(text, x + 4, y + 3, textColor.getValue());
    }
}
