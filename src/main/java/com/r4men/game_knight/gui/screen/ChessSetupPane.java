package com.r4men.game_knight.gui.screen;

import com.r4men.game_knight.gui.widget.DiscreteSlider;
import com.r4men.game_knight.gui.widget.FloatLabel;
import net.ethrocky.pane.core.State;
import net.ethrocky.pane.core.layout.Flex;
import net.ethrocky.pane.core.style.Theme;
import net.ethrocky.pane.runtime.PaneOverlay;
import net.ethrocky.pane.runtime.PaneScreen;
import net.ethrocky.pane.widget.*;

public class ChessSetupPane extends PaneScreen {
    private final State<Boolean> lichessGame = State.of(true);
    private final State<Boolean> onlineGame = State.of(false);
    private final State<Boolean> localGame = State.of(false);

    private final State<Float> minutes = State.of(10f);
    private final State<Float> increment = State.of(0f);

    public ChessSetupPane() {
        PaneOverlay.setTheme(new Theme(0xE61E1F24, 0xF2292A31, 0xFFE8E8EC, 0xFF9A9AA5,
                0xFFA85CFF, 0xFF3A3B44, 6, 4));

        Window w = Window.of("Game Setup");
        w.resizable(false);

        super("Setup", w);

        this.lichessGame.onChange((bool) -> {
            if (bool) {
                this.onlineGame.set(false);
                this.localGame.set(false);
            }
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

//        w.add(Tabs.of()
//                .tab("Lichess", Panel.column()));

        w.add(Panel.row()
                .add(Checkbox.of("Lichess", this.lichessGame))
                .add(Checkbox.of("Online", this.onlineGame))
                .add(Checkbox.of("Local", this.localGame))
                .justify(Flex.Justify.SPACE_BETWEEN));

        w.add(Label.of("Your presets").dim(true));

        w.add(Panel.row()
                        .add(Label.of("Minutes per side"))
                        .add(FloatLabel.of(this.minutes)
                                .dim(true))
                        .justify(Flex.Justify.SPACE_BETWEEN))
                .add(Panel.row()
                        .add(DiscreteSlider.of(this.minutes, 0, 0.5f, 0.75f, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 25, 30, 35, 40, 45, 60, 75, 90, 105, 120, 135, 150, 165, 180).grow(1)))
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
                                25, 30, 35, 40, 45, 60, 90, 120, 150, 180
                        ).grow(1))
                );

        w.add(Button.of("Start"));
    }
}
