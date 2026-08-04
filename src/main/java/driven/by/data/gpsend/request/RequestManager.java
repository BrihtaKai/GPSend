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
                "request_sender_new",
                true,
                getReplacers(request)
        );

        MessageUtils.sendMessage(
                target,
                "request_target_new",
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

        if (accepted) {

            MessageUtils.sendMessage(
                    request.owner(),
                    "request_sender_accept",
                    true,
                    getReplacers(request)
            );

            MessageUtils.sendMessage(
                    request.target(),
                    "request_target_accept",
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
                    "request_sender_deny",
                    true,
                    getReplacers(request)
            );

            MessageUtils.sendMessage(
                    request.target(),
                    "request_target_deny",
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
                                "request_sender_expire",
                                true,
                                getReplacers(request)
                        );

                        MessageUtils.sendMessage(
                                request.target(),
                                "request_target_expire",
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