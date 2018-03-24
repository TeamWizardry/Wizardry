package com.teamwizardry.wizardry.common.item.dusts;

import com.teamwizardry.librarianlib.features.base.item.ItemMod;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.common.advancement.IPickupAchievement;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;

/**
 * Created by Demoniaque on 6/21/2016.
 */
public class ItemDevilDust extends ItemMod implements IPickupAchievement {

	public ItemDevilDust() {
		super("devil_dust");
	}

	@Override
	public Advancement getAdvancementOnPickup(ItemStack stack, EntityPlayer player, EntityItem item) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		return server.getAdvancementManager().getAdvancement(new ResourceLocation(Wizardry.MODID, "advancements/advancement_devildust.json"));
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, @Nonnull EnumHand handIn) {

		//Vec3d origin;
		//float offX = 0.5f * (float) Math.sin(Math.toRadians(-90.0f - playerIn.rotationYaw));
		//float offZ = 0.5f * (float) Math.cos(Math.toRadians(-90.0f - playerIn.rotationYaw));
		//origin = new Vec3d(offX, 0, offZ).add(playerIn.getPositionVector().addVector(0, playerIn.height / 2, 0));
//
		//List<Entity> entityList = worldIn.getEntitiesWithinAABBExcludingEntity(playerIn, new AxisAlignedBB(playerIn.getPosition()).grow(5, 5, 5));
//
		//for (Entity entity : entityList) {
		//	if (Utils.isLyingInCone(entity.getPositionVector().addVector(0, entity.height / 2, 0), origin, origin.add(playerIn.getLook(0).scale(10)), (float) Math.toRadians(90))) {
		//		entity.setFire(100);
		//	}
		//}
//
		//ClientRunnable.run(new ClientRunnable() {
		//	@Override
		//	@SideOnly(Side.CLIENT)
		//	public void runIfClient() {
		//		ParticleBuilder glitter = new ParticleBuilder(10);
		//		glitter.setColor(Color.ORANGE);
		//		glitter.setRender(new ResourceLocation(Wizardry.MODID, Constants.MISC.SPARKLE_BLURRED));
		//		glitter.setAlphaFunction(new InterpFadeInOut(0.2f, 1f));
//
		//		ParticleSpawner.spawn(glitter, worldIn, new StaticInterp<>(origin), RandUtil.nextInt(30, 40), 0, (i, build) -> {
		//			glitter.setMotion(playerIn.getLook(0).scale(3));
		//		});
		//	}
		//});

		return super.onItemRightClick(worldIn, playerIn, handIn);
	}
}
