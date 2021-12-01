package org.gronia.menu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.gronia.plugin.Gronia;
import xyz.janboerman.guilib.api.GuiInventoryHolder;
import xyz.janboerman.guilib.api.GuiListener;
import xyz.janboerman.guilib.api.ItemBuilder;
import xyz.janboerman.guilib.api.menu.ItemButton;
import xyz.janboerman.guilib.api.menu.MenuButton;
import xyz.janboerman.guilib.api.menu.MenuHolder;
import xyz.janboerman.guilib.api.menu.RedirectButton;
import xyz.janboerman.guilib.util.CachedSupplier;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class CustomPageMenu extends MenuHolder<Gronia> implements MenuHolder.ButtonAddCallback, MenuHolder.ButtonRemoveCallback {
    private static final ItemStack DEFAULT_PREVIOUS_PAGE_BUTTON;
    private static final ItemStack DEFAULT_NEXT_PAGE_BUTTON;
    private final String title;
    private final GuiInventoryHolder<Gronia> myPage;
    protected final int previousButtonIndex;
    protected final int nextButtonIndex;
    protected final ItemStack previousPageButton;
    protected final ItemStack nextPageButton;
    private final Supplier<CustomPageMenu> previousPageSupplier;
    private Supplier<CustomPageMenu> nextPageSupplier;
    private CustomPageMenu renderedPage;
    private CustomPageMenu hostingPage;
    private ItemStack renderedNextStack;
    private ItemStack renderedPreviousStack;
    private int renderedNextIndex;
    private int renderedPreviousIndex;
    private boolean weHaveBeenOpened;

    public CustomPageMenu(Gronia plugin, GuiInventoryHolder<Gronia> page, Supplier<CustomPageMenu> previous, Supplier<CustomPageMenu> next) throws IllegalArgumentException {
        this(plugin, page, previous, next, DEFAULT_PREVIOUS_PAGE_BUTTON.clone(), DEFAULT_NEXT_PAGE_BUTTON.clone());
    }

    public CustomPageMenu(GuiListener guiListener, Gronia plugin, GuiInventoryHolder<Gronia> page, Supplier<CustomPageMenu> previous, Supplier<CustomPageMenu> next) throws IllegalArgumentException {
        this(guiListener, plugin, page, previous, next, DEFAULT_PREVIOUS_PAGE_BUTTON.clone(), DEFAULT_NEXT_PAGE_BUTTON.clone());
    }

    public CustomPageMenu(Gronia plugin, GuiInventoryHolder<Gronia> page, String title, Supplier<CustomPageMenu> previous, Supplier<CustomPageMenu> next) throws IllegalArgumentException {
        this(plugin, page, title, previous, next, DEFAULT_PREVIOUS_PAGE_BUTTON.clone(), DEFAULT_NEXT_PAGE_BUTTON.clone());
    }

    public CustomPageMenu(GuiListener guiListener, Gronia plugin, GuiInventoryHolder<Gronia> page, String title, Supplier<CustomPageMenu> previous, Supplier<CustomPageMenu> next) throws IllegalArgumentException {
        this(guiListener, plugin, page, title, previous, next, DEFAULT_PREVIOUS_PAGE_BUTTON.clone(), DEFAULT_NEXT_PAGE_BUTTON.clone());
    }

    public CustomPageMenu(Gronia plugin, GuiInventoryHolder<Gronia> page, Supplier<CustomPageMenu> previous, Supplier<CustomPageMenu> next, ItemStack previousPageButton, ItemStack nextPageButton) throws IllegalArgumentException {
        this(GuiListener.getInstance(), plugin, page, previous, next, previousPageButton, nextPageButton);
    }

    public CustomPageMenu(GuiListener guiListener, Gronia plugin, GuiInventoryHolder<Gronia> page, Supplier<CustomPageMenu> previous, Supplier<CustomPageMenu> next, ItemStack previousPageButton, ItemStack nextPageButton) throws IllegalArgumentException {
        super(guiListener, plugin, calculateInnerPageSize(page) + 9);
        this.renderedPage = this;
        this.hostingPage = this;
        this.myPage = page;
        this.previousButtonIndex = this.renderedPreviousIndex = calculateInnerPageSize(this.myPage);
        this.nextButtonIndex = this.renderedNextIndex = calculateInnerPageSize(this.myPage) + 8;
        this.previousPageSupplier = previous;
        this.nextPageSupplier = next;
        this.previousPageButton = this.renderedPreviousStack = previousPageButton;
        this.nextPageButton = this.renderedNextStack = nextPageButton;
        this.title = null;
        this.addButtonListeners();
    }

    public CustomPageMenu(Gronia plugin, GuiInventoryHolder<Gronia> page, String title, Supplier<CustomPageMenu> previous, Supplier<CustomPageMenu> next, ItemStack previousPageButton, ItemStack nextPageButton) throws IllegalArgumentException {
        this(GuiListener.getInstance(), plugin, page, title, previous, next, previousPageButton, nextPageButton);
    }

    public CustomPageMenu(GuiListener guiListener, Gronia plugin, GuiInventoryHolder<Gronia> page, String title, Supplier<CustomPageMenu> previous, Supplier<CustomPageMenu> next, ItemStack previousPageButton, ItemStack nextPageButton) throws IllegalArgumentException {
        super(guiListener, plugin, calculateInnerPageSize(page) + 9, title);
        this.renderedPage = this;
        this.hostingPage = this;
        this.myPage = page;
        this.previousButtonIndex = this.renderedPreviousIndex = calculateInnerPageSize(this.myPage);
        this.nextButtonIndex = this.renderedNextIndex = calculateInnerPageSize(this.myPage) + 8;
        this.previousPageSupplier = previous;
        this.nextPageSupplier = next;
        this.previousPageButton = this.renderedPreviousStack = previousPageButton;
        this.nextPageButton = this.renderedNextStack = nextPageButton;
        this.title = title;
        this.addButtonListeners();
    }

    protected boolean needsRedirects() {
        return this.getClass() != CustomPageMenu.class;
    }

    protected final CustomPageMenu getHostingPage() {
        return this.hostingPage;
    }

    protected final CustomPageMenu getRenderedPage() {
        return this.renderedPage;
    }

    protected GuiInventoryHolder<Gronia> getOwnedPage() {
        return this.myPage;
    }

    public GuiInventoryHolder<Gronia> getPage() {
        return this.getRenderedPage().getOwnedPage();
    }

    public int getPageSize() {
        return this.getPage().getInventory().getSize();
    }

    public boolean hasNextPage() {
        return this.getNextStorageMenu().isPresent();
    }

    public boolean hasPreviousPage() {
        return this.getPreviousStorageMenu().isPresent();
    }

    public Optional<? extends Supplier<? extends CustomPageMenu>> getNextStorageMenu() {
        return Optional.ofNullable(this.nextPageSupplier);
    }

    public Optional<? extends Supplier<? extends CustomPageMenu>> getPreviousStorageMenu() {
        return Optional.ofNullable(this.previousPageSupplier);
    }

    public static CustomPageMenu create(Gronia plugin, Iterator<? extends GuiInventoryHolder<Gronia>> pageSupplier) {
        return create(plugin, Objects.requireNonNull(pageSupplier, "PageSupplier cannot be null"), DEFAULT_PREVIOUS_PAGE_BUTTON.clone(), DEFAULT_NEXT_PAGE_BUTTON.clone());
    }

    public static CustomPageMenu create(Gronia plugin, String title, Iterator<? extends GuiInventoryHolder<Gronia>> pageSupplier) {
        return create(plugin, title, Objects.requireNonNull(pageSupplier, "PageSupplier cannot be null"), DEFAULT_PREVIOUS_PAGE_BUTTON.clone(), DEFAULT_NEXT_PAGE_BUTTON.clone());
    }

    public static CustomPageMenu create(Gronia plugin, Iterator<? extends GuiInventoryHolder<Gronia>> pageSupplier, ItemStack previousPageButton, ItemStack nextPageButton) {
        return create(plugin, Objects.requireNonNull(pageSupplier, "PageSupplier cannot be null"), null, previousPageButton, nextPageButton);
    }

    public static CustomPageMenu create(Gronia plugin, String title, Iterator<? extends GuiInventoryHolder<Gronia>> pageSupplier, ItemStack previousPageButton, ItemStack nextPageButton) {
        return create(plugin, title, Objects.requireNonNull(pageSupplier, "PageSupplier cannot be null"), null, previousPageButton, nextPageButton);
    }

    private static CustomPageMenu create(Gronia plugin, Iterator<? extends GuiInventoryHolder<Gronia>> nextSupplier, Supplier<CustomPageMenu> previous, ItemStack previousPageButton, ItemStack nextPageButton) {
        var page = nextSupplier.next();
        var pageMenu = new CustomPageMenu(plugin, page, previous, null, previousPageButton, nextPageButton);
        if (nextSupplier.hasNext()) {
            pageMenu.nextPageSupplier = new CachedSupplier<>(() -> create(plugin, nextSupplier, () -> pageMenu, previousPageButton == null ? null : previousPageButton.clone(), nextPageButton == null ? null : nextPageButton.clone()));
        }

        return pageMenu;
    }

    private static CustomPageMenu create(Gronia plugin, String title, Iterator<? extends GuiInventoryHolder<Gronia>> nextSupplier, Supplier<CustomPageMenu> previous, ItemStack previousPageButton, ItemStack nextPageButton) {
        var page = nextSupplier.next();
        var pageMenu = new CustomPageMenu(plugin, page, previous, null, previousPageButton, nextPageButton);
        if (nextSupplier.hasNext()) {
            pageMenu.nextPageSupplier = new CachedSupplier<>(() -> create(plugin, title, nextSupplier, () -> pageMenu, previousPageButton == null ? null : previousPageButton.clone(), nextPageButton == null ? null : nextPageButton.clone()));
        }

        return pageMenu;
    }

    public void updateView() {
        for (int index = 0; index < this.getPageSize(); ++index) {
            this.getInventory().setItem(index, this.getPage().getInventory().getItem(index));
        }

    }

    private void addButtonListeners() {
        GuiInventoryHolder<Gronia> page = this.getPage();
        if (page instanceof MenuHolder menuPage) {
            menuPage.addButtonAddCallback(this);
            menuPage.addButtonRemoveCallback(this);
        }

    }

    private void removeButtonListeners() {
        GuiInventoryHolder<Gronia> page = this.getPage();
        if (page instanceof MenuHolder menuPage) {
            menuPage.removeButtonAddCallback(this);
            menuPage.removeButtonRemoveCallback(this);
        }

    }

    public boolean onAdd(int slot, MenuButton button) {
        this.getPage().getInventory().setItem(slot, button.getIcon());
        return true;
    }

    public boolean onRemove(int slot, MenuButton button) {
        this.getPage().getInventory().setItem(slot, (ItemStack) null);
        return true;
    }

    public void resetButtons() {
        int currentInvSize = this.getPageSize();
        this.getRenderedPage().getNextStorageMenu().ifPresentOrElse((next) -> {
            var toNextPageButton = new ItemButton<MenuHolder<Gronia>>(this.renderedNextStack) {
                public void onClick(MenuHolder holder, InventoryClickEvent event) {
                    var nextStorageMenu = (CustomPageMenu) next.get();
                    GuiInventoryHolder<?> nextPage = nextStorageMenu.getOwnedPage();
                    Inventory nextInventory = nextPage.getInventory();
                    if (!CustomPageMenu.this.needsRedirects() && nextInventory.getSize() == currentInvSize && Objects.equals(nextStorageMenu.title, CustomPageMenu.this.title)) {
                        InventoryCloseEvent proxyCloseEvent = new InventoryCloseEvent(event.getView());
                        CustomPageMenu.this.getPlugin().getServer().getPluginManager().callEvent(proxyCloseEvent);
                        CustomPageMenu.this.removeButtonListeners();
                        CustomPageMenu.this.renderedPage = nextStorageMenu;
                        CustomPageMenu.this.renderedPage.hostingPage = CustomPageMenu.this;
                        CustomPageMenu.this.addButtonListeners();
                        CustomPageMenu.this.renderedPreviousIndex = CustomPageMenu.this.renderedPage.previousButtonIndex;
                        CustomPageMenu.this.renderedNextIndex = CustomPageMenu.this.renderedPage.nextButtonIndex;
                        CustomPageMenu.this.renderedPreviousStack = CustomPageMenu.this.renderedPage.previousPageButton;
                        CustomPageMenu.this.renderedNextStack = CustomPageMenu.this.renderedPage.nextPageButton;
                        CustomPageMenu.this.weHaveBeenOpened = false;
                        InventoryOpenEvent proxyOpenEvent = new InventoryOpenEvent(event.getView());
                        CustomPageMenu.this.getPlugin().getServer().getPluginManager().callEvent(proxyOpenEvent);
                        CustomPageMenu.this.updateView();
                    } else {
                        holder.getPlugin().getServer().getScheduler().runTask(holder.getPlugin(), () -> {
                            event.getView().close();
                            event.getWhoClicked().openInventory(nextStorageMenu.getInventory());
                        });
                    }

                }
            };
            this.setButton(this.renderedNextIndex, toNextPageButton);
        }, () -> {
            this.unsetButton(this.renderedNextIndex);
        });
        this.getRenderedPage().getPreviousStorageMenu().ifPresentOrElse((previous) -> {
            var toPreviousPageButton = new ItemButton<MenuHolder<Gronia>>(this.renderedPreviousStack) {
                public void onClick(MenuHolder holder, InventoryClickEvent event) {
                    var previousStorageMenu = (CustomPageMenu) previous.get();
                    GuiInventoryHolder<?> previousPage = previousStorageMenu.getOwnedPage();
                    Inventory previousInventory = previousPage.getInventory();
                    if (!CustomPageMenu.this.needsRedirects() && previousInventory.getSize() == currentInvSize && Objects.equals(previousStorageMenu.title, CustomPageMenu.this.title)) {
                        InventoryCloseEvent proxyCloseEvent = new InventoryCloseEvent(event.getView());
                        CustomPageMenu.this.getPlugin().getServer().getPluginManager().callEvent(proxyCloseEvent);
                        CustomPageMenu.this.removeButtonListeners();
                        CustomPageMenu.this.renderedPage = previousStorageMenu;
                        CustomPageMenu.this.renderedPage.hostingPage = CustomPageMenu.this;
                        CustomPageMenu.this.addButtonListeners();
                        CustomPageMenu.this.renderedPreviousIndex = CustomPageMenu.this.renderedPage.previousButtonIndex;
                        CustomPageMenu.this.renderedNextIndex = CustomPageMenu.this.renderedPage.nextButtonIndex;
                        CustomPageMenu.this.renderedPreviousStack = CustomPageMenu.this.renderedPage.previousPageButton;
                        CustomPageMenu.this.renderedNextStack = CustomPageMenu.this.renderedPage.nextPageButton;
                        CustomPageMenu.this.weHaveBeenOpened = false;
                        InventoryOpenEvent proxyOpenEvent = new InventoryOpenEvent(event.getView());
                        CustomPageMenu.this.getPlugin().getServer().getPluginManager().callEvent(proxyOpenEvent);
                        CustomPageMenu.this.updateView();
                    } else {
                        holder.getPlugin().getServer().getScheduler().runTask(holder.getPlugin(), () -> {
                            event.getView().close();
                            event.getWhoClicked().openInventory(previousStorageMenu.getInventory());
                        });
                    }

                }
            };
            this.setButton(this.renderedPreviousIndex, toPreviousPageButton);
        }, () -> {
            this.unsetButton(this.renderedPreviousIndex);
        });
    }

    public void onOpen(InventoryOpenEvent openEvent) {
        InventoryOpenEvent proxyEvent = new InventoryOpenEvent(new CustomPageMenu.ProxyView(openEvent.getView()));
        this.getPlugin().getServer().getPluginManager().callEvent(proxyEvent);
        if (!this.weHaveBeenOpened) {
            this.resetButtons();
            this.weHaveBeenOpened = true;
        }

        this.updateView();
        this.addButtonListeners();
    }

    public void onClose(InventoryCloseEvent closeEvent) {
        InventoryCloseEvent proxyEvent = new InventoryCloseEvent(new CustomPageMenu.ProxyView(closeEvent.getView()));
        this.getPlugin().getServer().getPluginManager().callEvent(proxyEvent);
        this.updateView();
        this.removeButtonListeners();
    }

    public void onClick(InventoryClickEvent clickEvent) {
        int rawSlot = clickEvent.getRawSlot();
        var currentPage = this.getPage();
        int myPageSize = this.getPageSize();
        int topInventorySize = clickEvent.getView().getTopInventory().getSize();
        boolean myButtonRowIsClicked = topInventorySize - 9 <= rawSlot && rawSlot < topInventorySize;
        if (myButtonRowIsClicked) {
            super.onClick(clickEvent);
        } else {
            InventoryView view = clickEvent.getView();
            InventoryView proxyView = new CustomPageMenu.ProxyView(view);
            InventoryType.SlotType slotType = clickEvent.getSlotType();
            InventoryType.SlotType proxySlotType;
            if (slotType != InventoryType.SlotType.OUTSIDE && slotType != InventoryType.SlotType.QUICKBAR) {
                proxySlotType = InventoryType.SlotType.CONTAINER;
            } else {
                proxySlotType = slotType;
            }

            int proxyRawSlot = getClickedInventory(clickEvent) == view.getBottomInventory() ? rawSlot - 9 : rawSlot;
            InventoryClickEvent proxyEvent = new InventoryClickEvent(proxyView, proxySlotType, proxyRawSlot, clickEvent.getClick(), clickEvent.getAction(), clickEvent.getHotbarButton());
            MenuButton<?> button;
            if (rawSlot < myPageSize && currentPage instanceof MenuHolder currentHolder && (button = ((MenuHolder<?>) currentPage).getButton(rawSlot)) != null && button instanceof RedirectButton redirectButton) {
                Inventory target = redirectButton.to(currentHolder, proxyEvent);
                GuiInventoryHolder<?> page = this.guiListener.getHolder(target);
                if (target.getSize() < 45) {
                    if (page == null) {
                        page = new MenuHolder<>(this.guiListener, this.getPlugin(), target) {
                            public void onClick(InventoryClickEvent event) {
                                if (event.getResult() == Event.Result.DENY) {
                                    event.setCancelled(false);
                                }

                                super.onClick(event);
                            }

                            public void onDrag(InventoryDragEvent event) {
                                if (event.getResult() == Event.Result.DENY) {
                                    event.setCancelled(false);
                                }

                                super.onDrag(event);
                            }
                        };
                    }

                    CustomPageMenu pageMenu = new CustomPageMenu(this.getPlugin(), (GuiInventoryHolder) page, view.getTitle(), this.previousPageSupplier, this.nextPageSupplier);
                    target = pageMenu.getInventory();
                }

                Inventory finalTarget = target;
                this.getPlugin().getServer().getScheduler().runTask(this.getPlugin(), () -> {
                    clickEvent.getWhoClicked().closeInventory();
                    clickEvent.getWhoClicked().openInventory(finalTarget);
                });
            } else {
                this.getPlugin().getServer().getPluginManager().callEvent(proxyEvent);
                clickEvent.setCancelled(proxyEvent.isCancelled());
            }

            this.updateView();
        }

    }

    public void onDrag(InventoryDragEvent dragEvent) {
        dragEvent.setCancelled(false);
        InventoryView view = dragEvent.getView();
        InventoryView proxyView = new CustomPageMenu.ProxyView(view);
        ItemStack newCursor = dragEvent.getCursor();
        ItemStack oldCursor = dragEvent.getOldCursor();
        boolean isRightClick = dragEvent.getType() == DragType.SINGLE;
        Map<Integer, ItemStack> newItems = dragEvent.getNewItems();
        int myPageSize = this.getPage().getInventory().getSize();
        if (newItems.keySet().stream().anyMatch((i) -> i >= myPageSize && i < myPageSize + 9)) {
            dragEvent.setCancelled(true);
        } else {
            Map<Integer, ItemStack> proxyItems = newItems.entrySet().stream().map((entry) -> {
                Integer slot = entry.getKey();
                ItemStack item = entry.getValue();
                if (slot > myPageSize) {
                    slot = slot - 9;
                }

                return Map.entry(slot, item);
            }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            InventoryDragEvent proxyEvent = new InventoryDragEvent(proxyView, newCursor, oldCursor, isRightClick, proxyItems);
            this.getPlugin().getServer().getPluginManager().callEvent(proxyEvent);
            dragEvent.setCursor(proxyEvent.getCursor());
            dragEvent.setResult(proxyEvent.getResult());
            this.getPlugin().getServer().getScheduler().runTask(this.getPlugin(), () -> {
                for (int i = 0; i < this.getPageSize(); ++i) {
                    Map<Integer, ItemStack> proxyNewItems = proxyEvent.getNewItems();
                    ItemStack oldItem = this.getPage().getInventory().getItem(i);
                    ItemStack addItem = (ItemStack) proxyNewItems.get(i);
                    if (addItem != null) {
                        ItemStack setItem;
                        if (oldItem == null) {
                            setItem = addItem;
                        } else {
                            setItem = oldItem;
                            oldItem.setAmount(oldItem.getAmount() + addItem.getAmount());
                        }

                        this.getPage().getInventory().setItem(i, setItem);
                    }
                }

            });
        }

        this.updateView();
    }

    private static int calculateInnerPageSize(GuiInventoryHolder<?> guiInventoryHolder) {
        int containedSize = guiInventoryHolder.getInventory().getSize();
        if (containedSize <= 0) {
            throw new IllegalArgumentException("Page cannot have a size of 0 or below");
        } else if (containedSize <= 45) {
            int remainder = containedSize % 9;
            return remainder == 0 ? containedSize : containedSize + (9 - remainder);
        } else {
            throw new IllegalArgumentException("The page cannot be larger than 45 slots");
        }
    }

    static {
        DEFAULT_PREVIOUS_PAGE_BUTTON = createPreviousButton();
        DEFAULT_NEXT_PAGE_BUTTON = createNextButton();
    }

    private class ProxyView extends InventoryView {
        private final InventoryView original;

        private ProxyView(InventoryView from) {
            this.original = from;
        }

        public Inventory getTopInventory() {
            return CustomPageMenu.this.getPage().getInventory();
        }

        public Inventory getBottomInventory() {
            return this.original.getBottomInventory();
        }

        public HumanEntity getPlayer() {
            return this.original.getPlayer();
        }

        public InventoryType getType() {
            return InventoryType.CHEST;
        }

        public String getTitle() {
            return this.original.getTitle();
        }
    }

    private static ItemStack createNextButton() {
        var nextPageButton = new ItemStack(Material.PLAYER_HEAD);
        Bukkit.getUnsafe().modifyItemStack(
                nextPageButton,
                "{SkullOwner:{Id:[I;-196519489,2140293705,-1793618238,-806686714],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWE5MmFkNDU2Zjc2ZWM3Y2FhMzU5NTkyMmQ1ZmNjMzhkY2E5MjhkYzY3MTVmNzUyZTc0YzhkZGRlMzQ0ZSJ9fX0=\"}]}}}"
        );

        return new ItemBuilder(nextPageButton).name("Next Page").build();
    }

    private static ItemStack createPreviousButton() {
        var previousPageButton = new ItemStack(Material.PLAYER_HEAD);
        Bukkit.getUnsafe().modifyItemStack(
                previousPageButton,
                "{SkullOwner:{Id:[I;382788716,-516730913,-1368080864,-1042777403],Properties:{textures:[{Value:\"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGZhNjA1ZTI1ZjRmYzJjZWE1YTc2NmQ3OWE4YmZhMjkwMzEzZTQ1ZDhmNWU5NTdkOTU4YTBmMzNmY2IxNiJ9fX0=\"}]}}}"
        );

        return new ItemBuilder(previousPageButton).name("Previous Page").build();
    }
}
