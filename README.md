# GPSend — GriefPrevention Claim Block Transfer Addon

GPSend lets players send and request GriefPrevention claim blocks from each other, through either plain commands or fully configurable GUIs. No more admins manually moving blocks around for players who want to trade or gift them.

## **2.X.X -> 3.0.0:**
Config was completely changed in v3.0.0 so when you update to v3.0.0 your config.yml will be backed up into v2.yml and new up to date config.yml will be created.

**Download:**
- [Releases](https://github.com/BrihtaKai/GPSend/releases) — stable builds
- [Actions](https://github.com/BrihtaKai/GPSend/actions) — dev builds

---

## Features

- **Send claim blocks** to a specific player or to everyone online at once.
- **Request claim blocks** from another player — they get a prompt to accept or deny, with automatic expiry if they don't respond in time.
- **GUI or command-line** — every action works either through an inventory GUI (`/gpsend`, `/gprequest new`) or a direct one-line command for the players who prefer typing it out.
- **Fully configurable GUIs** — materials, custom model data, display names, lore, and slots are all set in `config.yml`, so you can reskin every button with a resource pack.
- **All 5 GriefPrevention claim block modes supported** (total, bonus, accrued, remaining, remaining-bonus-cap), matching however your server has GriefPrevention configured.
- **PlaceholderAPI support** in GUI text and chat messages, if installed.
- **Fully customizable messages**, all in one place in `config.yml`.
- **Command aliases** — add extra trigger words for `/gpsend` and `/gprequest` without touching `plugin.yml`.

---

## Requirements

| Plugin | Required? | Purpose |
|---|---|---|
| [GriefPrevention](https://modrinth.com/plugin/griefprevention) | **Required** | Source of the claim block balances GPSend transfers |
| [PlaceholderAPI](https://modrinth.com/plugin/placeholderapi) | Optional | Enables placeholders in GUI text and messages |

---

## Installation

1. Drop the GPSend jar into your server's `plugins/` folder.
2. Restart (or `/reload`) the server. A default `config.yml` will be generated.
3. Adjust `claimblocks_type`, messages, and GUI appearance to your liking, then `/gpsend reload`.

---

## Commands

### Sending — `/gpsend`

| Command | Description | Permission |
|---|---|---|
| `/gpsend` | Open the claim block transfer GUI | `gpsend.send` |
| `/gpsend player <player> <amount>` | Send blocks directly to a specific player | `gpsend.send` |
| `/gpsend player <player>` | Open the amount-picker GUI for that player | `gpsend.send` |
| `/gpsend player` | Open the player-picker GUI | `gpsend.send` |
| `/gpsend all <amount>` | Send blocks directly to everyone online | `gpsend.sendall` |
| `/gpsend all` | Open the amount-picker GUI for "send to everyone" | `gpsend.sendall` |
| `/gpsend reload` | Reload `config.yml` | `gpsend.admin` |

### Requesting — `/gprequest`

| Command | Description | Permission |
|---|---|---|
| `/gprequest new <player> <amount>` | Send a claim block request directly | `gprequest.new` |
| `/gprequest new <player>` | Open the amount-picker GUI for that player | `gprequest.new` |
| `/gprequest new` | Open the player-picker GUI | `gprequest.new` |
| `/gprequest accept` | Accept your pending incoming request | — |
| `/gprequest deny` | Deny your pending incoming request | — |

A player can only have one active request involving them at a time (as either the requester or the target). Unanswered requests automatically expire after the time set by `request_expire_in` in the config.

---

## Configuration highlights

- **`claimblocks_type`** — which GriefPrevention balance transfers are measured against (total / bonus / accrued / remaining / remaining-bonus-cap). See the comments in `config.yml` for what each mode means.
- **`request_expire_in`** — how many minutes an unanswered request stays valid.
- **`command_alias_gpsend`** / **`command_alias_gprequest`** — additional command names that trigger each feature.
- **`gui.*`** — per-GUI slot layout, item materials, custom model data, display names, and lore, for full resource-pack compatibility.
- **`errors` / `transfer` / `send` / `request`** — every player-facing message, organized by which part of the plugin sends it.

---

## Contributing

Pull requests are welcome, especially for bug fixes and small feature additions. Please keep PRs focused and self-contained so they're easy to review.

---

## License

This project is licensed under the [MIT License](LICENSE).
