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

package nonamecrackers2.endertrigon.common.init;

import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableMap;

import net.minecraft.Util;
import net.minecraft.world.entity.boss.enderdragon.phases.DragonPhaseInstance;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import nonamecrackers2.endertrigon.common.config.EnderTrigonConfig;
import nonamecrackers2.endertrigon.common.entity.boss.enderdragon.phase.DragonCarryPlayerPhase;
import nonamecrackers2.endertrigon.common.entity.boss.enderdragon.phase.DragonChargeUpPhase;
import nonamecrackers2.endertrigon.common.entity.boss.enderdragon.phase.DragonCrashPlayerPhase;
import nonamecrackers2.endertrigon.common.entity.boss.enderdragon.phase.DragonDiveBombPlayerPhase;
import nonamecrackers2.endertrigon.common.entity.boss.enderdragon.phase.DragonSnatchPlayerPhase;
import nonamecrackers2.endertrigon.mixin.IMixinEnderDragonPhase;

public class EnderTrigonDragonPhases
{
	public static final Logger LOGGER = LogManager.getLogger("endertrigon/EnderTrigonDragonPhases");
	public static final Map<String, Class<? extends DragonPhaseInstance>> CUSTOM_DRAGON_PHASES = Util.make(ImmutableMap.<String, Class<? extends DragonPhaseInstance>>builder(), map -> {
		map.put("ChargeUp", DragonChargeUpPhase.class);
		map.put("SnatchPlayer", DragonSnatchPlayerPhase.class);
		map.put("CarryPlayer", DragonCarryPlayerPhase.class);
		map.put("CrashPlayer", DragonCrashPlayerPhase.class);
		map.put("DiveBombPlayer", DragonDiveBombPlayerPhase.class);
	}).build();
	private static Map<String, EnderDragonPhase<?>> builtPhases;
	
	public static void register()
	{
		if (builtPhases != null)
			throw new IllegalStateException("Dragon phases are already registered!");
		
		builtPhases = CUSTOM_DRAGON_PHASES.entrySet().stream().filter(e -> {
			return EnderTrigonConfig.COMMON.enabledCustomDragonPhases.get(e.getKey()).getAsBoolean();
		}).map(e -> {
			return Map.entry(e.getKey(), create(e.getValue(), e.getKey()));
		}).collect(ImmutableMap.toImmutableMap(Map.Entry::getKey, Map.Entry::getValue));
		
		LOGGER.debug("Registered custom dragon phases:");
		for (var entry : CUSTOM_DRAGON_PHASES.entrySet())
			LOGGER.debug("Enabled: " + (builtPhases.get(entry.getKey()) != null ? "TRUE" : "FALSE") + " ---> " + entry.getKey());
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends DragonPhaseInstance> Optional<EnderDragonPhase<T>> getPhase(String name)
	{
		assertValidId(name);
		if (builtPhases.containsKey(name))
			return Optional.of((EnderDragonPhase<T>)builtPhases.get(name));
		else
			return Optional.empty();
	}
	
	public static boolean isPhase(String id, EnderDragonPhase<? extends DragonPhaseInstance> phase)
	{
		assertValidId(id);
		if (!builtPhases.containsKey(id))
			return false;
		return builtPhases.get(id) == phase;
	}
	
	public static boolean isEnabled(String id)
	{
		assertValidId(id);
		return builtPhases.containsKey(id);
	}
	
	private static void assertValidId(String id)
	{
		if (!CUSTOM_DRAGON_PHASES.containsKey(id))
			throw new IllegalArgumentException("'" + id + "' is not a valid custom dragon phase id");
	}
	
	private static <T extends DragonPhaseInstance> EnderDragonPhase<T> create(Class<T> instanceClass, String name)
	{
		return IMixinEnderDragonPhase.callCreate(instanceClass, name);
	}

}
