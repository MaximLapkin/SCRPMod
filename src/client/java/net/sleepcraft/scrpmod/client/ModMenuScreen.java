package net.sleepcraft.scrpmod.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import java.awt.Color;

public class ModMenuScreen extends Screen {
    public static boolean isRainbowXpEnabled = false;
    public static boolean isCustomXpColorEnabled = false;
    public static int customXpColor = 0x00F3FF;

    // Настройки GUI темы
    public static int guiBgColor = 0x101820;        // цвет фона
    public static int guiBgAlpha = 180;             // прозрачность фона (0-255)
    public static int guiBorderColor = 0x00D2FF;    // цвет окантовки
    public static int guiSlotColor = 0x1A2040;      // цвет слотов
    public static int guiSlotAlpha = 140;           // прозрачность слотов
    public static int guiTextColor = 0xFFFFFF;      // цвет заголовка GUI
    public static boolean isCustomGuiThemeEnabled = false;

    // Геометрия окна
    private static int windowX = 100;
    private static int windowY = 100;
    private static int windowWidth = 380;
    private static int windowHeight = 230;

    private static final int MIN_WIDTH = 280;
    private static final int MIN_HEIGHT = 150;
    private static final int EDGE_PAD = 6; // Чувствительность краев для ресайза

    // Состояния мыши и драга
    private boolean isDragging = false;
    private boolean resizeRight = false;
    private boolean resizeLeft = false;
    private boolean resizeBottom = false;
    private double dragOffsetX = 0, dragOffsetY = 0;
    private int initialWidth, initialHeight, initialX;
    private boolean lastMouseState = false;

    // Скроллинг (Прокрутка)
    private double scrollAmount = 0;
    private int maxScrollHeight = 250; // Задаем базовую высоту контента для скролла

    // Навигация
    private int selectedTab = 0;
    private boolean isPersonalizationOpen = true;

    // Палитра
    private boolean isColorPickerOpen = false;
    private int pickerX, pickerY;
    private final int pickerWidth = 110;
    private final int pickerHeight = 60;
    private int markerX = 55, markerY = 30;

    private ButtonWidget rainbowBtn;
    private ButtonWidget customColorBtn;

    // Открытие второй подгруппы
    private boolean isGuiThemeOpen = false;

    // Палитра цветов UI
    private static final int COLOR_GRAD_START = new Color(22, 34, 58, 235).getRGB();
    private static final int COLOR_GRAD_END = new Color(8, 12, 20, 245).getRGB();
    private static final int TECH_CYAN = new Color(0, 210, 255).getRGB();
    private static final int TEXT_MUTED = new Color(110, 130, 160).getRGB();
    private static final int SIDEBAR_BG = new Color(10, 15, 26, 200).getRGB();
    private static final int LINE_COLOR = new Color(35, 50, 75, 120).getRGB();

    public ModMenuScreen() {
        super(Text.literal("SCRP HUD КОНСОЛЬ"));
    }

    @Override
    protected void init() {
        super.init();

        rainbowBtn = ButtonWidget.builder(Text.literal("Rainbow"), b -> {
            isRainbowXpEnabled = !isRainbowXpEnabled;
            if (isRainbowXpEnabled) isCustomXpColorEnabled = false;
        }).build();

        customColorBtn = ButtonWidget.builder(Text.literal("Custom"), b -> {
            isColorPickerOpen = !isColorPickerOpen;
        }).build();

        this.addDrawableChild(rainbowBtn);
        this.addDrawableChild(customColorBtn);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float partialTick) {
        this.renderInGameBackground(context);
        super.render(context, mouseX, mouseY, partialTick);

        long windowHandle = this.client.getWindow().getHandle();
        boolean isMousePressed = GLFW.glfwGetMouseButton(windowHandle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;

        // --- ЛОГИКА ОБРАБОТКИ ИЗМЕНЕНИЯ РАЗМЕРОВ И ПЕРЕМЕЩЕНИЯ ---
        if (isMousePressed && !lastMouseState) {
            // 1. Ресайз за правый край
            if (mouseX >= (windowX + windowWidth - EDGE_PAD) && mouseX <= (windowX + windowWidth + EDGE_PAD) && mouseY >= windowY && mouseY <= (windowY + windowHeight)) {
                resizeRight = true;
                dragOffsetX = mouseX;
                initialWidth = windowWidth;
            }
            // 2. Ресайз за левый край
            else if (mouseX >= (windowX - EDGE_PAD) && mouseX <= (windowX + EDGE_PAD) && mouseY >= windowY && mouseY <= (windowY + windowHeight)) {
                resizeLeft = true;
                dragOffsetX = mouseX;
                initialX = windowX;
                initialWidth = windowWidth;
            }
            // 3. Ресайз за нижний край
            if (mouseY >= (windowY + windowHeight - EDGE_PAD) && mouseY <= (windowY + windowHeight + EDGE_PAD) && mouseX >= windowX && mouseX <= (windowX + windowWidth)) {
                resizeBottom = true;
                dragOffsetY = mouseY;
                initialHeight = windowHeight;
            }

            // 4. Перетаскивание за шапку сайдбара
            if (!resizeRight && !resizeLeft && !resizeBottom && mouseX >= windowX && mouseX <= (windowX + windowWidth) && mouseY >= windowY && mouseY <= (windowY + 25)) {
                isDragging = true;
                dragOffsetX = mouseX - windowX;
                dragOffsetY = mouseY - windowY;
            }

            // Клик по сайдбару (Переключение вкладок)
            if (mouseX >= windowX && mouseX <= (windowX + 110) && mouseY >= windowY && mouseY <= (windowY + windowHeight)) {
                if (mouseY >= (windowY + 40) && mouseY <= (windowY + 55)) {
                    selectedTab = 0;
                    isColorPickerOpen = false;
                    scrollAmount = 0;
                }
            }

            // Клик по крестику палитры
            if (isColorPickerOpen && isPersonalizationOpen && selectedTab == 0) {
                int titleBarHeight = 12;
                int closeX = pickerX + pickerWidth - 10;
                int checkMouseY = mouseY + (int) scrollAmount;
                if (mouseX >= closeX && mouseX <= (closeX + 10) && checkMouseY >= pickerY && checkMouseY <= (pickerY + titleBarHeight)) {
                    isColorPickerOpen = false;
                }
            }

            // Клик по заголовкам подгрупп
            if (selectedTab == 0 && mouseX >= (windowX + 125) && mouseX <= (windowX + windowWidth - 15)) {
                int scrolledMouseY = mouseY + (int) scrollAmount;

                // Клик по "Персонализация"
                if (scrolledMouseY >= (windowY + 40) && scrolledMouseY <= (windowY + 55)) {
                    isPersonalizationOpen = !isPersonalizationOpen;
                }

                // Клик по "Тема GUI"
                int group2Y = windowY + 40 + (isPersonalizationOpen ? (isColorPickerOpen ? 170 : 75) : 20);
                if (scrolledMouseY >= group2Y && scrolledMouseY <= (group2Y + 15)) {
                    isGuiThemeOpen = !isGuiThemeOpen;
                }
            }
        }

        if (!isMousePressed) {
            isDragging = false;
            resizeRight = false;
            resizeLeft = false;
            resizeBottom = false;
        }
        lastMouseState = isMousePressed;

        if (isDragging) {
            windowX = (int) (mouseX - dragOffsetX);
            windowY = (int) (mouseY - dragOffsetY);
        }
        if (resizeRight) {
            windowWidth = Math.max(MIN_WIDTH, initialWidth + (int) (mouseX - dragOffsetX));
        }
        if (resizeLeft) {
            int deltaX = (int) (mouseX - dragOffsetX);
            int nextWidth = initialWidth - deltaX;
            if (nextWidth >= MIN_WIDTH) {
                windowX = initialX + deltaX;
                windowWidth = nextWidth;
            }
        }
        if (resizeBottom) {
            windowHeight = Math.max(MIN_HEIGHT, initialHeight + (int) (mouseY - dragOffsetY));
        }

        int visibleHeight = windowHeight - 35;
        int maxScroll = Math.max(0, maxScrollHeight - visibleHeight);
        if (scrollAmount > maxScroll) scrollAmount = maxScroll;
        if (scrollAmount < 0) scrollAmount = 0;

        // --- ОТРИСОВКА ИНТЕРФЕЙСА ---
        drawRoundedGradientQuad(context, windowX, windowY, windowWidth, windowHeight, 8, COLOR_GRAD_START, COLOR_GRAD_END);

        // Сайдбар слева
        context.fill(windowX, windowY, windowX + 110, windowY + windowHeight, SIDEBAR_BG);
        context.fill(windowX + 110, windowY + 2, windowX + 111, windowY + windowHeight - 2, LINE_COLOR);

        context.drawTextWithShadow(this.textRenderer, "SCRP SYSTEM", windowX + 12, windowY + 12, TECH_CYAN);

        // Объявляем tabColor только ОДИН раз
        int tabColor = (selectedTab == 0) ? TECH_CYAN : TEXT_MUTED;
        if (selectedTab == 0) {
            context.fill(windowX + 4, windowY + 41, windowX + 6, windowY + 53, TECH_CYAN);
        }
        context.drawTextWithShadow(this.textRenderer, "[+] SCRP HUD", windowX + 12, windowY + 43, tabColor);

        // Отрисовка основной части
        if (selectedTab == 0) {
            renderScRpHudTab(context, mouseX, mouseY);
        } else {
            rainbowBtn.setX(-1000);
            customColorBtn.setX(-1000);
        }
    }

    // Восстановленный и рабочий метод градиентного квада с рамками
    private void drawRoundedGradientQuad(DrawContext context, int x, int y, int width, int height, int radius, int colorStart, int colorEnd) {
        context.fillGradient(x, y, x + width, y + height, colorStart, colorEnd);
        context.drawHorizontalLine(x + radius, x + width - radius, y, TECH_CYAN);
        context.drawHorizontalLine(x + radius, x + width - radius, y + height - 1, LINE_COLOR);
        context.drawVerticalLine(x, y + radius, y + height - radius, TECH_CYAN);
        context.drawVerticalLine(x + width - 1, y + radius, y + height - radius, LINE_COLOR);
    }

    private void renderScRpHudTab(DrawContext context, int mouseX, int mouseY) {
        int contentX = windowX + 125;
        int contentY = windowY + 30;
        int contentWidth = windowWidth - 140;
        int contentHeight = windowHeight - 35;

        context.enableScissor(contentX - 5, contentY, contentX + contentWidth + 10, contentY + contentHeight);

        int currentY = contentY + 10 - (int) scrollAmount;

        // --- 1. ПОДГРУППА: ПЕРСОНАЛИЗАЦИЯ ---
        String arrow1 = isPersonalizationOpen ? "▼" : "▶";
        context.drawTextWithShadow(this.textRenderer, arrow1 + "  Персонализация", contentX, currentY, TECH_CYAN);
        context.fill(contentX, currentY + 12, contentX + contentWidth, currentY + 13, LINE_COLOR);

        if (isPersonalizationOpen) {
            int itemY = currentY + 20;
            context.drawTextWithShadow(this.textRenderer, "EXP Color Settings", contentX + 10, itemY + 4, TEXT_MUTED);

            int btn1X = contentX + 10;
            int btn1Y = itemY + 18;
            int btn1W = Math.min(100, contentWidth / 2 - 10);

            if (btn1Y + 20 < contentY || btn1Y > contentY + contentHeight) {
                rainbowBtn.setX(-1000);
            } else {
                rainbowBtn.setX(btn1X);
                rainbowBtn.setY(btn1Y);
                rainbowBtn.setWidth(btn1W);
            }

            boolean hover1 = mouseX >= btn1X && mouseX <= (btn1X + btn1W) && mouseY >= btn1Y && mouseY <= (btn1Y + 20);
            int bg1 = hover1 ? new Color(0, 210, 255, 45).getRGB() : new Color(20, 30, 50, 120).getRGB();
            String status1 = isRainbowXpEnabled ? "Rainbow: ON" : "Rainbow: OFF";
            context.fill(btn1X, btn1Y, btn1X + btn1W, btn1Y + 20, bg1);
            context.drawCenteredTextWithShadow(this.textRenderer, status1, btn1X + btn1W / 2, btn1Y + 6, isRainbowXpEnabled ? TECH_CYAN : Color.WHITE.getRGB());

            int btn2X = btn1X + btn1W + 10;
            int btn2Y = btn1Y;
            int btn2W = btn1W;

            if (btn2Y + 20 < contentY || btn2Y > contentY + contentHeight) {
                customColorBtn.setX(-1000);
            } else {
                customColorBtn.setX(btn2X);
                customColorBtn.setY(btn2Y);
                customColorBtn.setWidth(btn2W);
            }

            boolean hover2 = mouseX >= btn2X && mouseX <= (btn2X + btn2W) && mouseY >= btn2Y && mouseY <= (btn2Y + 20);
            int bg2 = hover2 ? new Color(0, 210, 255, 45).getRGB() : new Color(20, 30, 50, 120).getRGB();
            context.fill(btn2X, btn2Y, btn2X + btn2W, btn2Y + 20, bg2);
            context.drawCenteredTextWithShadow(this.textRenderer, "Custom", btn2X + btn2W / 2, btn2Y + 6, isCustomXpColorEnabled ? customXpColor : Color.WHITE.getRGB());

            pickerX = btn2X;
            pickerY = btn2Y + 24;

            if (isColorPickerOpen) {
                int titleBarHeight = 12;
                int spectrumY = pickerY + titleBarHeight;

                context.fill(pickerX - 3, pickerY - 3, pickerX + pickerWidth + 3, pickerY + pickerHeight + titleBarHeight + 14, 0xFF0D1322);
                context.drawHorizontalLine(pickerX - 3, pickerX + pickerWidth + 2, pickerY - 3, TECH_CYAN);
                context.drawHorizontalLine(pickerX - 3, pickerX + pickerWidth + 2, pickerY + pickerHeight + titleBarHeight + 13, TECH_CYAN);
                context.drawVerticalLine(pickerX - 3, pickerY - 3, pickerY + pickerHeight + titleBarHeight + 13, TECH_CYAN);
                context.drawVerticalLine(pickerX + pickerWidth + 2, pickerY - 3, pickerY + pickerHeight + titleBarHeight + 13, TECH_CYAN);

                int closeX = pickerX + pickerWidth - 10;
                int closeY = pickerY;
                boolean hoverClose = mouseX >= closeX && mouseX <= (closeX + 10) && mouseY >= closeY && mouseY <= (closeY + 12);
                context.drawTextWithShadow(this.textRenderer, "x", closeX, closeY - 2, hoverClose ? 0xFFFF007F : TEXT_MUTED);

                for (int i = 0; i < pickerWidth; i++) {
                    float hue = (float) i / pickerWidth;
                    int baseColor = Color.HSBtoRGB(hue, 0.9f, 0.8f);
                    context.fill(pickerX + i, spectrumY, pickerX + i + 1, spectrumY + pickerHeight, baseColor);
                }
                context.fillGradient(pickerX, spectrumY, pickerX + pickerWidth, spectrumY + pickerHeight, 0x00000000, 0xFF000000);

                long windowHandle = this.client.getWindow().getHandle();
                if (GLFW.glfwGetMouseButton(windowHandle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS) {
                    if (mouseX >= pickerX && mouseX <= (pickerX + pickerWidth) && mouseY >= spectrumY && mouseY <= (spectrumY + pickerHeight)) {
                        if (!(mouseX >= closeX && mouseY <= spectrumY)) {
                            markerX = mouseX - pickerX;
                            markerY = mouseY - spectrumY;

                            float hue = (float) markerX / pickerWidth;
                            float saturation = 0.9f;
                            float brightness = (float) (pickerHeight - markerY) / pickerHeight;

                            customXpColor = Color.HSBtoRGB(hue, saturation, brightness) & 0xFFFFFF;
                            isCustomXpColorEnabled = true;
                            isRainbowXpEnabled = false;
                        }
                    }
                }

                int mX = pickerX + markerX;
                int mY = spectrumY + markerY;
                context.fill(mX - 2, mY - 2, mX + 2, mY + 2, 0xFFFFFFFF);
                context.fill(mX - 1, mY - 1, mX + 1, mY + 1, 0xFF000000);

                int barY = spectrumY + pickerHeight + 5;
                context.fill(pickerX, barY, pickerX + pickerWidth, barY + 6, customXpColor | 0xFF000000);
            }

            currentY += isColorPickerOpen ? 170 : 75;
        } else {
            rainbowBtn.setX(-1000);
            customColorBtn.setX(-1000);
            currentY += 20;
        }

        // --- 2. ПОДГРУППА: ТЕМА GUI ---
        String arrowGui = isGuiThemeOpen ? "▼" : "▶";
        context.drawTextWithShadow(this.textRenderer, arrowGui + "  Тема GUI", contentX, currentY, TECH_CYAN);
        context.fill(contentX, currentY + 12, contentX + contentWidth, currentY + 13, LINE_COLOR);

        if (isGuiThemeOpen) {
            int itemY = currentY + 20;

            boolean hoverToggle = mouseX >= contentX + 10 && mouseX <= contentX + 110 && mouseY >= itemY && mouseY <= itemY + 20;
            int bgToggle = hoverToggle ? new Color(0, 210, 255, 45).getRGB() : new Color(20, 30, 50, 120).getRGB();
            context.fill(contentX + 10, itemY, contentX + 110, itemY + 20, bgToggle);
            context.drawCenteredTextWithShadow(this.textRenderer,
                    ModMenuScreen.isCustomGuiThemeEnabled ? "Theme: ON" : "Theme: OFF",
                    contentX + 60, itemY + 6,
                    ModMenuScreen.isCustomGuiThemeEnabled ? TECH_CYAN : Color.WHITE.getRGB());

            context.fill(contentX + 10, itemY + 28, contentX + 30, itemY + 44,
                    (ModMenuScreen.guiBgAlpha << 24) | ModMenuScreen.guiBgColor);
            context.drawTextWithShadow(this.textRenderer, "BG", contentX + 33, itemY + 32, TEXT_MUTED);

            context.fill(contentX + 55, itemY + 28, contentX + 75, itemY + 44,
                    ModMenuScreen.guiBorderColor | 0xFF000000);
            context.drawTextWithShadow(this.textRenderer, "Border", contentX + 78, itemY + 32, TEXT_MUTED);

            context.fill(contentX + 10, itemY + 50, contentX + 30, itemY + 66,
                    (ModMenuScreen.guiSlotAlpha << 24) | ModMenuScreen.guiSlotColor);
            context.drawTextWithShadow(this.textRenderer, "Slots", contentX + 33, itemY + 54, TEXT_MUTED);

            currentY += 80;
        } else {
            currentY += 20;
        }

        maxScrollHeight = currentY - contentY + (int) scrollAmount;
        context.disableScissor();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (mouseX >= (windowX + 110) && mouseX <= (windowX + windowWidth) && mouseY >= windowY && mouseY <= (windowY + windowHeight)) {
            this.scrollAmount -= verticalAmount * 14.0;
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
//бля
//да сука
//мхм