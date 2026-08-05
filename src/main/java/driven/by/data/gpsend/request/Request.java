package driven.by.data.gpsend.request;

import org.bukkit.entity.Player;

import java.time.Instant;

public record Request(
        Player owner,
        Player target,
        int amount,
        Instant createdAt
) {}
