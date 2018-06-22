package com.teamwizardry.wizardry.client.core;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Demoniaque.
 */
@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = Wizardry.MODID)
public class PhasedBlockRenderer {

	private static Set<PhaseObject> phaseObjects = new HashSet<>();

	public static void addPhase(World world, Set<BlockPos> blocks, int expiry) {
		phaseObjects.add(new PhaseObject(world, blocks, expiry));
	}

	public static final float WARP_TIME_PERIOD = 40f;
	public static final int COLOR = 0x28AEB7;
	public static final float ALPHA = 0.75f;
	public static final float BASE_ALPHA = 0.625f;

	public static BufferBuilder beginRender(float time) {
		float timeAngle = (float) Math.PI * time / WARP_TIME_PERIOD;

		float colorWarp = ((MathHelper.cos(timeAngle) + 1) / 2 * (1 - BASE_ALPHA) + BASE_ALPHA) / 0xFF;

		float colorR = ((COLOR & 0xFF0000) >> 16) * colorWarp;
		float colorG = ((COLOR & 0xFF00) >> 8) * colorWarp;
		float colorB = (COLOR & 0xFF) * colorWarp;

		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.enableCull();
		GlStateManager.cullFace(GlStateManager.CullFace.BACK);
		GlStateManager.depthMask(false);

		GlStateManager.color(colorR, colorG, colorB, ALPHA);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);

		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
		GL14.glBlendEquation(GL14.GL_FUNC_SUBTRACT);

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_NORMAL);
		buffer.setTranslation(-TileEntityRendererDispatcher.staticPlayerX, -TileEntityRendererDispatcher.staticPlayerY, -TileEntityRendererDispatcher.staticPlayerZ);

		return buffer;
	}

	public static void finishRender(BufferBuilder buffer) {
		Tessellator.getInstance().draw();
		buffer.setTranslation(0, 0, 0);

		GL14.glBlendEquation(GL14.GL_FUNC_ADD);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.depthMask(true);

		GlStateManager.enableDepth();
		GlStateManager.enableTexture2D();
	}

	public static void render(World world, BlockPos.MutableBlockPos mut, BlockPos pos, BufferBuilder buffer) {
		for (EnumFacing facing : EnumFacing.VALUES) {
			mut.setPos(pos);
			mut.offset(facing);
			IBlockState state = world.getBlockState(mut);
			if (state.getBlock().isAir(state, world, mut) || state.doesSideBlockRendering(world, mut, facing.getOpposite()))
				renderFace(facing, pos.getX(), pos.getY(), pos.getZ(), buffer);
		}
	}

	public static void renderFace(EnumFacing facing, float x, float y, float z, BufferBuilder buffer) {
		float xNormal = -facing.getFrontOffsetX();
		float yNormal = -facing.getFrontOffsetY();
		float zNormal = -facing.getFrontOffsetZ();

		int direction = -facing.getAxisDirection().getOffset();

		float xLocus = x + (1 - xNormal) / 2;
		float yLocus = y + (1 - yNormal) / 2;
		float zLocus = z + (1 - zNormal) / 2;
		float xShear = yNormal - zNormal;
		float yShear = zNormal - xNormal;
		float zShear = xNormal - yNormal;

		float yShearOne = xShear == 0 ? yShear : 0;
		float yShearTwo = xShear != 0 ? yShear : 0;

		int renderDirection = -direction;
		if (facing.getAxis().isHorizontal())
			renderDirection *= -1;

		for (int shearOne = -renderDirection; shearOne >= -1 && shearOne < 2; shearOne += 2 * renderDirection) {
			for (int shearTwo = shearOne * renderDirection; shearTwo >= -1 && shearTwo < 2; shearTwo -= 2 * shearOne * renderDirection) {

				float directionX = xShear * shearOne;
				float directionY = yShearOne * shearOne + yShearTwo * shearTwo;
				float directionZ = zShear * shearTwo;

				float calculatedX = directionX / 2 + xLocus + direction * 0.001f;
				float calculatedY = directionY / 2 + yLocus + direction * 0.001f;
				float calculatedZ = directionZ / 2 + zLocus + direction * 0.001f;

				buffer.pos(calculatedX, calculatedY, calculatedZ).normal(-xNormal, -yNormal, -zNormal).endVertex();
			}
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void render(RenderWorldLastEvent event) {
		if (Minecraft.getMinecraft().player == null) return;
		if (Minecraft.getMinecraft().getRenderManager().options == null) return;

		Set<PhaseObject> tmp = new HashSet<>(phaseObjects);

		BlockPos.MutableBlockPos mut = new BlockPos.MutableBlockPos();

		for (PhaseObject phaseObject : tmp) {
			if (Minecraft.getMinecraft().world.getTotalWorldTime() - phaseObject.lastWorldTick > phaseObject.expiry)
				phaseObjects.remove(phaseObject);


			float time = ClientTickHandler.getTicksInGame() +
					(Minecraft.getMinecraft().isGamePaused() ? 0 : ClientTickHandler.getPartialTicks());
			BufferBuilder bufferBuilder = beginRender(time);
			for (BlockPos pos : phaseObject.blocks)
				render(phaseObject.world, mut, pos, bufferBuilder);
			finishRender(bufferBuilder);
		}
	}


	private static class PhaseObject {

		public final long lastWorldTick;
		public final int expiry;
		private final Set<BlockPos> blocks;
		private final World world;

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
		}
	}
}
