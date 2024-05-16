package nonamecrackers2.endertrigon.client.event;

import net.minecraftforge.fml.config.ModConfig;
import nonamecrackers2.crackerslib.client.event.impl.ConfigMenuButtonEvent;
import nonamecrackers2.crackerslib.client.event.impl.RegisterConfigScreensEvent;
import nonamecrackers2.crackerslib.client.gui.ConfigHomeScreen;
import nonamecrackers2.crackerslib.client.gui.title.ImageTitle;
import nonamecrackers2.endertrigon.EnderTrigonMod;
import nonamecrackers2.endertrigon.common.config.EnderTrigonConfig;

public class EnderTrigonClientEvents
{
	public static void registerConfigScreen(RegisterConfigScreensEvent event)
	{
		event.builder(ConfigHomeScreen.builder(ImageTitle.ofMod(EnderTrigonMod.MODID, 156, 50, 2.0F)).crackersDefault("https://github.com/nonamecrackers2/ender-trigon/issues").build())
//				.addSpec(ModConfig.Type.CLIENT, EnderTrigonConfig.CLIENT_SPEC)
				.addSpec(ModConfig.Type.COMMON, EnderTrigonConfig.COMMON_SPEC).register();
	}
	
	public static void registerConfigMenuButton(ConfigMenuButtonEvent event)
	{
		event.defaultButtonWithSingleCharacter('E', 0xFFD99CFF);
	}
}
