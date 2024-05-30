package nonamecrackers2.endertrigon.common.data;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import nonamecrackers2.endertrigon.EnderTrigonMod;
import nonamecrackers2.endertrigon.common.init.EnderTrigonItems;

public class EnderTrigonItemModelProvider extends ItemModelProvider
{
	public EnderTrigonItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper)
	{
		super(output, EnderTrigonMod.MODID, existingFileHelper);
	}

	@Override
	protected void registerModels()
	{
		this.basicItem(EnderTrigonItems.DRAGON_HORN.get());
		
		ModelFile tootingDragonHorn = this.withExistingParent("tooting_" + EnderTrigonItems.DRAGON_HORN.getId().getPath(), "item/generated")
				.texture("layer0", this.modLoc("item/" + EnderTrigonItems.DRAGON_HORN.getId().getPath()))
				.transforms()
						.transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(0.0F, -125.0F, 0.0F).translation(-1.0F, 2.0F, 2.0F).scale(0.5F).end()
						.transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).rotation(0.0F, 55.0F, 0.0F).translation(-1.0F, 2.0F, 2.0F).scale(0.5F).end()
						.transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(0.0F, -55.0F, -5.0F).translation(-1.0F, -2.5F, -7.5F).end()
						.transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).rotation(0.0F, 115.0F, 5.0F).translation(0.0F, -2.5F, -7.5F).end().end();
		
		this.withExistingParent(EnderTrigonItems.DRAGON_HORN.getId().getPath(), "item/generated")
				.texture("layer0", this.modLoc("item/" + EnderTrigonItems.DRAGON_HORN.getId().getPath()))
				.transforms()
						.transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND).rotation(0.0F, 180.0F, 0.0F).translation(0.0F, 3.0F, 1.0F).scale(0.55F).end()
						.transform(ItemDisplayContext.THIRD_PERSON_LEFT_HAND).translation(0.0F, 3.0F, 1.0F).scale(0.55F).end()
						.transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND).rotation(0.0F, -90.0F, 25.0F).translation(1.13F, 3.2F, 1.13F).scale(0.68F).end()
						.transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND).rotation(0.0F, 90.0F, -25.0F).translation(1.13F, 3.2F, 1.13F).scale(0.68F).end().end()
				.override().predicate(EnderTrigonMod.id("tooting"), 1.0F).model(tootingDragonHorn).end();
	}
}
