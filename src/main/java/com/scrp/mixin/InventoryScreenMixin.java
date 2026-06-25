package com.scrp.mixin;

import com.scrp.client.screen.GUIThemeButton;
import com.scrp.client.screen.GUIThemeScreen;
import com.scrp.config.GUIConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.inventoryscreen.InventoryScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {

    @Inject(method = "init", at = @At("TAIL"))
    private void addThemeButton(CallbackInfo ci) {
        InventoryScreen screen = (InventoryScreen) (Object) this;
        
        // Добавляем кнопку для открытия меню тем в правый верхний угол инвентаря
        ButtonWidget themeButton = ButtonWidget.builder(Text.literal("🎨"), (button) -> {
            screen.getClient().setScreen(new GUIThemeScreen(screen));
        }).dimensions(screen.width - 30, 5, 25, 20).build();
        
        screen.addDrawableChild(themeButton);
    }

    @Inject(method = "drawBg", at = @At("HEAD"), cancellable = true)
    private void applyThemeColors(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        // Здесь может быть применена прозрачность и цвета при отрисовке фона инвентаря
        InventoryScreen screen = (InventoryScreen) (Object) this;
        
        // Применяем прозрачность к фону
        // Это требует более сложной реализации с шейдерами или переопределением отрисовки
    }
}
