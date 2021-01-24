package be4rjp.blockstudio.api;

import be4rjp.blockstudio.nms.NMSUtil;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.EulerAngle;

import java.lang.reflect.InvocationTargetException;

public class BSArmorStand {
    private final BSCube bsCube;
    private Location location;
    private ArmorStand armorStand;
    private Object entityArmorStand;
    
    public BSArmorStand(BSCube bsCube, Location location, boolean useBukkitAPI){
        this.bsCube = bsCube;
        this.location = location;
        if(useBukkitAPI){
            this.armorStand = location.getWorld().spawn(location, ArmorStand.class, armorStand -> {
                armorStand.setVisible(false);
                armorStand.setGravity(false);
                armorStand.setMarker(true);
            });
        }else{
            try {
                this.entityArmorStand = NMSUtil.createEntityArmorStand(bsCube.getLocation().getWorld());
                NMSUtil.setEntityPositionRotation(this.entityArmorStand, location.getX(), location.getY(), location.getZ(), 0F, 0F);
                
                for(Player player : bsCube.getBsObject().getPlayers()) {
                    if (player == null) continue;
                    NMSUtil.sendSpawnEntityLivingPacket(player, this.entityArmorStand);
                    NMSUtil.sendEntityMetadataPacket(player, this.entityArmorStand);
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }
    
    public void setHelmet(ItemStack itemStack){
        if(this.armorStand != null){
            this.armorStand.setHelmet(itemStack);
        }else{
            try {
                for (Player player : bsCube.getBsObject().getPlayers()) {
                    if (player == null) continue;
                    sendHelmetEquipmentPacket(player, itemStack);
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }
    
    public void sendHelmetEquipmentPacket(Player player, ItemStack itemStack){
        try{
            NMSUtil.sendEntityEquipmentPacket(player, this.entityArmorStand, itemStack);
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }
    
    public void sendSpawnPacket(Player player){
        if(this.entityArmorStand != null) {
            try {
                NMSUtil.sendSpawnEntityLivingPacket(player, this.entityArmorStand);
                NMSUtil.sendEntityMetadataPacket(player, this.entityArmorStand);
            } catch (Exception e) {
                //e.printStackTrace();
            }
            sendHelmetEquipmentPacket(player, bsCube.getHeadItemStack());
        }
    }
    
    public void sendDestroyPacket(Player player){
        if(this.entityArmorStand != null) {
            try{
                NMSUtil.sendEntityDestroyPacket(player, this.entityArmorStand);
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }
    
    public void teleport(Location location){
        if(this.armorStand != null){
            this.armorStand.teleport(location);
        }else{
            try {
                NMSUtil.setEntityPositionRotation(this.entityArmorStand, location.getX(), location.getY(), location.getZ(), 0F, 0F);
                for(Player player : bsCube.getBsObject().getPlayers()){
                    if(player == null) continue;
                    NMSUtil.sendEntityTeleportPacket(player, this.entityArmorStand);
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }
    
    public void setHeadPose(EulerAngle eulerAngle){
        if(this.armorStand != null){
            this.armorStand.setHeadPose(eulerAngle);
        }else{
            try{
                NMSUtil.setArmorStandHeadRotation(this.entityArmorStand, (float)Math.toDegrees(eulerAngle.getX()), (float)Math.toDegrees(eulerAngle.getY()), (float)Math.toDegrees(eulerAngle.getZ()));
                for(Player player : bsCube.getBsObject().getPlayers()) {
                    if (player == null) continue;
                    NMSUtil.sendEntityMetadataPacket(player, this.entityArmorStand);
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }
    
    public void remove(){
        if(this.armorStand != null){
            this.armorStand.remove();
        }else{
            try {
                for (Player player : bsCube.getBsObject().getPlayers()) {
                    if (player == null) continue;
                    sendDestroyPacket(player);
                }
            } catch (Exception e) {
                //e.printStackTrace();
            }
        }
    }
}
