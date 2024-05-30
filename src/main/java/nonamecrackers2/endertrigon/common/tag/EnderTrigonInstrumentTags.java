package nonamecrackers2.endertrigon.common.tag;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Instrument;
import nonamecrackers2.endertrigon.EnderTrigonMod;

public class EnderTrigonInstrumentTags
{
	public static final TagKey<Instrument> DRAGON_HORN = TagKey.create(Registries.INSTRUMENT, EnderTrigonMod.id("dragon_horn"));
}
