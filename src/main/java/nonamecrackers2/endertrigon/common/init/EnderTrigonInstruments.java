package nonamecrackers2.endertrigon.common.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Instrument;
import nonamecrackers2.endertrigon.EnderTrigonMod;

public class EnderTrigonInstruments
{
	public static final ResourceKey<Instrument> DRAGON_HORN = ResourceKey.create(Registries.INSTRUMENT, EnderTrigonMod.id("dragon_horn"));
	
	public static void register()
	{
		Registry.register(BuiltInRegistries.INSTRUMENT, DRAGON_HORN, new Instrument(EnderTrigonSoundEvents.DRAGON_HORN.getHolder().get(), 60, 32.0F));
	}
}
