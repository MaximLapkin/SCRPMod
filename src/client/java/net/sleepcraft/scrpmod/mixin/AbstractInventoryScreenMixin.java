package net.sleepcraft.scrpmod.mixin;

import net.sleepcraft.scrpmod.client.config.GUIConfig;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(AbstractInventoryScreen.class)
public class AbstractInventoryScreenMixin {

    @Inject(method = "render", at = @At("HEAD"))
    private void renderThemeBackground(CallbackInfo ci) {
        // Получаем параметры экрана
        AbstractInventoryScreen<?> screen = (AbstractInventoryScreen<?>) (Object) this;
        
        // Применяем прозрачность через изменение альфа-канала цветов
        // Параметры конфигурации будут использоваться при отрисовке
    }
}
