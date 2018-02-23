package com.teamwizardry.wizardry.api.book.hierarchy.entry.criterion.game;

import com.google.gson.JsonObject;
import com.teamwizardry.librarianlib.features.methodhandles.MethodHandleHelper;
import com.teamwizardry.librarianlib.features.utilities.client.ClientRunnable;
import com.teamwizardry.wizardry.api.book.hierarchy.entry.criterion.ICriterion;
import kotlin.jvm.functions.Function1;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

/**
 * @author WireSegal
 * Created at 9:56 PM on 2/21/18.
 */
public class CriterionAdvancement implements ICriterion {

	@SideOnly(Side.CLIENT)
	private static Function1<ClientAdvancementManager, Object> mh;

	static {
		ClientRunnable.run(new ClientRunnable() {
			@Override
			@SideOnly(Side.CLIENT)
			public void runIfClient() {
				mh = MethodHandleHelper.wrapperForGetter(ClientAdvancementManager.class, "field_192803_d, advancementToProgress");
			}
		});
	}

	private final ResourceLocation advancement;


	public CriterionAdvancement(JsonObject object) {
		advancement = new ResourceLocation(object.getAsJsonPrimitive("name").getAsString());
	}

	@SideOnly(Side.CLIENT)
	@SuppressWarnings("unchecked")
	private static Map<Advancement, AdvancementProgress> progress(ClientAdvancementManager manager) {
		return (Map<Advancement, AdvancementProgress>) mh.invoke(manager);
	}

	@Override
	public boolean isUnlocked(EntityPlayer player, boolean grantedInCode) {
		if (player instanceof EntityPlayerMP) {
			Advancement adv = ((EntityPlayerMP) player).getServerWorld().getAdvancementManager().getAdvancement(advancement);
			return adv != null && ((EntityPlayerMP) player).getAdvancements().getProgress(adv).isDone();
		}
		if (player instanceof EntityPlayerSP) {
			Boolean bool = ClientRunnable.produce(new ClientRunnable.ClientSupplier<Boolean>() {
				@Override
				@SideOnly(Side.CLIENT)
				public Boolean produceIfClient() {
					ClientAdvancementManager manager = ((EntityPlayerSP) player).connection.getAdvancementManager();
					Advancement adv = manager.getAdvancementList().getAdvancement(advancement);
					if (adv == null) return false;
					AdvancementProgress progress = progress(manager).get(adv);
					return progress != null && progress.isDone();
				}
			});
			return bool == Boolean.TRUE;
		}
		return false;
	}
}
