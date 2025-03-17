package de.jakob.lotm.entity;


import de.jakob.lotm.LOTM;
import de.jakob.lotm.pathways.beyonder.BeyonderEntity;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.EntityType;

public class FireSalamander {
    public static void spawn(Location location) {
        World world = location.getWorld();
        if(world == null)
            return;

        Axolotl salamander = (Axolotl) world.spawnEntity(location, EntityType.AXOLOTL);
        salamander.setVariant(Axolotl.Variant.LUCY);
        salamander.setCustomName("§4Fire Salamander");
        salamander.setCustomNameVisible(true);
        salamander.getAttribute(Attribute.MAX_HEALTH).setBaseValue(20);
        salamander.getAttribute(Attribute.SCALE).setBaseValue(2);
        salamander.getScoreboardTags().add("salamander");
        salamander.getScoreboardTags().add("no_spawn");

        BeyonderEntity beyonderEntity = (BeyonderEntity) LOTM.getInstance().createBeyonder(salamander.getUniqueId(), LOTM.getInstance().getPathway("red_priest"), 7, false, false, true);
        if(beyonderEntity == null)
            return;
        beyonderEntity.setDropsCharacteristic(false);
        beyonderEntity.addAbility(LOTM.getInstance().getPathway("red_priest").getAbility("pyrokinesis"));
        beyonderEntity.addAbility(LOTM.getInstance().getPathway("red_priest").getAbility("fire_resistance"));
        beyonderEntity.addMultiplierModifier(.3);
    }
}
