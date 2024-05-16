package nonamecrackers2.endertrigon.common.data;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import nonamecrackers2.crackerslib.common.util.data.ConfigLangGeneratorHelper;
import nonamecrackers2.endertrigon.EnderTrigonMod;
import nonamecrackers2.endertrigon.common.config.EnderTrigonConfig;
import nonamecrackers2.endertrigon.common.init.EnderTrigonBlocks;
import nonamecrackers2.endertrigon.common.init.EnderTrigonEntityTypes;

public class EnderTrigonLangProvider extends LanguageProvider
{
	public EnderTrigonLangProvider(PackOutput output)
	{
		super(output, EnderTrigonMod.MODID, "en_us");
	}
	
	@Override
	protected void addTranslations()
	{
//		ConfigLangGeneratorHelper.langForSpec(EnderTrigonMod.MODID, EnderTrigonConfig.CLIENT_SPEC, this, ConfigLangGeneratorHelper.Info.ONLY_RANGE);
		ConfigLangGeneratorHelper.langForSpec(EnderTrigonMod.MODID, EnderTrigonConfig.COMMON_SPEC, this, ConfigLangGeneratorHelper.Info.ONLY_RANGE);
		this.add(EnderTrigonEntityTypes.BABY_ENDER_DRAGON.get(), "Baby Ender Dragon");
		this.add(EnderTrigonEntityTypes.DRAGON_FLAME.get(), "Dragon Flame");
		this.add(EnderTrigonBlocks.BABY_DRAGON_EGG.get(), "Baby Dragon Egg");
		this.add("death.attack.dragonFlame", "%1$s was incinerated by %2$s");
	  	this.add("death.attack.dragonFlame.item", "%1$s was incinerated by %2$s using %3$s");
	  	this.add("endertrigon.subtitle.baby_dragon_egg_breaks", "Baby Dragon Egg Breaks");
	  	this.add("endertrigon.subtitle.dragon_lays_egg", "Ender Dragon Lays Egg");
	}
}
