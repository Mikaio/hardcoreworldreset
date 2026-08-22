package com.frankloq;

import java.util.UUID;

public record DeathRecord(
    UUID uuid,
    String name,
    int deaths
) {
}