package dev.xkmc.youkaishomecoming.compat.curios;

import dev.xkmc.youkaishomecoming.init.registrate.GLItems;
import top.theillusivec4.curios.api.client.CuriosRendererRegistry;

public class CuriosClientCompat {

	public static void registerRenderers() {
		for (var entry : GLItems.HAT_ITEMS) {
			CuriosRendererRegistry.register(entry.get(), HatCurioRenderer::new);
		}
	}

}
