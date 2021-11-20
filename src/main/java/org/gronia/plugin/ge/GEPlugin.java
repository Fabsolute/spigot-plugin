package org.gronia.plugin.ge;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.gronia.plugin.*;
import xyz.janboerman.guilib.GuiLibrary;
import xyz.janboerman.guilib.api.GuiListener;
import xyz.janboerman.guilib.api.ItemBuilder;
import xyz.janboerman.guilib.api.mask.Mask;
import xyz.janboerman.guilib.api.mask.Pattern;
import xyz.janboerman.guilib.api.mask.patterns.BorderPattern;
import xyz.janboerman.guilib.api.menu.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GEPlugin extends SubPlugin<GEPlugin> {
    private MenuHolder<Gronia> menu1;
    private GuiListener guiListener;

    public GuiListener getGuiListener() {
        return guiListener;
    }

    public GEPlugin(JavaPlugin plugin) {
        super(plugin);
    }

    @Override
    public String getName() {
        return "ge";
    }

    @Override
    public SubListener<GEPlugin> getListener() {
        return null;
    }

    @Override
    public SubCommandExecutor<GEPlugin> getExecutor() {
        return new SubCommandExecutor<>(this) {
            @Override
            public boolean onSubCommand(CommandSender sender, Command cmd, String s, String[] args) {
                var player = (Player) sender;
                String command = args[0];
                switch (command.toLowerCase()) {
                    case "gui":
                        player.openInventory(menu1.getInventory());
                        return true;
                    case "pages":
                        var pageMenu = PageMenu.create(GEPlugin.this.getPlugin(), Stream.generate(() -> menu1).limit(5).iterator());
                        player.openInventory(pageMenu.getInventory());
                        return true;
                    case "freediamonds":
                        var menu = new MenuHolder<>(GEPlugin.this.getPlugin(), 45);
                        for (int slot = 0; slot < menu.getInventory().getSize(); slot++) {
                            menu.setButton(slot, new ClaimButton<>(new ItemStack(Material.DIAMOND, 64)));
                        }
                        player.openInventory(menu.getInventory());
                        return true;
                    case "claimallitems":
                        var mutableRewardsList = Arrays.stream(Material.values())
                                .filter(Material::isItem)
                                .map(ItemStack::new)
                                .collect(Collectors.toCollection(ArrayList::new));
                        ClaimItemsMenu claimItemsMenu = new ClaimItemsMenu(GEPlugin.this.getPlugin(), 45, mutableRewardsList);
                        player.openInventory(claimItemsMenu.getInventory());
                        return true;
                    case "dragpage":
                        DragPage dragPage = new DragPage(GEPlugin.this.getPlugin());
                        player.openInventory(dragPage.getInventory());
                        return true;
                    case "dragpages":
                        pageMenu = PageMenu.create(GEPlugin.this.getPlugin(), Stream.generate(() -> new DragPage(guiListener, GEPlugin.this.getPlugin())).iterator());
                        player.openInventory(pageMenu.getInventory());
                        return true;
                    case "border":
                        var maskDemo = new MenuHolder<>(GEPlugin.this.getPlugin(), 54);
                        Inventory maskInventory = maskDemo.getInventory();
                        BorderPattern borderPattern = Pattern.border(9, 6);
                        Mask<BorderPattern.Border, ItemStack> mask = Mask.ofMap(Map.of(
                                BorderPattern.Border.OUTER, new ItemStack(Material.BLACK_STAINED_GLASS_PANE),
                                BorderPattern.Border.INNER, new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE)));
                        Mask.applyInventory(mask, borderPattern, maskInventory);
                        player.openInventory(maskInventory);
                        return true;
                    case "animation":
                        AnimationDemo animationDemo = new AnimationDemo(GEPlugin.this.getPlugin());
                        player.openInventory(animationDemo.getInventory());
                        return true;
                    default:
                        return false;
                }
            }
        };
    }

    @Override
    public SubTabCompleter<GEPlugin> getTabCompleter() {
        return null;
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Plugin guiLib = getServer().getPluginManager().getPlugin("GuiLib");
        GuiLibrary guiLibrary = (GuiLibrary) guiLib;
        guiListener = guiLibrary.getGuiListener();
        assert HandlerList.getRegisteredListeners(guiLibrary).stream().anyMatch(regListener -> regListener.getListener() == guiListener) : "guiListener is not registered.";

        // inventory holder debug
        //new TestCommandExecutor(this);

        //basic usage
        menu1 = new MenuHolder<>(this.getPlugin(), 9, "Example Gui 1");
        MenuHolder<Gronia> menu2 = new MenuHolder<>(this.getPlugin(), InventoryType.HOPPER, "Example Gui 2");

        //menu 1 stuff
        menu1.setButton(0, new RedirectItemButton<>(new ItemStack(Material.PURPLE_GLAZED_TERRACOTTA), menu2::getInventory));
        menu1.setButton(8, new CloseButton<>());
        String permission = "foo.bar";
        menu1.setButton(4, new PermissionButton<>(
                permission,
                new ItemButton<>(new ItemStack(Material.GREEN_GLAZED_TERRACOTTA)) {
                    @Override
                    public void onClick(MenuHolder holder, InventoryClickEvent event) {
                        event.getWhoClicked().sendMessage("You have permission " + permission + ".");
                    }
                },
                humanEntity -> humanEntity.sendMessage("You don't have permission " + permission + ".")));

        //menu 2 stuff
        ItemStack onStack = new ItemBuilder(Material.STRUCTURE_VOID).name("Enabled").build();
        ItemStack offStack = new ItemBuilder(Material.BARRIER).name("Disabled").build();
        menu2.setButton(0, new ToggleButton<>(offStack) {
            @Override
            public void afterToggle(MenuHolder holder, InventoryClickEvent event) {
                event.getWhoClicked().sendMessage("Is the button enabled? " + (isEnabled() ? "yes" : "no"));
            }

            @Override
            public ItemStack updateIcon(MenuHolder menuHolder, InventoryClickEvent event) {
                return isEnabled() ? onStack : offStack;
            }
        });
        menu2.setButton(2, new BackButton<>(menu1::getInventory));
    }
}
