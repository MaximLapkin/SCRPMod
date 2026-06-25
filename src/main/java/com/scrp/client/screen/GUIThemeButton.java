package com.scrp.client.screen;

import com.scrp.config.GUIConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class GUIThemeButton extends ButtonWidget {
    private GUIThemeScreen themeScreen;

    public GUIThemeButton(int x, int y, int width, int height, Text message, PressAction onPress, GUIThemeScreen themeScreen) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION_SUPPLIER);
        this.themeScreen = themeScreen;
    }

    @Override
    protected void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        // Кнопка будет отображаться стандартно, но с возможностью открыть меню тем
        super.renderButton(context, mouseX, mouseY, delta);
    }
}
