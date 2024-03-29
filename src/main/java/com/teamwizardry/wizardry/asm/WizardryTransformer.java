package com.teamwizardry.wizardry.asm;

import com.teamwizardry.wizardry.Wizardry;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by Demoniaque.
 */
public class WizardryTransformer implements IClassTransformer {

	private static final String CLASS_BLOCK = "net/minecraft/block/Block";
	private static final String CLASS_BLOCK_POS = "net/minecraft/util/math/BlockPos";
	private static final String CLASS_BLOCK_STATE = "net/minecraft/block/state/IBlockState";
	private static final String CLASS_BLOCK_ACCESS = "net/minecraft/world/IBlockAccess";
	private static final String CLASS_ENTITY_PLAYER = "net/minecraft/entity/player/EntityPlayer";
	private static final String CLASS_ENTITY = "net/minecraft/entity/Entity";
	private static final String CLASS_ENTITY_LIVING_BASE = "net/minecraft/entity/EntityLivingBase";
	private static final String CLASS_MOVER_TYPE = "net/minecraft/entity/MoverType";
	private static final String CLASS_EVENT = "net/minecraftforge/fml/common/eventhandler/Event";
	private static final String CLASS_MOVE_EVENT = "com/teamwizardry/wizardry/api/events/EntityMoveEvent";
	private static final String CLASS_TRAVEL_EVENT = "com/teamwizardry/wizardry/api/events/EntityTravelEvent";

	private static final String ASM_HOOKS = "com/teamwizardry/wizardry/asm/WizardryASMHooks";

	private static void log(String str) {
		System.out.println("[" + Wizardry.MODID + " ASM] " + str);
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		switch (transformedName) {

			/*
			  Overrides doRenderShadowAndFire to disable it when a player is legit vanished.
			 */
			case "net.minecraft.client.renderer.entity.Render": {
				return transformSingleMethod(
						basicClass,
						"func_76979_b",
						"doRenderShadowAndFire",
						"(L" + CLASS_ENTITY + ";DDDFF)V",
						methodNode -> {
							LabelNode node1 = new LabelNode();
							InsnList newInstructions = new InsnList();

							//newInstructions.add(new FrameNode(F_SAME, 0, null, 0, null));
							newInstructions.add(new VarInsnNode(ALOAD, 1));
							newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "entityRenderShadowAndFire",
									"(L" + CLASS_ENTITY + ";)Z", false));

							newInstructions.add(new JumpInsnNode(IFNE, node1));
							newInstructions.add(new InsnNode(RETURN));

							newInstructions.add(node1);
							methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), newInstructions);
							methodNode.instructions.resetLabels();
							return true;
						}
				);
			}

			/*
			  Overrides the move method in Entity which controls everything about entity movement.
			  Used in time slow and low grav.
			 */
			case "net.minecraft.entity.Entity": {
				return transformMultipleMethods(
					basicClass,
					new MethodTransformer[]{
							new MethodTransformer(
									"func_70091_d",
									"move",
									"(L" + CLASS_MOVER_TYPE + ";DDD)V",
									methodNode -> {
										InsnList newInstructions = new InsnList();
										LabelNode node1 = new LabelNode();

										newInstructions.add(new VarInsnNode(ALOAD, 0));
										newInstructions.add(new VarInsnNode(ALOAD, 1));
										newInstructions.add(new VarInsnNode(DLOAD, 2));
										newInstructions.add(new VarInsnNode(DLOAD, 4));
										newInstructions.add(new VarInsnNode(DLOAD, 6));
										newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "entityPreMoveHook",
												"(L" + CLASS_ENTITY + ";L" + CLASS_MOVER_TYPE + ";DDD)L" + CLASS_MOVE_EVENT + ";", false));
										newInstructions.add(new InsnNode(DUP));
										newInstructions.add(new MethodInsnNode(INVOKEVIRTUAL, CLASS_EVENT, "isCanceled", "()Z", false));
										newInstructions.add(new JumpInsnNode(IFEQ, node1));
										newInstructions.add(new InsnNode(POP));
										newInstructions.add(new InsnNode(RETURN));
										newInstructions.add(node1);
										newInstructions.add(new InsnNode(DUP));
										newInstructions.add(new InsnNode(DUP));
										newInstructions.add(new InsnNode(DUP));
										newInstructions.add(new FieldInsnNode(GETFIELD, CLASS_MOVE_EVENT, "type", "L" + CLASS_MOVER_TYPE + ";"));
										newInstructions.add(new VarInsnNode(ASTORE, 1));
										newInstructions.add(new FieldInsnNode(GETFIELD, CLASS_MOVE_EVENT, "x", "D"));
										newInstructions.add(new VarInsnNode(DSTORE, 2));
										newInstructions.add(new FieldInsnNode(GETFIELD, CLASS_MOVE_EVENT, "y", "D"));
										newInstructions.add(new VarInsnNode(DSTORE, 4));
										newInstructions.add(new FieldInsnNode(GETFIELD, CLASS_MOVE_EVENT, "z", "D"));
										newInstructions.add(new VarInsnNode(DSTORE, 6));

										methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), newInstructions);
										methodNode.instructions.resetLabels();
										return true;
									}
							),
							new MethodTransformer(
									"func_191955_a",
									"onInsideBlock",
									"(L" + CLASS_BLOCK_STATE + ";)V",
									methodNode -> {
										// Change access modifier to public
										methodNode.access = ACC_PUBLIC;
										log("Successfully patched -> 'onInsideBlock' with '(L" + CLASS_BLOCK_STATE + ";)V'");
										return true;
									}
							)
					}
				);
			}


			/*
			  Overrides the travel method that controls how entities move. Like slipperiness.
			 */
			case "net.minecraft.entity.EntityLivingBase": {
				return transformSingleMethod(
						basicClass,
						"func_191986_a",
						"travel",
						"(FFF)V",
						methodNode -> {
							LabelNode node1 = new LabelNode();
							InsnList newInstructions = new InsnList();

							newInstructions.add(new VarInsnNode(ALOAD, 0));
							newInstructions.add(new VarInsnNode(FLOAD, 1));
							newInstructions.add(new VarInsnNode(FLOAD, 2));
							newInstructions.add(new VarInsnNode(FLOAD, 3));
							newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "travel",
									"(L" + CLASS_ENTITY_LIVING_BASE + ";FFF)L" + CLASS_TRAVEL_EVENT + ";", false));
							newInstructions.add(new InsnNode(DUP));
							newInstructions.add(new MethodInsnNode(INVOKEVIRTUAL, CLASS_EVENT, "isCanceled", "()Z", false));
							newInstructions.add(new JumpInsnNode(IFEQ, node1));
							newInstructions.add(new InsnNode(POP));
							newInstructions.add(new InsnNode(RETURN));
							newInstructions.add(node1);
							newInstructions.add(new InsnNode(DUP));
							newInstructions.add(new InsnNode(DUP));
							newInstructions.add(new FieldInsnNode(GETFIELD, CLASS_TRAVEL_EVENT, "strafe", "F"));
							newInstructions.add(new VarInsnNode(FSTORE, 1));
							newInstructions.add(new FieldInsnNode(GETFIELD, CLASS_TRAVEL_EVENT, "vertical", "F"));
							newInstructions.add(new VarInsnNode(FSTORE, 2));
							newInstructions.add(new FieldInsnNode(GETFIELD, CLASS_TRAVEL_EVENT, "forward", "F"));
							newInstructions.add(new VarInsnNode(FSTORE, 3));

							methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), newInstructions);

							for (int i = 0; i < methodNode.instructions.size(); i++) {
								AbstractInsnNode insnNode = methodNode.instructions.get(i);
								if (insnNode instanceof MethodInsnNode) {
									MethodInsnNode node = (MethodInsnNode) insnNode;
									if (node.getOpcode() == Opcodes.INVOKEVIRTUAL
											&& node.owner.equals(CLASS_BLOCK)
											&& node.name.equals("getSlipperiness")
											&& node.desc.equals("(L" +
											CLASS_BLOCK_STATE + ";L" +
											CLASS_BLOCK_ACCESS + ";L" +
											CLASS_BLOCK_POS + ";L" +
											CLASS_ENTITY + ";)F")) {

										InsnList afterInstructions = new InsnList();

										afterInstructions.add(new VarInsnNode(ALOAD, 0));
										afterInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "slipperyHook",
												"(FL" + CLASS_ENTITY + ";)F", false));

										methodNode.instructions.insert(insnNode, afterInstructions);
									}
								}
							}

							methodNode.instructions.resetLabels();
							return true;
						}
				);
			}

			/*
			  Overrides player clipping.
			 */
			case "net.minecraft.entity.player.EntityPlayer": {
				return transformSingleMethod(
						basicClass,
						"func_70071_h_",
						"onUpdate",
						"()V",
						methodNode -> {
							for (int i = 0; i < methodNode.instructions.size(); i++) {
								AbstractInsnNode insnNode = methodNode.instructions.get(i);
								if (insnNode instanceof FieldInsnNode) {
									FieldInsnNode fInsnNode = (FieldInsnNode) insnNode;
									if (fInsnNode.getOpcode() == Opcodes.PUTFIELD
											&& fInsnNode.owner.equals(CLASS_ENTITY_PLAYER)
											&& equalsEither(fInsnNode.name, "field_70145_X", "noClip")
											&& fInsnNode.desc.equals("Z")) {

										InsnList newInstructions = new InsnList();

										newInstructions.add(new VarInsnNode(ALOAD, 0));
										newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "playerClipEventHook",
												"(ZL" + CLASS_ENTITY_PLAYER + ";)Z", false));

										methodNode.instructions.insertBefore(insnNode, newInstructions);
										methodNode.instructions.resetLabels();
										return true;
									}
								}
							}
							return false;
						}
				);
			}
		}
		return basicClass;
	}

	private boolean equalsEither(String name, String srgName, String mcpName) {
		return name.equals(srgName) || name.equals(mcpName);
	}

	private byte[] transformSingleMethod(byte[] basicClass, String srgName, String mcpName,
										 String desc, Predicate<MethodNode> transformer) {
		return transformClass(basicClass, classNode -> {
			for (MethodNode methodNode : classNode.methods) {
				if (equalsEither(methodNode.name, srgName, mcpName) && methodNode.desc.equals(desc)) {
					if (transformer.test(methodNode)) {
						log("Successfully patched -> '" + srgName + "', '" + mcpName + "' with '" + desc + "'");
					} else {
						log("Failed to patch      -> '" + srgName + "', '" + mcpName + "' with '" + desc + "'");
					}
				}
			}
		});
	}

	public class MethodTransformer {
		public String srgName;
		public String mcpName;
		public String desc;
		public Predicate<MethodNode> transformer;

		public MethodTransformer(String srgName, String mcpName, String desc, Predicate<MethodNode> transformer) {
			this.srgName = srgName;
			this.mcpName = mcpName;
			this.desc = desc;
			this.transformer = transformer;
		}
	}

	private byte[] transformMultipleMethods(byte[] basicClass, MethodTransformer[] transformers) {
		return transformClass(basicClass, classNode -> {
			for (MethodNode methodNode : classNode.methods) {
				for (MethodTransformer transformer : transformers) {
					if (equalsEither(methodNode.name, transformer.srgName, transformer.mcpName) && methodNode.desc.equals(transformer.desc)) {
						if (transformer.transformer.test(methodNode)) {
							log("Successfully patched -> '" + transformer.srgName + "', '" + transformer.mcpName + "' with '" + transformer.desc + "'");
						} else {
							log("Failed to patch      -> '" + transformer.srgName + "', '" + transformer.mcpName + "' with '" + transformer.desc + "'");
						}
					}
				}
			}
		});
	}

	private byte[] transformClass(byte[] basicClass, Consumer<ClassNode> transformer) {
		ClassReader reader = new ClassReader(basicClass);
		ClassNode classNode = new ClassNode();
		reader.accept(classNode, 0);

		transformer.accept(classNode);

		SafeClassWriter writer = new SafeClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES) {
			@Override
			protected String getCommonSuperClass(final String type1, final String type2) {
				//  the default asm merge uses Class.forName(), this prevents that.
				return "java/lang/Object";
			}
		};
		classNode.accept(writer);
		return writer.toByteArray();
	}
}
