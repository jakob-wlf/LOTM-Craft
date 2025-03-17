package de.jakob.lotm.pathways.beyonder;

import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.Pathway;
import de.jakob.lotm.util.minecraft.ParticleUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BeyonderSpirit extends BeyonderEntity{

    @Getter @Setter
    private Particle.DustOptions[] dust;

    @Getter @Setter
    private double[] dustSizes = new double[] {random.nextDouble(.4, .9), random.nextDouble(.4, .9), random.nextDouble(.4, .9)};

    @Getter @Setter
    private int dustAmount = 25;

    @Getter @Setter
    private boolean showEntity = false;
    @Getter @Setter
    private int lifespan;

    public BeyonderSpirit(UUID uuid, Pathway currentPathway, int currentSequence, boolean hostile, int lifespan) {
        super(uuid, currentPathway, currentSequence, hostile);

        this.lifespan = lifespan;

        dust = new Particle.DustOptions[random.nextBoolean() ? 1 : random.nextInt(4) + 1];
        for(int i = 0; i < dust.length; i++) {
            dust[i] = ParticleUtil.coloredDustOptions[random.nextInt(ParticleUtil.coloredDustOptions.length)];
        }

        getEntity().addScoreboardTag("spirit");
        getEntity().setSilent(true);
    }

    @Override
    protected void tick(int tick) {
        if(!getEntity().hasAI())
            return;

        searchTarget();

        getEntity().setVisibleByDefault(false);

        Player nearestPlayer = LOTM.getInstance().getNearestPlayer(getEntity().getLocation());
        if((nearestPlayer == null || nearestPlayer.getLocation().distance(getEntity().getLocation()) > 50)) {
            destroyBeyonder();
            return;
        }

        for(Player player : getEntity().getNearbyEntities(80, 60, 80).stream().filter(entity -> entity.getType() == EntityType.PLAYER).map(entity -> (Player) entity).toList()) {
            boolean maySeeSpirits = false;

            for (String tag : player.getScoreboardTags()) {
                if(tag.startsWith("see_spirits")) {
                    maySeeSpirits = true;
                    break;
                }
            }

            if(!maySeeSpirits) {
                player.hideEntity(LOTM.getInstance(), getEntity());
                continue;
            }
            player.spawnParticle(Particle.DUST, getEntity().getLocation(), dustAmount, dustSizes[0], dustSizes[1], dustSizes[2], 0, dust[random.nextInt(dust.length)]);
            if(player == getMasterEntity()) {
                player.spawnParticle(Particle.END_ROD, getEntity().getLocation(), random.nextInt(3) == 0 ? 1 : 0, dustSizes[0], dustSizes[1], dustSizes[2], 0);
            }
            if(showEntity)
                player.showEntity(LOTM.getInstance(), getEntity());
        }

        if(tick % 20 == 0)
            getEntity().getWorld().loadChunk(getEntity().getLocation().getChunk());

        move();

        if(currentTarget != null && currentTarget.getWorld() == getEntity().getWorld()) {
            if(tick % 30 == 0) {
                useAbility();
            }
        }

        double lifespanMultiplier = getMasterEntity() == null ? 1 : 8;

        if(tick >= lifespan * lifespanMultiplier) {
            destroyBeyonder();
            return;
        }
    }
}
