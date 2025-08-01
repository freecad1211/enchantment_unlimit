package io.github.freecad1211.enchantment_unlimit;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Enchantment_unlimit extends JavaPlugin {

    private Map<String, Integer> customMaxLevels = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("Enchantment_unlimit 플러그인이 활성화되었습니다.");

        // config.yml 파일이 없으면 생성합니다.
        saveDefaultConfig();

        // config에서 커스텀 최대 레벨을 로드합니다.
        loadCustomMaxLevels();

        // AnvilPrepareEvent 리스너를 등록하고 설정 값을 전달합니다.
        getServer().getPluginManager().registerEvents(new AnvilListener(this), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("Enchantment_unlimit 플러그인이 비활성화되었습니다.");
    }

    private void loadCustomMaxLevels() {
        if (getConfig().isConfigurationSection("max-levels")) {
            for (String key : getConfig().getConfigurationSection("max-levels").getKeys(false)) {
                customMaxLevels.put(key, getConfig().getInt("max-levels." + key));
            }
        }
    }

    // 다른 클래스에서 설정을 가져올 수 있도록 메서드를 만듭니다.
    public Map<String, Integer> getCustomMaxLevels() {
        return customMaxLevels;
    }
}