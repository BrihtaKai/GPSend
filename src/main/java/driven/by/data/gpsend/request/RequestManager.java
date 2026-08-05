package driven.by.data.gpsend.request;

import driven.by.data.gpsend.GPSend;
import driven.by.data.gpsend.utils.MessageUtils;
import driven.by.data.gpsend.utils.SendingHandler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RequestManager {

    private final GPSend instance = GPSend.getInstance();

    private final Set<Request> requests = new HashSet<>();

    private boolean cycleStarted = false;


    public boolean newRequest(Player owner, Player target, int amount) {

        if (owner.equals(target)) {
            return false;
        }
        if (isInvolved(owner) || isInvolved(target)) {
            return false;
        }
        Request request = new Request(
                owner,
                target,
                amount,
                Instant.now()
        );
        requests.add(request);
        MessageUtils.sendMessage(
                owner,
                "request.new.sender_confirmation",
                true,
                getReplacers(request)
        );

        MessageUtils.sendMessage(
                target,
                "request.new.target_prompt",
                true,
                getReplacers(request)
        );

        return true;
    }

    public boolean isInvolved(Player player) {
        return requests.stream()
                .anyMatch(r ->
                        r.owner().equals(player) ||
                                r.target().equals(player)
                );
    }


    public void solveRequest(Request request, boolean accepted) {

        if (request == null) {
            return;
        }
        if (!request.owner().isOnline() || !request.target().isOnline()) {
            requests.remove(request);
        }

        if (accepted) {

            MessageUtils.sendMessage(
                    request.owner(),
                    "request.accept.sender_notice",
                    true,
                    getReplacers(request)
            );

            MessageUtils.sendMessage(
                    request.target(),
                    "request.accept.target_confirmation",
                    true,
                    getReplacers(request)
            );


            SendingHandler.handleSending(
                    request.target(),
                    request.owner().getName(),
                    request.amount(),
                    true
            );

        } else {

            MessageUtils.sendMessage(
                    request.owner(),
                    "request.deny.sender_notice",
                    true,
                    getReplacers(request)
            );

            MessageUtils.sendMessage(
                    request.target(),
                    "request.deny.target_confirmation",
                    true,
                    getReplacers(request)
            );
        }

        requests.remove(request);
    }


    public Request getRequestForTarget(Player target) {

        return requests.stream()
                .filter(r -> r.target().equals(target))
                .findFirst()
                .orElse(null);
    }


    public boolean isExpired(Request request) {

        Duration duration = Duration.between(
                request.createdAt(),
                Instant.now()
        );

        return duration.toMinutes() >= instance.getConfig()
                .getInt("request_expire_in");
    }


    public void startExpireRemoveCycle() {

        if (cycleStarted) {
            return;
        }

        cycleStarted = true;

        Bukkit.getScheduler().runTaskTimer(
                instance,
                () -> {

                    requests.removeIf(request -> {

                        if (!isExpired(request)) {
                            return false;
                        }

                        MessageUtils.sendMessage(
                                request.owner(),
                                "request.expire.sender_notice",
                                true,
                                getReplacers(request)
                        );

                        MessageUtils.sendMessage(
                                request.target(),
                                "request.expire.target_notice",
                                true,
                                getReplacers(request)
                        );

                        return true;
                    });

                },
                0L,
                1200L
        );
    }


    private Map<String, String> getReplacers(Request request) {

        Map<String, String> replacers = new HashMap<>();

        replacers.put("%owner%", request.owner().getName());
        replacers.put("%target%", request.target().getName());
        replacers.put("%amount%", String.valueOf(request.amount()));

        return replacers;
    }
}