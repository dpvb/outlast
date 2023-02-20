package dev.dpvb.outlast.events;

import dev.dpvb.outlast.internal.OutlastPlugin;
import dev.dpvb.outlast.messages.Message;
import dev.dpvb.outlast.sql.SQLService;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class ADModifier implements Listener {

    private static final double LOW_AD = 1.0;
    private static final double HIGH_AD = OutlastPlugin.Configuration.getADBound();


    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        // handle death attack damage
        final Player victim = event.getPlayer();
        final AttributeInstance victimAD = victim.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        assert victimAD != null;
        final double currAD = victimAD.getBaseValue();
        final double newAD = Math.max(LOW_AD, currAD - 1);
        victimAD.setBaseValue(newAD);
        SQLService.getInstance().getPlayerCache().updateModel(victim.getUniqueId(), sqlPlayer -> sqlPlayer.setAttack_damage((byte) newAD));
        Message.mini("<yellow>Your attack damage is now <red>" + newAD + "<yellow>!").send(victim);

        final Player killer = victim.getKiller();
        if (killer == null) {
            return;
        }

        // Handle player kill getting AD
        final AttributeInstance killerAD = killer.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE);
        assert killerAD != null;
        final double killerCurrAD = killerAD.getBaseValue();
        final double killerNewAD = Math.min(HIGH_AD, killerCurrAD + 1);
        killerAD.setBaseValue(killerNewAD);
        SQLService.getInstance().getPlayerCache().updateModel(killer.getUniqueId(), sqlPlayer -> sqlPlayer.setAttack_damage((byte) killerNewAD));
        Message.mini("<yellow>Your attack damage is now <red>" + killerNewAD + "<yellow>!").send(killer);
    }

}
