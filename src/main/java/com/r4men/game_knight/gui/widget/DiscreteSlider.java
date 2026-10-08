package com.r4men.game_knight.gui.widget;

import net.ethrocky.pane.core.*;
import net.ethrocky.pane.core.style.Theme;
import net.ethrocky.pane.render.PaneRenderer;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.DoubleSupplier;

public class DiscreteSlider extends Component {
    private final State<Float> value;
    private final float[] stops;

    private DoubleSupplier minimum = () -> Double.NEGATIVE_INFINITY;
    private DoubleSupplier maximum = () -> Double.POSITIVE_INFINITY;

    // Assumes PaneRenderer.fill accepts an ARGB int.
    private int disabledTrackColor = 0xFFE05252;

    private boolean dragging;

    private record AllowedRange(int first, int last) {}

    private DiscreteSlider(State<Float> value, float... stops) {
        this.value = Objects.requireNonNull(value, "value");
        Objects.requireNonNull(stops, "stops");

        if (stops.length < 2) {
            throw new IllegalArgumentException(
                    "A discrete slider needs at least two stops"
            );
        }

        this.stops = stops.clone();
        Arrays.sort(this.stops);

        for (int i = 0; i < this.stops.length; i++) {
            float stop = this.stops[i];

            if (!Float.isFinite(stop)) {
                throw new IllegalArgumentException("Stops must be finite");
            }

            if (i > 0 && stop == this.stops[i - 1]) {
                throw new IllegalArgumentException("Stops must be unique");
            }
        }

        refreshLimits();
    }

    public static DiscreteSlider of(State<Float> value, float... stops) {
        return new DiscreteSlider(value, stops);
    }

    public DiscreteSlider limits(
            DoubleSupplier minimum,
            DoubleSupplier maximum
    ) {
        this.minimum = Objects.requireNonNull(minimum, "minimum");
        this.maximum = Objects.requireNonNull(maximum, "maximum");

        refreshLimits();
        return this;
    }

    public DiscreteSlider disabledTrackColor(int color) {
        this.disabledTrackColor = color;
        return this;
    }

    private AllowedRange allowedRange() {
        double min = minimum.getAsDouble();
        double max = maximum.getAsDouble();

        if (Double.isNaN(min) || Double.isNaN(max) || min > max) {
            throw new IllegalStateException(
                    "Invalid slider limits: " + min + " .. " + max
            );
        }

        int first = 0;
        while (first < stops.length && stops[first] < min) {
            first++;
        }

        int last = stops.length - 1;
        while (last >= 0 && stops[last] > max) {
            last--;
        }

        if (first > last) {
            throw new IllegalStateException(
                    "No slider stops within limits: " + min + " .. " + max
            );
        }

        return new AllowedRange(first, last);
    }

    /**
     * Re-evaluate dynamic limits and snap the value if necessary.
     * Call this after external state changes for immediate synchronization.
     */
    public void refreshLimits() {
        normalizeValue(allowedRange());
    }

    private void normalizeValue(AllowedRange range) {
        int index = nearestStopIndex(
                value.get(),
                range.first(),
                range.last()
        );

        setValue(stops[index]);
    }

    private void setValue(float snapped) {
        if (Float.compare(value.get(), snapped) != 0) {
            value.set(snapped);
        }
    }

    private int nearestStopIndex(double raw, int first, int last) {
        if (!Double.isFinite(raw)) {
            return first;
        }

        int nearestIndex = first;
        double nearestDistance = Math.abs(raw - stops[first]);

        for (int i = first + 1; i <= last; i++) {
            double distance = Math.abs(raw - stops[i]);

            if (distance < nearestDistance) {
                nearestIndex = i;
                nearestDistance = distance;
            }
        }

        return nearestIndex;
    }

    private float fraction(int index) {
        return index / (float) (stops.length - 1);
    }

    @Override
    public Size measure(TextMeasurer tm) {
        return new Size(
                style.width != null ? style.width : 120,
                tm.height() + 6
        );
    }

    @Override
    protected boolean handleMouse(UiEvent.Mouse e) {
        refreshLimits();

        switch (e.kind()) {
            case DOWN -> {
                if (e.inside(bounds)) {
                    dragging = true;
                    setFromX(e.x());
                    return true;
                }
            }
            case MOVE -> {
                if (dragging) {
                    setFromX(e.x());
                    return true;
                }
            }
            case UP -> {
                if (dragging) {
                    dragging = false;
                    return true;
                }
            }
            default -> {}
        }

        return false;
    }

    private void setFromX(double x) {
        AllowedRange range = allowedRange();

        double t = bounds.w() <= 0
                ? 0
                : (x - bounds.x()) / bounds.w();

        t = Math.clamp(t, 0, 1);

        // Map to the original stop positions, then restrict selection.
        int index = (int) Math.round(t * (stops.length - 1));
        index = Math.clamp(index, range.first(), range.last());

        setValue(stops[index]);
    }

    @Override
    protected void paintSelf(PaneRenderer r, Theme theme) {
        AllowedRange range = allowedRange();
        normalizeValue(range);

        int width = Math.max(0, bounds.w());
        if (width == 0) {
            return;
        }

        int trackY = bounds.y() + bounds.h() / 2 - 1;

        int allowedStart = Math.round(width * fraction(range.first()));
        int allowedEnd = Math.round(width * fraction(range.last()));

        int selectedIndex = nearestStopIndex(
                value.get(),
                range.first(),
                range.last()
        );

        int selectedX = Math.round(width * fraction(selectedIndex));

        // Base track.
        r.fill(
                new Rect(bounds.x(), trackY, width, 2),
                theme.border()
        );

        // Fill only the allowed portion up to the selected stop.
        if (selectedX > allowedStart) {
            r.fill(
                    new Rect(
                            bounds.x() + allowedStart,
                            trackY,
                            selectedX - allowedStart,
                            2
                    ),
                    theme.accent()
            );
        }

        // Excluded region below the minimum.
        if (allowedStart > 0) {
            r.fill(
                    new Rect(bounds.x(), trackY, allowedStart, 2),
                    disabledTrackColor
            );
        }

        // Excluded region above the maximum.
        if (allowedEnd < width) {
            r.fill(
                    new Rect(
                            bounds.x() + allowedEnd,
                            trackY,
                            width - allowedEnd,
                            2
                    ),
                    disabledTrackColor
            );
        }

        int handleWidth = Math.min(4, width);
        int handleX = bounds.x() + Math.clamp(
                selectedX - handleWidth / 2,
                0,
                width - handleWidth
        );

        r.fill(
                new Rect(handleX, bounds.y(), handleWidth, bounds.h()),
                hovered || dragging ? theme.accent() : theme.text()
        );
    }
}