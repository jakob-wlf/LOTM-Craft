package de.jakob.lotm.pathways.impl.tyrant.impl;

import de.jakob.lotm.pathways.*;
import de.jakob.lotm.pathways.abilities.Ability;
import de.jakob.lotm.pathways.abilities.AbilityType;
import de.jakob.lotm.pathways.beyonder.Beyonder;
import de.jakob.lotm.pathways.beyonder.BeyonderPlayer;
import de.jakob.lotm.util.minecraft.ParticleSpawner;
import lombok.NoArgsConstructor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

@NoArgsConstructor
public class SirenSong extends Ability {
    private final HashMap<Beyonder, Integer> selectedAbilities = new HashMap<>();
    private final String[] abilities = new String[] {"Dazing Song", "Melody of Death", "Battle Hymn", "Thunderous Crescendo"};
    private final int[] entityAbilities = new int[] {0, 2};
    private final HashMap<Integer, Integer> spiritualityCost = new HashMap<>(Map.of(
            0, 10,
            1, 10,
            2, 10,
            3, 10
    ));

    private final Map<Integer, PotionEffect[]> effectsForSong = Map.of(
            0, new PotionEffect[] {
                PotionEffectType.BLINDNESS.createEffect(80, 1),
                PotionEffectType.SLOWNESS.createEffect(80, 1),
                PotionEffectType.NAUSEA.createEffect(200, 1)
            },
            1, new PotionEffect[] {
                    PotionEffectType.WEAKNESS.createEffect(80, 1),
                    PotionEffectType.MINING_FATIGUE.createEffect(80, 1),
            },
            2, new PotionEffect[] {
                    PotionEffectType.STRENGTH.createEffect(80, 1),
                    PotionEffectType.REGENERATION.createEffect(80, 1),
                    PotionEffectType.RESISTANCE.createEffect(80, 1),
            },
            3, new PotionEffect[] {
                    PotionEffectType.GLOWING.createEffect(80, 1)
            }
    );


    private final Random random = new Random();

    //TODO: Instead of not allowing multiple songs, cancel the previous song and start the new one
    public SirenSong(Pathway pathway, int sequence, AbilityType abilityType, String name, Material material, String description, String id) {
        super(pathway, sequence, abilityType, name, material,description, id);
    }

    @Override
    public void useAbility(Beyonder beyonder) {
        if(!selectedAbilities.containsKey(beyonder) && beyonder instanceof BeyonderPlayer)
            selectedAbilities.put(beyonder, 0);

        int selectedAbility = beyonder instanceof BeyonderPlayer ? selectedAbilities.get(beyonder) : entityAbilities[random.nextInt(entityAbilities.length)];

        if(!(beyonder instanceof BeyonderPlayer) && selectedAbility == 0)
            selectedAbility = 1;

        if(beyonder.removeSpirituality(spiritualityCost.get(selectedAbility)))
            castAbility(beyonder, selectedAbility);
    }

    @Override
    public void rightClick(Beyonder beyonder) {
        if(!selectedAbilities.containsKey(beyonder)) {
            selectedAbilities.put(beyonder, 0);
            return;
        }

        selectedAbilities.replace(beyonder, selectedAbilities.get(beyonder) + 1);
        if(selectedAbilities.get(beyonder) > abilities.length - 1)
            selectedAbilities.replace(beyonder, 0);
    }

    private void castAbility(Beyonder beyonder, int ability) {
        switch (ability) {
            case 0 -> dazingSong(beyonder);
            case 1 -> melodyOfDeath(beyonder);
            case 2 -> battleHymn(beyonder);
            case 3 -> thunderousCrescendo(beyonder);
        }
    }

    private void thunderousCrescendo(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        Location location = entity.getLocation();
        World world = entity.getWorld();

        List<LivingEntity> affectedEntities = getNearbyLivingEntities(entity, 25, location, world);

        for(LivingEntity target : affectedEntities) {
            Vector direction = target.getLocation().toVector().subtract(location.toVector()).setY(.75).normalize();
            target.setVelocity(direction.multiply(4));
            target.damage(4 * beyonder.getCurrentMultiplier(), entity);
            if(target instanceof Player player)
                player.playSound(player, Sound.ENTITY_RAVAGER_ROAR, 1, 1);
        }

        if(entity instanceof Player player)
            player.playSound(player, Sound.ENTITY_RAVAGER_ROAR, 1, 1);

        ParticleSpawner.displayParticles(world, Particle.CLOUD, location, 50, 0, 0, 0, 0.2, 128);
    }

    private void dazingSong(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        Location location = entity.getLocation();
        World world = entity.getWorld();

        if(entity.getScoreboardTags().contains("siren_song"))
            return;

        entity.getScoreboardTags().add("siren_song");

        List<LivingEntity> affectedEntities = getNearbyLivingEntities(entity, 25, location, world).stream().filter(e -> {
            for (String s : e.getScoreboardTags()) {
                if (s.startsWith("siren_song"))
                    return false;
            }
            return true;
        }).toList();


        for(LivingEntity target : affectedEntities) {
            target.getScoreboardTags().add("siren_song_dazed");
            if(target instanceof Player player)
                player.playSound(player, Sound.MUSIC_DISC_CAT, 1, 1);
        }

        if(entity instanceof Player player)
            player.playSound(player, Sound.MUSIC_DISC_CAT, 1, 1);

        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {
                if(counter > 20 * 35) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        affectedEntities.forEach(e -> {
                            e.getScoreboardTags().remove("siren_song_dazed");
                            if(e instanceof Player player)
                                player.stopSound(Sound.MUSIC_DISC_CAT);
                        });

                        entity.getScoreboardTags().remove("siren_song");
                    }, 20 * 4);

                    if(entity instanceof Player player)
                        player.stopSound(Sound.MUSIC_DISC_CAT);
                    cancel();
                    return;
                }

                for(LivingEntity target : affectedEntities) {
                    if(!target.isValid())
                        continue;
                    target.addPotionEffects(List.of(effectsForSong.get(0)));
                    ParticleSpawner.displayParticles(world, Particle.WITCH, target.getEyeLocation(), 20, .5, .5, .5, 0, 128);
                    ParticleSpawner.displayParticles(world, Particle.SNEEZE, target.getEyeLocation(), 5, .5, .5, .5, 0, 128);
                }

                ParticleSpawner.displayParticles(world, Particle.NOTE, location, 20, 10, 10, 10, 0, 128);

                counter+=10;
            }
        }.runTaskTimer(plugin, 0, 10);
    }

    private void melodyOfDeath(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        Location location = entity.getLocation();
        World world = entity.getWorld();

        if(entity.getScoreboardTags().contains("siren_song"))
            return;

        entity.getScoreboardTags().add("siren_song");

        List<LivingEntity> affectedEntities = getNearbyLivingEntities(entity, 25, location, world).stream().filter(e -> {
            for (String s : e.getScoreboardTags()) {
                if (s.startsWith("siren_song"))
                    return false;
            }
            return true;
        }).toList();

        for(LivingEntity target : affectedEntities) {
            target.getScoreboardTags().add("siren_song_death");
            if(target instanceof Player player)
                player.playSound(player, Sound.MUSIC_DISC_13, 1, 1);
        }

        if(entity instanceof Player player)
            player.playSound(player, Sound.MUSIC_DISC_13, 1, 1);

        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {
                if(counter > 20 * 25) {
                    affectedEntities.forEach(e -> {
                        e.getScoreboardTags().remove("siren_song_death");
                        if(e instanceof Player player)
                            player.stopSound(Sound.MUSIC_DISC_13);
                    });

                    entity.getScoreboardTags().remove("siren_song");

                    if(entity instanceof Player player)
                        player.stopSound(Sound.MUSIC_DISC_13);
                    cancel();
                    return;
                }

                for(LivingEntity target : affectedEntities) {
                    if(!target.isValid())
                        continue;

                    target.addPotionEffects(List.of(effectsForSong.get(1)));
                    if(counter % 30 == 0)
                        target.damage(3 * beyonder.getCurrentMultiplier(), entity);
                    ParticleSpawner.displayParticles(world, Particle.SMOKE, target.getEyeLocation(), 20, .5, .5, .5, 0, 128);
                    ParticleSpawner.displayParticles(world, Particle.DAMAGE_INDICATOR, target.getEyeLocation(), 5, .5, .5, .5, 0, 128);
                }

                ParticleSpawner.displayParticles(world, Particle.NOTE, location, 20, 10, 10, 10, 0, 128);

                counter+=10;
            }
        }.runTaskTimer(plugin, 0, 10);
    }

    private void battleHymn(Beyonder beyonder) {
        LivingEntity entity = beyonder.getEntity();
        Location location = entity.getLocation();
        World world = entity.getWorld();

        if(entity.getScoreboardTags().contains("siren_song_battle"))
            return;

        entity.getScoreboardTags().add("siren_song_battle");

        if(entity instanceof Player player) {
            player.playSound(player, Sound.MUSIC_DISC_BLOCKS, 1, 1);
        }

        new BukkitRunnable() {

            int counter = 0;

            @Override
            public void run() {
                if(counter > 20 * 35) {
                    entity.getScoreboardTags().remove("siren_song_battle");

                    if(entity instanceof Player player)
                        player.stopSound(Sound.MUSIC_DISC_BLOCKS);
                    cancel();
                    return;
                }

                entity.addPotionEffects(List.of(effectsForSong.get(2)));

                ParticleSpawner.displayParticles(world, Particle.NOTE, location, 20, 10, 10, 10, 0, 128);

                counter+=10;
            }
        }.runTaskTimer(plugin, 0, 10);
    }

    @Override
    public void onHold(Beyonder beyonder, Player player) {
        if(!selectedAbilities.containsKey(beyonder))
            selectedAbilities.put(beyonder, 0);


        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§9" + abilities[selectedAbilities.get(beyonder)]));
    }
}
