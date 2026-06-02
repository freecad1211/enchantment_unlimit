package io.github.freecad1211.enchantment_unlimit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnchantCommand implements CommandExecutor, TabCompleter {

    private final EnchantmentUnlimit plugin;

    public EnchantCommand(EnchantmentUnlimit plugin) {
        this.plugin = plugin;
    }

    // /unlimitenchant <닉네임> <인챈트> <레벨>
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("enchantment_unlimit.admin")) {
            sender.sendMessage(ChatColor.RED + "이 명령어를 사용할 권한이 없습니다.");
            return true;
        }

        if (args.length != 3) {
            sender.sendMessage(ChatColor.YELLOW + "사용법: /unlimitenchant <닉네임> <인챈트> <레벨>");
            sender.sendMessage(ChatColor.YELLOW + "예시: /unlimitenchant Steve sharpness 10");
            return true;
        }

        // 1. 플레이어 확인
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "플레이어 '" + args[0] + "'을(를) 찾을 수 없습니다.");
            return true;
        }

        // 2. 인챈트 확인
        Enchantment enchant = resolveEnchantment(args[1]);
        if (enchant == null) {
            sender.sendMessage(ChatColor.RED + "인챈트 '" + args[1] + "'을(를) 찾을 수 없습니다.");
            sender.sendMessage(ChatColor.YELLOW + "예시: sharpness, protection, unbreaking, fortune ...");
            return true;
        }

        // 3. 레벨 파싱
        int level;
        try {
            level = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "레벨은 숫자로 입력해주세요.");
            return true;
        }

        // 4. 레벨 범위 확인 (1 ~ config 최대 레벨)
        if (level < 1) {
            sender.sendMessage(ChatColor.RED + "레벨은 1 이상이어야 합니다.");
            return true;
        }

        int maxLevel = getMaxLevel(enchant);
        if (level > maxLevel) {
            sender.sendMessage(ChatColor.RED + "'" + args[1] + "' 인챈트의 최대 레벨은 " + maxLevel + " 입니다.");
            return true;
        }

        // 5. 손에 든 아이템에 인챈트 적용
        ItemStack item = target.getInventory().getItemInMainHand();
        if (item == null || item.getType().isAir()) {
            sender.sendMessage(ChatColor.RED + target.getName() + " 님이 아이템을 들고 있지 않습니다.");
            return true;
        }

        item.addUnsafeEnchantment(enchant, level);

        // 6. 결과 메시지
        String enchantName = enchant.getKey().getKey();
        sender.sendMessage(ChatColor.GREEN + target.getName() + " 님의 아이템에 "
                + ChatColor.AQUA + enchantName + " " + toRoman(level)
                + ChatColor.GREEN + " 인챈트를 적용했습니다.");
        target.sendMessage(ChatColor.GOLD + "당신의 아이템에 "
                + ChatColor.AQUA + enchantName + " " + toRoman(level)
                + ChatColor.GOLD + " 인챈트가 적용되었습니다!");

        return true;
    }

    // 인챈트 이름 -> Enchantment 변환 (대소문자 무시)
    private Enchantment resolveEnchantment(String name) {
        // minecraft:sharpness 형태도 지원
        if (!name.contains(":")) {
            name = "minecraft:" + name.toLowerCase();
        }
        NamespacedKey key = NamespacedKey.fromString(name.toLowerCase());
        if (key == null) return null;
        return Enchantment.getByKey(key);
    }

    // config의 max-levels 적용, 없으면 바닐라 최대 레벨 사용
    private int getMaxLevel(Enchantment enchant) {
        Map<String, Integer> customMaxLevels = plugin.getCustomMaxLevels();
        String enchantName = enchant.getKey().getKey().toUpperCase();
        if (customMaxLevels.containsKey(enchantName)) {
            return customMaxLevels.get(enchantName);
        }
        // config에 없으면 바닐라 최대 레벨 기준으로 10배까지 허용 (원하면 조정 가능)
        return Math.max(enchant.getMaxLevel(), enchant.getMaxLevel() * 10);
    }

    // 레벨 숫자를 로마자로 변환 (1~10 지원, 초과 시 숫자 그대로)
    private String toRoman(int level) {
        String[] romans = {"I","II","III","IV","V","VI","VII","VIII","IX","X"};
        if (level >= 1 && level <= 10) return romans[level - 1];
        return String.valueOf(level);
    }

    // 탭 자동완성
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            // 온라인 플레이어 목록
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (args.length == 2) {
            // 인챈트 목록
            List<String> enchants = Arrays.stream(Enchantment.values())
                    .map(e -> e.getKey().getKey())
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
            return enchants;
        }
        if (args.length == 3) {
            // 레벨 힌트 (1~최대)
            Enchantment enchant = resolveEnchantment(args[1]);
            if (enchant != null) {
                int max = getMaxLevel(enchant);
                List<String> levels = new ArrayList<>();
                for (int i = 1; i <= Math.min(max, 10); i++) {
                    levels.add(String.valueOf(i));
                }
                return levels;
            }
        }
        return new ArrayList<>();
    }
}
