package snownee.kiwi.mixin.client;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

@Mixin(CreativeModeInventoryScreen.class)
public abstract class CreativeModeInventoryScreenMixin extends EffectRenderingInventoryScreen<CreativeModeInventoryScreen.ItemPickerMenu> {
	@Shadow
	private float scrollOffs;

	@Unique
	private static float persistentScrollOffs = 0;

	public CreativeModeInventoryScreenMixin(
			CreativeModeInventoryScreen.ItemPickerMenu menu,
			Inventory inventory,
			Component component) {
		super(menu, inventory, component);
	}

	@Inject(method = "removed", at = @At("HEAD"))
	private void kiwi$saveScrollOffs(CallbackInfo ci) {
		persistentScrollOffs = this.scrollOffs;
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void kiwi$restoreScrollOffs(CallbackInfo ci) {
		this.scrollOffs = persistentScrollOffs;
		this.menu.scrollTo(this.scrollOffs);
	}
}