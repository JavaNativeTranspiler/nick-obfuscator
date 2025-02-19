/*package dev.name.transformer.transformers.obf.flow.table;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.*;
import dev.name.asm.ir.extensions.processor.Processor;
import dev.name.asm.ir.nodes.Label;
import dev.name.asm.ir.types.Node;
import dev.name.util.Random;
import dev.name.transformer.Transformer;
import dev.name.util.ClassPool;
import lombok.SneakyThrows;

import java.util.*;
import java.util.List;

import static dev.name.asm.util.Bytecode.*;

public class DispatchTableTransformer extends Transformer implements Random {
    @Override
    public String name() {
        return "Dispatch Table Flow Transformer";
    }

    @Override
    @SneakyThrows
    public void transform(final ClassPool pool) {
        for (final Class klass : pool) {
            for (final Method method : klass.methods) {
                if (method.name.equals("<init>")) continue;
                if (method.instructions.size() <= 0) continue;
                if (method.access.isNative() || method.access.isAbstract()) continue;

                final Instructions instructions = method.instructions;
                final Label wrapper = new Label();
                final Label gate = new Label();

                wrapLabels(instructions);
                wrapConditionals(instructions);
                Processor.process(method, Processor.Mode.PRE);

                final Map<Label, Integer> keys = compute_table(method);
                final Label[] _LABELS = new Label[keys.size()];
                final int[] _KEYS = new int[keys.size()];

                int index = 0;

                for (final Map.Entry<Label, Integer> entry : keys.entrySet()) {
                    _LABELS[index] = entry.getKey();
                    _KEYS[index] = entry.getValue();
                    index++;
                }

                for (final Node instruction : instructions) {
                    if (!(instruction instanceof Jump jump)) continue;
                    if (jump.opcode != GOTO) continue;
                    final Integer key = keys.get(jump.label);
                    if (key == null) continue;
                    jump.insertBefore(new Constant(key));
                    jump.label = gate;
                }

                instructions.add(gate);
                gate.insertAfter(new Lookup(wrapper, _KEYS, _LABELS));

                final Node first = instructions.first;

                int entrance = RANDOM.nextInt();
                while (keys.containsValue(entrance)) entrance ^= RANDOM.nextInt();

                first.insertBefore(new Constant(entrance));
                first.insertBefore(gate.jump(GOTO));
                first.insertBefore(wrapper);
            }
        }
    }

    private static Map<Label, Integer> compute_table(final Method method) {
        final Instructions instructions = method.instructions;
        final Map<Label, Integer> map = new LinkedHashMap<>();
        final List<Label> labels = new ArrayList<>();

        for (final Node instruction : instructions) {
            if (!(instruction instanceof Label label)) continue;
            labels.add(label);
        }

        final int size = labels.size();

        final List<Integer> keys = new ArrayList<>(size);
        for (int i = 0; i < size; i++) keys.add(RANDOM.nextInt());

        Collections.sort(keys);
        Collections.shuffle(labels, RANDOM);

        for (int i = 0; i < size; i++) map.put(labels.get(i), keys.get(i));

        return map;
    }
}*/


  package dev.name.transformer.transformers.obf.flow.table;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Method;
import dev.name.asm.ir.extensions.processor.Processor;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.nodes.*;
import dev.name.asm.ir.types.Block;
import dev.name.asm.ir.types.Flags;
import dev.name.asm.ir.types.LocalPool;
import dev.name.asm.ir.types.Node;
import dev.name.util.asm.Bytecode;
import dev.name.transformer.Transformer;
import dev.name.util.java.ClassPool;
import dev.name.util.math.Random;
import lombok.SneakyThrows;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;

import java.util.*;

import static dev.name.asm.ir.types.Flags.Instruction.HANDLER;
import static dev.name.asm.ir.types.Flags.Instruction.WRAPPED_CONDITION;
import static dev.name.util.asm.Bytecode.wrapConditionals;
import static dev.name.util.asm.Bytecode.wrapLabels;

  public class DispatchTableTransformer extends Transformer implements Random {
      @Override
      public String name() {
          return "Dispatch Table Flow Transformer";
      }

      @Override
      @SneakyThrows
      public void transform(final ClassPool pool) {
          for (final Class klass : pool) {
              for (final Method method : klass.methods) {
                  if (method.instructions.size() <= 0) continue;
                  if (method.access.isNative() || method.access.isAbstract()) continue;
                  if (method.name.equals("<init>")) continue;

                  final Instructions instructions = method.instructions;
                  final Label wrapper = new Label();
                  final Label gate = new Label();

                  wrapLabels(instructions);
                  wrapConditionals(instructions);
                  Processor.process(method, Processor.Mode.PRE);

                  Frame<BasicValue>[] frames = Bytecode.analyze(method);
                  final LocalPool locals = new LocalPool(method);

                  for (final Block block : method.blocks)
                      block.handler.flags.set(HANDLER, true);

                  final List<Label> blocked = wrapStack(method, frames, locals);

                  final Map<Label, Integer> keys = compute_table(method, blocked);
                  final Label[] _LABELS = new Label[keys.size()];
                  final int[] _KEYS = new int[keys.size()];

                  int index = 0;

                  for (final Map.Entry<Label, Integer> entry : keys.entrySet()) {
                      _LABELS[index] = entry.getKey();
                      _KEYS[index] = entry.getValue();
                      index++;
                  }

                  for (final Node instruction : instructions) {
                      if (!(instruction instanceof Jump jump)) continue;
                      if (jump.opcode != GOTO) continue;
                      if (jump.label.flags.has(HANDLER)) continue;
                      final Integer key = keys.get(jump.label);
                      if (key == null) continue;
                      if (blocked.contains(jump.label)) continue;
                      jump.insertBefore(new Constant(key));
                      jump.label = gate;
                  }

                  instructions.add(gate);
                  gate.insertAfter(new Lookup(wrapper, _KEYS, _LABELS));

                  final Node first = instructions.first;

                  int entrance = RANDOM.nextInt();
                  while (keys.containsValue(entrance)) entrance ^= RANDOM.nextInt();

                  first.insertBefore(new Constant(entrance));
                  first.insertBefore(gate.jump(GOTO));
                  first.insertBefore(wrapper);
              }
          }
      }

      private static Map<Label, Integer> compute_table(final Method method, final List<Label> blocked) {
          final Instructions instructions = method.instructions;
          final Node last = instructions.last;

          final Map<Label, Integer> map = new LinkedHashMap<>();
          final List<Label> labels = new ArrayList<>();

          for (final Node instruction : instructions) {
              if (!(instruction instanceof Label label)) continue;
              if (label.equals(last)) continue;
              if (blocked.contains(label)) continue;
              final Flags flags = label.flags;
              if (flags.has(HANDLER) || flags.has(WRAPPED_CONDITION)) continue;
              labels.add(label);
          }

          final int size = labels.size();

          final List<Integer> keys = new ArrayList<>(size);
          for (int i = 0; i < size; i++) keys.add(RANDOM.nextInt());

          Collections.sort(keys);
          Collections.shuffle(labels, RANDOM);

          for (int i = 0; i < size; i++) map.put(labels.get(i), keys.get(i));

          return map;
      }

      public static List<Label> wrapStack(final Method method, final Frame<BasicValue>[] frames, final LocalPool pool) {
          final Map<Node, Frame<BasicValue>> frame_map = new HashMap<>();
          final Node[] arr = method.instructions.toArray();

          for (int i = 0; i < frames.length; i++) frame_map.put(arr[i], frames[i]);

          final Map<Label, List<LocalPool.Local>> localMap = new HashMap<>();
          final List<Label> blocked = new ArrayList<>();

          for (final Node instruction : method.instructions) {
              if (instruction instanceof Lookup lookup) {
                  final Frame<BasicValue> frame = frame_map.get(lookup);
                  if (frame == null || frame.getStackSize() <= 0) throw new IllegalStateException();

                  final LocalPool.Entry entry = pool.nextEntry();
                  // discard lookup index
                  for (int i = 0; i < frame.getStackSize() - 1; i++) entry.next(LocalPool.type(frame.getStack(i)));
                  final List<LocalPool.Local> locals = new ArrayList<>(List.of(entry.get()));

                  localMap.put(lookup._default, locals);
                  for (final Label label : lookup.labels) localMap.put(label, locals);

                  Collections.reverse(locals);

                  for (final LocalPool.Local local : locals) {
                      final boolean c2 = local.type == LONG || local.type == DOUBLE;
                      lookup.insertBefore(new Instruction(c2 ? DUP_X2 : DUP_X1));
                      lookup.insertBefore(new Instruction(POP));

                      lookup.insertBefore(local.store());
                  }

                  Collections.reverse(locals);

                  continue;
              }

              if (instruction instanceof Table table) {
                  final Frame<BasicValue> frame = frame_map.get(table);
                  if (frame == null || frame.getStackSize() <= 0) throw new IllegalStateException();

                  final LocalPool.Entry entry = pool.nextEntry();
                  // discard table index
                  for (int i = 0; i < frame.getStackSize() - 1; i++) entry.next(LocalPool.type(frame.getStack(i)));
                  final List<LocalPool.Local> locals = new ArrayList<>(List.of(entry.get()));
                  localMap.put(table._default, locals);
                  for (final Label label : table.labels) localMap.put(label, locals);

                  Collections.reverse(locals);

                  for (final LocalPool.Local local : locals) {
                      final boolean c2 = local.type == LONG || local.type == DOUBLE;
                      table.insertBefore(new Instruction(c2 ? DUP_X2 : DUP_X1));
                      table.insertBefore(new Instruction(POP));
                      table.insertBefore(local.store());
                  }

                  Collections.reverse(locals);
              }
          }

          for (final Node instruction : method.instructions) {
              if (!(instruction instanceof Label label) || localMap.containsKey(label)) continue;

              final Frame<BasicValue> frame = frame_map.get(label);
              if (frame == null || frame.getStackSize() <= 0) continue; // deadcode

              final LocalPool.Entry entry = pool.nextEntry();
              for (int i = 0; i < frame.getStackSize(); i++) entry.next(LocalPool.type(frame.getStack(i)));
              localMap.put(label, new ArrayList<>(List.of(entry.get())));
          }

          for (final Node node : method.instructions) {
              if (!(node instanceof Label label)) continue;
              if (!localMap.containsKey(label)) continue;
              final Frame<BasicValue> frame = frame_map.get(label);

              for (int i = 0; i < frame.getLocals(); i++)
                  if (frame.getLocal(i).equals(BasicValue.UNINITIALIZED_VALUE)) {
                      blocked.add(label);
                      localMap.remove(label);
                      break;
                  }

              if (blocked.contains(label)) continue;

              for (int i = 0; i < frame.getStackSize(); i++)
                  if (frame.getStack(i).equals(BasicValue.UNINITIALIZED_VALUE)) {
                      blocked.add(label);
                      localMap.remove(label);
                      break;
                  }
          }


          localMap.forEach((label, locals) -> {
              Collections.reverse(locals);

              for (final Node node : method.instructions.toArray()) {
                  if (!(node instanceof Jump jump) || !jump.label.equals(label)) continue;

                  if (jump.opcode == GOTO) {
                      for (final LocalPool.Local local : locals) jump.insertBefore(local.store());
                      continue;
                  }

                  final Label wrapper = jump.label;
                  if (!wrapper.flags.has(Flags.Instruction.WRAPPED_CONDITION)) continue;

                  Collections.reverse(locals);

                  for (final LocalPool.Local local : locals) wrapper.insertAfter(local.store());

                  Collections.reverse(locals);
              }

              // handle case where it jumps to a handler ? ignored. above
              if (label.flags.has(Flags.Instruction.HANDLER)) {
                  Collections.reverse(locals);
                  return;
              }

              final Frame<BasicValue> frame = frame_map.get(label);
              for (final LocalPool.Local local : locals) {
                  if (local.type == LocalPool.OBJECT) {
                      label.insertAfter(local.load());
                      //final BasicValue bv = frame.getStack()
                  } else label.insertAfter(local.load());
              }

              Collections.reverse(locals);
          });

          return blocked;
      }
  }