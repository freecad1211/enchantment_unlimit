// Enchantment_unlimit.java
package io.github.freecad1211.enchantment_unlimit;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

// CommandExecutor 인터페이스를 구현합니다.
public class Enchantment_unlimit extends JavaPlugin implements CommandExecutor {

    private Map<String, Integer> customMaxLevels = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("Enchantment_unlimit 플러그인이 활성화되었습니다.");

        saveDefaultConfig();
        loadCustomMaxLevels();

        // 명령어 핸들러를 등록합니다.
        this.getCommand("enchantment_unlimit").setExecutor(this);

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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // 명령어의 첫 번째 인수가 "reload"인지 확인합니다.
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {

            // sender가 권한을 가지고 있는지 확인합니다.
            if (!sender.hasPermission("enchantment_unlimit.admin")) {
                sender.sendMessage(ChatColor.RED + "이 명령어를 사용할 권한이 없습니다.");
                return true;
            }

            // config 파일을 다시 불러옵니다.
            reloadConfig();
            // 커스텀 최대 레벨을 다시 로드합니다.
            loadCustomMaxLevels();

            sender.sendMessage(ChatColor.GREEN + "Enchantment_unlimit 플러그인의 설정이 다시 로드되었습니다.");
            getLogger().info("Enchantment_unlimit 플러그인 설정이 다시 로드되었습니다.");
            return true;
        }

        // 명령어가 올바르지 않은 경우 사용법을 안내합니다.
        sender.sendMessage(ChatColor.YELLOW + "올바른 사용법: /" + label + " reload");
        return true;
    }
}