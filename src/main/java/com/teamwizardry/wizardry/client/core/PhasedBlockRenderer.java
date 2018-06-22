package com.teamwizardry.wizardry.client.core;

import com.google.common.collect.HashMultimap;
import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
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

	public static final float WARP_SCALE = 0.0025f;

	public static BufferBuilder beginRender(float time) {
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.shadeModel(GL11.GL_SMOOTH);

		float timeAngle = (float) Math.PI * time / 30;
		float warpNormal = MathHelper.cos(timeAngle) * WARP_SCALE;

		float colorR = 0x37 * warpNormal / 0xFF;
		float colorG = 0x75 * warpNormal / 0xFF;
		float colorB = 0x7A * warpNormal / 0xFF;
		float alpha = 0x96 / (float) 0xFF;

		GlStateManager.color(colorR, colorG, colorB, alpha);

		GlStateManager.depthMask(false);
		GL14.glBlendEquation(GL14.GL_FUNC_SUBTRACT);

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);

		return buffer;
	}

	public static void finishRender() {
		Tessellator.getInstance().draw();
		GL14.glBlendEquation(GL14.GL_FUNC_ADD);

		GlStateManager.depthMask(true);
		GlStateManager.enableDepth();
		GlStateManager.enableTexture2D();
	}

	public static void render(BlockPos pos, Iterable<EnumFacing> sides, float time, BufferBuilder buffer) {
		for (EnumFacing facing : sides)
			renderFace(facing,
					(float) (pos.getX() - TileEntityRendererDispatcher.staticPlayerX),
					(float) (pos.getY() - TileEntityRendererDispatcher.staticPlayerY),
					(float) (pos.getZ() - TileEntityRendererDispatcher.staticPlayerZ),
					time,
					buffer);
	}

	public static void renderFace(EnumFacing facing, float x, float y, float z, float time, BufferBuilder buffer) {
		float xNormal = facing.getFrontOffsetX();
		float yNormal = facing.getFrontOffsetY();
		float zNormal = facing.getFrontOffsetZ();

		float xLocus = x + (1 - xNormal) / 2;
		float yLocus = y + (1 - yNormal) / 2;
		float zLocus = z + (1 - zNormal) / 2;
		float xShear = yNormal - zNormal;
		float yShear = zNormal - xNormal;
		float zShear = xNormal - yNormal;

		float timeAngle = (float) Math.PI * time / 30;
		float warpNormal = MathHelper.cos(timeAngle) * WARP_SCALE;
		float warpAffine = MathHelper.sin(timeAngle) * WARP_SCALE;

		float yShearOne = xShear == 0 ? yShear : 0;
		float yShearTwo = xShear != 0 ? yShear : 0;

		for (int shearOne = 0; shearOne < 2; shearOne++) {
			for (int shearTwo = 0; shearTwo < 2; shearTwo++) {
				float calculatedX = xShear * (shearOne - 0.5f) + xLocus;
				float calculatedY = yShearOne * (shearOne - 0.5f) + yShearTwo * (shearTwo - 0.5f) + yLocus;
				float calculatedZ = zShear * (shearTwo - 0.5f) + zLocus;

				float positionAngleX = (float) Math.PI * calculatedX / 10;
				float positionAngleY = (float) Math.PI * calculatedY / 10;
				float positionAngleZ = (float) Math.PI * calculatedZ / 10;

				float xShift = MathHelper.sin(positionAngleY) * warpNormal + MathHelper.cos(positionAngleZ) * warpAffine + 2 * WARP_SCALE;
				float yShift = MathHelper.sin(positionAngleZ) * warpNormal + MathHelper.cos(positionAngleX) * warpAffine + 2 * WARP_SCALE;
				float zShift = MathHelper.sin(positionAngleX) * warpNormal + MathHelper.cos(positionAngleY) * warpAffine + 2 * WARP_SCALE;

				float xPosition = calculatedX + xShift * xShear;
				float yPosition = calculatedY + yShift * yShear;
				float zPosition = calculatedZ + zShift * zShear;

				buffer.pos(xPosition, yPosition, zPosition).normal(xNormal, yNormal, zNormal).endVertex();
			}
		}
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

			float time = ClientTickHandler.getTicks() + event.getPartialTicks();
			BufferBuilder bufferBuilder = beginRender(time);
			for (Map.Entry<BlockPos, Collection<EnumFacing>> entry : phaseObject.sides.asMap().entrySet())
				render(entry.getKey(), entry.getValue(), time, bufferBuilder);
			finishRender();
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
