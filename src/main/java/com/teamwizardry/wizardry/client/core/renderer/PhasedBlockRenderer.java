package com.teamwizardry.wizardry.client.core.renderer;

import com.teamwizardry.librarianlib.core.client.ClientTickHandler;
import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
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

import static org.lwjgl.opengl.GL11.GL_POLYGON_OFFSET_FILL;

/**
 * Created by Demoniaque.
 */
@Mod.EventBusSubscriber(modid = Wizardry.MODID, value = Side.CLIENT)
public class PhasedBlockRenderer {

	public static final float WARP_TIME_PERIOD = 40f;
	public static final float WARP_SPACE_PERIOD = 2f;
	public static final float WARP_MAGNITUDE = 0;
	public static final int COLOR = 0x28AEB7;
	public static final float ALPHA = 0.75f;
	public static final float BASE_ALPHA = 0.5f;
	private static Set<PhaseObject> phaseObjects = new HashSet<>();

	public static void addPhase(World world, Set<BlockPos> blocks, int expiry) {
		phaseObjects.add(new PhaseObject(world, blocks, expiry));
	}

	public static BufferBuilder beginRender() {
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.disableTexture2D();
		GlStateManager.enableCull();
		GlStateManager.cullFace(GlStateManager.CullFace.BACK);
		GlStateManager.depthMask(false);
		GL11.glEnable(GL_POLYGON_OFFSET_FILL);
		GL11.glPolygonOffset(-0.1f, -0.1f);

		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.shadeModel(GL11.GL_SMOOTH);

		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
		GL14.glBlendEquation(GL14.GL_FUNC_SUBTRACT);

		BufferBuilder buffer = Tessellator.getInstance().getBuffer();
		buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
		buffer.setTranslation(-TileEntityRendererDispatcher.staticPlayerX, -TileEntityRendererDispatcher.staticPlayerY, -TileEntityRendererDispatcher.staticPlayerZ);

		return buffer;
	}

	public static void finishRender(BufferBuilder buffer) {
		Tessellator.getInstance().draw();
		buffer.setTranslation(0, 0, 0);

		GL14.glBlendEquation(GL14.GL_FUNC_ADD);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.depthMask(true);

		GL11.glDisable(GL_POLYGON_OFFSET_FILL);
		GlStateManager.enableDepth();
		GlStateManager.enableTexture2D();
	}

	public static void render(PhaseObject object, float time, BufferBuilder buffer) {
		float timeAngle = (float) Math.PI / 2 * (time - object.when) / object.expiry;

		float colorWarp = (MathHelper.cos(timeAngle) * (1 - BASE_ALPHA) + BASE_ALPHA) / 0xFF;

		float colorR = ((COLOR & 0xFF0000) >> 16) * colorWarp;
		float colorG = ((COLOR & 0xFF00) >> 8) * colorWarp;
		float colorB = (COLOR & 0xFF) * colorWarp;

		for (SurfaceFace side : object.surface) renderFace(side, colorR, colorG, colorB, time, buffer);
	}

	public static void renderFace(SurfaceFace face, float r, float g, float b, float time, BufferBuilder buffer) {
		for (float[] vertexData : face.shapeData) {
			float timeAngle = (float) Math.PI * time / WARP_TIME_PERIOD;

			float periodicValue = MathHelper.sin(timeAngle) * vertexData[6];
			float magnitude = (periodicValue + 1) * WARP_MAGNITUDE + .01f;
			float vectorX = magnitude * vertexData[3];
			float vectorY = magnitude * vertexData[4];
			float vectorZ = magnitude * vertexData[5];

			float x = vertexData[0] + vectorX;
			float y = vertexData[1] + vectorY;
			float z = vertexData[2] + vectorZ;

			buffer.pos(x, y, z).tex(0, 0).color(r, g, b, ALPHA).normal(face.xNormal, face.yNormal, face.zNormal).endVertex();
		}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void render(RenderWorldLastEvent event) {
		if (Minecraft.getMinecraft().player == null) return;
		if (Minecraft.getMinecraft().getRenderManager().options == null) return;

		Set<PhaseObject> tmp = new HashSet<>(phaseObjects);

		for (PhaseObject phaseObject : tmp) {
			if (Minecraft.getMinecraft().world.getTotalWorldTime() - phaseObject.lastWorldTick > phaseObject.expiry)
				phaseObjects.remove(phaseObject);

			float time = ClientTickHandler.getTicksInGame() +
					(Minecraft.getMinecraft().isGamePaused() ? 0 : ClientTickHandler.getPartialTicks());
			BufferBuilder bufferBuilder = beginRender();
			render(phaseObject, time, bufferBuilder);
			finishRender(bufferBuilder);
		}
	}


	private static class PhaseObject {

		public final long lastWorldTick;
		public final float when;
		public final int expiry;

		private Set<SurfaceFace> surface;

		public PhaseObject(World world, Set<BlockPos> blocks, int expiry) {
			this.surface = SurfaceFace.fromPlaces(blocks);
			this.lastWorldTick = world.getTotalWorldTime();
			this.expiry = expiry;
			this.when = ClientTickHandler.getTicksInGame();
		}
	}

	private static class SurfaceFace {
		private static BlockPos.MutableBlockPos VISITOR = new BlockPos.MutableBlockPos();
		public final EnumFacing face;
		public final BlockPos position;
		public final float[][] shapeData = new float[4][7];
		public final float xNormal, yNormal, zNormal;

		public SurfaceFace(EnumFacing face, BlockPos position, Vec3d shapeLocus) {
			this.face = face;
			this.position = position;

			xNormal = -face.getXOffset();
			yNormal = -face.getYOffset();
			zNormal = -face.getZOffset();

			int direction = -face.getAxisDirection().getOffset();
			int renderDirection = face.getAxis().isHorizontal() ? direction : -direction;

			float xLocus = position.getX() + (1 - xNormal) / 2;
			float yLocus = position.getY() + (1 - yNormal) / 2;
			float zLocus = position.getZ() + (1 - zNormal) / 2;
			float xShear = yNormal - zNormal;
			float yShear = zNormal - xNormal;
			float zShear = xNormal - yNormal;

			float yShearOne = xShear == 0 ? yShear : 0;
			float yShearTwo = xShear != 0 ? yShear : 0;


			int idx = 0;
			for (int shearOne = -renderDirection; shearOne >= -1 && shearOne < 2; shearOne += 2 * renderDirection) {
				for (int shearTwo = shearOne * renderDirection; shearTwo >= -1 && shearTwo < 2; shearTwo -= 2 * shearOne * renderDirection) {

					float directionX = xShear * shearOne;
					float directionY = yShearOne * shearOne + yShearTwo * shearTwo;
					float directionZ = zShear * shearTwo;

					float calculatedX = directionX / 2 + xLocus;
					float calculatedY = directionY / 2 + yLocus;
					float calculatedZ = directionZ / 2 + zLocus;

					float distanceToLocusX = (float) shapeLocus.x - calculatedX;
					float distanceToLocusY = (float) shapeLocus.y - calculatedY;
					float distanceToLocusZ = (float) shapeLocus.z - calculatedZ;

					float descale = distanceToLocusX * distanceToLocusX +
							distanceToLocusY * distanceToLocusY +
							distanceToLocusZ * distanceToLocusZ;

					float positionWarp = MathHelper.sin((calculatedX + calculatedY + calculatedZ) *
							(float) Math.PI / WARP_SPACE_PERIOD);

					shapeData[idx++] = new float[]{calculatedX, calculatedY, calculatedZ,
							distanceToLocusX / descale, distanceToLocusY / descale, distanceToLocusZ / descale, positionWarp};
				}
			}
		}

		public static Set<SurfaceFace> fromPlaces(Set<BlockPos> places) {
			Set<SurfaceFace> faces = new HashSet<>();

			if (places.isEmpty())
				return faces;

			double total = places.size();
			int xLocus = 0, yLocus = 0, zLocus = 0;

			for (BlockPos pos : places) {
				xLocus += pos.getX();
				yLocus += pos.getY();
				zLocus += pos.getZ();
			}

			Vec3d locus = new Vec3d(xLocus / total, yLocus / total, zLocus / total);

			for (BlockPos pos : places) {
				for (EnumFacing facing : EnumFacing.VALUES) {
					VISITOR.setPos(pos.getX() + facing.getXOffset(),
							pos.getY() + facing.getYOffset(),
							pos.getZ() + facing.getZOffset());

					if (!places.contains(VISITOR))
						faces.add(new SurfaceFace(facing, pos, locus));
				}
			}

			return faces;
		}
	}
}
