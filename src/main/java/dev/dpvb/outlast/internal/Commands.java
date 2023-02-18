package dev.dpvb.outlast.internal;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import dev.dpvb.outlast.sql.SQLService;
import dev.dpvb.outlast.sql.cache.LocationCache;
import dev.dpvb.outlast.sql.cache.PlayerCache;
import dev.dpvb.outlast.sql.cache.TeamCache;
import dev.dpvb.outlast.sql.models.SQLLocation;
import dev.dpvb.outlast.sql.models.SQLTeam;
import dev.dpvb.outlast.teams.TeamError;
import dev.dpvb.outlast.teams.TeamInvite;
import dev.dpvb.outlast.teams.TeamService;
import dev.dpvb.outlast.teleportation.ChannelingTeleport;
import dev.dpvb.outlast.teleportation.TeleportRequest;
import dev.dpvb.outlast.teleportation.TeleportService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

class Commands {

    private final TeamCache teamCache;
    private final PlayerCache playerCache;
    private final LocationCache locationCache;

    Commands() {
        final SQLService sql = SQLService.getInstance();
        teamCache = sql.getTeamCache();
        playerCache = sql.getPlayerCache();
        locationCache = sql.getLocationCache();
    }

    public void initCommands(Plugin plugin) throws Exception {
        final PaperCommandManager<CommandSender> manager = PaperCommandManager.createNative(plugin, CommandExecutionCoordinator.simpleCoordinator());
        new AnnotationParser<>(manager, CommandSender.class, parameters -> SimpleCommandMeta.empty()).parse(this);
    }

    @Suggestions("team-members")
    public List<String> teamMembers(CommandContext<CommandSender> sender, String input) {
        return List.of("TODO"); // TODO impl
    }

    // Player commands
    // TP commands
    @CommandMethod(value = "tp <player>", requiredSender = Player.class)
    @CommandDescription("Sends a teleport request to the named player")
    public void teleport(CommandSender sender, @NotNull @Argument(value = "player", suggestions = "players-except-self") Player target) {
        final Player player = (Player) sender;
        if (target.equals(player)) {
            player.sendPlainMessage("You can't send a teleport request to yourself.");
            return;
        }
        final var teleportRequest = TeleportService.getInstance().requestTeleport(player, target);
        player.sendPlainMessage("This request will expire in " + TeleportRequest.TIMEOUT + " seconds.");
        target.sendPlainMessage("You have received a teleport request from " + player.getName() + ".");
        target.sendMessage(
                Component.text("Accept with ").append(
                        Component.text("/tpaccept")
                                .color(NamedTextColor.AQUA)
                                .hoverEvent(Component.text("Accept teleport request"))
                                .clickEvent(ClickEvent.suggestCommand("/tpaccept"))
                ).append(Component.text(". The request will expire in " + TeleportRequest.TIMEOUT + " seconds."))
        ); // TODO tpdeny?
    }

    @Suggestions("players-except-self")
    public List<String> playersExceptSelf(CommandContext<CommandSender> sender, String input) {
        final String playerName = sender.getSender().getName();
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> !name.equals(playerName))
                .toList();
    }

    @CommandMethod(value = "tpaccept", requiredSender = Player.class)
    @CommandDescription("Accepts any teleport request sent to you")
    public void acceptTeleports(CommandSender sender) {
        final Player player = (Player) sender;
        final var teleportRequest = TeleportService.getInstance().getRequest(player);
        // Check for Teleport Request!
        if (teleportRequest == null) {
            player.sendPlainMessage("You have no pending teleport requests.");
            return;
        }

        // Attempt to Accept Teleport Request
        if (teleportRequest.accept()) {
            teleportRequest.getSender().sendPlainMessage("Your teleport request was accepted.");
            player.sendPlainMessage("You accepted " + teleportRequest.getSender().getName() + "'s teleport request!");
            TeleportService.getInstance().teleportPlayer(teleportRequest.getSender(), player);
            return;
        }

        player.sendPlainMessage("The teleport request " + switch (teleportRequest.getState()) {
            case SENT -> new IllegalStateException();
            case ACCEPTED -> "was already accepted.";
            case DENIED -> "was denied.";
            case EXPIRED -> "expired.";
        });
    }

    @CommandMethod(value = "spawn", requiredSender = Player.class)
    @CommandDescription("Teleports you to the spawn point")
    public void spawn(CommandSender sender) {
        final Player player = (Player) sender;
        if (TeleportService.getInstance().teleportSpawn(player) == null) {
            player.sendMessage("An error occurred. No spawn location is set right now.");
        }
    }
    // end TP commands

    @CommandMethod(value = "report <player>", requiredSender = Player.class)
    @CommandDescription("Reports the named player")
    public void report(CommandSender sender, @NotNull @Argument("player") Player target) {
    }

    // Team commands
    @CommandMethod(value = "team create <name>", requiredSender = Player.class)
    @CommandDescription("Creates a team with a unique name")
    public void createTeam(CommandSender sender, @NotNull @Argument("name") @Regex(".{1,30}") String name) {
        final Player player = (Player) sender;
        // create the team
        try {
            TeamService.getInstance().createTeam(name, player.getUniqueId());
            player.sendPlainMessage("Created Team " + name + ".");
        } catch (TeamError.Exists e) {
            player.sendPlainMessage(e.getMessage());
        } catch (TeamError.PlayerAlreadyTeamed e) {
            player.sendPlainMessage("You are already on a team.");
        }
    }

    @CommandMethod(value = "team invite <player>", requiredSender = Player.class)
    @CommandDescription("Invite a Player to the Team")
    public void invitePlayer(CommandSender sender, @NotNull @Argument(value = "player", suggestions = "players-except-self") Player target) {
        final Player player = (Player) sender;
        final TeamService teamService = TeamService.getInstance();
        // check if they are attempting to invite themselves.
        if (player.equals(target)) {
            player.sendPlainMessage("You can't invite yourself.");
            return;
        }

        // check if they are not on a team.
        String team = teamService.getTeam(player.getUniqueId());
        if (team == null) {
            player.sendPlainMessage("You must be on a team to perform this command.");
            return;
        }

        // check if the sender is the leader.
        if (!teamService.isLeaderOfTeam(player.getUniqueId(), team)) {
            player.sendPlainMessage("You must be the team leader to do this.");
            return;
        }

        // check if the player you want to invite is already on a team
        if (teamService.getTeam(target.getUniqueId()) != null) {
            player.sendPlainMessage("That player is already on a team.");
            return;
        }

        // check if team is full
        if (teamService.isTeamFull(team)) {
            player.sendPlainMessage("The team is full, so you can not add anymore players.");
            return;
        }

        // invite player
        final TeamInvite teamInvite = teamService.invitePlayer(target, team);
        player.sendPlainMessage("Request will timeout in " + TeamInvite.TIMEOUT + " seconds.");
        target.sendPlainMessage(player.getName() + " sent you an invite to join Team " + team);
    }

    @CommandMethod(value = "team join", requiredSender = Player.class)
    @CommandDescription("Accept any pending requests to join a Team.")
    public void joinTeam(CommandSender sender) {
        final Player player = (Player) sender;
        final TeamService teamService = TeamService.getInstance();

        // check for pending requests and join if possible
        final TeamInvite teamInvite = teamService.getInvite(player);
        if (teamInvite == null) {
            player.sendPlainMessage("You do not have any pending team requests.");
            return;
        }

        // attempt to accept and join
        if (teamInvite.getState() == TeamInvite.State.SENT) {
            try {
                teamService.joinTeam(teamInvite.getTeamName(), player.getUniqueId());
                teamInvite.accept(); // move to accepted state after joining
                player.sendPlainMessage("Joined team!");
            } catch (TeamError.DoesNotExist | TeamError.Full e) {
                player.sendPlainMessage(e.getMessage());
            } catch (TeamError.PlayerAlreadyTeamed ignored) {
                player.sendPlainMessage("You are already in a team.");
            }
            return;
        }

        player.sendPlainMessage("The team request " + switch (teamInvite.getState()) {
            case SENT -> new IllegalStateException();
            case ACCEPTED -> "was already accepted.";
            case DECLINED -> "was declined.";
            case EXPIRED -> "expired.";
        });
    }

    @CommandMethod(value = "team info [name]", requiredSender = Player.class)
    @CommandDescription("Displays information about your team or the given team name")
    public void getTeamInfo(CommandSender sender, @Nullable @Argument("name") @Regex(".{1,30}") String name) {
        final Player player = (Player) sender;
        // Get the name of the team you want info on.
        if (name == null) {
            name = playerCache.getModel(player.getUniqueId()).getTeam_name();
            if (name == null) {
                player.sendPlainMessage("You are not on a team!");
                return;
            }
        } else {
            SQLTeam team = teamCache.getModel(name);
            if (team == null) {
                player.sendPlainMessage("That team does not exist.");
                return;
            }
        }

        final String teamName = name;
        OfflinePlayer teamLeader = Bukkit.getOfflinePlayer(teamCache.getModel(teamName).getLeader());
        List<OfflinePlayer> otherMembers = playerCache.getModels().stream()
                .filter(sqlPlayer -> teamName.equals(sqlPlayer.getTeam_name()))
                .map(sqlPlayer -> Bukkit.getOfflinePlayer(sqlPlayer.getPlayer_uuid()))
                .toList();
        player.sendPlainMessage("Team " + name + ":");
        player.sendPlainMessage(teamLeader.getName() + " (Leader)");
        for (OfflinePlayer offlinePlayer : otherMembers) {
            if (offlinePlayer.equals(teamLeader)) {
                continue;
            }
            player.sendPlainMessage(offlinePlayer.getName() + "");
        }
    }

    @CommandMethod(value = "team userinfo <player>", requiredSender = Player.class)
    @CommandDescription("Gets the team info of the named player")
    public void getUserInfo(CommandSender sender, @NotNull @Argument("player") Player target) {
    }

    @CommandMethod(value = "team leave", requiredSender = Player.class)
    @CommandDescription("Leave your team")
    public void leaveTeam(CommandSender sender) {
        final Player player = (Player) sender;
        try {
            TeamService.getInstance().leaveTeam(player.getUniqueId());
            player.sendPlainMessage("You left the team.");
        } catch (TeamError.PlayerNotTeamed ignored) {
            player.sendPlainMessage("You are not in a team.");
        }
    }

    @CommandMethod(value = "team setleader <player>", requiredSender = Player.class)
    @CommandDescription("Sets the team leader to the named teammate")
    public void setTeamLeader(CommandSender sender, @NotNull @Argument(value = "player", suggestions = "team-members") Player target) {
    }

    @CommandMethod(value = "team sethome", requiredSender = Player.class)
    @CommandDescription("Sets the team home to your current location")
    public void setTeamHome(CommandSender sender) {
    }

    @CommandMethod(value = "team home", requiredSender = Player.class)
    @CommandDescription("Teleports you to your team's home location if one is set")
    public void teamHome(CommandSender sender) {
        final Player player = (Player) sender;
        // TODO Check for team
        final var pendingTeleport = TeleportService.getInstance().teleportHome(player);
        if (pendingTeleport == null) {
            player.sendPlainMessage("Your team does not have a home set.");
        }
    }

    @CommandMethod(value = "team help", requiredSender = Player.class)
    @CommandDescription("Lists all team commands")
    public void teamHelp(CommandSender sender) {
    }
    // team commands end

    @CommandMethod(value = "shop", requiredSender = Player.class)
    @CommandDescription("Opens the GUI shop")
    public void shop(CommandSender sender) {
    }

    @CommandMethod(value = "balance", requiredSender = Player.class)
    @CommandDescription("Gets your eco balance")
    public void balance(CommandSender sender) {
    }

    @CommandMethod(value = "pay <player> <amount>", requiredSender = Player.class)
    @CommandDescription("Pay another player")
    public void pay(CommandSender sender, @NotNull @Argument("player") Player target, @Argument("amount") int amount) {
    }

    // Admin commands
    @CommandMethod(value = "setspawn", requiredSender = Player.class)
    @CommandDescription("Sets the spawn location to your current location")
    @CommandPermission("outlast.admin")
    public void setSpawn(CommandSender sender) {
        final Player player = (Player) sender;
        final SQLLocation spawn = locationCache.getModel("spawn");
        if (spawn == null) {
            locationCache.createModel("spawn", loc -> {
                loc.setLocation(player.getLocation());
            });
        } else {
            locationCache.updateModel("spawn", loc -> {
                loc.setLocation(player.getLocation());
            });
        }
        player.sendPlainMessage("Spawn point set.");
    }

    @CommandMethod(value = "vanish", requiredSender = Player.class)
    @CommandDescription("Toggle your invisibility")
    @CommandPermission("outlast.admin")
    public void vanish(CommandSender sender) {
    }

    @CommandMethod(value = "seeinv <player>", requiredSender = Player.class)
    @CommandDescription("Displays the inventory of the named player")
    @CommandPermission("outlast.admin")
    public void seeInventory(CommandSender sender, @NotNull @Argument("player") Player target) {
    }

    @CommandMethod(value = "tpo <player>", requiredSender = Player.class)
    @CommandDescription("Teleports to the named player silently (without requesting)")
    @CommandPermission("outlast.admin")
    public void teleportOverride(CommandSender sender, @NotNull @Argument(value = "player", suggestions = "players-except-self") Player target) {
        final Player player = (Player) sender;
        if (target.equals(player)) {
            player.sendPlainMessage("You can't teleport to yourself.");
            return;
        }
        player.teleport(target);
    }

    @CommandMethod(value = "test", requiredSender = Player.class)
    @CommandDescription("Outlast test method!")
    @CommandPermission("outlast.test")
    public void test(CommandSender sender) {
        final Player player = (Player) sender;
    }
}
