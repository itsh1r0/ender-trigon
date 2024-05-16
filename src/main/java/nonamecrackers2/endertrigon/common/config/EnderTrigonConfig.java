package nonamecrackers2.endertrigon.common.config;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BooleanSupplier;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import net.minecraftforge.common.ForgeConfigSpec;
import nonamecrackers2.crackerslib.common.config.ConfigHelper;
import nonamecrackers2.endertrigon.EnderTrigonMod;
import nonamecrackers2.endertrigon.common.init.EnderTrigonDragonPhases;

public class EnderTrigonConfig
{
	public static final List<String> CANNOT_BE_TOGGLED = ImmutableList.of("CarryPlayer", "ChargeUp");
//	public static final ClientConfig CLIENT;
//	public static final ForgeConfigSpec CLIENT_SPEC;
	public static final CommonConfig COMMON;
	public static final ForgeConfigSpec COMMON_SPEC;
	
	static
	{
//		var clientPar = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
//		CLIENT = clientPar.getLeft();
//		CLIENT_SPEC = clientPar.getRight();
		var commonPar = new ForgeConfigSpec.Builder().configure(CommonConfig::new);
		COMMON = commonPar.getLeft();
		COMMON_SPEC = commonPar.getRight();
	}
	
//	public static class ClientConfig extends ConfigHelper
//	{
//		public final ForgeConfigSpec.ConfigValue<Boolean> renderThreeHeads;
//		
//		public ClientConfig(ForgeConfigSpec.Builder builder)
//		{
//			super(builder, EnderTrigonMod.MODID);
//			
//			this.renderThreeHeads = this.createValue(true, "renderThreeHeads", false, "Specifies if the extra heads should be rendered on the Ender Dragon");
//		}
//	}
	
	public static class CommonConfig extends ConfigHelper
	{
		public final Map<String, BooleanSupplier> enabledCustomDragonPhases;
		public final ForgeConfigSpec.ConfigValue<Boolean> skipLandingPhase;
		public final ForgeConfigSpec.ConfigValue<Boolean> crashPhaseDestroysBlocks;
		public final ForgeConfigSpec.ConfigValue<Integer> maxBabyEnderDragons;
		public final ForgeConfigSpec.ConfigValue<Integer> attacksUntilPerch;
		
		public CommonConfig(ForgeConfigSpec.Builder builder)
		{
			super(builder, EnderTrigonMod.MODID);
			
			builder.comment("Enaled Custom Phases").push("enabled_custom_phases");
			
			this.enabledCustomDragonPhases = EnderTrigonDragonPhases.CUSTOM_DRAGON_PHASES.entrySet().stream().map(e -> 
			{
				BooleanSupplier supplier;
				if (CANNOT_BE_TOGGLED.contains(e.getKey()))
				{
					supplier = () -> true;
				}
				else
				{
					var config = this.createValue(true, "enable" + e.getKey(), true, "Specifies if this custom dragon phase should be enabled or not");
					supplier = config::get;
				}
				return Map.<String, BooleanSupplier>entry(e.getKey(), supplier);
			}).collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue));
			
			builder.pop();
			
			this.skipLandingPhase = this.createValue(true, "skipLandingPhase", false, "Specifies if the landing phase should have a much higher chance to be skipped. Makes the fight quicker by avoiding long dull periods where the Ender Dragon is flying for a long time, before it perches");
			this.crashPhaseDestroysBlocks = this.createValue(true, "crashPhaseDestroysBlocks", false, "Specifies if the crash phase should cause blocks to be flung");
			this.maxBabyEnderDragons = this.createRangedIntValue(4, 1, 16, "maxBabyEnderDragons", false, "Specifies the max amount of baby ender dragons the Ender Dragon can spawn during its hatching (dive bomb) phase");
			this.attacksUntilPerch = this.createRangedIntValue(4, 1, 16, "attacksUntilPerch", false, "Specifies the amount of special attacks, plus the amount of remaining end crystals, the dragon must do until it can potentially perch");
		}
	}
}
