package snownee.kiwi;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import snownee.kiwi.KiwiModule.Category;

public class GroupSetting {

	public static GroupSetting of(Category category) {
		return new GroupSetting(category.value(), category.after());
	}

	private String[] groups;
	private String[] after;

	public GroupSetting(String[] groups, String[] after) {
		this.groups = groups;
		this.after = after;
	}

	public void apply(List<ItemStack> toAdd) {
		/* off */
		List<CreativeModeTab> tabs = Stream.of(groups)
				.map(Kiwi::getGroup)
				.filter(Objects::nonNull)
				.toList();
		Set<Item> afterItems = Stream.of(after)
				.map(ResourceLocation::tryParse)
				.filter(Objects::nonNull)
				.map(BuiltInRegistries.ITEM::get)
				.filter(Predicate.not(Items.AIR::equals))
				.collect(Collectors.toSet());
		/* on */
		for (CreativeModeTab tab : tabs) {
			ItemGroupEvents.modifyEntriesEvent(tab).register(entries -> {
				List<ItemStack> stacks = getEnabledStacks(toAdd, entries.getEnabledFeatures());
				addAfter(stacks, entries.getDisplayStacks(), afterItems);
				addAfter(stacks, entries.getSearchTabStacks(), afterItems);
			});
		}
	}

	private void addAfter(List<ItemStack> toAdd, List<ItemStack> destination, Set<Item> afterItems) {
		int lastFound = -1;
		if (!afterItems.isEmpty()) {
			for (int i = 0; i < destination.size(); i++) {
				ItemStack stack = destination.get(i);
				if (afterItems.contains(stack.getItem())) {
					lastFound = i;
				}
			}
		}
		if (lastFound >= 0) {
			destination.addAll(lastFound + 1, toAdd);
		} else {
			destination.addAll(toAdd);
		}
	}

	private List<ItemStack> getEnabledStacks(List<ItemStack> newStacks, FeatureFlagSet enabledFeatures) {
		// If not all stacks are enabled, filter the list, otherwise use it as-is
		if (newStacks.stream().allMatch($ -> isEnabled($, enabledFeatures))) {
			return newStacks;
		}

		return newStacks.stream().filter($ -> isEnabled($, enabledFeatures)).toList();
	}

	private boolean isEnabled(ItemStack stack, FeatureFlagSet enabledFeatures) {
		return stack.getItem().isEnabled(enabledFeatures);
	}
}
