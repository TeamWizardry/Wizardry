package com.teamwizardry.wizardry.client.core;

import com.google.common.collect.HashMultimap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Demoniaque.
 */
@SideOnly(Side.CLIENT)
public class PhasedBlockRenderer {

	public static PhasedBlockRenderer INSTANCE = new PhasedBlockRenderer();
	private Set<PhaseObject> phaseObjects = new HashSet<>();

	private PhasedBlockRenderer() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public void addPhase(World world, Set<BlockPos> blocks, int expiry) {
		phaseObjects.add(new PhaseObject(world, blocks, expiry));
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void render(RenderWorldLastEvent event) {
		if (Minecraft.getMinecraft().player == null) return;
		if (Minecraft.getMinecraft().getRenderManager().options == null) return;

		EntityPlayer player = Minecraft.getMinecraft().player;

		Set<PhaseObject> tmp = new HashSet<>(phaseObjects);

		for (PhaseObject phaseObject : tmp) {
			if (Minecraft.getMinecraft().world.getTotalWorldTime() - phaseObject.lastWorldTick > phaseObject.expiry)
				phaseObjects.remove(phaseObject);

			for (BlockPos pos : phaseObject.sides.keys())
				for (EnumFacing facing : phaseObject.sides.get(pos)) {

					GlStateManager.pushMatrix();

					double interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.getPartialTicks();
					double interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.getPartialTicks();
					double interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.getPartialTicks();

					GlStateManager.translate(-interpPosX, -interpPosY, -interpPosZ);

					GlStateManager.translate(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);

					double offset = 0.001;

					switch (facing) {
						case DOWN:
							GlStateManager.translate(0, -0.5 + offset, 0);
							GlStateManager.rotate(90, 1, 0, 0);
							break;
						case UP:
							GlStateManager.translate(0, 0.5 - offset, 0);
							GlStateManager.rotate(90, 1, 0, 0);
							break;
						case NORTH:
							GlStateManager.translate(0, 0, -0.5 + offset);
							break;
						case SOUTH:
							GlStateManager.translate(0, 0, 0.5 - offset);
							break;
						case WEST:
							GlStateManager.rotate(90, 0, 1, 0);
							GlStateManager.translate(0, 0, -0.5 + offset);
							break;
						case EAST:
							GlStateManager.rotate(90, 0, 1, 0);
							GlStateManager.translate(0, 0, 0.5 - offset);
							break;
					}

					GlStateManager.enableAlpha();
					GlStateManager.enableBlend();
					GlStateManager.disableTexture2D();
					GlStateManager.shadeModel(GL11.GL_SMOOTH);
					GlStateManager.color(1, 1, 1, 1);

					Color c = Color.BLUE;

					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder buff = tessellator.getBuffer();
					buff.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
					buff.pos(-0.5, 0.5, 0).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
					buff.pos(0.5, 0.5, 0).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
					buff.pos(0.5, -0.5, 0).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();
					buff.pos(-0.5, -0.5, 0).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).endVertex();

					tessellator.draw();

					GlStateManager.enableDepth();
					GlStateManager.enableTexture2D();

					GlStateManager.popMatrix();
				}

		}
	}

	class PhaseObject {

		public final long lastWorldTick;
		public final int expiry;
		private final Set<BlockPos> blocks;
		private final World world;
		private final HashMultimap<BlockPos, EnumFacing> sides = HashMultimap.create();

		public PhaseObject(World world, Set<BlockPos> blocks, int expiry) {
			this.blocks = blocks;
			this.world = world;
			this.lastWorldTick = world.getTotalWorldTime();
			this.expiry = expiry;
			init();
		}

		private void init() {
			Set<Long> longs = new HashSet<>();
			for (BlockPos pos : blocks) longs.add(pos.toLong());

			for (BlockPos pos : blocks) {
				BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos(pos);
				for (EnumFacing facing : EnumFacing.values()) {
					mutable.move(facing);
					if (!longs.contains(mutable.toLong())) {

						IBlockState state = world.getBlockState(mutable);
						mutable.move(facing.getOpposite());

						if (state.getBlock() == Blocks.AIR) continue;

						sides.put(mutable.toImmutable(), facing);
					} else {
						mutable.move(facing.getOpposite());
					}
				}
			}
		}
	}
}
