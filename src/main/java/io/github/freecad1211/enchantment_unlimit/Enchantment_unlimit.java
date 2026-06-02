package io.github.freecad1211.enchantment_unlimit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentUnlimit extends JavaPlugin {

    private Map<String, Integer> customMaxLevels = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadCustomMaxLevels();

        EnchantCommand enchantCommand = new EnchantCommand(this);
        getCommand("unlimitenchant").setExecutor(enchantCommand);
        getCommand("unlimitenchant").setTabCompleter(enchantCommand);

        getCommand("enchantment_unlimit").setExecutor(this);

        getLogger().info("EnchantmentUnlimit 플러그인이 활성화되었습니다.");
    }

    @Override
    public void onDisable() {
        getLogger().info("EnchantmentUnlimit 플러그인이 비활성화되었습니다.");
    }

    private void loadCustomMaxLevels() {
        customMaxLevels.clear();
        if (getConfig().isConfigurationSection("max-levels")) {
            for (String key : getConfig().getConfigurationSection("max-levels").getKeys(false)) {
                customMaxLevels.put(key.toUpperCase(), getConfig().getInt("max-levels." + key));
            }
        }
    }

    public Map<String, Integer> getCustomMaxLevels() {
        return customMaxLevels;
    }

    // /enchantment_unlimit reload
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("enchantment_unlimit.admin")) {
                sender.sendMessage(ChatColor.RED + "이 명령어를 사용할 권한이 없습니다.");
                return true;
            }
            reloadConfig();
            loadCustomMaxLevels();
            sender.sendMessage(ChatColor.GREEN + "설정이 다시 로드되었습니다.");
            return true;
        }
        sender.sendMessage(ChatColor.YELLOW + "사용법: /" + label + " reload");
        return true;
    }
}
