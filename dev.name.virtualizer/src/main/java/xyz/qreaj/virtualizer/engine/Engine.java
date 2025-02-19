package xyz.qreaj.virtualizer.engine;

import lombok.Getter;
import xyz.qreaj.virtualizer.opcodes.OpcodeFactory;
import xyz.qreaj.virtualizer.opcodes.invoke.INVOKE;
import xyz.qreaj.virtualizer.opcodes.stack.*;
import xyz.qreaj.virtualizer.opcodes.type.*;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;

public class Engine {
    private ArrayList<Opcode> opcodes;
    ArrayList<LABEL> labels;
    public static String DEFAULT_CONSTANT_POOL_FILENAME = "a.virtualized";

    public static byte[] DATA = new byte[0];

    @Getter
    private static ConstantPoolReader constantPoolReader;

    static {
        if (Engine.class.getClassLoader().getResourceAsStream(DEFAULT_CONSTANT_POOL_FILENAME) != null) {
            DataInputStream dis = new DataInputStream(Engine.class.getClassLoader().getResourceAsStream(DEFAULT_CONSTANT_POOL_FILENAME));
            constantPoolReader = new ConstantPoolReader(dis);
            try {
                constantPoolReader.load();
                DATA = constantPoolReader.getModules();
            } catch (IOException e) {
                throw new IllegalArgumentException("Constant Pool is damaged");
            }
        }
    }

    public Engine(int start, int end) throws IOException {
        opcodes = new ArrayList<>();
        InputStream inputStream = new ByteArrayInputStream(DATA,start,end - start + 1);
        DataInputStream dis = new DataInputStream(inputStream);
        while (dis.available() > 0) {
            short id = dis.readShort();
            Opcode opcode = OpcodeFactory.createOpcode(id);
            opcode.readData(dis);
            opcodes.add(opcode);
        }
        labels = new ArrayList<>();
        for (int i = 0;i<opcodes.size();i++) {
            Opcode opcode = opcodes.get(i);
            if (opcode instanceof LABEL label) {
                label.index = (short) i;
                labels.add(label);
            }
        }

        labels.sort(Comparator.comparingInt(label -> label.line)); // sort indexes
    }
    public Object execute(Object ... runtimeStack) throws Throwable {
        StackEngine engine = new StackEngine();

        for (int i = 0;i<opcodes.size();i++) {
            Opcode opcode = opcodes.get(i);

            if (opcode instanceof VariableOpcode var) {
                engine.store(var.value);
            } else if (opcode instanceof ArithmeticOpcode arithmeticOpcode) {
                if (arithmeticOpcode.inputIs2ArgsElseOne) {
                    engine.store(arithmeticOpcode.calculate(engine.top(), engine.top()));
                } else {
                    engine.store(arithmeticOpcode.calculate(engine.top(), null));
                }

            } else if (opcode instanceof CAST cast) {
                engine.store(engine.top());
            } else if (opcode instanceof STORE store) {
                engine.store(store.id, engine.top());
            } else if (opcode instanceof LOAD load) {
                engine.store(engine.loadObject(load.id));
            } else if (opcode instanceof INVOKE invoke) {
                Object[] stack = new Object[invoke.getLen()];

                for (int j = 0; j < invoke.getLen(); j++) {
                    stack[j] = engine.top();
                }
                Object o = invoke.invoke(stack);

                if (o != null) {
                    engine.store(o);
                }
            } else if (opcode instanceof RETURN) {
                break;
            } else if (opcode instanceof GOTO gotoOpcode) {
                i = labels.get(gotoOpcode.offset).index;
            } else if (opcode instanceof SingleConditionJumpOpcode compare) {
                if (compare.getResult(engine.top())) {
                    i = labels.get(compare.offset).index;
                }
            } else if (opcode instanceof DoubleConditionJumpOpcode doubleConditionJumpOpcode) {

                if (doubleConditionJumpOpcode.evaluate(engine.top(), engine.top())) {
                    i = labels.get(doubleConditionJumpOpcode.offset).index;
                }
            } else if (opcode instanceof RUNTIME_LOAD runtimeLoad) {
                engine.store(runtimeStack[runtimeLoad.id]);
            } else if (opcode instanceof RUNTIME_STORE runtimeStore) {
                runtimeStack[runtimeStore.id] = engine.top();
            }

        }

        if (engine.empty()) return null;
        return engine.top();
    }
}
