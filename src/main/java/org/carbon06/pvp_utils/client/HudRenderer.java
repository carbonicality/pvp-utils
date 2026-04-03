package org.carbon06.pvp_utils.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.client.event.RenderTooltipEvent;

@Mod.EventBusSubscriber(value=Dist.CLIENT,modid="pvp_utils")
public class HudRenderer {
    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        Minecraft mc=Minecraft.getInstance();
        if (mc.player==null) return;
        GuiGraphics guiGraphics=event.getGuiGraphics();
        int screenWidth=mc.getWindow().getGuiScaledWidth();
        int screenHeight=mc.getWindow().getGuiScaledHeight();
        int totemCount=countTotems(mc.player.getInventory());
        if (totemCount>0) {
            int hotbarWidth=182;
            int hotbarX=(screenWidth-hotbarWidth)/2;
            int hotbarY=screenHeight-22;
            int x=hotbarX-30;
            int y=hotbarY+3;
            ItemStack totemStack=new ItemStack(Items.TOTEM_OF_UNDYING);
            guiGraphics.renderItem(totemStack,x,y);
            String countText=String.valueOf(totemCount);
            int textX = x+18;
            int textY = y+4;
            guiGraphics.drawString(mc.font,countText,textX,textY,0xFFFFFF);
        }
        renderArmourHUD(guiGraphics,mc,screenWidth,screenHeight);
    }

    @SubscribeEvent
    public static void onRenderCrosshairPre(RenderGuiOverlayEvent.Pre event) {
        Minecraft mc=Minecraft.getInstance();
        if (event.getOverlay()==net.minecraftforge.client.gui.overlay.VanillaGuiOverlay.CROSSHAIR.type()) {
            if (mc.hitResult!=null&&mc.hitResult.getType()==HitResult.Type.ENTITY) {
                event.setCanceled(true);
                GuiGraphics guiGraphics=event.getGuiGraphics();
                int cx=mc.getWindow().getGuiScaledWidth()/2;
                int cy=mc.getWindow().getGuiScaledHeight()/2;
                guiGraphics.hLine(cx-4,cx+4,cy-1,0xFFFF4444);
                guiGraphics.vLine(cx,cy-6,cy+4,0xFFFF4444);
            }
        }
    }

    @SubscribeEvent
    public static void onRenderTooltip(RenderTooltipEvent.Pre event) {
        Minecraft mc=Minecraft.getInstance();
        if (mc.player==null) return;
        ItemStack stack=event.getItemStack();
        if (!(stack.getItem() instanceof net.minecraft.world.item.BlockItem blockItem)) return;
        if (!(blockItem.getBlock() instanceof net.minecraft.world.level.block.ShulkerBoxBlock)) return;
        net.minecraft.nbt.CompoundTag tag =stack.getTagElement("BlockEntityTag");
        if (tag==null||!tag.contains("Items")) return;
        net.minecraft.nbt.ListTag items=tag.getList("Items",10);
        if (items.isEmpty()) return;
        GuiGraphics guiGraphics=event.getGraphics();
        int cols=9;
        int rows=(int) Math.ceil(items.size()/(float)cols);
        int panelWidth=cols*18+8;
        int panelHeight=rows*18+8;
        int startX=event.getX();
        int startY=event.getY()-panelHeight-15;
        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(0,0,400);
        //bg
        guiGraphics.fill(startX,startY,startX+panelWidth,startY+panelHeight,0xFF100010);
        //border
        guiGraphics.fill(startX,startY,startX+panelWidth,startY+1,0xFF5A0091);
        guiGraphics.fill(startX,startY+panelHeight-1,startX+panelWidth,startY+panelHeight,0xFF5A0091);
        guiGraphics.fill(startX,startY,startX+1,startY+panelHeight,0xFF5A0091);
        guiGraphics.fill(startX+panelWidth-1,startY,startX+panelWidth,startY+panelHeight,0xFF5A0091);
        for (int i=0;i<items.size();i++) {
            net.minecraft.nbt.CompoundTag itemTag=items.getCompound(i);
            ItemStack contents =ItemStack.of(itemTag);
            int col =i%cols;
            int row =i/cols;
            int x = startX+4+col*18;
            int y = startY+4+row*18;
            guiGraphics.renderItem(contents,x,y);
            guiGraphics.renderItemDecorations(mc.font,contents,x,y);
        }
        guiGraphics.pose().popPose();
    }

    private static int countTotems(Inventory inventory) {
        int count=0;
        for (int i=0;i<inventory.getContainerSize();i++) {
            ItemStack stack=inventory.getItem(i);
            if (stack.is(Items.TOTEM_OF_UNDYING)) {
                count+=stack.getCount();
            }
        }
        ItemStack offhand=inventory.offhand.get(0);
        if (offhand.is(Items.TOTEM_OF_UNDYING)) {
            count+=offhand.getCount();
        }
        return count;
    }

    private static void renderArmourHUD(GuiGraphics guiGraphics, Minecraft mc,int screenWidth, int screenHeight) {
        Inventory inventory=mc.player.getInventory();
        int hotbarWidth=182;
        int hotbarX=(screenWidth-hotbarWidth)/2;
        int hotbarY=screenHeight-22;
        int startX=hotbarX+hotbarWidth+5;
        int y=hotbarY+3;
        for (int i=0;i<4;i++) {
            ItemStack armourPiece=inventory.armor.get(i);
            if (!armourPiece.isEmpty()&&armourPiece.isDamageableItem()) {
                int x=startX+(i*25);
                int maxDurability=armourPiece.getMaxDamage();
                int currentDurability=maxDurability-armourPiece.getDamageValue();
                float ratio=currentDurability/(float)maxDurability;
                int color;
                if (ratio>0.5f) {
                    color=0x55FF55;
                } else if (ratio>0.25f) {
                    color=0xFFFF55;
                } else {
                    color=0xFF5555;
                }
                String durabilityText= String.valueOf(currentDurability);
                int textX=x+(16-mc.font.width(durabilityText))/2;
                int textY=y-10;
                guiGraphics.drawString(mc.font,durabilityText,textX,textY,color);
                guiGraphics.renderItem(armourPiece,x,y);
            }
        }
    }
}
