package com.battleclient.gui;

import com.battleclient.core.BattleClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

/**
 * MainMenuOverlay
 *
 * Hooks into the vanilla GuiMainMenu to:
 *  1. Draw the Battle Client logo (top-left, 80x80)
 *  2. Draw "BATTLE CLIENT" text + version below logo
 *  3. Draw "Press RSHIFT to open mods" hint (bottom-center)
 */
public class MainMenuOverlay {

    private static final ResourceLocation LOGO = new ResourceLocation("battleclient", "textures/gui/logo.png");
    private static final Minecraft mc = Minecraft.getMinecraft();

    @SubscribeEvent
    public void onGuiDraw(GuiScreenEvent.DrawScreenEvent.Post event) {
        if (!(event.gui instanceof GuiMainMenu)) return;

        int sw = mc.currentScreen.width;
        int sh = mc.currentScreen.height;

        // ── Logo no canto superior esquerdo ─────────────────────────────
        int logoSize = 80;
        int lx = 12;
        int ly = 12;

        try {
            GlStateManager.color(1f, 1f, 1f, 1f);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            mc.getTextureManager().bindTexture(LOGO);
            Gui.drawModalRectWithCustomSizedTexture(lx, ly, 0, 0, logoSize, logoSize, logoSize, logoSize);
            GlStateManager.disableBlend();
        } catch (Exception e) {
            // Fallback: quadrado azul com "BC"
            Gui.drawRect(lx, ly, lx + logoSize, ly + logoSize, 0xFF0055FF);
            mc.fontRendererObj.drawStringWithShadow("\u00A7fBC", lx + 30, ly + 36, 0xFFFFFFFF);
        }

        // ── Texto do client ao lado/abaixo da logo ──────────────────────
        mc.fontRendererObj.drawStringWithShadow(
            "\u00A7f\u00A7lBATTLE \u00A79\u00A7lCLIENT",
            lx + logoSize + 6,
            ly + 18,
            0xFFFFFFFF
        );
        mc.fontRendererObj.drawString(
            "\u00A78v" + BattleClient.VERSION + "  \u2022  Forge 1.8.9",
            lx + logoSize + 6,
            ly + 32,
            0xFF666666
        );

        // ── Hint na parte inferior ───────────────────────────────────────
        String hint = "\u00A78[\u00A79RSHIFT\u00A78] \u00A77Abrir Battle Client";
        int hw = mc.fontRendererObj.getStringWidth(hint);
        mc.fontRendererObj.drawStringWithShadow(hint, sw / 2 - hw / 2, sh - 18, 0xFFFFFFFF);

        // ── Watermark canto inferior direito ─────────────────────────────
        String wm = "\u00A79Battle Client \u00A78| \u00A77" + BattleClient.VERSION;
        int wmw = mc.fontRendererObj.getStringWidth(wm);
        mc.fontRendererObj.drawStringWithShadow(wm, sw - wmw - 6, sh - 14, 0xFFFFFFFF);
    }
}
