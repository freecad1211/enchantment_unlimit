package io.github.freecad1211.enchantment_unlimit;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player; // Player 임포트
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.view.AnvilView;

import java.util.Map;

public class AnvilListener implements Listener {

    private final Enchantment_unlimit plugin;

    public AnvilListener(Enchantment_unlimit plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        // 이벤트를 실행한 플레이어를 가져옵니다.
        if (!(event.getView().getPlayer() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getView().getPlayer();

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
            String enchantKey = firstEnchant.getKey().getKey();
            Map<String, Integer> customMaxLevels = plugin.getCustomMaxLevels();

            // *** 여기에 로직 추가 ***
            if (customMaxLevels.containsKey(enchantKey)) {
                int maxLevel = customMaxLevels.get(enchantKey);
                // 현재 레벨이 설정된 최대 레벨 이상인지 확인
                if (firstLevel >= maxLevel) {
                    // 플레이어에게 메시지 전송
                    player.sendMessage(ChatColor.RED + String.format("'%s' 인챈트는 이미 최대 레벨(%d)입니다.", enchantKey, maxLevel));
                    // 이벤트 취소 (결과 창을 비움)
                    event.setResult(null);
                    return;
                }
            }

            int newLevel = firstLevel + 1;

            if (customMaxLevels.containsKey(enchantKey) && newLevel > customMaxLevels.get(enchantKey)) {
                return;
            }

            ItemStack resultBook = new ItemStack(Material.ENCHANTED_BOOK);
            EnchantmentStorageMeta resultMeta = (EnchantmentStorageMeta) resultBook.getItemMeta();

            resultMeta.addStoredEnchant(firstEnchant, newLevel, true);
            resultBook.setItemMeta(resultMeta);

            event.setResult(resultBook);

            plugin.getServer().getScheduler().runTask(plugin, () -> {
                AnvilView view = event.getView();
                int repairCost = newLevel * 7;
                view.setRepairCost(repairCost);
            });
        }
    }
}