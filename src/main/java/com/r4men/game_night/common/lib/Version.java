package com.r4men.game_night.common.lib;

import net.neoforged.fml.ModContainer;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.jetbrains.annotations.NotNull;

public record Version(int major, int minor, int build) implements Comparable<Version> {
    public Version(ArtifactVersion artifactVersion) {
        this(artifactVersion.getMajorVersion(), artifactVersion.getMinorVersion(), artifactVersion.getIncrementalVersion());
    }

    public Version(ModContainer container) {
        this(container.getModInfo().getVersion());
    }

    public static Version get(String s) {
        String[] split = s.replace('.', ':').split(":");
        if (split.length != 3) {
            return null;
        }

        int[] digits = new int[3];
        for (int i = 0; i < digits.length; i++) {
            try {
                digits[i] = Integer.parseInt(split[i]);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return new Version(digits[0], digits[1], digits[2]);
    }

    @Override
    public int compareTo(Version version) {
        if (version.major > major) {
            return -1;
        } else if (version.major == major) {
            if (version.minor > minor) {
                return -1;
            } else if (version.minor == minor) {
                return Integer.compare(build, version.build);
            }
        }

        return 1;
    }

    @Override
    public @NotNull String toString() {
        return major + "." + minor + "." + build;
    }
}
