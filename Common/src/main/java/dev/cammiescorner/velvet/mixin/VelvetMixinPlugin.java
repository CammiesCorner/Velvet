/*
 * Satin
 * Copyright (C) 2019-2024 Ladysnake
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; If not, see <https://www.gnu.org/licenses>.
 */
package dev.cammiescorner.velvet.mixin;

import dev.cammiescorner.velvet.Velvet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public final class VelvetMixinPlugin implements IMixinConfigPlugin {
	private static final Logger LOGGER = LogManager.getLogger("Velvet");
	public static final boolean ALLOW_RENDER_LAYER_MIXINS;

	static {
		if(Velvet.LOADER.isModLoaded("canvas")) {
			LOGGER.warn("[Velvet] Canvas is present, custom block renders will not work");
			ALLOW_RENDER_LAYER_MIXINS = false;
		}
		else if(Velvet.LOADER.isModLoaded("iris") || Velvet.LOADER.isModLoaded("oculus")) {
			LOGGER.warn("[Velvet] Iris is present, custom block renders will not work");
			ALLOW_RENDER_LAYER_MIXINS = false;
		}
		else {
			if(Velvet.LOADER.isModLoaded("sodium") || Velvet.LOADER.isModLoaded("rubidium") || Velvet.LOADER.isModLoaded("embeddium"))
				LOGGER.warn("[Velvet] Sodium is present, custom block renders may not work");

			ALLOW_RENDER_LAYER_MIXINS = true;
		}
	}

	@Override
	public void onLoad(String mixinPackage) {
		// NO-OP
	}

	@Override
	public String getRefMapperConfig() {
		return null;
	}

	@Override
	public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
		if(mixinClassName.contains("blockrendertype"))
			return ALLOW_RENDER_LAYER_MIXINS;

		return true;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
		// NO-OP
	}

	@Override
	public List<String> getMixins() {
		return List.of();
	}

	@Override
	public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		// NO-OP
	}

	@Override
	public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
		// NO-OP
	}
}
