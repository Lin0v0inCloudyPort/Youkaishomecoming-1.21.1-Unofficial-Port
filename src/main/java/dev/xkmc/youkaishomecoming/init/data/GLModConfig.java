package dev.xkmc.youkaishomecoming.init.data;

import dev.xkmc.youkaishomecoming.init.GensokyoLegacy;
import dev.xkmc.l2core.util.ConfigInit;
import net.neoforged.neoforge.common.ModConfigSpec;

public class GLModConfig {

    public static class Server extends ConfigInit {

        public final ModConfigSpec.IntValue higiHealingPeriod;
        public final ModConfigSpec.DoubleValue fairyHealingFactor;

        public final ModConfigSpec.DoubleValue youkaifiedHealthBonus;
        public final ModConfigSpec.DoubleValue youkaifiedAttackBonus;
        public final ModConfigSpec.DoubleValue youkaifiedSpeedBonus;
        public final ModConfigSpec.DoubleValue fairyHealthReduction;
        public final ModConfigSpec.DoubleValue fairyAttackReduction;
        public final ModConfigSpec.DoubleValue fairySpeedBonus;
        public final ModConfigSpec.DoubleValue fairyHitboxReduction;

        public final ModConfigSpec.IntValue koishiHatCooldown;

        public final ModConfigSpec.BooleanValue curiosSupportEnabled;

        public final ModConfigSpec.BooleanValue spawnDeer;
        public final ModConfigSpec.BooleanValue spawnBoar;
        public final ModConfigSpec.BooleanValue spawnTuna;
        public final ModConfigSpec.BooleanValue spawnCrab;
        public final ModConfigSpec.BooleanValue spawnLamprey;

        public final ModConfigSpec.DoubleValue spawnDeerRate;
        public final ModConfigSpec.DoubleValue spawnBoarRate;
        public final ModConfigSpec.DoubleValue spawnTunaRate;
        public final ModConfigSpec.DoubleValue spawnLampreyRate;

        public final ModConfigSpec.IntValue frogEatCountForHat;
        public final ModConfigSpec.IntValue frogEatRaiderVillagerSightRange;
        public final ModConfigSpec.IntValue frogEatRaiderVillagerNoSightRange;
        public final ModConfigSpec.BooleanValue koishiAttackEnable;
        public final ModConfigSpec.IntValue koishiAttackCoolDown;
        public final ModConfigSpec.DoubleValue koishiAttackChance;
        public final ModConfigSpec.IntValue koishiAttackDamage;
        public final ModConfigSpec.IntValue koishiAttackBlockCount;

        public final ModConfigSpec.DoubleValue danmakuMinPHPDamage;
        public final ModConfigSpec.DoubleValue danmakuPlayerPHPDamage;
        public final ModConfigSpec.DoubleValue danmakuHealOnHitTarget;
        public final ModConfigSpec.BooleanValue enableExtraCoolDown;

        public final ModConfigSpec.IntValue danmakuMaxResource;
        public final ModConfigSpec.IntValue danmakuMaxPower;
        public final ModConfigSpec.DoubleValue danmakuPowerBonus;
        public final ModConfigSpec.DoubleValue grazeEffectiveness;
        public final ModConfigSpec.IntValue missInvulTime;
        public final ModConfigSpec.IntValue bombInvulTime;
        public final ModConfigSpec.DoubleValue maxPowerLossOnMiss;
        public final ModConfigSpec.IntValue initialResource;
        public final ModConfigSpec.IntValue initialPower;

        public final ModConfigSpec.BooleanValue fairyAttackYoukaified;
        public final ModConfigSpec.DoubleValue fairySummonReinforcement;

        public final ModConfigSpec.BooleanValue exRumiaConversion;
        public final ModConfigSpec.BooleanValue reimuSummonFlesh;
        public final ModConfigSpec.BooleanValue reimuSummonKill;
        public final ModConfigSpec.BooleanValue reimuSummonMoney;
        public final ModConfigSpec.IntValue reimuSummonCost;
        public final ModConfigSpec.BooleanValue reimuHairbandFlightEnable;
        public final ModConfigSpec.BooleanValue rumiaHairbandDrop;

        Server(Builder builder) {
            markL2();
            builder.push("food_effect", "Potion Effects");
            {
                higiHealingPeriod = builder.text("Higi Healing Interval")
                        .defineInRange("higiHealingPeriod", 60, 0, 10000);
                fairyHealingFactor = builder.text("Fairy Healing Factor")
                        .defineInRange("fairyHealingFactor", 2d, 1, 100);
            }
            builder.pop();

            builder.push("effect_modifiers", "Effect Attribute Modifiers");
            builder.comment("Changes to these values require a game restart to take effect");
            {
                youkaifiedHealthBonus = builder.text("Max health bonus from Youkaified effect (flat add)")
                        .defineInRange("youkaifiedHealthBonus", 20.0, 0, 1000);
                youkaifiedAttackBonus = builder.text("Attack damage bonus from Youkaified effect (multiplier on base, e.g. 0.5 = +50%)")
                        .defineInRange("youkaifiedAttackBonus", 0.5, 0, 100);
                youkaifiedSpeedBonus = builder.text("Movement speed bonus from Youkaified effect (multiplier on total)")
                        .defineInRange("youkaifiedSpeedBonus", 0.3, 0, 100);
                fairyHealthReduction = builder.text("Max health change from Fairy effect (multiplier on total, negative = reduction)")
                        .defineInRange("fairyHealthReduction", -0.5, -1, 100);
                fairyAttackReduction = builder.text("Attack damage change from Fairy effect (multiplier on total, negative = reduction)")
                        .defineInRange("fairyAttackReduction", -0.5, -1, 100);
                fairySpeedBonus = builder.text("Movement speed change from Fairy effect (multiplier on total)")
                        .defineInRange("fairySpeedBonus", 0.2, -1, 100);
                fairyHitboxReduction = builder.text("Danmaku hitbox change from Fairy effect (flat add to HITBOX attribute, negative shrinks the hitbox)")
                        .defineInRange("fairyHitboxReduction", -0.5, -1, 1);
            }
            builder.pop();

            builder.push("blocks", "Blocks");
            {
            }
            builder.pop();

            builder.push("suwako_hat", "Suwako Hat");
            {
                frogEatCountForHat = builder.text("Number of raiders with different types frogs need to eat in front of villager to drop Suwako hat")
                        .defineInRange("frogEatCountForHat", 3, 1, 10);
                frogEatRaiderVillagerSightRange = builder.text("Range for villagers with direct sight when frog eat raiders")
                        .defineInRange("frogEatRaiderVillagerSightRange", 20, 1, 64);
                frogEatRaiderVillagerNoSightRange = builder.text("Range for villagers without direct sight when frog eat raiders")
                        .defineInRange("frogEatRaiderVillagerNoSightRange", 10, 1, 64);
            }
            builder.pop();

            builder.push("koishi_attack", "Koishi Attack");
            {
                koishiAttackEnable = builder.text("Enable koishi attack when player has youkaifying or youkaified effect")
                        .define("koishiAttackEnable", true);
                koishiAttackCoolDown = builder.text("Time in ticks for minimum time between koishi attacks")
                        .defineInRange("koishiAttackCoolDown", 6000, 1, 1000000);
                koishiAttackChance = builder.text("Chance every tick to do koishi attack")
                        .defineInRange("koishiAttackChance", 0.001, 0, 1);
                koishiAttackDamage = builder.text("Koishi attack damage")
                        .defineInRange("koishiAttackDamage", 100, 0, 100000000);
                koishiAttackBlockCount = builder.text("Number of times player needs to consecutively block Koishi attack to get hat")
                        .defineInRange("koishiAttackBlockCount", 3, 0, 100);
                koishiHatCooldown = builder.text("Time in ticks before Koishi hat re-applies the unconscious effect after being interrupted (attacked, opened a container, etc.)")
                        .defineInRange("koishiHatCooldown", 200, 0, 100000);
            }
            builder.pop();

            builder.push("danmaku_battle", "Danmaku Battle");
            {
                danmakuMinPHPDamage = builder.text("Minimum damage youkai danmaku will deal against non-player")
                        .defineInRange("danmakuMinPHPDamage", 0.02, 0, 1);
                danmakuPlayerPHPDamage = builder.text("Minimum damage youkai danmaku will deal against player")
                        .defineInRange("danmakuPlayerPHPDamage", 0.1, 0, 1);
                danmakuHealOnHitTarget = builder.text("When danmaku hits target, heal youkai health by percentage of max health")
                        .defineInRange("danmakuHealOnHitTarget", 0.2, 0, 1);
                enableExtraCoolDown = builder.text("Extra Damage Cooldown")
                        .comment("Enable extra damage cool down on some youkai")
                        .define("enableExtraCoolDown", true);
                danmakuMaxResource = builder.text("Max resource (life/bomb) obtainable from danmaku battle")
                        .defineInRange("danmakuMaxResource", 10, 4, 20);
                danmakuMaxPower = builder.text("Max Power player can obtain from grazing")
                        .defineInRange("danmakuMaxPower", 4, 1, 20);
                danmakuPowerBonus = builder.text("Danmaku damage bonus each level of power increase")
                        .defineInRange("danmakuPowerBonus", 0.25, 0, 1);
                grazeEffectiveness = builder.text("Multiplier for grazing")
                        .defineInRange("grazeEffectiveness", 1d, 0, 10);
                missInvulTime = builder.text("Danmaku invulnerability and disabled time when you take a hit")
                        .defineInRange("missInvulTime", 60, 10, 100);
                bombInvulTime = builder.text("Danmaku invulnerability and disabled time when you use a bomb")
                        .defineInRange("bombInvulTime", 30, 10, 100);
                maxPowerLossOnMiss = builder.text("Maximum loss of power when you take a hit")
                        .defineInRange("maxPowerLossOnMiss", 1d, 0, 10);
                initialResource = builder.text("Initial life and bomb when you initiate a danmaku battle")
                        .defineInRange("initialResource", 2, 0, 10);
                initialPower = builder.text("Initial power when you initiate a danmaku battle")
                        .defineInRange("initialPower", 1, 0, 10);
            }
            builder.pop();

            builder.push("fairy", "Fairy");
            {
                fairyAttackYoukaified = builder.text("Fairies will actively attack players with youkaifying/ed effects")
                        .define("fairyAttackYoukaified", true);
                fairySummonReinforcement = builder.text("Chance for fairies to summon other fairies when killed by non-danmaku damage")
                        .defineInRange("fairySummonReinforcement", 0.5, 0, 1);
            }
            builder.pop();

            builder.push("rumia", "Rumia");
            {
                exRumiaConversion = builder.text("Enable Ex Rumia conversion when Rumia takes too high damage in one hit")
                        .define("exRumiaConversion", true);
                rumiaHairbandDrop = builder.text("If true, killing EX Rumia with danmaku will drop her hairband")
                        .define("rumiaHairbandDrop", true);
            }
            builder.pop();

            builder.push("reimu", "Reimu");
            {
                reimuSummonFlesh = builder.text("If true, eating flesh food near villagers will summon Reimu to attack the player")
                        .define("reimuSummonFlesh", true);
                reimuSummonKill = builder.text("Summon Reimu when youkaified player kills villager in front of other villagers")
                        .define("reimuSummonKill", true);
                reimuSummonMoney = builder.text("Summon Reimu when player throws emerald or gold into donation box")
                        .define("reimuSummonMoney", true);
                reimuSummonCost = builder.text("Cost of emerald/gold to summon Reimu")
                        .defineInRange("reimuSummonCost", 8, 1, 100000);
                reimuHairbandFlightEnable = builder.text("Enable creative flight on Reimu hairband")
                        .define("reimuHairbandFlightEnable", true);
            }
            builder.pop();
            builder.push("compat", "Mod Compatibility");
            {
                curiosSupportEnabled = builder.text("Master switch for Curios slot support on this mod's hats, hairbands and other accessories. When false, accessories are recognized only in vanilla equipment slots even if Curios is loaded")
                        .define("curiosSupportEnabled", true);
            }
            builder.pop();

            builder.push("natural_spawn", "Natural Spawning");
            {
                spawnDeer = builder.text("Allow Deer to spawn naturally")
                        .define("spawnDeer", true);
                spawnBoar = builder.text("Allow Boar to spawn naturally")
                        .define("spawnBoar", true);
                spawnTuna = builder.text("Allow Tuna to spawn naturally")
                        .define("spawnTuna", true);
                spawnCrab = builder.text("Allow Crab to spawn naturally")
                        .define("spawnCrab", true);
                spawnLamprey = builder.text("Allow Lamprey to spawn naturally")
                        .define("spawnLamprey", true);
                spawnDeerRate = builder.text("Fraction of natural Deer spawns to keep (0 blocks all, 1 keeps every attempt). Applied on top of spawnDeer")
                        .defineInRange("spawnDeerRate", 1.0, 0.0, 1.0);
                spawnBoarRate = builder.text("Fraction of natural Boar spawns to keep (0 blocks all, 1 keeps every attempt). Applied on top of spawnBoar")
                        .defineInRange("spawnBoarRate", 1.0, 0.0, 1.0);
                spawnTunaRate = builder.text("Fraction of natural Tuna spawns to keep (0 blocks all, 1 keeps every attempt). Applied on top of spawnTuna")
                        .defineInRange("spawnTunaRate", 1.0, 0.0, 1.0);
                spawnLampreyRate = builder.text("Fraction of natural Lamprey spawns to keep (0 blocks all, 1 keeps every attempt). Applied on top of spawnLamprey")
                        .defineInRange("spawnLampreyRate", 1.0, 0.0, 1.0);
            }
            builder.pop();
        }

    }

    public static final Server SERVER = GensokyoLegacy.REGISTRATE.registerSynced(Server::new);

    public static class Client extends ConfigInit {

        public final ModConfigSpec.IntValue powerInfoXAnchor;
        public final ModConfigSpec.IntValue powerInfoXOffset;
        public final ModConfigSpec.IntValue powerInfoYAnchor;
        public final ModConfigSpec.IntValue powerInfoYOffset;

        Client(Builder builder) {
            markL2();
            builder.push("power_info", "Power Info Overlay");
            {
                powerInfoXAnchor = builder.text("Horizontal anchor for the power info overlay (-1 left, 0 center, 1 right)")
                        .defineInRange("powerInfoXAnchor", 1, -1, 1);
                powerInfoXOffset = builder.text("Horizontal pixel offset for the power info overlay")
                        .defineInRange("powerInfoXOffset", -8, -1000, 1000);
                powerInfoYAnchor = builder.text("Vertical anchor for the power info overlay (-1 top, 0 center, 1 bottom)")
                        .defineInRange("powerInfoYAnchor", 0, -1, 1);
                powerInfoYOffset = builder.text("Vertical pixel offset for the power info overlay")
                        .defineInRange("powerInfoYOffset", 0, -1000, 1000);
            }
            builder.pop();
        }

    }

    public static final Client CLIENT = GensokyoLegacy.REGISTRATE.registerClient(Client::new);

    /**
     * Registers any relevant listeners for config
     */
    public static void init() {
    }


}
