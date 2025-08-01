package io.github.freecad1211.enchantment_unlimit;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.view.AnvilView; // AnvilView를 임포트합니다.

import java.util.Map;

public class AnvilListener implements Listener {

    private final Enchantment_unlimit plugin;

    public AnvilListener(Enchantment_unlimit plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        AnvilInventory inventory = event.getInventory();
        ItemStack firstItem = inventory.getItem(0);
        ItemStack secondItem = inventory.getItem(1);

        if (firstItem == null || secondItem == null ||
                firstItem.getType() != Material.ENCHANTED_BOOK ||
                secondItem.getType() != Material.ENCHANTED_BOOK) {
            return;
        }

        EnchantmentStorageMeta firstMeta = (EnchantmentStorageMeta) firstItem.getItemMeta();
        EnchantmentStorageMeta secondMeta = (EnchantmentStorageMeta) secondItem.getItemMeta();

        Map<Enchantment, Integer> firstEnchants = firstMeta.getStoredEnchants();
        Map<Enchantment, Integer> secondEnchants = secondMeta.getStoredEnchants();

        if (firstEnchants.size() != 1 || secondEnchants.size() != 1) {
            return;
        }

        Enchantment firstEnchant = firstEnchants.keySet().iterator().next();
        int firstLevel = firstEnchants.get(firstEnchant);

        Enchantment secondEnchant = secondEnchants.keySet().iterator().next();
        int secondLevel = secondEnchants.get(secondEnchant);

        if (firstEnchant.equals(secondEnchant) && firstLevel == secondLevel) {
            int newLevel = firstLevel + 1;

            String enchantKey = firstEnchant.getKey().getKey();
            Map<String, Integer> customMaxLevels = plugin.getCustomMaxLevels();

            if (customMaxLevels.containsKey(enchantKey) && newLevel > customMaxLevels.get(enchantKey)) {
                return;
            }

            ItemStack resultBook = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta resultMeta = (EnchantmentStorageMeta) resultBook.getItemMeta();

            resultMeta.addStoredEnchant(firstEnchant, newLevel, true);
            resultBook.setItemMeta(resultMeta);

            event.setResult(resultBook);

            // 중요: AnvilView를 통해 비용을 설정해야 합니다.
            // 이벤트가 발생한 후 다음 틱(tick)에 비용을 설정하여 안정적으로 적용되도록 합니다.
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                AnvilView view = event.getView();
                int repairCost = newLevel * 7; // 동적 비용 계산
                view.setRepairCost(repairCost);
            });
        }
    }
}