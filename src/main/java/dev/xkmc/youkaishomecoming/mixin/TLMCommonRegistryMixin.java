package dev.xkmc.youkaishomecoming.mixin;

import com.github.tartaricacid.touhoulittlemaid.TouhouLittleMaid;
import com.github.tartaricacid.touhoulittlemaid.init.registry.CommonRegistry;
import dev.xkmc.youkaishomecoming.compat.touhoulittlemaid.YHMaidExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CommonRegistry.class)
public class TLMCommonRegistryMixin {

	@Inject(method = "modApiInit", at = @At(value = "INVOKE",
			target = "Lcom/github/tartaricacid/touhoulittlemaid/entity/task/TaskManager;init()V"), remap = false)
	private static void youkaishomecoming$injectExtension(CallbackInfo ci) {
		TouhouLittleMaid.EXTENSIONS.add(new YHMaidExtension());
	}

}
