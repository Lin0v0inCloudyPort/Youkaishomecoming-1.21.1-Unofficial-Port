package dev.xkmc.youkaishomecoming.compat.touhoulittlemaid;

import com.github.tartaricacid.touhoulittlemaid.entity.passive.EntityMaid;
import dev.xkmc.danmakuapi.content.item.DanmakuItem;
import dev.xkmc.danmakuapi.content.item.SpellItem;
import dev.xkmc.danmakuapi.content.spell.item.ItemSpell;
import dev.xkmc.youkaishomecoming.mixin.SpellItemAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class MaidDanmakuShootTask extends Behavior<EntityMaid> {

    private static final int DANMAKU_COOLDOWN = 20;
    private static final int MAX_SPELL_TICKS = 60;

    private int danmakuCooldown = 0;
    private boolean equipped = false;
    private ItemSpell currentSpell = null;
    private int currentSpellTicks = 0;
    private int nextSearchStart = 0;

    public MaidDanmakuShootTask() {
        super(Map.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT));
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, EntityMaid maid) {
        var target = maid.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
        if (target.isEmpty()) return false;
        if (!isDanmakuOrSpell(maid.getMainHandItem())) {
            if (!equipped) equipFromBackpack(maid);
        }
        return isDanmakuOrSpell(maid.getMainHandItem())
                && maid.getSensing().hasLineOfSight(target.get());
    }

    @Override
    protected boolean canStillUse(ServerLevel level, EntityMaid maid, long gameTime) {
        var target = maid.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
        if (target.isEmpty()) return false;
        return isDanmakuOrSpell(maid.getMainHandItem())
                && maid.getSensing().hasLineOfSight(target.get());
    }

    @Override
    protected void tick(ServerLevel level, EntityMaid maid, long gameTime) {
        var target = maid.getBrain().getMemory(MemoryModuleType.ATTACK_TARGET);
        if (target.isEmpty()) return;
        LivingEntity enemy = target.get();
        maid.getLookControl().setLookAt(enemy);

        if (currentSpell != null) {
            boolean done = currentSpell.tick(maid);
            currentSpellTicks++;
            if (!done && currentSpellTicks < MAX_SPELL_TICKS) return;
            currentSpell = null;
            currentSpellTicks = 0;
            // swap to next different spell and start immediately
            swapToNextSpell(maid);
            startSpellIfHeld(maid, enemy);
            return;
        }

        ItemStack mainHand = maid.getMainHandItem();
        if (mainHand.getItem() instanceof SpellItem) {
            startSpellIfHeld(maid, enemy);
        } else if (mainHand.getItem() instanceof DanmakuItem) {
            if (danmakuCooldown > 0) {
                danmakuCooldown--;
                return;
            }
            maid.performRangedAttack(enemy, 1.0f);
            danmakuCooldown = DANMAKU_COOLDOWN;
        }
    }

    private void startSpellIfHeld(EntityMaid maid, LivingEntity enemy) {
        ItemStack mainHand = maid.getMainHandItem();
        if (!(mainHand.getItem() instanceof SpellItem spellItem)) return;
        var supplier = ((SpellItemAccessor) (Object) spellItem).getSpell();
        currentSpell = supplier.get();
        currentSpellTicks = 0;
        currentSpell.start(maid, enemy);
    }

    private void swapToNextSpell(EntityMaid maid) {
        var inv = maid.getMaidInv();
        ItemStack handStack = maid.getMainHandItem();
        Item handItem = handStack.getItem();
        int slots = inv.getSlots();

        // round-robin: search from nextSearchStart, wrapping around
        int bestSlot = -1;
        for (int i = 0; i < slots; i++) {
            int idx = (nextSearchStart + i) % slots;
            ItemStack stack = inv.getStackInSlot(idx);
            if (stack.isEmpty()) continue;
            if (!(stack.getItem() instanceof SpellItem)) continue;
            if (stack.getItem() == handItem) continue;
            bestSlot = idx;
            break;
        }
        if (bestSlot == -1) return;
        // simple 1:1 swap
        ItemStack newSpell = inv.getStackInSlot(bestSlot);
        inv.setStackInSlot(bestSlot, handStack);
        maid.setItemInHand(InteractionHand.MAIN_HAND, newSpell);
        nextSearchStart = (bestSlot + 1) % slots;
    }

    @Override
    protected void start(ServerLevel level, EntityMaid maid, long gameTime) {
        danmakuCooldown = 0;
        equipped = true;
        currentSpell = null;
        currentSpellTicks = 0;
    }

    @Override
    protected void stop(ServerLevel level, EntityMaid maid, long gameTime) {
        equipped = false;
        currentSpell = null;
        currentSpellTicks = 0;
    }

    private static boolean isDanmakuOrSpell(ItemStack stack) {
        return stack.getItem() instanceof DanmakuItem || stack.getItem() instanceof SpellItem;
    }

    static void equipFromBackpack(EntityMaid maid) {
        var inv = maid.getMaidInv();
        int spellSlot = -1;
        int danmakuSlot = -1;
        for (int i = 0; i < inv.getSlots(); i++) {
            ItemStack stack = inv.getStackInSlot(i);
            if (stack.getItem() instanceof SpellItem && spellSlot == -1) {
                spellSlot = i;
                break;
            }
            if (stack.getItem() instanceof DanmakuItem && danmakuSlot == -1) {
                danmakuSlot = i;
            }
        }
        int slot = spellSlot != -1 ? spellSlot : danmakuSlot;
        if (slot == -1) return;
        ItemStack oldHand = maid.getMainHandItem();
        maid.setItemInHand(InteractionHand.MAIN_HAND, inv.getStackInSlot(slot));
        inv.setStackInSlot(slot, oldHand);
    }

}
