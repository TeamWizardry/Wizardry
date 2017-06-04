package com.teamwizardry.wizardry.asm;

import com.teamwizardry.librarianlib.asm.ClassnameMap;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.FMLLog;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created by LordSaad.
 */
public class WizardryTransformer implements IClassTransformer {

	public static final ClassnameMap CLASS_MAPPINGS = new ClassnameMap(
			"net/minecraft/entity/player/EntityPlayer", "aax",
			"net/minecraft/entity/Entity", "sm"
	);
	private static final String ASM_HOOKS = "com/teamwizardry/wizardry/asm/WizardryASMHooks";
	private static final Map<String, Transformer> transformers = new HashMap<>();

	static {
		transformers.put("net.minecraft.entity.player.EntityPlayer", WizardryTransformer::transformPlayerClipping);
		transformers.put("net.minecraft.entity.Entity", WizardryTransformer::transformEntity);
	}

	private static byte[] transformEntity(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("move", "func_70091_d", "a",
				"(Lnet/minecraft/entity/MoverType;DDD)V");

		return transform(basicClass, sig, "Entity Pre Move Action Event",
				combineBackFocus(
						(AbstractInsnNode node) -> node.getOpcode() == RETURN,
						(AbstractInsnNode node) -> node instanceof LabelNode,
						(MethodNode method, AbstractInsnNode node) -> {
							InsnList newInstructions = new InsnList();

							newInstructions.add(new VarInsnNode(ALOAD, 0));
							newInstructions.add(new VarInsnNode(ALOAD, 1));
							newInstructions.add(new VarInsnNode(DLOAD, 2));
							newInstructions.add(new VarInsnNode(DLOAD, 4));
							newInstructions.add(new VarInsnNode(DLOAD, 6));
							newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "entityPreMoveHook",
									"(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/MoverType;DDD)Z", false));

							newInstructions.add(new JumpInsnNode(IFNE, (LabelNode) node));

							method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
							return true;
						}));
	}

	private static byte[] transformPlayerClipping(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("onUpdate", "func_70071_h", "h", "()V");

		return transform(basicClass, sig, "Player Clipping Event",
				combine((AbstractInsnNode node) -> node.getOpcode() == PUTFIELD, // Filter
						(MethodNode method, AbstractInsnNode node) -> { // Action
							InsnList newInstructions = new InsnList();

							newInstructions.add(new VarInsnNode(ALOAD, 0));
							newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "playerClipEventHook",
									"(ZLnet/minecraft/entity/player/EntityPlayer;)Z", false));

							method.instructions.insertBefore(node, newInstructions);
							return true;
						}));
	}

	// BOILERPLATE =====================================================================================================

	private static byte[] transform(byte[] basicClass, MethodSignature sig, String simpleDesc, MethodAction action) {
		ClassReader reader = new ClassReader(basicClass);
		ClassNode node = new ClassNode();
		reader.accept(node, 0);

		log("Applying Transformation to method (" + sig + ")");
		log("Attempting to insert: " + simpleDesc);
		boolean didAnything = findMethodAndTransform(node, sig, action);

		if (didAnything) {
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			node.accept(writer);
			return writer.toByteArray();
		}

		return basicClass;
	}

	public static boolean findMethodAndTransform(ClassNode node, MethodSignature sig, MethodAction pred) {
		for (MethodNode method : node.methods) {
			if (sig.matches(method)) {

				boolean finish = pred.test(method);
				log("Patch result: " + (finish ? "Success" : "!!!!!!! Failure !!!!!!!"));

				return finish;
			}
		}

		log("Patch result: !!!!!!! Couldn't locate method! !!!!!!!");

		return false;
	}

	public static MethodAction combine(NodeFilter filter, NodeAction action) {
		return (MethodNode node) -> applyOnNode(node, filter, action);
	}

	public static MethodAction combineByLast(NodeFilter filter, NodeAction action) {
		return (MethodNode node) -> applyOnNodeByLast(node, filter, action);
	}

	public static boolean applyOnNodeByLast(MethodNode method, NodeFilter filter, NodeAction action) {
		ListIterator<AbstractInsnNode> iterator = method.instructions.iterator(method.instructions.size());

		boolean didAny = false;
		while (iterator.hasPrevious()) {
			AbstractInsnNode anode = iterator.previous();
			if (filter.test(anode)) {
				didAny = true;
				if (action.test(method, anode))
					break;
			}
		}

		return didAny;
	}

	public static boolean applyOnNode(MethodNode method, NodeFilter filter, NodeAction action) {
		Iterator<AbstractInsnNode> iterator = method.instructions.iterator();

		boolean didAny = false;
		while (iterator.hasNext()) {
			AbstractInsnNode anode = iterator.next();
			if (filter.test(anode)) {
				didAny = true;
				if (action.test(method, anode))
					break;
			}
		}

		return didAny;
	}

	public static MethodAction combineFrontPivot(NodeFilter pivot, NodeFilter filter, NodeAction action) {
		return (MethodNode node) -> applyOnNodeFrontPivot(node, pivot, filter, action);
	}

	public static boolean applyOnNodeFrontPivot(MethodNode method, NodeFilter pivot, NodeFilter filter, NodeAction action) {
		ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

		boolean didAny = false;
		while (iterator.hasNext()) {
			AbstractInsnNode pivotTest = iterator.next();
			if (pivot.test(pivotTest)) {
				log("Found pivot:");
				prettyPrint(pivotTest);
				while (iterator.hasPrevious()) {
					AbstractInsnNode anode = iterator.previous();
					if (filter.test(anode)) {
						didAny = true;
						if (action.test(method, anode))
							break;
					}
				}
			}
		}

		return didAny;
	}

	public static MethodAction combineBackPivot(NodeFilter pivot, NodeFilter filter, NodeAction action) {
		return (MethodNode node) -> applyOnNodeBackPivot(node, pivot, filter, action);
	}

	public static boolean applyOnNodeBackPivot(MethodNode method, NodeFilter pivot, NodeFilter filter, NodeAction action) {
		ListIterator<AbstractInsnNode> iterator = method.instructions.iterator(method.instructions.size());

		boolean didAny = false;
		while (iterator.hasPrevious()) {
			AbstractInsnNode pivotTest = iterator.previous();
			if (pivot.test(pivotTest)) {
				log("Found pivot:");
				prettyPrint(pivotTest);
				while (iterator.hasNext()) {
					AbstractInsnNode anode = iterator.next();
					if (filter.test(anode)) {
						didAny = true;
						if (action.test(method, anode))
							break;
					}
				}
			}
		}

		return didAny;
	}

	public static MethodAction combineFrontFocus(NodeFilter focus, NodeFilter filter, NodeAction action) {
		return (MethodNode node) -> applyOnNodeFrontFocus(node, focus, filter, action);
	}

	public static boolean applyOnNodeFrontFocus(MethodNode method, NodeFilter focus, NodeFilter filter, NodeAction action) {
		ListIterator<AbstractInsnNode> iterator = method.instructions.iterator();

		boolean didAny = false;
		while (iterator.hasNext()) {
			AbstractInsnNode focusTest = iterator.next();
			if (focus.test(focusTest)) {
				log("Found focus:");
				prettyPrint(focusTest);
				while (iterator.hasNext()) {
					AbstractInsnNode anode = iterator.next();
					if (filter.test(anode)) {
						didAny = true;
						if (action.test(method, anode))
							break;
					}
				}
			}
		}

		return didAny;
	}

	public static MethodAction combineBackFocus(NodeFilter focus, NodeFilter filter, NodeAction action) {
		return (MethodNode node) -> applyOnNodeBackFocus(node, focus, filter, action);
	}

	public static boolean applyOnNodeBackFocus(MethodNode method, NodeFilter focus, NodeFilter filter, NodeAction action) {
		ListIterator<AbstractInsnNode> iterator = method.instructions.iterator(method.instructions.size());

		boolean didAny = false;
		while (iterator.hasPrevious()) {
			AbstractInsnNode focusTest = iterator.previous();
			if (focus.test(focusTest)) {
				log("Found focus:");
				prettyPrint(focusTest);
				while (iterator.hasPrevious()) {
					AbstractInsnNode anode = iterator.previous();
					if (filter.test(anode)) {
						didAny = true;
						if (action.test(method, anode))
							break;
					}
				}
			}
		}

		return didAny;
	}

	private static void log(String str) {
		FMLLog.info("[Wizardry ASM] %s", str);
	}

	private static void prettyPrint(AbstractInsnNode node) {
		Printer printer = new Textifier();

		TraceMethodVisitor visitor = new TraceMethodVisitor(printer);
		node.accept(visitor);

		StringWriter sw = new StringWriter();
		printer.print(new PrintWriter(sw));
		printer.getText().clear();

		log(sw.toString().replaceAll("\n", ""));
	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (transformers.containsKey(transformedName)) {
			String[] arr = transformedName.split("\\.");
			log("Transforming " + arr[arr.length - 1]);
			return transformers.get(transformedName).apply(basicClass);
		}

		return basicClass;
	}

	private interface Transformer extends Function<byte[], byte[]> {
		// NO-OP
	}

	// Basic interface aliases to not have to clutter up the code with generics over and over again

	private interface MethodAction extends Predicate<MethodNode> {
		// NO-OP
	}

	private interface NodeFilter extends Predicate<AbstractInsnNode> {
		// NO-OP
	}

	private interface NodeAction extends BiPredicate<MethodNode, AbstractInsnNode> {
		// NO-OP
	}

	private static class MethodSignature {
		private final String funcName, srgName, obfName, funcDesc, obfDesc;

		public MethodSignature(String funcName, String srgName, String obfName, String funcDesc) {
			this.funcName = funcName;
			this.srgName = srgName;
			this.obfName = obfName;
			this.funcDesc = funcDesc;
			this.obfDesc = obfuscate(funcDesc);
		}

		private static String obfuscate(String desc) {
			for (String s : CLASS_MAPPINGS.keySet())
				if (desc.contains(s))
					desc = desc.replaceAll(s, CLASS_MAPPINGS.get(s));

			return desc;
		}

		@Override
		public String toString() {
			return "Names [" + funcName + ", " + srgName + ", " + obfName + "] Descriptor " + funcDesc + " / " + obfDesc;
		}

		private boolean matches(String methodName, String methodDesc) {
			return (methodName.equals(funcName) || methodName.equals(obfName) || methodName.equals(srgName))
					&& (methodDesc.equals(funcDesc) || methodDesc.equals(obfDesc));
		}

		private boolean matches(MethodNode method) {
			return matches(method.name, method.desc);
		}

		private boolean matches(MethodInsnNode method) {
			return matches(method.name, method.desc);
		}

	}
}
