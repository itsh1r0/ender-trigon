package nonamecrackers2.endertrigon.common.data;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;

public class EnderTrigonDataEvents
{
	public static void gatherData(GatherDataEvent event)
	{
		DataGenerator generator = event.getGenerator();
		generator.addProvider(event.includeClient(), (DataProvider.Factory<EnderTrigonLangProvider>)EnderTrigonLangProvider::new);
		generator.addProvider(event.includeClient(), (DataProvider.Factory<EnderTrigonItemModelProvider>)output -> new EnderTrigonItemModelProvider(output, event.getExistingFileHelper()));
		generator.addProvider(event.includeServer(), (DataProvider.Factory<EnderTrigonRecipeProvider>)output -> new EnderTrigonRecipeProvider(output, event.getLookupProvider()));
	}
}
