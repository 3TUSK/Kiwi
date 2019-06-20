# Kiwi

Make modding no longer cumbersome.

## Registration

``` java
@KiwiModule(modid = MyMod.MODID)
@KiwiModule.Group("buildingBlocks")
public class MyModule extends AbstractModule
{
    // Register a simple item. Kiwi will automatically register it
    public static final Item FIRST_ITEM = new Item(itemProp().rarity(Rarity.EPIC));

    // Register a simple block and its BlockItem
    public static final Block FIRST_BLOCK = new Block(blockProp(Material.WOOD));
}
```

## Plugin

``` java
// This module will be loaded only when `dependency` mod is loaded
@KiwiModule(modid = MyAddon.MODID, name = "test", dependency = "dependency")
@KiwiModule.Optional
public class MyAddon extends AbstractModule
{
    // This method is called in FMLCommonSetupEvent
    @Override
    public void init()
    {
        // Do something
    }
}
```
