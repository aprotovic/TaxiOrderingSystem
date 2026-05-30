package com.taxiapp;

/**
 * Central repository of inline CSS styles used across the application.
 * Provides a consistent dark-themed, modern UI without needing external .css files.
 */
public final class AppStyles {

    private AppStyles() {}

    // ── Palette ────────────────────────────────────────────────────────────────
    public static final String COLOR_BG         = "#1a1a2e";
    public static final String COLOR_CARD       = "#16213e";
    public static final String COLOR_ACCENT     = "#e94560";
    public static final String COLOR_ACCENT2    = "#0f3460";
    public static final String COLOR_TEXT       = "#eaeaea";
    public static final String COLOR_SUBTEXT    = "#a0a0b0";
    public static final String COLOR_SUCCESS    = "#2ecc71";
    public static final String COLOR_WARNING    = "#f39c12";

    // ── Root scene / background ────────────────────────────────────────────────
    public static final String SCENE_BG =
        "-fx-background-color: " + COLOR_BG + ";";

    // ── Card panel ────────────────────────────────────────────────────────────
    public static final String CARD =
        "-fx-background-color: " + COLOR_CARD + ";" +
        "-fx-background-radius: 14;" +
        "-fx-padding: 32 36 32 36;" +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.55), 24, 0, 0, 6);";

    // ── Typography ────────────────────────────────────────────────────────────
    public static final String TITLE =
        "-fx-font-size: 26px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: " + COLOR_TEXT + ";" +
        "-fx-font-family: 'Segoe UI', Arial, sans-serif;";

    public static final String SUBTITLE =
        "-fx-font-size: 13px;" +
        "-fx-text-fill: " + COLOR_SUBTEXT + ";" +
        "-fx-font-family: 'Segoe UI', Arial, sans-serif;";

    public static final String SECTION_HEADER =
        "-fx-font-size: 22px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: " + COLOR_TEXT + ";" +
        "-fx-font-family: 'Segoe UI', Arial, sans-serif;";

    public static final String LABEL =
        "-fx-font-size: 12px;" +
        "-fx-text-fill: " + COLOR_SUBTEXT + ";" +
        "-fx-font-family: 'Segoe UI', Arial, sans-serif;";

    // ── Input fields ──────────────────────────────────────────────────────────
    public static final String TEXT_FIELD =
        "-fx-background-color: #0f3460;" +
        "-fx-text-fill: " + COLOR_TEXT + ";" +
        "-fx-prompt-text-fill: #606080;" +
        "-fx-border-color: #2a2a5a;" +
        "-fx-border-radius: 8;" +
        "-fx-background-radius: 8;" +
        "-fx-padding: 8 12 8 12;" +
        "-fx-font-size: 13px;" +
        "-fx-pref-height: 36px;";

    public static final String COMBO_BOX =
        "-fx-background-color: #0f3460;" +
        "-fx-text-fill: " + COLOR_TEXT + ";" +
        "-fx-border-color: #2a2a5a;" +
        "-fx-border-radius: 8;" +
        "-fx-background-radius: 8;" +
        "-fx-pref-height: 36px;" +
        "-fx-font-size: 13px;";

    // ── Buttons ───────────────────────────────────────────────────────────────
    public static final String BTN_PRIMARY =
        "-fx-background-color: " + COLOR_ACCENT + ";" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 13px;" +
        "-fx-font-weight: bold;" +
        "-fx-background-radius: 8;" +
        "-fx-padding: 10 20 10 20;" +
        "-fx-cursor: hand;" +
        "-fx-pref-width: 220px;";

    public static final String BTN_PRIMARY_HOVER =
        "-fx-background-color: #c73652;" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 13px;" +
        "-fx-font-weight: bold;" +
        "-fx-background-radius: 8;" +
        "-fx-padding: 10 20 10 20;" +
        "-fx-cursor: hand;" +
        "-fx-pref-width: 220px;";

    public static final String BTN_SECONDARY =
        "-fx-background-color: #0f3460;" +
        "-fx-text-fill: " + COLOR_TEXT + ";" +
        "-fx-font-size: 13px;" +
        "-fx-background-radius: 8;" +
        "-fx-padding: 10 20 10 20;" +
        "-fx-cursor: hand;" +
        "-fx-pref-width: 220px;";

    public static final String BTN_SECONDARY_HOVER =
        "-fx-background-color: #1a4a80;" +
        "-fx-text-fill: " + COLOR_TEXT + ";" +
        "-fx-font-size: 13px;" +
        "-fx-background-radius: 8;" +
        "-fx-padding: 10 20 10 20;" +
        "-fx-cursor: hand;" +
        "-fx-pref-width: 220px;";

    public static final String BTN_DANGER =
        "-fx-background-color: #8b0000;" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 13px;" +
        "-fx-background-radius: 8;" +
        "-fx-padding: 10 20 10 20;" +
        "-fx-cursor: hand;" +
        "-fx-pref-width: 220px;";

    public static final String BTN_SUCCESS =
        "-fx-background-color: #1a6b3c;" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 13px;" +
        "-fx-background-radius: 8;" +
        "-fx-padding: 10 20 10 20;" +
        "-fx-cursor: hand;" +
        "-fx-pref-width: 220px;";

    public static final String LINK_BUTTON =
        "-fx-background-color: transparent;" +
        "-fx-text-fill: " + COLOR_ACCENT + ";" +
        "-fx-font-size: 12px;" +
        "-fx-cursor: hand;" +
        "-fx-underline: true;";

    // ── ListView ──────────────────────────────────────────────────────────────
    public static final String LIST_VIEW =
        "-fx-background-color: #0d1b3e;" +
        "-fx-border-color: #2a2a5a;" +
        "-fx-border-radius: 8;" +
        "-fx-background-radius: 8;" +
        "-fx-control-inner-background: #0d1b3e;" +
        "-fx-text-fill: " + COLOR_TEXT + ";" +
        "-fx-font-size: 13px;";

    // ── Separator ─────────────────────────────────────────────────────────────
    public static final String SEPARATOR =
        "-fx-background-color: #2a2a5a;";

    /**
     * Applies hover effect to a button: swaps from normalStyle → hoverStyle on
     * mouse enter and reverts on exit.
     */
    public static void addHover(javafx.scene.control.Button btn, String normalStyle, String hoverStyle) {
        btn.setStyle(normalStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(normalStyle));
    }
}
