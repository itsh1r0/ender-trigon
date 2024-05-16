package nonamecrackers2.endertrigon.common.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraftforge.data.event.GatherDataEvent;

public class EnderTrigonDataEvents
{
	public static void gatherData(GatherDataEvent event)
	{
		DataGenerator generator = event.getGenerator();
		generator.addProvider(event.includeClient(), (DataProvider.Factory<EnderTrigonLangProvider>)EnderTrigonLangProvider::new);
	}
}
