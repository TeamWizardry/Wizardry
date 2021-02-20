package com.teamwizardry.wizardry.data;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.teamwizardry.wizardry.Wizardry;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootEntry;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableManager;
import net.minecraft.loot.conditions.SurvivesExplosion;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class BlockLootProvider implements IDataProvider {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private final DataGenerator dataGenerator;
	private final Map<Block, Function<Block, LootTable.Builder>> functionTable = new HashMap<>();

	public BlockLootProvider(DataGenerator dataGenerator) {
		this.dataGenerator = dataGenerator;
	}


	@Override
	public void act(DirectoryCache cache) throws IOException {
		Map<ResourceLocation, LootTable.Builder> tables = new HashMap<>();

		for (Block b : ForgeRegistries.BLOCKS) {
			if (!Wizardry.MODID.equals(b.getRegistryName().getNamespace()))
				continue;
			Function<Block, LootTable.Builder> func = functionTable.getOrDefault(b, BlockLootProvider::normalDrop);
			tables.put(b.getRegistryName(), func.apply(b));
		}

		for (Map.Entry<ResourceLocation, LootTable.Builder> e : tables.entrySet()) {
			Path path = getPath(dataGenerator.getOutputFolder(), e.getKey());
			IDataProvider.save(GSON, cache, LootTableManager.toJson(e.getValue().setParameterSet(LootParameterSets.BLOCK).build()), path);
		}

	}

	private static Path getPath(Path root, ResourceLocation id) {
		return root.resolve("data/" + id.getNamespace() + "/loot_tables/blocks/" + id.getPath() + ".json");
	}

	@SuppressWarnings("unused")
    private static LootTable.Builder empty(Block b) {
		return LootTable.builder();
	}

	@SuppressWarnings("unused")
    private static LootTable.Builder nbtDrop(Block b, String... tags) {
		LootEntry.Builder<?> entry = ItemLootEntry.builder(b);
		CopyNbt.Builder func = CopyNbt.builder(CopyNbt.Source.BLOCK_ENTITY);
		for (String tag : tags) {
			func = func.replaceOperation(tag, "BlockEntityTag." + tag);
		}
		LootPool.Builder pool = LootPool.builder().name("main").rolls(ConstantRange.of(1)).addEntry(entry)
				.acceptCondition(SurvivesExplosion.builder())
				.acceptFunction(func);
		return LootTable.builder().addLootPool(pool);
	}

	private static LootTable.Builder normalDrop(Block b) {
		LootEntry.Builder<?> entry = ItemLootEntry.builder(b);
		LootPool.Builder pool = LootPool.builder().name("main").rolls(ConstantRange.of(1)).addEntry(entry)
				.acceptCondition(SurvivesExplosion.builder());
		return LootTable.builder().addLootPool(pool);
	}

	@Override
	public String getName() {
		return "Wizardy Block loot tables";
	}
}
