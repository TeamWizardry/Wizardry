package com.teamwizardry.wizardry.common.item;

import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.item.GlowingOverlayHelper;
import com.teamwizardry.wizardry.api.item.IGlowOverlayable;
import com.teamwizardry.wizardry.common.entity.EntityHallowedSpirit;
import com.teamwizardry.wizardry.common.tile.TileManaBattery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDebugger extends Item implements IGlowOverlayable {

	public ItemDebugger() {
		setRegistryName("debugger");
		setUnlocalizedName("debugger");
		GameRegistry.register(this);
		setMaxStackSize(1);
		setCreativeTab(Wizardry.tab);
		addPropertyOverride(new ResourceLocation(Wizardry.MODID, "overlay"), GlowingOverlayHelper.OVERLAY_OVERRIDE);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (worldIn.getTileEntity(pos) instanceof TileManaBattery) {
			TileManaBattery tmb = (TileManaBattery) worldIn.getTileEntity(pos);
			if (!worldIn.isRemote) {
				playerIn.addChatMessage(new TextComponentString("Mana: " + tmb.current_mana + "/" + tmb.MAX_MANA));
			}
		}
		return EnumActionResult.PASS;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		if(worldIn.isRemote) {

			// create a builder
			/*ParticleBuilder builder = new ParticleBuilder(30); // world, lifetime
			
			builder.setPositionFunction(new InterpBezier3D(
				new Vec3d(0,0,0), new Vec3d(3,3,3),
				new Vec3d(1,-1,1), new Vec3d(2,4,2)
			)); // a basic bezier curve
			
			InterpFunction<Color> color = new InterpColorHSV(Color.RED, 255, 360*3);
			// going from red, ending at 255 (max) transparency, and doing 3 full rotations of hue
			
			builder.setRender(new RenderFunctionBasic( // basic particle rendering
				new ResourceLocation(Wizardry.MODID, "particles/sparkle")
			));
			
			ParticleSpawner.spawn(builder, worldIn,
				new InterpBezier3D( // the curve to spawn the particles along
					new Vec3d(0, 60, 0), new Vec3d(10, 64, 10)
				),
				20, // number of particles, reduced based on the current particle setting
				20, // [optional] the travel time. Allows you to, say, have a particle line move like a mana burst
				(i, build) -> {
					// [optional] a lambda that's called each time a particle is created
					// (not when they're spawned, the particles are prebuilt)
					
					build.setColor(new StaticInterp<>(color.get(i)));
					// get the color for the point `i` and use that as the static color for this particle
				}
			);*/
			
			
			
			/*builder = new ParticleBuilder(60);
			
			builder.setMotion(new Vec3d(0, 0.2, 0));
			builder.setColor(Color.RED);
			builder.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle"));
			
			ParticleSpawner.spawn(builder, worldIn,
				new InterpCircle(playerIn.getPositionVector().addVector(0, 1, 0), new Vec3d(0,1,0), 1),
				32,
				20
			);
			
			builder = new ParticleBuilder(60);
			
			
			builder.setColor(Color.GREEN);
			builder.setRender(new ResourceLocation(Wizardry.MODID, "particles/sparkle"));
			
			InterpCircle zeroCircle = new InterpCircle(new Vec3d(0,0,0), new Vec3d(0,1,0), 1);
			
			ParticleSpawner.spawn(builder, worldIn,
				new InterpCircle(playerIn.getPositionVector().addVector(0, 3, 0), new Vec3d(0,1,0), 1),
				32,
				20,
				(i, build) -> {
					build.setMotion(zeroCircle.get(i).normalize().scale(0.2).addVector(0, 0.1, 0));
				}
			);*/
			
			/*
			// create a builder
			ParticleBuilder builder = new ParticleBuilder(120); // world, lifetime
			
			// create a union
			InterpUnion<Vec3d> union = new InterpUnion<>();
			
			// add a line, returns func.get(1)
			Vec3d end = union.add(new InterpLine(
				new Vec3d(0, 0, 0), new Vec3d(0, 3, 3)
			), 2); // a weight of 2 (func takes 2/totalWeight of the time)
			
			union.with(new InterpLine( // add another line
				end, new Vec3d(0, 3, 6)
			), 1).with(new InterpBezier3D( // by using `with` the calls can be chained
				new Vec3d(0, 3, 6), new Vec3d(0, 0, 0), new Vec3d(0, 6, 3), new Vec3d(0, 3, 0)
			), 4); // a weight
			
			InterpUnionImpl<Vec3d> impl = union.build();
			builder.setPosition(impl);
			
			builder.setColor(new InterpColorHSV(Color.RED, 255, 360*3));
			// going from red, ending at 255 (max) transparency, and doing 3 full rotations of hue
			
			builder.setRender(new RenderFunctionBasic( // basic particle rendering
				new ResourceLocation(Wizardry.MODID, "particles/sparkle")
			));
			
			ParticleBase p = builder.build(worldIn, new Vec3d(0, 57, 0)); // create an actual particle
			Minecraft.getMinecraft().effectRenderer.addEffect(p); // spawn it
			*/
		} else {
			EntityHallowedSpirit spirit = new EntityHallowedSpirit(worldIn);
			spirit.setPosition(playerIn.posX, playerIn.posY, playerIn.posZ);
			worldIn.spawnEntityInWorld(spirit);
		}
//		if (!worldIn.isRemote) {
//			if (playerIn.isSneaking())
//				WizardryDataHandler.setBloodType(playerIn, null);
//			else {
//				Set<String> values = BloodRegistry.getRegistry().values();
//				String i = values.toArray(new String[values.size()])[worldIn.rand.nextInt(values.size())];
//				WizardryDataHandler.setBloodType(playerIn, BloodRegistry.getBloodTypeById(i));
//			}
//			//return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
//		}
//		if (playerIn.isSneaking())
//			if (GuiScreen.isCtrlKeyDown())
//				WizardryDataHandler.setBurnoutAmount(playerIn, 50);
//			else
//				WizardryDataHandler.setBurnoutAmount(playerIn, 0);
//		else if (GuiScreen.isCtrlKeyDown()) WizardryDataHandler.setMana(playerIn, 50);
//		else WizardryDataHandler.setMana(playerIn, 0);
//
//		//System.out.println(ModuleRegistry.getInstance().getModuleId(ModuleRegistry.getInstance().getModuleById(0)));
//		System.out.println(ModuleRegistry.getInstance().getModuleId(ModuleRegistry.getInstance().getModuleById(1)));
//		TeleportUtil.INSTANCE.teleportToDimension(playerIn, 100, 0, 100, 0);
		return new ActionResult<>(EnumActionResult.SUCCESS, itemStackIn);
	}

	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
}
