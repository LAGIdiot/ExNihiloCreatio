package exnihilocreatio.compatibility.jei.burnout;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import exnihilocreatio.ExNihiloCreatio;
import exnihilocreatio.compatibility.jei.hammer.HammerRecipe;
import exnihilocreatio.compatibility.jei.hammer.HammerRecipeCategory;
import exnihilocreatio.registries.manager.ExNihiloRegistryManager;
import exnihilocreatio.registries.types.BurnOutReward;
import exnihilocreatio.registries.types.HammerReward;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.ITooltipCallback;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.IRecipeCategory;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public class BurnOutRecipeCategory implements IRecipeCategory<BurnOutRecipe> {
    public static final String UID = "exnihilocreatio:burnout";
    private static final ResourceLocation texture = new ResourceLocation(ExNihiloCreatio.MODID, "textures/gui/jei_burnout.png");

    private final IDrawableStatic background;
    private final IDrawableStatic slotHighlight;
    private boolean hasHighlight;
    private int highlightX;
    private int highlightY;

    public BurnOutRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(texture, 0, 0, 166, 128);
        this.slotHighlight = helper.createDrawable(texture, 166, 0, 18, 18);
    }

    @Override
    @Nonnull
    public String getUid() {
        return UID;
    }

    @Override
    @Nonnull
    public String getTitle() {
        return "Burn Out";
    }

    @Override
    @Nonnull
    public String getModName() {
        return ExNihiloCreatio.MODID;
    }

    @Override
    @Nonnull
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public void drawExtras(@Nonnull Minecraft minecraft) {
        if (hasHighlight) {
            slotHighlight.draw(minecraft, highlightX, highlightY);
        }
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout recipeLayout, @Nonnull BurnOutRecipe recipeWrapper, @Nonnull IIngredients ingredients) {
        recipeLayout.getItemStacks().init(0, true, 74, 9);
        recipeLayout.getItemStacks().set(0, ingredients.getInputs(ItemStack.class).get(0));

        IFocus<?> focus = recipeLayout.getFocus();

        if (focus != null) {
            hasHighlight = focus.getMode() == IFocus.Mode.OUTPUT;
        }

        int slotIndex = 2;
        for (int i = 0; i < recipeWrapper.getOutputs().size(); i++) {
            final int slotX = 2 + (i % 9 * 18);
            final int slotY = 36 + (i / 9 * 18);
            ItemStack outputStack = recipeWrapper.getOutputs().get(i);
            recipeLayout.getItemStacks().init(slotIndex + i, false, slotX, slotY);
            recipeLayout.getItemStacks().set(slotIndex + i, outputStack);

            if (focus != null) {
                ItemStack focusStack = (ItemStack) focus.getValue();
                if (focus.getMode() == IFocus.Mode.OUTPUT && !focusStack.isEmpty()
                        && focusStack.getItem() == outputStack.getItem()
                        && focusStack.getItemDamage() == outputStack.getItemDamage()) {
                    highlightX = slotX;
                    highlightY = slotY;
                }
            }
        }


        recipeLayout.getItemStacks().addTooltipCallback(new BurnOutRecipeCategory.BurnOutTooltipCallback(recipeWrapper));
    }

    @Override
    public IDrawable getIcon() {
        return null;
    }

    private static class BurnOutTooltipCallback implements ITooltipCallback<ItemStack> {
        private final BurnOutRecipe recipe;

        private BurnOutTooltipCallback(BurnOutRecipe recipeWrapper) {
            this.recipe = recipeWrapper;
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void onTooltip(int slotIndex, boolean input, @Nonnull ItemStack ingredient, @Nonnull List<String> tooltip) {
            if (!input) {
                ItemStack blockStack = recipe.getInputs().get(0);
                Block blockBase = Block.getBlockFromItem(blockStack.getItem());

                @SuppressWarnings("deprecation")
                IBlockState block = blockBase.getStateFromMeta(blockStack.getMetadata());

                List<BurnOutReward> rewards = ExNihiloRegistryManager.BURNOUT_REGISTRY.getRewards(block);

                for (BurnOutReward reward : rewards) {
                    float chance = 100.0F * reward.getChance();

                    String format = chance >= 10 ? " - %3.0f%% (x%d)" : "%1.1f%% - (x%d)";

                    tooltip.add(String.format(format, chance, reward.getStack().getCount()));
                }
            }
        }
    }

}