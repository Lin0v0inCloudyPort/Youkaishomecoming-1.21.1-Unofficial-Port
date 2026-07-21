package dev.xkmc.youkaishomecoming.content.client;

import dev.xkmc.youkaishomecoming.content.attachment.graze.GrazeCapability;
import dev.xkmc.youkaishomecoming.init.data.GLModConfig;
import dev.xkmc.youkaishomecoming.init.registrate.GLMeta;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;

import java.util.List;

public class PowerInfoOverlay implements LayeredDraw.Layer {

	@Override
	public void render(GuiGraphics g, DeltaTracker delta) {
		var mc = Minecraft.getInstance();
		if (mc.screen != null) return;
		var pl = mc.player;
		if (pl == null) return;
		var graze = GLMeta.GRAZE.type().getOrCreate(pl);
		List<GrazeCapability.InfoLine> info = graze.getInfoLines(pl);
		if (info.isEmpty()) return;
		var font = mc.font;
		int lh = font.lineHeight + 2;
		int th = lh * info.size();
		int tw = 0;
		for (var e : info) {
			tw = Math.max(tw, font.width(e.text()));
		}
		tw += 14;

		int w = g.guiWidth(), h = g.guiHeight();
		int xa = GLModConfig.CLIENT.powerInfoXAnchor.get();
		int xo = GLModConfig.CLIENT.powerInfoXOffset.get();
		int ya = GLModConfig.CLIENT.powerInfoYAnchor.get();
		int yo = GLModConfig.CLIENT.powerInfoYOffset.get();

		int x = xo + (xa + 1) * (w - tw) / 2;
		int y = yo + (ya + 1) * (h - th) / 2;

		for (var e : info) {
			g.blit(e.icon().loc(), x, y, e.x(), e.y(), 10, 10, e.icon().w(), e.icon().h());
			g.drawString(font, e.text(), x + 14, y, e.color(), false);
			y += lh;
		}
	}

}
