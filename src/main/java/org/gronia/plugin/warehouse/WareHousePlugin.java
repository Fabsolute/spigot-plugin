package org.gronia.plugin.warehouse;

import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.configuration.file.YamlRepresenter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.*;
import org.gronia.plugin.sack.SackPlugin;
import org.gronia.utils.NumberMap;
import org.gronia.utils.configuration.CaseMemoryConfiguration;
import org.gronia.utils.configuration.InventoryMysqlConfiguration;
import org.gronia.utils.configuration.MysqlConfiguration;
import org.gronia.utils.pair.Pair2;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class WareHousePlugin extends SubPlugin<WareHousePlugin> {

    public static final Yaml yaml;

    static {
        var loaderOptions = new LoaderOptions();
        loaderOptions.setMaxAliasesForCollections(2147483647);

        var yamlOptions = new DumperOptions();
        var yamlRepresenter = new YamlRepresenter();
        yaml = new Yaml(new YamlConstructor(), yamlRepresenter, yamlOptions, loaderOptions);
    }

    private InventoryMysqlConfiguration wareHouseTally;
    public final Map<Inventory, Pair2<String, String>> inventoryMap = new HashMap<>();

    public WareHousePlugin(JavaPlugin plugin) {
        super(plugin);
        this.setPassword("1"); // todo remove
    }

    @Override
    public String getName() {
        return "warehouse";
    }

    @Override
    public SubListener<WareHousePlugin> getListener() {
        return new WareHouseListener(this);
    }

    @Override
    public SubCommandExecutor<WareHousePlugin> getExecutor() {
        return new WareHouseCommand(this);
    }

    @Override
    public SubTabCompleter<WareHousePlugin> getTabCompleter() {
        return null;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        this.getServer().getScheduler().runTaskTimerAsynchronously(this.getPlugin(), this::saveConfig, 1200, 1200);
    }

    public InventoryMysqlConfiguration getWareHouseTally() {
        if (wareHouseTally == null) {
            this.wareHouseTally = MysqlConfiguration.loadConfiguration(InventoryMysqlConfiguration.class, "warehouse_tally");
        }

        return this.wareHouseTally;
    }

    public void takeItem(final Player player, String materialName, int count) {
        var stack = ItemRegistry.createItem(materialName);
        stack.setAmount(count);

        this.getSubPlugin(SackPlugin.class).getUtils().pickItemToPlayer(player, stack, true);

        Map<ItemStack, Integer> changes = new HashMap<>(1);
        changes.put(stack, -count);
        this.applyStackable(player.getName(), changes);
    }

    public void applyStackable(final String name, Map<ItemStack, Integer> changes) {
        this.applyStackable(name, changes, new HashMap<>());
    }

    public void applyStackable(final String name, Map<ItemStack, Integer> changes, Map<String, Integer> diffs) {
        InventoryMysqlConfiguration config = this.getWareHouseTally();

        for (var change : changes.entrySet()) {
            String materialName = ItemRegistry.getInternalName(change.getKey());
            String hash = createHash(change.getKey());
            int count = -change.getValue();

            var hashConfig = config.getConfig(name, materialName, hash);

            var totalCount = hashConfig.getInt("count", 0);
            int newCount = totalCount - count;

            if (!hashConfig.contains("item")) {
                var item = change.getKey().clone();
                item.setAmount(1);
                hashConfig.set("item", item);
            }

            hashConfig.set("count", newCount);
        }

        config.setDirty();

        List<Pair2<String, Boolean>> messages = new ArrayList<>();
        for (var diffEntry : diffs.entrySet()) {
            var diff = diffEntry.getValue();
            if (diff == 0) {
                continue;
            }

            var materialName = diffEntry.getKey();
            var newCount = this.getCount(name, materialName);

            messages.add(Pair2.of(ChatColor.DARK_PURPLE + "WareHouse " + ChatColor.RESET + name + " " + (diff > 0 ? "stored" : "took") + " " + ChatColor.GREEN + "" + Math.abs(diff) + " " + materialName + ChatColor.WHITE + ". New count is " + ChatColor.GOLD + newCount + ChatColor.WHITE + ".", diff <= 0));
        }

        this.sendMessages(messages, name);
    }

    public String createHash(ItemStack itemStack) {
        itemStack = itemStack.clone();
        itemStack.setAmount(1);

        var content = yaml.dump(itemStack);
        StringBuilder output = new StringBuilder();

        try {
            var m = MessageDigest.getInstance("MD5");
            m.reset();
            m.update(content.getBytes());
            var digest = m.digest();
            var bigInt = new BigInteger(1, digest);
            output.append(bigInt.toString(16));
            while (output.length() < 32) {
                output.insert(0, "0");
            }
        } catch (NoSuchAlgorithmException ignored) {
        }

        return output.toString();
    }

    public void sendMessages(List<Pair2<String, Boolean>> messages, String name) {
        if (messages.size() == 1) {
            this.getPlugin().getServer().broadcastMessage(messages.get(0).p1());
        } else if (messages.size() > 1) {
            var message = " did something in the storage.";
            if (messages.stream().allMatch(Pair2::p2)) {
                message = " took some things.";
            } else if (messages.stream().noneMatch(Pair2::p2)) {
                message = " stored some things.";
            }

            TextComponent component = new TextComponent(ChatColor.DARK_PURPLE + "WareHouse " + ChatColor.RESET + name + message + ChatColor.GREEN + ChatColor.BOLD + " HOVER");
            component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(String.join("\n", messages.stream().map(Pair2::p1).toList()))));
            this.getPlugin().getServer().spigot().broadcast(component);
            for (var msg : messages) {
                Bukkit.getLogger().log(Level.INFO, msg.p1());
            }
        }
    }

    private int getCount(String caseName, String materialName) {
        int output = 0;
        var config = this.getWareHouseTally().getConfig(caseName, materialName);
        for (var hash : config.getKeys(false)) {
            var hashConfig = config.getConfigurationSection(hash);
            assert hashConfig != null;
            output += hashConfig.getInt("count", 0);
        }

        return output;
    }

    @Override
    public void saveConfig() {
        try {
            if (this.wareHouseTally != null) {
                this.wareHouseTally.save();
            }
        } catch (SQLException ignored) {
        }

        super.saveConfig();
    }
}
