package org.gronia.plugin.craft;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.gronia.plugin.NumberMap;
import org.gronia.plugin.SubCommandExecutor;
import org.gronia.plugin.storage.StoragePlugin;

import java.util.List;
import java.util.Map;


public class CraftCommand extends SubCommandExecutor<CraftPlugin> {
    public CraftCommand(CraftPlugin plugin) {
        super(plugin);
    }

    @Override
    public boolean onSubCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            return this.warnUser(player);
        }

        String command = args[0];

        if (command.equalsIgnoreCase("downgrade")) {
            StoragePlugin plugin = this.getPlugin().getSubPlugin(StoragePlugin.class);

            Map<String, Integer> loans = plugin.getAPI().getLoans();
            NumberMap<String> changes = new NumberMap<>();

            List<String> blockedMaterials = this.getPlugin().getConfig().getStringList("blocked_materials");
            ConfigurationSection forcedRecipes = this.getPlugin().getConfig().getConfigurationSection("forced_recipes");
            assert forcedRecipes != null;

            for (Map.Entry<String, Integer> loan : loans.entrySet()) {
                String materialName = loan.getKey();
                if (blockedMaterials.contains(materialName)) {
                    continue;
                }

                int count = -loan.getValue();
                String recipeName = forcedRecipes.getString(materialName, null);
                if (recipeName != null) {
                    Recipe recipe = this.getPlugin().getServer().getRecipe(NamespacedKey.minecraft(recipeName));
                    if (recipe instanceof ShapelessRecipe) {
                        this.getPlugin().applyRecipe((ShapelessRecipe) recipe, materialName, count, changes);
                    } else if (recipe instanceof ShapedRecipe) {
                        this.getPlugin().applyRecipe((ShapedRecipe) recipe, materialName, count, changes);
                    }
                    continue;
                }

                Material material = Material.valueOf(materialName.toUpperCase());

                List<Recipe> recipes = this.getPlugin().getServer().getRecipesFor(new ItemStack(material));
                int recipeCount = 0;
                ShapedRecipe shapedRecipe = null;
                ShapelessRecipe shapelessRecipe = null;

                for (Recipe recipe : recipes) {
                    if (recipe instanceof ShapedRecipe) {
                        shapedRecipe = (ShapedRecipe) recipe;
                        recipeCount++;
                    }

                    if (recipe instanceof ShapelessRecipe) {
                        shapelessRecipe = (ShapelessRecipe) recipe;
                        recipeCount++;
                    }
                }

                if (recipeCount == 1) {
                    if (shapedRecipe != null) {
                        this.getPlugin().applyRecipe(shapedRecipe, materialName, count, changes);
                    } else {
                        this.getPlugin().applyRecipe(shapelessRecipe, materialName, count, changes);
                    }
                } else if (recipeCount != 0) {
                    player.sendMessage("[Craft] " + ChatColor.WHITE + "Multi recipe " + ChatColor.GREEN + materialName);
                }
            }

            plugin.getAPI().applyStackable(player.getName(), changes);

            return true;
        }

        if (command.equalsIgnoreCase("recipe")) {
            if (args.length < 2) {
                return this.warnUser(player);
            }

            String materialName = args[1].toLowerCase();
            Material material;
            try {
                material = Material.valueOf(materialName.toUpperCase());
            } catch (IllegalArgumentException e) {
                return this.warnUser(player);
            }

            List<Recipe> recipes = this.getPlugin().getServer().getRecipesFor(new ItemStack(material));
            for (Recipe recipe : recipes) {
                if (recipe instanceof ShapedRecipe) {
                    String recipeName = ((ShapedRecipe) recipe).getKey().getKey();
                    player.sendMessage("[Craft] " + ChatColor.WHITE + "Recipe " + ChatColor.GREEN + recipeName + ChatColor.WHITE + ".");
                }

                if (recipe instanceof ShapelessRecipe) {
                    String recipeName = ((ShapelessRecipe) recipe).getKey().getKey();
                    player.sendMessage("[Craft] " + ChatColor.WHITE + "Recipe " + ChatColor.GREEN + recipeName + ChatColor.WHITE + ".");
                }
            }
        }

        if (command.equalsIgnoreCase("auto")) {
            if (args.length < 2) {
                return this.warnUser(player);
            }

            String subCommand = args[1].toLowerCase();
            if (subCommand.equals("add")) {
                if (args.length < 4) {
                    return this.warnUser(player);
                }

                String materialName = args[2].toLowerCase();
                try {
                    Material.valueOf(materialName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return this.warnUser(player);
                }

                String recipeName = args[3].toLowerCase();
                Recipe recipe = this.getPlugin().getServer().getRecipe(NamespacedKey.minecraft(recipeName));
                if (recipe == null) {
                    return this.warnUser(player);
                }

                ConfigurationSection autoCrafts = this.getPlugin().getConfig().getConfigurationSection("auto");
                assert autoCrafts != null;

                autoCrafts.set(materialName, recipeName);
                this.getPlugin().saveConfig();

                return true;
            }

            if (subCommand.equals("remove")) {
                if (args.length < 3) {
                    return this.warnUser(player);
                }
                String materialName = args[2].toLowerCase();
                try {
                    Material.valueOf(materialName.toUpperCase());
                } catch (IllegalArgumentException e) {
                    return this.warnUser(player);
                }

                ConfigurationSection autoCrafts = this.getPlugin().getConfig().getConfigurationSection("auto");
                assert autoCrafts != null;

                autoCrafts.set(materialName, null);
                this.getPlugin().saveConfig();

                return true;
            }

            if (subCommand.equals("list")) {
                ConfigurationSection autoCrafts = this.getPlugin().getConfig().getConfigurationSection("auto");
                assert autoCrafts != null;
                for (String material : autoCrafts.getKeys(false)) {
                    player.sendMessage("[Craft] Auto Craft" + ChatColor.GOLD + material);
                }
                return true;
            }

            return false;
        }

        if (command.equalsIgnoreCase("select")) {
            if (args.length < 3) {
                return this.warnUser(player);
            }

            String materialName = args[1].toLowerCase();
            try {
                Material.valueOf(materialName.toUpperCase());
            } catch (IllegalArgumentException e) {
                return this.warnUser(player);
            }
            String recipeName = args[2].toLowerCase();
            Recipe recipe = this.getPlugin().getServer().getRecipe(NamespacedKey.minecraft(recipeName));
            if (recipe == null) {
                return this.warnUser(player);
            }

            ConfigurationSection forcedRecipes = this.getPlugin().getConfig().getConfigurationSection("forced_recipes");
            assert forcedRecipes != null;

            forcedRecipes.set(materialName, recipeName);
            this.getPlugin().saveConfig();

            return true;
        }

        if (command.equalsIgnoreCase("block")) {
            if (args.length < 2) {
                return this.warnUser(player);
            }

            String materialName = args[1].toLowerCase();
            try {
                Material.valueOf(materialName.toUpperCase());
            } catch (IllegalArgumentException e) {
                return this.warnUser(player);
            }

            List<String> blockedMaterials = this.getPlugin().getConfig().getStringList("blocked_materials");
            if (blockedMaterials.contains(materialName)) {
                return true;
            }

            blockedMaterials.add(materialName);
            this.getPlugin().getConfig().set("blocked_materials", blockedMaterials);

            this.getPlugin().saveConfig();

            return true;
        }

        return false;
    }

    public boolean warnUser(Player player) {
        player.sendMessage("[Craft] " + ChatColor.WHITE + "Type " + ChatColor.GREEN + "/help craft" + ChatColor.WHITE + " for help");
        return true;
    }
}
