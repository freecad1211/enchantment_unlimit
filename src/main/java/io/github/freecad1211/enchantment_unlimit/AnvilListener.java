// AnvilListener.java
package io.github.freecad1211.enchantment_unlimit;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Map;

public class AnvilListener implements Listener {

    private final Enchantment_unlimit plugin;

    public AnvilListener(Enchantment_unlimit plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAnvilPrepare(PrepareAnvilEvent event) {
        AnvilInventory inv = event.getInventory();
        ItemStack sourceItem = inv.getItem(0);
        ItemStack ingredient = inv.getItem(1);
        ItemStack result = event.getResult();

        // 1. 결과 아이템이 비어있으면 로직을 실행하지 않습니다.
        if (result == null || result.getType() == Material.AIR) {
            return;
        }

        // 2. config.yml에서 설정된 커스텀 최대 레벨을 가져옵니다.
        Map<String, Integer> customMaxLevels = plugin.getCustomMaxLevels();

        // 3. 인챈트 로직을 처리하는 새로운 메서드를 호출합니다.
        ItemStack newResult = applyCustomEnchantments(sourceItem, ingredient, result, customMaxLevels);

        // 4. 새로운 결과 아이템으로 이벤트를 업데이트합니다.
        event.setResult(newResult);
    }

    private ItemStack applyCustomEnchantments(ItemStack source, ItemStack ingredient, ItemStack originalResult, Map<String, Integer> customMaxLevels) {
        // 결과 아이템이 없으면 null 반환
        if (originalResult == null || originalResult.getType() == Material.AIR) {
            return null;
        }

        // 결과 아이템을 수정할 수 있도록 복사본을 만듭니다.
        ItemStack newResult = originalResult.clone();
        ItemMeta resultMeta = newResult.getItemMeta();

        // 소스 아이템과 재료 아이템의 인챈트 정보를 가져옵니다.
        Map<Enchantment, Integer> sourceEnchants = source.getEnchantments();
        Map<Enchantment, Integer> ingredientEnchants;

        // 재료 아이템이 인챈트 북인지 확인하여 인챈트 정보를 가져옵니다.
        if (ingredient != null && ingredient.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta bookMeta = (EnchantmentStorageMeta) ingredient.getItemMeta();
            ingredientEnchants = bookMeta.getStoredEnchants();
        } else if (ingredient != null) {
            ingredientEnchants = ingredient.getEnchantments();
        } else {
            return newResult;
        }

        // 재료 아이템의 모든 인챈트를 순회합니다.
        for (Map.Entry<Enchantment, Integer> entry : ingredientEnchants.entrySet()) {
            Enchantment enchant = entry.getKey();
            int ingredientLevel = entry.getValue();

            // 소스 아이템에 이미 인챈트가 있는지 확인합니다.
            if (sourceEnchants.containsKey(enchant)) {
                int sourceLevel = sourceEnchants.get(enchant);
                int newLevel;

                // 인챈트 레벨이 같으면 1 증가시킵니다.
                if (sourceLevel == ingredientLevel) {
                    newLevel = sourceLevel + 1;
                }
                // 인챈트 레벨이 다르면 더 높은 레벨을 선택합니다.
                else {
                    newLevel = Math.max(sourceLevel, ingredientLevel);
                }

                // config에 정의된 최대 레벨을 초과하지 않도록 합니다.
                String enchantName = enchant.getKey().getKey().toUpperCase();
                if (customMaxLevels.containsKey(enchantName)) {
                    int maxLevel = customMaxLevels.get(enchantName);
                    // config에 정의된 레벨보다 낮으면 무시하고 높은 레벨로 적용
                    if (newLevel <= maxLevel) {
                        resultMeta.addEnchant(enchant, newLevel, true);
                    } else {
                        // config에 정의된 레벨보다 높으면 그대로 적용
                        resultMeta.addEnchant(enchant, newLevel, true);
                    }
                } else {
                    // config에 정의되지 않은 인챈트라면 바닐라 최대 레벨을 따릅니다.
                    if (newLevel > enchant.getMaxLevel()) {
                        resultMeta.addEnchant(enchant, newLevel, true);
                    } else {
                        // 바닐라 최대 레벨을 초과하지 않는 경우 그대로 적용
                        resultMeta.addEnchant(enchant, newLevel, true);
                    }
                }
            } else {
                // 소스 아이템에 없는 인챈트는 그대로 추가합니다.
                resultMeta.addEnchant(enchant, ingredientLevel, true);
            }
        }

        // 최종 결과 아이템에 메타데이터를 적용합니다.
        newResult.setItemMeta(resultMeta);
        return newResult;
    }
}