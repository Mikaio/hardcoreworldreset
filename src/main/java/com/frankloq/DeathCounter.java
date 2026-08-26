package com.frankloq;

import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DeathCounter {

    private static final Map<UUID, DeathRecord> deaths = new HashMap<>();

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

                String[] parts = line.split("=", 3);

                if (parts.length != 3) {
                    continue;
                }

                UUID uuid = UUID.fromString(parts[0]);
                String name = parts[1];
                int count = Integer.parseInt(parts[2]);

                deaths.put(
                    uuid,
                    new DeathRecord(uuid, name, count)
                );
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println(
                "Failed to load death counter: " + e.getMessage()
            );
        }
    }

    public static void save() {
        try {
            Files.createDirectories(DEATHS_FILE.getParent());

            StringBuilder content = new StringBuilder();

            for (DeathRecord record : deaths.values()) {
                content.append(record.uuid())
                    .append("=")
                    .append(record.name())
                    .append("=")
                    .append(record.deaths())
                    .append("\n");
            }

            Files.writeString(
                DEATHS_FILE,
                content.toString()
            );
        } catch (IOException e) {
            System.err.println(
                "Failed to save death counter: " + e.getMessage()
            );
        }
    }

    public static void increment(UUID uuid, String name) {
        DeathRecord current = deaths.get(uuid);

        if (current == null) {
            deaths.put(
                uuid,
                new DeathRecord(uuid, name, 1)
            );
        } else {
            deaths.put(
                uuid,
                new DeathRecord(
                    uuid,
                    name,
                    current.deaths() + 1
                )
            );
        }

        save();
    }

    public static int getDeaths(UUID uuid) {
        DeathRecord record = deaths.get(uuid);

        return record == null ? 0 : record.deaths();
    }

    public static List<DeathRecord> getAllDeaths() {
        return new ArrayList<>(deaths.values());
    }
}