package com.teamwizardry.wizardry.asm;

import com.teamwizardry.librarianlib.asm.LibLibTransformer;
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

	public static final LibLibTransformer.ClassnameMap CLASS_MAPPINGS = new LibLibTransformer.ClassnameMap(
			"net/minecraft/entity/player/EntityPlayer", "aeb",
			"net/minecraft/entity/Entity", "ve",
			"net/minecraft/entity/MoverType", "vt",
			"net/minecraft/entity/EntityLivingBase", "vn",
			"net/minecraft/client/renderer/entity/RenderLivingBase", "bzy",
			"net/minecraft/client/renderer/entity/Render", "bze"
	);

	private static final String ASM_HOOKS = "com/teamwizardry/wizardry/asm/WizardryASMHooks";
	private static final Map<String, Transformer> transformers = new HashMap<>();

	static {
		transformers.put("net.minecraft.entity.player.EntityPlayer", WizardryTransformer::transformPlayerClipping);
		transformers.put("net.minecraft.entity.Entity", WizardryTransformer::transformEntity);
		transformers.put("net.minecraft.entity.EntityLivingBase", WizardryTransformer::tranformEntityTravel);
		transformers.put("net.minecraft.client.renderer.entity.Render", WizardryTransformer::transformShadowAndFireRendering);
	}

	private static byte[] transformShadowAndFireRendering(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("doRenderShadowAndFire", "func_76979_b", "c", "(Lnet/minecraft/entity/Entity;DDDFF)V");

		return transform(basicClass, sig, "Render Shadow And Fire Event", (MethodNode method) -> {

			LabelNode node1 = new LabelNode();
			InsnList newInstructions = new InsnList();

			newInstructions.add(new VarInsnNode(ALOAD, 1));
			newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "entityRenderShadowAndFire",
					"(Lnet/minecraft/entity/Entity;)Z", false));

			newInstructions.add(new JumpInsnNode(IFNE, node1));
			newInstructions.add(new InsnNode(RETURN));

			newInstructions.add(node1);
			method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
			method.instructions.resetLabels();
			return true;
		});
	}

	private static byte[] tranformEntityTravel(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("travel", "func_191986_a", "a", "(FFF)V");

		return transform(basicClass, sig, "EntityLivingBase Travel Event",
				combineBackFocus((AbstractInsnNode node) -> node.getOpcode() == RETURN,
						(AbstractInsnNode node) -> node instanceof LabelNode,
						(MethodNode method, AbstractInsnNode node) -> {
							LabelNode node1 = new LabelNode();
							InsnList newInstructions = new InsnList();

							newInstructions.add(new VarInsnNode(ALOAD, 0));
							newInstructions.add(new VarInsnNode(FLOAD, 1));
							newInstructions.add(new VarInsnNode(FLOAD, 2));
							newInstructions.add(new VarInsnNode(FLOAD, 3));
							newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "travel",
									"(Lnet/minecraft/entity/EntityLivingBase;FFF)Z", false));

							newInstructions.add(new JumpInsnNode(IFNE, node1));
							newInstructions.add(new InsnNode(RETURN));
							newInstructions.add(node1);

							method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
							method.instructions.resetLabels();
							return true;
						}));
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
							LabelNode node1 = new LabelNode();

							newInstructions.add(new VarInsnNode(ALOAD, 0));
							newInstructions.add(new VarInsnNode(ALOAD, 1));
							newInstructions.add(new VarInsnNode(DLOAD, 2));
							newInstructions.add(new VarInsnNode(DLOAD, 4));
							newInstructions.add(new VarInsnNode(DLOAD, 6));
							newInstructions.add(new MethodInsnNode(INVOKESTATIC, ASM_HOOKS, "entityPreMoveHook",
									"(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/MoverType;DDD)Z", false));

							newInstructions.add(new JumpInsnNode(IFNE, node1));
							newInstructions.add(new InsnNode(RETURN));
							newInstructions.add(node1);

							method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
							method.instructions.resetLabels();
							return true;
						}));
	}

	private static byte[] transformPlayerClipping(byte[] basicClass) {
		MethodSignature sig = new MethodSignature("onUpdate", "func_70071_h_", "B_", "()V");

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

	public static void log(String str) {
		FMLLog.info("[Wizardry ASM] %s", str);
	}

	public static byte[] transform(byte[] basicClass, MethodSignature sig, String simpleDesc, MethodAction action) {
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

	public static boolean applyOnNode(MethodNode method, NodeFilter filter, NodeAction action) {
		AbstractInsnNode[] nodes = method.instructions.toArray();
		Iterator<AbstractInsnNode> iterator = new InsnArrayIterator(nodes);

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

	public static MethodAction combineByLast(NodeFilter filter, NodeAction action) {
		return (MethodNode node) -> applyOnNodeByLast(node, filter, action);
	}

	public static boolean applyOnNodeByLast(MethodNode method, NodeFilter filter, NodeAction action) {
		AbstractInsnNode[] nodes = method.instructions.toArray();
		ListIterator<AbstractInsnNode> iterator = new InsnArrayIterator(nodes, method.instructions.size());

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

	public static MethodAction combineFrontPivot(NodeFilter pivot, NodeFilter filter, NodeAction action) {
		return (MethodNode node) -> applyOnNodeFrontPivot(node, pivot, filter, action);
	}

	public static boolean applyOnNodeFrontPivot(MethodNode method, NodeFilter pivot, NodeFilter filter, NodeAction action) {
		AbstractInsnNode[] nodes = method.instructions.toArray();
		ListIterator<AbstractInsnNode> iterator = new InsnArrayIterator(nodes);

		int pos = 0;

		boolean didAny = false;
		while (iterator.hasNext()) {
			pos++;
			AbstractInsnNode pivotTest = iterator.next();
			if (pivot.test(pivotTest)) {
				ListIterator<AbstractInsnNode> internal = new InsnArrayIterator(nodes, pos);
				while (internal.hasPrevious()) {
					AbstractInsnNode anode = internal.previous();
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
		AbstractInsnNode[] nodes = method.instructions.toArray();
		ListIterator<AbstractInsnNode> iterator = new InsnArrayIterator(nodes, method.instructions.size());

		int pos = method.instructions.size();

		boolean didAny = false;
		while (iterator.hasPrevious()) {
			pos--;
			AbstractInsnNode pivotTest = iterator.previous();
			if (pivot.test(pivotTest)) {
				ListIterator<AbstractInsnNode> internal = new InsnArrayIterator(nodes, pos);
				while (internal.hasNext()) {
					AbstractInsnNode anode = internal.next();
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
		AbstractInsnNode[] nodes = method.instructions.toArray();
		ListIterator<AbstractInsnNode> iterator = new InsnArrayIterator(nodes);

		int pos = method.instructions.size();

		boolean didAny = false;
		while (iterator.hasNext()) {
			pos++;
			AbstractInsnNode focusTest = iterator.next();
			if (focus.test(focusTest)) {
				ListIterator<AbstractInsnNode> internal = new InsnArrayIterator(nodes, pos);
				while (internal.hasNext()) {
					AbstractInsnNode anode = internal.next();
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
		AbstractInsnNode[] nodes = method.instructions.toArray();
		ListIterator<AbstractInsnNode> iterator = new InsnArrayIterator(nodes, method.instructions.size());

		int pos = method.instructions.size();

		boolean didAny = false;
		while (iterator.hasPrevious()) {
			pos--;
			AbstractInsnNode focusTest = iterator.previous();
			if (focus.test(focusTest)) {
				ListIterator<AbstractInsnNode> internal = new InsnArrayIterator(nodes, pos);
				while (internal.hasPrevious()) {
					AbstractInsnNode anode = internal.previous();
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

	public static void prettyPrint(MethodNode node) {
		Printer printer = new Textifier();

		TraceMethodVisitor visitor = new TraceMethodVisitor(printer);
		node.accept(visitor);

		StringWriter sw = new StringWriter();
		printer.print(new PrintWriter(sw));
		printer.getText().clear();

		log(sw.toString());
	}

	public static void prettyPrint(AbstractInsnNode node) {
		Printer printer = new Textifier();

		TraceMethodVisitor visitor = new TraceMethodVisitor(printer);
		node.accept(visitor);

		StringWriter sw = new StringWriter();
		printer.print(new PrintWriter(sw));
		printer.getText().clear();

		log(sw.toString());
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

	public interface Transformer extends Function<byte[], byte[]> {
		// NO-OP
	}

	public interface MethodAction extends Predicate<MethodNode> {
		// NO-OP
	}

	// Basic interface aliases to not have to clutter up the code with generics over and over again

	public interface NodeFilter extends Predicate<AbstractInsnNode> {
		// NO-OP
	}

	public interface NodeAction extends BiPredicate<MethodNode, AbstractInsnNode> {
		// NO-OP
	}

	private static class InsnArrayIterator implements ListIterator<AbstractInsnNode> {

		private int index;
		private final AbstractInsnNode[] array;

		public InsnArrayIterator(AbstractInsnNode[] array) {
			this(array, 0);
		}

		public InsnArrayIterator(AbstractInsnNode[] array, int index) {
			this.array = array;
			this.index = index;
		}

		@Override
		public boolean hasNext() {
			return array.length > index + 1 && index >= 0;
		}

		@Override
		public AbstractInsnNode next() {
			if (hasNext())
				return array[++index];
			return null;
		}

		@Override
		public boolean hasPrevious() {
			return index > 0 && index <= array.length;
		}

		@Override
		public AbstractInsnNode previous() {
			if (hasPrevious())
				return array[--index];
			return null;
		}

		@Override
		public int nextIndex() {
			return hasNext() ? index + 1 : array.length;
		}

		@Override
		public int previousIndex() {
			return hasPrevious() ? index - 1 : 0;
		}

		@Override
		public void remove() {
			throw new Error("Unimplemented");
		}

		@Override
		public void set(AbstractInsnNode e) {
			throw new Error("Unimplemented");
		}

		@Override
		public void add(AbstractInsnNode e) {
			throw new Error("Unimplemented");
		}
	}

	public static class MethodSignature {
		private final String funcName, srgName, obfName, funcDesc, obfDesc;

		public MethodSignature(String funcName, String srgName, String obfName, String funcDesc) {
			this.funcName = funcName;
			this.srgName = srgName;
			this.obfName = obfName;
			this.funcDesc = funcDesc;
			this.obfDesc = obfuscate(funcDesc);
		}

		@Override
		public String toString() {
			return "Names [" + funcName + ", " + srgName + ", " + obfName + "] Descriptor " + funcDesc + " / " + obfDesc;
		}

		private static String obfuscate(String desc) {
			for (String s : CLASS_MAPPINGS.keySet())
				if (desc.contains(s))
					desc = desc.replaceAll(s, CLASS_MAPPINGS.get(s));

			return desc;
		}

		public boolean matches(String methodName, String methodDesc) {
			return (methodName.equals(funcName) || methodName.equals(obfName) || methodName.equals(srgName))
					&& (methodDesc.equals(funcDesc) || methodDesc.equals(obfDesc));
		}

		public boolean matches(MethodNode method) {
			return matches(method.name, method.desc);
		}

		public boolean matches(MethodInsnNode method) {
			return matches(method.name, method.desc);
		}

	}
}
