package nonamecrackers2.endertrigon.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Instrument;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import nonamecrackers2.endertrigon.EnderTrigonMod;

public class EnderTrigonInstruments
{
	public static final DeferredRegister<Instrument> INSTRUMENTS = DeferredRegister.create(BuiltInRegistries.INSTRUMENT, EnderTrigonMod.MODID);
	
	public static final DeferredHolder<Instrument, Instrument> DRAGON_HORN = INSTRUMENTS.register("dragon_horn", () -> new Instrument(EnderTrigonSoundEvents.DRAGON_HORN.getDelegate(), 60, 32.0F));
	
	public static void register(IEventBus modBus)
	{
		INSTRUMENTS.register(modBus);
	}
}
