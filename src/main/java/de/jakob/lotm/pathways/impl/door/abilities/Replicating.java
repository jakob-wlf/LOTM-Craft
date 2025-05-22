package de.jakob.lotm.pathways.impl.door.abilities;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Marker;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

public class Replicating extends Ability {

    private final Set<Beyonder> onCooldown = new HashSet<>();

    public Replicating(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);

        canBeCopied = false;
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(onCooldown.contains(beyonder))
            return;

        onCooldown.add(beyonder);
        Bukkit.getScheduler().runTaskLater(plugin, () -> onCooldown.remove(beyonder), 15);

        LivingEntity entity = beyonder.getEntity();
        Location location = entity.getEyeLocation();
        World world = entity.getWorld();

        //TODO: Make BeyonderEntity useable

        Marker abilityMarker = entity.getNearbyEntities(18, 18, 18).stream().filter(e -> e.getType() == EntityType.MARKER).map(e -> (Marker) e).filter(marker -> {
            for (String s : marker.getScoreboardTags())
                if (s.startsWith("ability_cast") && !s.equals("ability_cast_" + entity.getUniqueId()))
                    return true;
            return false;
        }).min(Comparator.comparing(m -> m.getLocation().distance(entity.getLocation()))).orElse(null);

        if(abilityMarker == null) {
            ParticleSpawner.displayParticles(world, Particle.ANGRY_VILLAGER, location.subtract(0, .45, 0), 50, .2, .2, .2, 0, 120);
            ParticleSpawner.displayParticles(world, Particle.SMOKE, location.subtract(0, .45, 0), 50, .2, .2, .2, 0, 120);
            playSound(beyonder, Sound.ITEM_BOOK_PAGE_TURN, 1, 1);
            playSound(beyonder, Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }


        Ability tempAbility = LOTM.getInstance().getAbilitiesBeingUsed().get(abilityMarker.getUniqueId());
        if(tempAbility == null)
            return;

        Ability ability = tempAbility.copy(AbilityType.REPLICATED);
        if(ability == null)
            return;

        //TODO: Implement failure chance

        beyonder.getAbilities().removeIf(a -> a.getId().equalsIgnoreCase(ability.getId()) && a.getAbilityType() == AbilityType.RECORDED);

        if(beyonder.getAbilities().stream().anyMatch(a -> a.getId().equalsIgnoreCase(ability.getId()))) {
            playSound(beyonder, Sound.ITEM_BOOK_PAGE_TURN, 1, 1);
            playSound(beyonder, Sound.ENTITY_VILLAGER_NO, 1, 1);
            return;
        }

        beyonder.addAbility(ability);

        ParticleSpawner.displayParticles(world, Particle.WITCH, location.subtract(0, .45, 0), 50, .2, .2, .2, 0, 120);
        ParticleSpawner.displayParticles(world, Particle.ENCHANT, location.subtract(0, .45, 0), 50, .2, .2, .2, 0, 120);
        playSound(beyonder, Sound.ITEM_BOOK_PAGE_TURN, 1, 1);
        playSound(beyonder, Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
    }

}
