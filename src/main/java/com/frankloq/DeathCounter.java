package com.frankloq;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeathCounter {

    private static final Map<UUID, Integer> deaths = new HashMap<>();

    private static final Path DEATHS_FILE = FabricLoader.getInstance()
            .getConfigDir()
            .resolve("hardcoreworldreset-deaths.properties");

    public static void load() {
        deaths.clear();

        if (!Files.exists(DEATHS_FILE)) {
            return;
        }

        try {
            for (String line : Files.readAllLines(DEATHS_FILE)) {
                if (line.isBlank() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("=");

                if (parts.length != 2) {
                    continue;
                }

                UUID uuid = UUID.fromString(parts[0]);
                int count = Integer.parseInt(parts[1]);

                deaths.put(uuid, count);
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Failed to load death counter: " + e.getMessage());
        }
    }

    public static void save() {
        try {
            Files.createDirectories(DEATHS_FILE.getParent());

            StringBuilder content = new StringBuilder();

            for (Map.Entry<UUID, Integer> entry : deaths.entrySet()) {
                content.append(entry.getKey())
                        .append("=")
                        .append(entry.getValue())
                        .append("\n");
            }

            Files.writeString(DEATHS_FILE, content.toString());
        } catch (IOException e) {
            System.err.println("Failed to save death counter: " + e.getMessage());
        }
    }

    public static void increment(UUID uuid) {
        deaths.merge(uuid, 1, Integer::sum);
        save();
    }

    public static int getDeaths(UUID uuid) {
        return deaths.getOrDefault(uuid, 0);
    }

    public static Map<UUID, Integer> getAllDeaths() {
        return new HashMap<>(deaths);
    }
}