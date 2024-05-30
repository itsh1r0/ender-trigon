/*
 * Copyright 2022 nonamecrackers2

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/


package nonamecrackers2.endertrigon;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import nonamecrackers2.endertrigon.client.event.EnderTrigonClientEvents;
import nonamecrackers2.endertrigon.client.init.EnderTrigonRenderers;
import nonamecrackers2.endertrigon.common.config.EnderTrigonConfig;
import nonamecrackers2.endertrigon.common.data.EnderTrigonDataEvents;
import nonamecrackers2.endertrigon.common.init.EnderTrigonBlockEntityTypes;
import nonamecrackers2.endertrigon.common.init.EnderTrigonBlocks;
import nonamecrackers2.endertrigon.common.init.EnderTrigonDragonPhases;
import nonamecrackers2.endertrigon.common.init.EnderTrigonEntityTypes;
import nonamecrackers2.endertrigon.common.init.EnderTrigonInstruments;
import nonamecrackers2.endertrigon.common.init.EnderTrigonItems;
import nonamecrackers2.endertrigon.common.init.EnderTrigonSoundEvents;

@Mod(EnderTrigonMod.MODID)
public class EnderTrigonMod
{
	public static final String MODID = "endertrigon";
	
	public EnderTrigonMod()
	{
		ModContainer mod = ModLoadingContext.get().getActiveContainer();
		IEventBus modBus = mod.getEventBus();
		EnderTrigonEntityTypes.register(modBus);
		EnderTrigonBlocks.register(modBus);
		EnderTrigonBlockEntityTypes.register(modBus);
		EnderTrigonSoundEvents.register(modBus);
		EnderTrigonItems.register(modBus);
		EnderTrigonInstruments.register(modBus);
		modBus.addListener(this::commonSetup);
		modBus.addListener(this::clientSetup);
		modBus.addListener(EnderTrigonEntityTypes::registerAttributes);
		modBus.addListener(EnderTrigonItems::buildCreativeTabContents);
		modBus.addListener(EnderTrigonDataEvents::gatherData);
		modBus.register(EnderTrigonRenderers.class);
		mod.registerConfig(ModConfig.Type.COMMON, EnderTrigonConfig.COMMON_SPEC);
	}
	
	public void commonSetup(FMLCommonSetupEvent event)
	{
		event.enqueueWork(() -> {
			EnderTrigonDragonPhases.register();
		});
	}
	
	public void clientSetup(FMLClientSetupEvent event)
	{
		IEventBus modBus = ModLoadingContext.get().getActiveContainer().getEventBus();
		modBus.addListener(EnderTrigonClientEvents::registerConfigScreen);
		modBus.addListener(EnderTrigonClientEvents::registerConfigMenuButton);
		event.enqueueWork(() ->
		{
			ItemProperties.register(EnderTrigonItems.DRAGON_HORN.get(), id("tooting"), (item, level, entity, i) -> {
		         return entity != null && entity.isUsingItem() && entity.getUseItem() == item ? 1.0F : 0.0F;
		    });
		});
	}
	
	public static ResourceLocation id(String path)
	{
		return new ResourceLocation(MODID, path);
	}
}
