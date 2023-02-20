package dev.dpvb.outlast.internal;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.SimpleCommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import dev.dpvb.outlast.messages.Message;
import dev.dpvb.outlast.messages.Messages;
import dev.dpvb.outlast.sql.SQLService;
import dev.dpvb.outlast.sql.cache.LocationCache;
import dev.dpvb.outlast.sql.cache.PlayerCache;
import dev.dpvb.outlast.sql.cache.TeamCache;
import dev.dpvb.outlast.sql.models.SQLLocation;
import dev.dpvb.outlast.sql.models.SQLPlayer;
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
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
        if (sender.getSender() instanceof final Player player) {
            final TeamService teamService = TeamService.getInstance();
            String teamName = teamService.getTeam(player.getUniqueId());
            if (teamName == null) {
                return List.of("");
            }
            return teamService.getTeamMembers(teamName).stream()
                    .map(Bukkit::getPlayer)
                    .filter(p -> p != null && !player.equals(p))
                    .map(Player::getName)
                    .collect(Collectors.toList());
        }
        return List.of("");
    }

    @Suggestions("players-except-self")
    public List<String> playersExceptSelf(CommandContext<CommandSender> sender, String input) {
        final String playerName = sender.getSender().getName();
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(name -> !name.equals(playerName))
                .toList();
    }


    // Player commands
    // TP commands
    @CommandMethod(value = "tp <player>", requiredSender = Player.class)
    @CommandDescription("Sends a teleport request to the named player")
    public void teleport(CommandSender sender, @NotNull @Argument(value = "player", suggestions = "players-except-self") Player target) {
        final Player player = (Player) sender;
        if (target.equals(player)) {
            Message.mini("<red>You can't send a teleport request to yourself.").send(player);
            return;
        }
        final var teleportRequest = TeleportService.getInstance().requestTeleport(player, target);
        Message.mini("<yellow>Your teleport request to <#939EFE>" + target.getName() + " <yellow>will expire in " + TeleportRequest.TIMEOUT + " seconds.").send(player);
        Message.mini("<yellow>You received a teleport request from <#939EFE>" + player.getName() + "<yellow>.").send(target);
        target.sendMessage(
                Component.text("Accept with ")
                        .color(NamedTextColor.YELLOW).append(
                        Component.text("/tpaccept")
                                .color(TextColor.color(147,158,254))
                                .hoverEvent(Component.text("Accept teleport request"))
                                .clickEvent(ClickEvent.suggestCommand("/tpaccept"))
                ).append(Component.text(". The request will expire in " + TeleportRequest.TIMEOUT + " seconds.").color(NamedTextColor.YELLOW))
        ); // TODO tpdeny?
    }

    @CommandMethod(value = "tpaccept", requiredSender = Player.class)
    @CommandDescription("Accepts any teleport request sent to you")
    public void acceptTeleports(CommandSender sender) {
        final Player player = (Player) sender;
        final var teleportRequest = TeleportService.getInstance().getRequest(player);
        // Check for Teleport Request!
        if (teleportRequest == null) {
            Message.mini("<red>You have no pending teleport requests.").send(player);
            return;
        }

        // Attempt to Accept Teleport Request
        if (teleportRequest.accept()) {
            Message.mini("<yellow>Your teleport request was accepted.").send(teleportRequest.getSender());
            Message.mini("<yellow>You accepted <#939EFE>" + teleportRequest.getSender().getName() + "<yellow>'s teleport request!").send(player);
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
    @CommandMethod(value = "team", requiredSender = Player.class)
    @CommandDescription("List all team commands here.")
    public void team(CommandSender sender) {
        final Player player = (Player) sender;
        Message.mini("<bold><gray>>> <#939EFE>TEAM COMMANDS <gray><<").send(player);
        Message.mini("<#939EFE>- /team create <name> <yellow>(Create a team)").send(player);
        Message.mini("<#939EFE>- /team invite <username> <yellow>(Invite player to your team)").send(player);
        Message.mini("<#939EFE>- /team join <yellow>(Accept incoming team invite)").send(player);
        Message.mini("<#939EFE>- /team leave <yellow>(Leave the team you are in)").send(player);
        Message.mini("<#939EFE>- /team sethome <yellow>(Set the home location of your team)").send(player);
        Message.mini("<#939EFE>- /team home <yellow>(Teleport to your team home)").send(player);
        Message.mini("<#939EFE>- /team setleader <username> <yellow>(Give leader to a teammate)").send(player);
        Message.mini("<#939EFE>- /team info [name] <yellow>(Learn more about a team)").send(player);
    }

    @CommandMethod(value = "team create <name>", requiredSender = Player.class)
    @CommandDescription("Creates a team with a unique name")
    public void createTeam(CommandSender sender, @NotNull @Argument("name") @Regex(".{1,30}") String name) {
        final Player player = (Player) sender;
        // create the team
        try {
            TeamService.getInstance().createTeam(name, player.getUniqueId());
            Message.mini("<yellow>Successfully created team <#939EFE>" + name + "<yellow>!").send(player);
        } catch (TeamError.Exists e) {
            Message.mini("<red>" + e.getMessage()).send(player);
        } catch (TeamError.PlayerAlreadyTeamed e) {
            Message.mini("<red>You are already on a team.").send(player);
        }
    }

    @CommandMethod(value = "team invite <player>", requiredSender = Player.class)
    @CommandDescription("Invite a Player to the Team")
    public void invitePlayer(CommandSender sender, @NotNull @Argument(value = "player", suggestions = "players-except-self") Player target) {
        final Player player = (Player) sender;
        final TeamService teamService = TeamService.getInstance();
        // check if they are not on a team.
        String team = teamService.getTeam(player.getUniqueId());
        if (team == null) {
            Message.mini("<red>You must be on a team to perform this command.").send(player);
            return;
        }

        // check if the sender is the leader.
        if (!teamService.isLeaderOfTeam(player.getUniqueId(), team)) {
            Message.mini("<red>You must be the team leader to do this.").send(player);
            return;
        }

        // check if they are attempting to invite themselves.
        if (player.equals(target)) {
            Message.mini("<red>You can't invite yourself.").send(player);
            return;
        }

        // check if the player you want to invite is already on a team
        if (teamService.getTeam(target.getUniqueId()) != null) {
            Message.mini("<red>That player is already on a team.").send(player);
            return;
        }

        // check if team is full
        if (teamService.isTeamFull(team)) {
            Message.mini("<red>The team is full, so you can not add anymore players.").send(player);
            return;
        }

        // invite player
        final TeamInvite teamInvite = teamService.invitePlayer(target, team);
        Message.mini("<yellow>Your invite to <#939EFE>" + target.getName() + " <yellow>will expire in " + TeamInvite.TIMEOUT + " seconds.").send(player);
        Message.mini("<#939EFE>" + player.getName() + " <yellow>sent you an invite to join <#939EFE>" + team + "<yellow>!").send(target);
        Message.mini("<yellow>Use <#939EFE>/team join <yellow>to join the team.").send(target);
    }

    @CommandMethod(value = "team join", requiredSender = Player.class)
    @CommandDescription("Accept any pending requests to join a Team.")
    public void joinTeam(CommandSender sender) {
        final Player player = (Player) sender;
        final TeamService teamService = TeamService.getInstance();

        // check for pending requests and join if possible
        final TeamInvite teamInvite = teamService.getInvite(player);
        if (teamInvite == null) {
            Message.mini("<red>You do not have any pending team requests.").send(player);
            return;
        }

        // attempt to accept and join
        if (teamInvite.getState() == TeamInvite.State.SENT) {
            try {
                teamService.joinTeam(teamInvite.getTeamName(), player.getUniqueId());
                teamInvite.accept(); // move to accepted state after joining
                Message.mini("<#939EFE>" + player.getName() + " <yellow>joined <#939EFE>" + teamInvite.getTeamName() + "<yellow>!").sendTeam(teamInvite.getTeamName());
            } catch (TeamError.DoesNotExist | TeamError.Full e) {
                Message.mini("<red>" + e.getMessage()).send(player);
            } catch (TeamError.PlayerAlreadyTeamed ignored) {
                Message.mini("<red>You are already in a team.").send(player);
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
                Message.mini("<red>You are not in a team!").send(player);
                return;
            }
        } else {
            SQLTeam team = teamCache.getModel(name);
            if (team == null) {
                Message.mini("<red>That team does not exist!").send(player);
                return;
            }
        }

        final String teamName = name;
        OfflinePlayer teamLeader = Bukkit.getOfflinePlayer(teamCache.getModel(teamName).getLeader());
        List<OfflinePlayer> otherMembers = playerCache.getModels().stream()
                .filter(sqlPlayer -> teamName.equals(sqlPlayer.getTeam_name()))
                .map(sqlPlayer -> Bukkit.getOfflinePlayer(sqlPlayer.getPlayer_uuid()))
                .toList();

        // Display It!
        Message.mini("<#4B5CE9><bold>Team <white>" + name).send(player);
        Message.mini("<#939EFE>> " + teamLeader.getName() + "<#4B5CE9><italic> (Leader)").send(player);
        for (OfflinePlayer offlinePlayer : otherMembers) {
            if (offlinePlayer.equals(teamLeader)) {
                continue;
            }
            Message.mini("<#939EFE>> " + offlinePlayer.getName() + "").send(player);
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
            final String leftTeam = TeamService.getInstance().leaveTeam(player.getUniqueId());
            if (leftTeam != null) {
                Message.mini("<#939EFE>" + player.getName() + " <yellow>left the team.").sendTeam(leftTeam);
            }
            Message.mini("<yellow>You left the team.").send(player);
        } catch (TeamError.PlayerNotTeamed ignored) {
            Message.mini("<red>You are not on a team.").send(player);
        }
    }

    @CommandMethod(value = "team setleader <player>", requiredSender = Player.class)
    @CommandDescription("Sets the team leader to the named teammate")
    public void setTeamLeader(CommandSender sender, @NotNull @Argument(value = "player", suggestions = "team-members") Player target) {
        final Player player = (Player) sender;
        final TeamService teamService = TeamService.getInstance();
        // check if player is in team.
        String team = teamService.getTeam(player.getUniqueId());
        if (team == null) {
            Message.mini("<red>You must be on a team to perform this command.").send(player);
            return;
        }

        // check if sender is the leader
        if (!teamService.isLeaderOfTeam(player.getUniqueId(), team)) {
            Message.mini("<red>You must be the team leader to do this.").send(player);
            return;
        }

        // check if they are attempting to set leader to themselves
        if (player.equals(target)) {
            Message.mini("<red>You are already the team leader.").send(player);
            return;
        }

        // check if the player you want to invite is on your team.
        String targetTeam = teamService.getTeam(target.getUniqueId());
        if (targetTeam == null || !targetTeam.equals(team)) {
            Message.mini("<red>They are not on your team.").send(player);
            return;
        }

        // set the team leader;
        teamService.setLeader(team, target);
        Message.mini("<#939EFE>" + player.getName() + " <yellow>transferred leadership to <#939EFE>" + target.getName() + "<yellow>!").sendTeam(targetTeam);
    }

    @CommandMethod(value = "team sethome", requiredSender = Player.class)
    @CommandDescription("Sets the team home to your current location")
    public void setTeamHome(CommandSender sender) {
        final Player player = (Player) sender;
        // check if player is on a team.
        final TeamService teamService = TeamService.getInstance();
        String teamName = teamService.getTeam(player.getUniqueId());
        if (teamName == null) {
            Message.mini("<red>You must be on a team to perform this command.").send(player);
            return;
        }

        // check if player is leader
        if (!teamService.isLeaderOfTeam(player.getUniqueId(), teamName)) {
            Message.mini("<red>You must be the team leader to perform this command.").send(player);
            return;
        }

        // attempt to set the team home.
        try {
            teamService.setTeamHome(teamName, player.getLocation());
            Message.mini("<yellow>Set the team home to your location.").send(player);
        } catch (TeamError.DoesNotExist e) {
            Message.mini("<red>" + e.getMessage()).send(player);
        }
    }

    @CommandMethod(value = "team home", requiredSender = Player.class)
    @CommandDescription("Teleports you to your team's home location if one is set")
    public void teamHome(CommandSender sender) {
        final Player player = (Player) sender;
        final TeamService teamService = TeamService.getInstance();
        final String teamName = teamService.getTeam(player.getUniqueId());

        // Check if player is in a team.
        if (teamName == null) {
            Message.mini("<red>You are not in a team.").send(player);
            return;
        }

        // Teleport them home baby
        final ChannelingTeleport pendingTeleport = TeleportService.getInstance().teleportHome(player, teamName);
        if (pendingTeleport == null) {
            Message.mini("<red>Your team does not have a home set.").send(player);
        }
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

    @CommandMethod(value = "stats [username]", requiredSender = Player.class)
    @CommandDescription("Get a player's statistics")
    public void stats(CommandSender sender, @Nullable @Argument("username") Player target) {
        final Player player = (Player) sender;
        if (target == null) {
            target = player;
        }

        SQLPlayer model = playerCache.getModel(target.getUniqueId());
        final short kills = model.getKills();
        final short deaths = model.getDeaths();
        final int coins = model.getCoins();
        final String firstJoinTime = new SimpleDateFormat("dd MMM yyyy HH:mm:ss z").format(model.getFirst_join_time());

        Message.mini("<bold><gray>>> <red>"+ target.getName() + "'s Stats <gray><<").send(player);
        Message.mini("<gray>Kills: <red>" + kills).send(player);
        Message.mini("<gray>Deaths: <red>" + deaths).send(player);
        Message.mini("<gray>Coins: <red>" + coins).send(player);
        Message.mini("<gray>First Join: <red>" + firstJoinTime).send(player);

    }

    @CommandMethod(value = "ad", requiredSender = Player.class)
    @CommandDescription("View your attack damage")
    public void ad(CommandSender sender) {
        final Player player = (Player) sender;
        final double ad = player.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).getBaseValue();
        Message.mini("<gray>Your attack damage is currently <red>" + ad + "<gray>!").send(player);
    }

    // Admin commands
    @CommandMethod(value = "setspawn", requiredSender = Player.class)
    @CommandDescription("Sets the spawn location to your current location")
    @CommandPermission("outlast.admin")
    public void setSpawn(CommandSender sender) {
        final Player player = (Player) sender;
        final SQLLocation spawn = locationCache.getModel("spawn");
        if (spawn == null) {
            locationCache.createModel("spawn", loc -> loc.setLocation(player.getLocation()));
        } else {
            locationCache.updateModel("spawn", loc -> loc.setLocation(player.getLocation()));
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


    @CommandMethod(value = "heal", requiredSender = Player.class)
    @CommandDescription("Heal to full health.")
    @CommandPermission("outlast.heal")
    public void heal(CommandSender sender) {
        final Player player = (Player) sender;
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setFoodLevel(20);
        Message.mini("<yellow>You were healed.").send(player);
    }

    @CommandMethod(value = "test", requiredSender = Player.class)
    @CommandDescription("Outlast test method!")
    @CommandPermission("outlast.test")
    public void test(CommandSender sender) {
        final Player player = (Player) sender;

//        var team = TeamService.getInstance().getTeam(player.getUniqueId());
//        if (team != null) {
//            team = TeamService.getInstance().getTeamMembers(team).stream()
//                    .map(Bukkit::getOfflinePlayer)
//                    .map(op -> {
//                        if (!op.isOnline()) return op.getName() + " (offline)";
//                        return "<hover:show_text:'Click to message " + op.getName() + "'>" +
//                                "<click:suggest_command:'/msg " + op.getName() + "'>" + op.getName() + "</click>";
//                    })
//                    .collect(Collectors.joining(", "));
//        } else {
//            team = "No team";
//        }
//        player.sendMessage(Message.mini("Test: <players>")
//                .resolve(Placeholder.parsed("players", team))
//        );
    }
}
