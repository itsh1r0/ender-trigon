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
		
		public CommonConfig(ForgeConfigSpec.Builder builder)
		{
			super(builder, EnderTrigonMod.MODID);
			
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
		}
	}
}
