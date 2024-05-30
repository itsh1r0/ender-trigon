package nonamecrackers2.endertrigon.common.data;

import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import nonamecrackers2.endertrigon.common.init.EnderTrigonItems;

public class EnderTrigonRecipeProvider extends RecipeProvider
{
	public EnderTrigonRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider)
	{
		super(output, lookupProvider);
	}
	
	@Override
	protected void buildRecipes(RecipeOutput consumer)
	{
		ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, EnderTrigonItems.DRAGON_HORN.get()).define('~', Items.GOAT_HORN).define('A', Items.ENDER_EYE).pattern(" A ").pattern("A~A").pattern(" A ").unlockedBy("has_ender_eye", has(Items.ENDER_EYE)).unlockedBy("has_goat_horn", has(Items.GOAT_HORN)).save(consumer);
	}
}
