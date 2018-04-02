package com.teamwizardry.wizardry.client.core;

import com.google.common.base.Ticker;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.teamwizardry.librarianlib.core.LibrarianLib;
import com.teamwizardry.librarianlib.features.helpers.ItemNBTHelper;
import com.teamwizardry.wizardry.Wizardry;
import com.teamwizardry.wizardry.api.ClientConfigValues;
import com.teamwizardry.wizardry.api.item.BaublesSupport;
import com.teamwizardry.wizardry.api.util.RandUtilSeed;
import com.teamwizardry.wizardry.init.ModItems;
import com.teamwizardry.wizardry.init.ModPotions;
import it.unimi.dsi.fastutil.longs.Long2BooleanMap;
import it.unimi.dsi.fastutil.longs.Long2BooleanOpenHashMap;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.lwjgl.opengl.GL11;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

// TODO: fix cape while in vehicles
public final class CapeHandler {
	private final LoadingCache<EntityPlayer, RenderCape> capes = CacheBuilder.newBuilder()
			.weakKeys()
			.expireAfterAccess(10, TimeUnit.SECONDS)
			.ticker(new Ticker() {
				@Override
				public long read() {
					return Minecraft.getSystemTime();
				}
			})
			.build(CacheLoader.from(RenderCape::create));

	private CapeHandler() {
	}

	public static CapeHandler instance() {
		return Holder.INSTANCE;
	}

	@SubscribeEvent
	public void onPlayerRender(RenderPlayerEvent.Post event) {
		EntityPlayer player = event.getEntityPlayer();
		float delta = event.getPartialRenderTick();

		boolean iWalked = new Vec3d(player.posX, player.posY, player.posZ).distanceTo(new Vec3d(player.prevPosX, player.prevPosY, player.prevPosZ)) > 0.15;

		//TODO: Remove `ClientConfigValues.renderCape` once we have a real cosmetics system
		if (ClientConfigValues.renderCape && !player.isInvisible() && ((player.getActivePotionEffect(ModPotions.VANISH) != null && iWalked) || player.getActivePotionEffect(ModPotions.VANISH) == null))
			if (delta < 1) { // not rendering in inventory
				double x = -TileEntityRendererDispatcher.staticPlayerX;
				double y = -TileEntityRendererDispatcher.staticPlayerY;
				double z = -TileEntityRendererDispatcher.staticPlayerZ;
				instance().getCape(player).render(player, x, y, z, delta);
			}
	}

	private RenderCape getCape(EntityPlayer player) {
		return capes.getUnchecked(player);
	}

	@SubscribeEvent
	public void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.side == Side.CLIENT && event.phase == TickEvent.Phase.END) {
			getCape(event.player).update(event.player);
		}
	}

	private static final class Holder {
		private static final CapeHandler INSTANCE = new CapeHandler();
	}

	private static final class RenderCape {
		private static final ResourceLocation TEXTURE = new ResourceLocation(Wizardry.MODID, "textures/capes/cape_normal_4.png");

		private static final int PLAYER_SKIP_RANGE = 4 * 4;

		private static final int FLUID_CACHE_CLEAR_RATE = 4;

		private static final int FLUID_CACHE_CLEAR_SIZE = 6;

		private static final int ITERATIONS = 11;

		private static final float DELTA_TIME = 0.0052F;

		private static final float GRAVITY = -2800;

		private static final float FLUID_FORCE = -100;

		private static final int HEIGHT = 8;

		private static final int WIDTH = 4;

		private final ImmutableList<Point> points;

		private final ImmutableList<Quad> quads;

		private final Long2BooleanMap fluidCache = new Long2BooleanOpenHashMap(10);

		private final BlockPos.MutableBlockPos scratchPos = new BlockPos.MutableBlockPos();

		private double playerPosX = 0;

		private double playerPosY = 0;

		private double playerPosZ = 0;

		private RenderCape(ImmutableList<Point> points, ImmutableList<Quad> quads) {
			this.points = points;
			this.quads = quads;
		}

		private static RenderCape create() {
			return create(WIDTH, HEIGHT);
		}

		private static RenderCape create(int width, int height) {
			List<Point> points = Lists.newArrayList();
			List<Quad> quads = Lists.newArrayList();
			float scale = 2 / 16F;
			int columns = width + 1;
			PlayerCollisionResolver collision = new PlayerCollisionResolver();
			for (int y = 0; y <= height; y++) {
				for (int x = 0; x <= width; x++) {
					ImmutableList.Builder<ConstraintResolver> res = ImmutableList.builder();
					float mass = 1;
					if (y == 0 || (y == 1 && (x == 0 || x == width))) {
						mass = 0;
						res.add(new PinResolver(-(x - width * 0.5F) * scale, -y * scale));
					}
					if (y > 0 || x > 0) {
						mass = 1 - (y / (float) height) * 0.1F;
						LinkResolver link = new LinkResolver();
						if (y > 0) {
							link.attach(points.get(x + (y - 1) * columns), scale, 1);
						}
						if (x > 0) {
							link.attach(points.get(points.size() - 1), scale * (1 + y / (float) height * 0.1F), 1);
						}
						res.add(link);
					}
					res.add(collision);
					points.add(new Point(-(x - width * 0.5F) * scale, -y * scale, 0, mass, res.build()));
					if (x > 0 && y > 0) {
						int p00x = x - 1, p00y = y - 1;
						int p01x = x - 1, p01y = y;
						int p11x = x, p10y = y;
						int p10x = x, p11y = y - 1;
						Quad.Vertex v00 = Quad.vert(points.get(p00x + p00y * columns), p00x, p00y, width, height);
						Quad.Vertex v01 = Quad.vert(points.get(p01x + p01y * columns), p01x, p01y, width, height);
						Quad.Vertex v11 = Quad.vert(points.get(p11x + p10y * columns), p11x, p10y, width, height);
						Quad.Vertex v10 = Quad.vert(points.get(p10x + p11y * columns), p10x, p11y, width, height);
						quads.add(new Quad(v00, v01, v11, v10));
					}
				}
			}
			return new RenderCape(ImmutableList.copyOf(points), ImmutableList.copyOf(quads));
		}

		private boolean hasCape(EntityPlayer player) {
			return !BaublesSupport.getItem(player, ModItems.CAPE).isEmpty();
		}

		private void update(EntityPlayer player) {
			if (hasCape(player)) {
				updatePlayerPos(player);
				updatePoints(player);
				updateFluidCache(player);
			}
		}

		private void updatePlayerPos(EntityPlayer player) {
			double dist = (playerPosX - player.posX) * (playerPosX - player.posX) +
					(playerPosY - player.posY) * (playerPosY - player.posY) +
					(playerPosZ - player.posZ) * (playerPosZ - player.posZ);
			if (dist > PLAYER_SKIP_RANGE) {
				double moveX = player.posX - playerPosX;
				double moveY = player.posY - playerPosY;
				double moveZ = player.posZ - playerPosZ;
				for (Point point : points) {
					point.posX += moveX;
					point.posY += moveY;
					point.posZ += moveZ;
					point.lastPosX += moveX;
					point.lastPosY += moveY;
					point.lastPosZ += moveZ;
					point.prevPosX += moveX;
					point.prevPosY += moveY;
					point.prevPosZ += moveZ;
				}
			}
			playerPosX = player.posX;
			playerPosY = player.posY;
			playerPosZ = player.posZ;
		}

		private void updatePoints(EntityPlayer player) {
			for (Point point : points) {
				point.update(player.world, this, DELTA_TIME);
			}
			for (int i = 0; i < ITERATIONS; i++) {
				for (int j = points.size(); j-- > 0; ) {
					points.get(j).resolveConstraints(player);
				}
			}
		}

		private void updateFluidCache(EntityPlayer player) {
			if (player.ticksExisted % FLUID_CACHE_CLEAR_RATE == 0 && fluidCache.size() > FLUID_CACHE_CLEAR_SIZE) {
				fluidCache.clear();
			}
		}

		private boolean isFluid(World world, float x, float y, float z) {
			long key = scratchPos.setPos(x, y, z).toLong();
			if (fluidCache.containsKey(key)) {
				return fluidCache.get(key);
			}
			boolean isFluid = isFluid(world.getBlockState(scratchPos));
			fluidCache.put(key, isFluid);
			return isFluid;
		}

		private boolean isFluid(IBlockState state) {
			return state.getBlock() instanceof IFluidBlock || state.getBlock() instanceof BlockLiquid;
		}

		private void render(EntityPlayer player, double x, double y, double z, float delta) {
			if (!hasCape(player)) {
				return;
			}
			if (player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() instanceof ItemElytra) {
				return;
			}
			Tessellator tes = Tessellator.getInstance();
			BufferBuilder buf = tes.getBuffer();
			buf.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);
			buf.setTranslation(x, y, z);
			for (Quad quad : quads) {
				Quad.Vertex v00 = quad.getV00();
				Quad.Vertex v01 = quad.getV01();
				Quad.Vertex v11 = quad.getV11();
				Quad.Vertex v10 = quad.getV10();
				float v00x = v00.getX(delta);
				float v00y = v00.getY(delta);
				float v00z = v00.getZ(delta);
				float v01x = v01.getX(delta);
				float v01y = v01.getY(delta);
				float v01z = v01.getZ(delta);
				float v11x = v11.getX(delta);
				float v11y = v11.getY(delta);
				float v11z = v11.getZ(delta);
				float v10x = v10.getX(delta);
				float v10y = v10.getY(delta);
				float v10z = v10.getZ(delta);
				float nx = (v11y - v00y) * (v10z - v01z) - (v11z - v00z) * (v10y - v01y);
				float ny = (v10x - v01x) * (v11z - v00z) - (v10z - v01z) * (v11x - v00x);
				float nz = (v11x - v00x) * (v10y - v01y) - (v11y - v00y) * (v10x - v01x);
				float len = MathHelper.sqrt(nx * nx + ny * ny + nz * nz);
				nx /= len;
				ny /= len;
				nz /= len;
				buf.pos(v00x, v00y, v00z)
						.tex(v00.getU(), v00.getV())
						.normal(nx, ny, nz)
						.endVertex();
				buf.pos(v01x, v01y, v01z)
						.tex(v01.getU(), v01.getV())
						.normal(nx, ny, nz)
						.endVertex();
				buf.pos(v11x, v11y, v11z)
						.tex(v11.getU(), v11.getV())
						.normal(nx, ny, nz)
						.endVertex();
				buf.pos(v10x, v10y, v10z)
						.tex(v10.getU(), v10.getV())
						.normal(nx, ny, nz)
						.endVertex();
			}
			buf.setTranslation(0, 0, 0);
			GlStateManager.color(1, 1, 1);
			GlStateManager.disableCull();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

			ItemStack stack = BaublesSupport.getItem(player, ModItems.CAPE);
			String cape = null;

			if (LibrarianLib.PROXY.getResource(Wizardry.MODID, "textures/capes/cape_" + player.getUniqueID().toString().replace("-", "") + ".png") == null) {
				UUID uuid = ItemNBTHelper.getUUID(stack, "uuid");
				if (uuid != null) {
					RandUtilSeed seed = new RandUtilSeed(uuid.hashCode());
					cape = "cape_normal_" + seed.nextInt(1, 4);
				}
			} else {
				cape = "cape_" + player.getUniqueID().toString().replace("-", "");
			}

			Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(Wizardry.MODID, "textures/capes/" + cape + ".png"));

			tes.draw();
			GlStateManager.enableCull();
		}

		private interface ConstraintResolver {
			void resolve(EntityPlayer player, Point point);
		}

		private static final class Quad {
			private final Vertex v00;

			private final Vertex v01;

			private final Vertex v11;

			private final Vertex v10;

			private Quad(Vertex v00, Vertex v01, Vertex v11, Vertex v10) {
				this.v00 = v00;
				this.v01 = v01;
				this.v11 = v11;
				this.v10 = v10;
			}

			private static Vertex vert(Point point, int u, int v, int width, int height) {
				return new Vertex(point, u / (float) width, v / (float) height);
			}

			private Vertex getV00() {
				return v00;
			}

			private Vertex getV01() {
				return v01;
			}

			private Vertex getV11() {
				return v11;
			}

			private Vertex getV10() {
				return v10;
			}

			private static final class Vertex {
				private final Point point;

				private final double u;

				private final double v;

				private Vertex(Point point, double u, double v) {
					this.point = point;
					this.u = u;
					this.v = v;
				}

				private float getX(float delta) {
					return point.prevPosX + (point.posX - point.prevPosX) * delta;
				}

				private float getY(float delta) {
					return point.prevPosY + (point.posY - point.prevPosY) * delta;
				}

				private float getZ(float delta) {
					return point.prevPosZ + (point.posZ - point.prevPosZ) * delta;
				}

				private double getU() {
					return u;
				}

				private double getV() {
					return v;
				}
			}
		}

		private static final class Point {
			private final ImmutableList<ConstraintResolver> constraintResolvers;
			private float prevPosX;
			private float prevPosY;
			private float prevPosZ;
			private float posX;
			private float posY;
			private float posZ;
			private float lastPosX;
			private float lastPosY;
			private float lastPosZ;
			private float motionX;
			private float motionY;
			private float motionZ;
			private float invMass;

			private Point(float posX, float posY, float posZ, float invMass, ImmutableList<ConstraintResolver> constraintResolvers) {
				this.posX = posX;
				this.posY = posY;
				this.posZ = posZ;
				this.invMass = invMass;
				this.constraintResolvers = constraintResolvers;
			}

			private void applyForce(float x, float y, float z) {
				motionX += x;
				motionY += y;
				motionZ += z;
			}

			private void update(World world, RenderCape cape, float delta) {
				prevPosX = posX;
				prevPosY = posY;
				prevPosZ = posZ;
				applyForce(0, cape.isFluid(world, posX, posY, posZ) ? FLUID_FORCE : GRAVITY, 0);
				float x = posX + (posX - lastPosX) * 0.75F + motionX * 0.5F * (delta * delta);
				float y = posY + (posY - lastPosY) * 0.75F + motionY * 0.5F * (delta * delta);
				float z = posZ + (posZ - lastPosZ) * 0.75F + motionZ * 0.5F * (delta * delta);
				lastPosX = posX;
				lastPosY = posY;
				lastPosZ = posZ;
				posX = x;
				posY = y;
				posZ = z;
				motionX = motionY = motionZ = 0;
			}

			private void resolveConstraints(EntityPlayer player) {
				for (ConstraintResolver r : constraintResolvers) {
					r.resolve(player, this);
				}
			}
		}

		private static final class PinResolver implements ConstraintResolver {
			private final float x;

			private final float y;

			private PinResolver(float x, float y) {
				this.x = x;
				this.y = y;
			}

			@Override
			public void resolve(EntityPlayer player, Point point) {
				float yaw = (float) Math.toRadians(player.renderYawOffset);
				float height;
				float back;
				if (player.isSneaking()) {
					height = 1.15F;
					back = 0.135F;
				} else {
					height = 1.38F;
					back = 0.14F;
				}
				float vx = MathHelper.cos(yaw) * x + MathHelper.cos(yaw - (float) Math.PI / 2) * back;
				float vz = MathHelper.sin(yaw) * x + MathHelper.sin(yaw - (float) Math.PI / 2) * back;
				point.posX = (float) player.posX + vx;
				point.posY = (float) player.posY + height + y;
				point.posZ = (float) player.posZ + vz;
			}
		}

		private static final class LinkResolver implements ConstraintResolver {
			private final List<Link> constraints = Lists.newArrayList();

			private LinkResolver attach(Point point, float length, float strength) {
				constraints.add(new Link(point, length, strength));
				return this;
			}

			@Override
			public void resolve(EntityPlayer player, Point point) {
				for (int i = constraints.size(); i-- > 0; ) {
					constraints.get(i).resolve(point);
				}
			}

			private static final class Link {
				private static final float EPSILON = 1e-6F;

				private final Point dest;

				private final float length;

				private final float strength;

				private Link(Point dest, float length, float strength) {
					this.dest = dest;
					this.length = length;
					this.strength = strength;
				}

				private void resolve(Point point) {
					float dX = point.posX - dest.posX;
					float dY = point.posY - dest.posY;
					float dZ = point.posZ - dest.posZ;
					float dist = MathHelper.sqrt(dX * dX + dY * dY + dZ * dZ);
					float d = dist * (point.invMass + dest.invMass);
					float diff = d < EPSILON ? length / 2 : (dist - length) / d;
					float px = dX * diff * strength;
					float py = dY * diff * strength;
					float pz = dZ * diff * strength;
					point.posX -= px * point.invMass;
					point.posY -= py * point.invMass;
					point.posZ -= pz * point.invMass;
					dest.posX += px * dest.invMass;
					dest.posY += py * dest.invMass;
					dest.posZ += pz * dest.invMass;
				}
			}
		}

		private static final class PlayerCollisionResolver implements ConstraintResolver {
			@Override
			public void resolve(EntityPlayer player, Point point) {
				float yaw = (float) (Math.toRadians(player.renderYawOffset) - Math.PI / 2);
				float dx = MathHelper.cos(yaw);
				float dz = MathHelper.sin(yaw);
				float px = (float) player.posX;
				float py = (float) player.posY + 0.56F;
				float pz = (float) player.posZ;
				if (player.isSneaking()) {
					float backX = px + dx * 0.45F;
					float backZ = pz + dz * 0.45F;
					float dy = 0.52F;
					float len = MathHelper.sqrt(1 + dy * dy);
					dx /= len;
					dy /= len;
					dz /= len;
					float rz = (point.posX - (float) player.posX) * dx + (point.posZ - (float) player.posZ) * dz;
					float a = (MathHelper.clamp(-rz, -0.5F, -0.4F) + 0.5F) / 0.1F;
					collideWithPlane(point, backX, py, backZ, dx, dy, dz, a);
				} else {
					float rx = (point.posX - (float) player.posX) * MathHelper.cos(yaw + (float) Math.PI / 2) + (point.posZ - (float) player.posZ) * MathHelper.sin(yaw + (float) Math.PI / 2);
					float a = 1 - (MathHelper.clamp(Math.abs(rx), 0.24F, 0.36F) - 0.24F) / (0.36F - 0.24F);
					collideWithPlane(point, px + dx * 0.14F, py, pz + dz * 0.14F, dx, 0, dz, a);
				}
			}

			private float getDistToPlane(Point point, float px, float py, float pz, float nx, float ny, float nz) {
				return nx * (point.posX - px) + ny * (point.posY - py) + nz * (point.posZ - pz);
			}

			private void collideWithPlane(Point point, float px, float py, float pz, float nx, float ny, float nz, float amount) {
				float d = getDistToPlane(point, px, py, pz, nx, ny, nz);
				if (d < 0) {
					point.posX += nx * -d * amount;
					point.posY += ny * -d * amount;
					point.posZ += nz * -d * amount;
				}
			}
		}
	}
}
