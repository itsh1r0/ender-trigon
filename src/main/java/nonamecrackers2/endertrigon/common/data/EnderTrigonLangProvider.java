package nonamecrackers2.endertrigon.common.data;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import nonamecrackers2.crackerslib.common.util.data.ConfigLangGeneratorHelper;
import nonamecrackers2.endertrigon.EnderTrigonMod;
import nonamecrackers2.endertrigon.common.config.EnderTrigonConfig;
import nonamecrackers2.endertrigon.common.init.EnderTrigonBlocks;
import nonamecrackers2.endertrigon.common.init.EnderTrigonEntityTypes;
import nonamecrackers2.endertrigon.common.init.EnderTrigonItems;

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
		this.add(EnderTrigonItems.DRAGON_HORN.get(), "Dragon Horn");
		this.add("block.endertrigon.baby_dragon_egg.use", "Place down to spawn a tamed, baby Ender Dragon");
		this.add("block.endertrigon.dragon_horn.use", "Use to call tamed baby Ender Dragons");
		this.add("death.attack.dragonFlame", "%1$s was incinerated by %2$s");
	  	this.add("death.attack.dragonFlame.item", "%1$s was incinerated by %2$s using %3$s");
	  	this.add("endertrigon.subtitle.baby_dragon_egg_breaks", "Baby Dragon Egg Breaks");
	  	this.add("endertrigon.subtitle.dragon_lays_egg", "Ender Dragon Lays Egg");
	  	this.add("endertrigon.subtitle.dragon_horn", "Dragon horn plays");
	  	this.add("instrument.endertrigon.dragon_horn", "Dragon Calling");
	}
}
