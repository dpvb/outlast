package dev.dpvb.outlast.internal;

import cloud.commandframework.annotations.*;
import cloud.commandframework.annotations.suggestions.Suggestions;
import cloud.commandframework.context.CommandContext;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

class Commands {

    @Suggestions("team-members")
    public List<String> teamMembers(CommandContext<CommandSender> sender, String input) {
        return List.of("TODO"); // TODO impl
    }

    // Player commands
    @CommandMethod(value = "tp <player>", requiredSender = Player.class)
    @CommandDescription("Sends a teleport request to the named player")
    public void teleport(CommandSender sender, @NotNull @Argument("player") Player target) {
    }

    @CommandMethod(value = "tpaccept", requiredSender = Player.class)
    @CommandDescription("Accepts any teleport request sent to you")
    public void acceptTeleports(CommandSender sender) {
    }

    @CommandMethod(value = "spawn", requiredSender = Player.class)
    @CommandDescription("Teleports you to the spawn point")
    public void spawn(CommandSender sender) {
    }

    @CommandMethod(value = "report <player>", requiredSender = Player.class)
    @CommandDescription("Reports the named player")
    public void report(CommandSender sender, @NotNull @Argument("player") Player target) {
    }

    @CommandMethod(value = "team create <name>", requiredSender = Player.class)
    @CommandDescription("Creates a team with a unique name")
    public void createTeam(CommandSender sender, @NotNull @Argument("name") @Regex(".{1,30}") String name) {
    }

    @CommandMethod(value = "team join <name>", requiredSender = Player.class)
    @CommandDescription("Joins a team with the given name")
    public void joinTeam(CommandSender sender, @NotNull @Argument("name") @Regex(".{1,30}") String name) {
    }

    @CommandMethod(value = "team info [name]", requiredSender = Player.class)
    @CommandDescription("Displays information about your team or the given team name")
    public void getTeamInfo(CommandSender sender, @Nullable @Argument("name") @Regex(".{1,30}") String name) {
    }

    @CommandMethod(value = "team userinfo <player>", requiredSender = Player.class)
    @CommandDescription("Gets the team info of the named player")
    public void getUserInfo(CommandSender sender, @NotNull @Argument("player") Player target) {
    }

    @CommandMethod(value = "team leave", requiredSender = Player.class)
    @CommandDescription("Leave your team")
    public void createTeam(CommandSender sender) {
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
    }

    @CommandMethod(value = "team help", requiredSender = Player.class)
    @CommandDescription("Lists all team commands")
    public void teamHelp(CommandSender sender) {
    }

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
    public void teleportOverride(CommandSender sender, @NotNull @Argument("player") Player target) {
    }
}