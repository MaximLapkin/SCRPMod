package net.sleepcraft.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import net.sleepcraft.scrpmod.client.ModMenuScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.awt.Color;

@Mixin(HandledScreen.class)
public abstract class HandledScreenMixin<T extends ScreenHandler> {

    @Shadow protected int x;
    @Shadow protected int y;
    @Shadow protected int backgroundWidth;
    @Shadow protected int backgroundHeight;

    @Inject(method = "render", at = @At("HEAD"))
    private void onBeforeBackground(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!ModMenuScreen.isCustomGuiThemeEnabled) return;

        int bgColor = (ModMenuScreen.guiBgAlpha << 24) | (ModMenuScreen.guiBgColor & 0x00FFFFFF);
        context.fill(x, y, x + backgroundWidth, y + backgroundHeight, bgColor);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onAfterRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!ModMenuScreen.isCustomGuiThemeEnabled) return;

        int borderColor = ModMenuScreen.guiBorderColor | 0xFF000000;
        int slotColor = (ModMenuScreen.guiSlotAlpha << 24) | (ModMenuScreen.guiSlotColor & 0x00FFFFFF);

        // Внешняя рамка
        context.drawHorizontalLine(x,                  x + backgroundWidth,     y,                     borderColor);
        context.drawHorizontalLine(x,                  x + backgroundWidth,     y + backgroundHeight,  borderColor);
        context.drawVerticalLine  (x,                  y,                       y + backgroundHeight,  borderColor);
        context.drawVerticalLine  (x + backgroundWidth,y,                       y + backgroundHeight,  borderColor);

        // Внутренняя рамка (полупрозрачная)
        int innerBorder = (borderColor & 0x00FFFFFF) | 0x55000000;
        context.drawHorizontalLine(x + 1, x + backgroundWidth - 1, y + 1,                    innerBorder);
        context.drawHorizontalLine(x + 1, x + backgroundWidth - 1, y + backgroundHeight - 1, innerBorder);
        context.drawVerticalLine  (x + 1, y + 1, y + backgroundHeight - 1,                   innerBorder);
        context.drawVerticalLine  (x + backgroundWidth - 1, y + 1, y + backgroundHeight - 1, innerBorder);

        // Слоты
        HandledScreen<?> self = (HandledScreen<?>) (Object) this;
        var handler = self.getScreenHandler();
        if (handler != null) {
            for (var slot : handler.slots) {
                int sx = x + slot.x - 1;
                int sy = y + slot.y - 1;
                context.fill(sx, sy, sx + 18, sy + 18, slotColor);
                context.drawHorizontalLine(sx,      sx + 17, sy,      borderColor & 0x88FFFFFF);
                context.drawHorizontalLine(sx,      sx + 17, sy + 17, borderColor & 0x44FFFFFF);
                context.drawVerticalLine  (sx,      sy,      sy + 17, borderColor & 0x88FFFFFF);
                context.drawVerticalLine  (sx + 17, sy,      sy + 17, borderColor & 0x44FFFFFF);
            }
        }
    }
}