package net.sleepcraft.mixin.client;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import net.sleepcraft.scrpmod.client.ModMenuScreen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.awt.Color;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    // Убираем @Shadow field_32169 — он не нужен
    // EXPERIENCE_BAR_PROGRESS_TEXTURE тоже недоступен через Shadow в этой версии,
    // поэтому задаём Identifier вручную
    private static final Identifier XP_BAR_PROGRESS =
            Identifier.ofVanilla("hud/experience_bar_progress");

    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderExperienceBar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!ModMenuScreen.isRainbowXpEnabled && !ModMenuScreen.isCustomXpColorEnabled) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        int width = 182;
        int height = 5;
        int screenWidth = context.getScaledWindowWidth();
        int screenHeight = context.getScaledWindowHeight();
        int barX = (screenWidth - width) / 2;
        int barY = screenHeight - 32 + 3;

        float progress = client.player.experienceProgress;
        int filledWidth = (int) (progress * (float) width);
        if (filledWidth <= 0) return;

        // Определяем точный цвет без искажений
        int pureColor;
        if (ModMenuScreen.isRainbowXpEnabled) {
            float hue = (System.currentTimeMillis() % 5000L) / 5000.0F;
            pureColor = Color.HSBtoRGB(hue, 1.0F, 1.0F) | 0xFF000000;
        } else {
            pureColor = ModMenuScreen.customXpColor | 0xFF000000;
        }

        // Шаг 1: рисуем текстуру бара БЕЛЫМ — это даёт форму без зелёного смещения.
        // Белый * любой цвет текстуры = исходный цвет текстуры, но нам нужна только альфа-маска.
        // Поэтому сначала очищаем зону своим цветом через scissor (по границе заполненной части),
        // а текстуру рисуем поверх белым чтобы края и форма бара остались корректными.

        // Заливаем точный цвет внутри границ заполненной части бара
        context.enableScissor(barX, barY, barX + filledWidth, barY + height);
        context.fill(barX, barY, barX + filledWidth, barY + height, pureColor);
        context.disableScissor();

        // Шаг 2: рисуем текстуру поверх с небольшой прозрачностью чтобы
        // форма/блики бара остались видны, но цвет диктовался твоим выбором
        context.drawGuiTexture(
                RenderPipelines.GUI_TEXTURED,
                XP_BAR_PROGRESS,
                width, height,
                0, 0,
                barX, barY,
                filledWidth, height,
                0x55FFFFFF  // почти прозрачный белый — сохраняет форму текстуры, не меняет цвет
        );

        // Уровень поверх
        if (client.player.experienceLevel > 0) {
            String levelStr = String.valueOf(client.player.experienceLevel);
            int textX = (screenWidth - client.textRenderer.getWidth(levelStr)) / 2;
            int textY = screenHeight - 36;

            context.drawText(client.textRenderer, levelStr, textX + 1, textY, 0, false);
            context.drawText(client.textRenderer, levelStr, textX - 1, textY, 0, false);
            context.drawText(client.textRenderer, levelStr, textX, textY + 1, 0, false);
            context.drawText(client.textRenderer, levelStr, textX, textY - 1, 0, false);
            context.drawText(client.textRenderer, levelStr, textX, textY, 8453920, false);
        }
    }
}