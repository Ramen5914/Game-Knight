package com.r4men.game_knight.gui.screen;

import com.r4men.game_knight.client.auth.CredentialStorage;
import com.r4men.game_knight.client.auth.LichessAccountClient;
import com.r4men.game_knight.client.auth.LichessTimeControl;
import com.r4men.game_knight.gui.widget.DiscreteSlider;
import com.r4men.game_knight.gui.widget.EvenTabs;
import com.r4men.game_knight.gui.widget.FloatLabel;
import net.ethrocky.pane.core.State;
import net.ethrocky.pane.core.layout.Flex;
import net.ethrocky.pane.core.style.Theme;
import net.ethrocky.pane.runtime.PaneOverlay;
import net.ethrocky.pane.runtime.PaneScreen;
import net.ethrocky.pane.widget.Button;
import net.ethrocky.pane.widget.Label;
import net.ethrocky.pane.widget.Panel;
import net.ethrocky.pane.widget.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class ChessSetupPane extends PaneScreen {
    private final State<Boolean> lichessGame = State.of(true);
    private final State<Boolean> onlineGame = State.of(false);
    private final State<Boolean> localGame = State.of(false);

    private final State<Float> minutes = State.of(10f);
    private final State<Float> increment = State.of(0f);

    private final State<String> ratingText = State.of("Loading Lichess rating...");

    private LichessAccountClient.LichessAccountStatus account;
    private String accountMessage = "Loading Lichess rating...";

    private static final long TOAST_DURATION_MS = 3000;
    private static final long TOAST_FADE_MS = 500;
    private String screenToast;
    private long screenToastShownAt;

    public ChessSetupPane() {
        PaneOverlay.setTheme(new Theme(0xE61E1F24, 0xF2292A31, 0xFFE8E8EC, 0xFF9A9AA5,
                0xFFA85CFF, 0xFF3A3B44, 6, 4));

        Window w = Window.of("Game Setup");
        w.resizable(false);

        super("Setup", w);

        this.minutes.onChange(_ -> refreshRatingText());
        this.increment.onChange(_ -> refreshRatingText());

        this.lichessGame.onChange((bool) -> {
            if (bool) {
                this.onlineGame.set(false);
                this.localGame.set(false);
            }

            refreshRatingText();
        });

        this.onlineGame.onChange((bool) -> {
            if (bool) {
                this.lichessGame.set(false);
                this.localGame.set(false);
            }
        });

        this.localGame.onChange((bool) -> {
            if (bool) {
                this.lichessGame.set(false);
                this.onlineGame.set(false);
            }
        });

        Panel lichessTab = Panel.column();
        lichessTab.align(Flex.Align.STRETCH);

        Panel gameKnightTab = Panel.column();
        gameKnightTab.align(Flex.Align.STRETCH);

        Panel localTab = Panel.column();
        localTab.align(Flex.Align.STRETCH);

        EvenTabs tabs = EvenTabs.of()
                .tab("Lichess", lichessTab)
                .tab("Game Knight", gameKnightTab)
                .tab("Local", localTab);

        tabs.onTabChanged(i -> {
           if (i == 0) showScreenToast("Lichess games require a time\ncontrol of at least 10 minutes");
        });

        tabs.grow(1);
        w.add(tabs);

        lichessTab.add(
                Panel.row()
                    .add(Label.of("Minutes per side"))
                    .add(FloatLabel.of(this.minutes)
                            .dim(true))
                    .justify(Flex.Justify.SPACE_BETWEEN))
                .add(Panel.row()
                        .add(DiscreteSlider.of(
                                        this.minutes,
                                        0, 0.25f, 0.5f, 0.75f, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                                        11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 25, 30, 35,
                                        40, 45, 60, 75, 90, 105, 120, 135, 150, 165, 180)
                                .limits(
                                        () -> 10.0,
                                        () -> Double.POSITIVE_INFINITY)
                                .grow(1)))
                .add(Panel.row()
                        .add(Label.of("Increment in seconds"))
                        .add(FloatLabel.of(this.increment)
                                .dim(true))
                        .justify(Flex.Justify.SPACE_BETWEEN))
                .add(Panel.row()
                        .add(DiscreteSlider.of(
                                        this.increment,
                                        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                                        11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                                        25, 30, 35, 40, 45, 60, 90, 120, 150, 180)
                                .limits(
                                        () -> Double.NEGATIVE_INFINITY,
                                        () -> Double.POSITIVE_INFINITY)
                                .grow(1))
                );

        lichessTab.add(Label.of(this.ratingText).dim(true));

        // Online Tab
        gameKnightTab.add(
                        Panel.row()
                                .add(Label.of("Minutes per side"))
                                .add(FloatLabel.of(this.minutes)
                                        .dim(true))
                                .justify(Flex.Justify.SPACE_BETWEEN)
                )
                .add(Panel.row()
                        .add(DiscreteSlider.of(
                                        this.minutes,
                                        0, 0.25f, 0.5f, 0.75f, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                                        11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 25, 30, 35,
                                        40, 45, 60, 75, 90, 105, 120, 135, 150, 165, 180)
                                .limits(
                                        () -> increment.get() == 0f ? 0.25 : 0.0,
                                        () -> Double.POSITIVE_INFINITY)
                                .grow(1)))
                .add(Panel.row()
                        .add(Label.of("Increment in seconds"))
                        .add(FloatLabel.of(this.increment)
                                .dim(true))
                        .justify(Flex.Justify.SPACE_BETWEEN))
                .add(Panel.row()
                        .add(DiscreteSlider.of(
                                        this.increment,
                                        0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                                        11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                                        25, 30, 35, 40, 45, 60, 90, 120, 150, 180)
                                .limits(
                                        () -> minutes.get() == 0f ? 1.0 : 0.0,
                                        () -> Double.POSITIVE_INFINITY)
                                .grow(1))
                );

        w.add(Button.of("Start"));

        loadAccount();

        showScreenToast("Lichess games require a time\ncontrol of at least 10 minutes");
    }

    private void refreshRatingText() {
        if (!lichessGame.get()) {
            ratingText.set("Lichess rating: —");
            return;
        }

        if (account == null) {
            ratingText.set(accountMessage);
            return;
        }

        LichessTimeControl control = LichessTimeControl.from(
                minutes.get(),
                increment.get()
        );

        int rating = account.rating(control);

        ratingText.set(
                control.displayName() + " rating: "
                        + (rating >= 0 ? Integer.toString(rating) : "Unrated")
        );
    }

    // Call on the client/UI thread after loading account data.
    public void setAccount(
            LichessAccountClient.LichessAccountStatus account
    ) {
        this.account = account;
        refreshRatingText();
    }

    // Call on the client/UI thread if loading fails or no token exists.
    public void setAccountMessage(String message) {
        this.account = null;
        this.accountMessage = message;
        refreshRatingText();
    }

    private void loadAccount() {
        Minecraft client = Minecraft.getInstance();

        CompletableFuture.runAsync(() -> {
            try {
                CredentialStorage storage = new CredentialStorage();
                String token = storage.loadLichessToken();

                if (token == null || token.isBlank()) {
                    client.execute(() ->
                            setAccountMessage(
                                    "Sign in to view your Lichess rating"
                            )
                    );
                    return;
                }

                LichessAccountClient.LichessAccountStatus result =
                        LichessAccountClient.fetchAccountStatus(token);

                client.execute(() -> setAccount(result));

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();

                client.execute(() ->
                        setAccountMessage("Lichess rating loading interrupted")
                );

            } catch (IOException | RuntimeException e) {
                client.execute(() ->
                        setAccountMessage("Could not load Lichess rating")
                );

                // Log only the exception type here: the account client's
                // exception message can include the HTTP response body.
                System.err.println(
                        "Failed to load Lichess rating: "
                                + e.getClass().getSimpleName()
                );
            }
        });
    }

    private void showScreenToast(String message) {
        screenToast = message;
        screenToastShownAt = System.currentTimeMillis();
    }

    @Override
    public void extractRenderState(
            GuiGraphicsExtractor graphics,
            int mouseX,
            int mouseY,
            float delta
    ) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        if (screenToast == null) {
            return;
        }

        long elapsed = System.currentTimeMillis() - screenToastShownAt;

        if (elapsed >= TOAST_DURATION_MS) {
            screenToast = null;
            return;
        }

        long remaining = TOAST_DURATION_MS - elapsed;
        float opacity = Math.min(1f, remaining / (float) TOAST_FADE_MS);

        // Avoid submitting nearly transparent text.
        if (opacity < 0.02f) {
            return;
        }

        String[] lines = screenToast.split("\n", -1);

        int padding = 8;
        int margin = 10;
        int lineHeight = font.lineHeight + 2;
        int textWidth = 0;

        for (String line : lines) {
            textWidth = Math.max(textWidth, font.width(line));
        }

        int toastWidth = textWidth + padding * 2;
        int toastHeight = lines.length * lineHeight + padding * 2;

        int x = Math.max(4, width - toastWidth - margin);
        int y = margin;

        graphics.fill(
                x, y,
                x + toastWidth, y + toastHeight,
                withOpacity(0xF2292A31, opacity)
        );

        graphics.fill(
                x, y,
                x + toastWidth, y + 2,
                withOpacity(0xFFA85CFF, opacity)
        );

        for (int i = 0; i < lines.length; i++) {
            graphics.text(
                    font,
                    lines[i],
                    x + padding,
                    y + padding + i * lineHeight,
                    withOpacity(0xFFE8E8EC, opacity),
                    false
            );
        }
    }

    private static int withOpacity(int argb, float opacity) {
        int originalAlpha = (argb >>> 24) & 0xFF;
        int alpha = Math.round(originalAlpha * opacity);

        return (alpha << 24) | (argb & 0x00FFFFFF);
    }
}
