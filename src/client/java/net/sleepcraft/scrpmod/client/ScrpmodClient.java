package net.sleepcraft.scrpmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier; // ОБЯЗАТЕЛЬНО ИМПОРТИРУЕМ КЛАСС ДЛЯ ID
import org.lwjgl.glfw.GLFW;

public class ScrpmodClient implements ClientModInitializer {
    private static KeyBinding openMenuKey;

    @Override
    public void onInitializeClient() {
        // ИСПРАВЛЕНИЕ: Создаем объект категории, передавая Identifier вместо обычной строки String
        KeyBinding.Category customCategory = new KeyBinding.Category(Identifier.of("scrpmod", "general"));

        // Регистрируем клавишу "H" и передаем объект категории
        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.scrpmod.open_menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                customCategory
        ));

        // Проверяем нажатие каждый тик
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.wasPressed()) {
                MinecraftClient.getInstance().setScreen(new ModMenuScreen());
            }
        });
    }
}