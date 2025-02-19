package dev.name.asm.ir.extensions.template;

import dev.name.asm.ir.components.Class;
import dev.name.asm.ir.components.Module;
import dev.name.asm.ir.components.Record;
import dev.name.asm.ir.components.*;
import dev.name.asm.ir.instructions.Instructions;
import dev.name.asm.ir.types.*;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static dev.name.asm.ir.extensions.template.Template.*;

@SuppressWarnings("unused")
public sealed abstract class Template<T> implements Opcodes permits
        AnnotationTemplate,
        ClassTemplate,
        FieldTemplate,
        MethodTemplate,
        ModuleTemplate,
        RecordTemplate
{
    public static int
            ANNOTATION =    0b000001,
            CLASS =         0b000010,
            FIELD =         0b000100,
            METHOD =        0b001000,
            MODULE =        0b010000,
            RECORD =        0b100000;

    public abstract T assemble();
    public abstract int type();

    public static non-sealed abstract class AnnotationTemplate extends Template<Annotation> {
        public final int type() { return ANNOTATION; }

        public List<Object> args = new ArrayList<>();

        public abstract String desc();
        public abstract boolean visible();

        @Override
        public final Annotation assemble() {
            return new Annotation(desc(), args, visible());
        }
    }

    public static non-sealed abstract class ClassTemplate extends Template<Class> {
        public final int type() { return CLASS; }

        public List<Method> methods = new ArrayList<>();
        public List<Field> fields = new ArrayList<>();
        public List<Record> records = new ArrayList<>();
        public List<Attribute> attributes = new ArrayList<>();
        public List<Annotation> annotations = new ArrayList<>();
        public List<Annotation.Type> typeAnnotations = new ArrayList<>();

        public abstract int version();
        public abstract Access access();
        public abstract String name();
        public abstract String signature();
        public abstract String superClass();
        public abstract String outerClass();
        public abstract String outerMethod();
        public abstract String outerMethodDesc();
        public abstract String nestHostClass();
        public abstract Module module();
        public abstract List<String> permitted();
        public abstract List<String> interfaces();
        public abstract List<String> nestMembers();
        public abstract List<InnerClass> innerClasses();

        @Override
        public final Class assemble() {
            final Class klass = new Class();
            klass.version = version();
            klass.access = access();
            klass.name = name();
            klass.signature = signature();
            klass.superName = superClass();
            klass.outerClass = outerClass();
            klass.outerMethod = outerMethod();
            klass.outerMethodDesc = outerMethodDesc();
            klass.nestHostClass = nestHostClass();
            klass.module = module();
            klass.permitted = permitted();
            klass.interfaces = interfaces();
            klass.nestMembers = nestMembers();
            klass.innerClasses = innerClasses();
            methods.forEach(klass::addMethod);
            fields.forEach(klass::addField);
            records.forEach(klass::addRecord);
            klass.attributes.addAll(attributes);
            klass.annotations.addAll(annotations);
            klass.typeAnnotations.addAll(typeAnnotations);
            return klass;
        }
    }

    public static non-sealed abstract class FieldTemplate extends Template<Field> {
        public final int type() { return FIELD; }

        public List<Attribute> attributes = new ArrayList<>();
        public List<Annotation> annotations = new ArrayList<>();
        public List<Annotation.Type> typeAnnotations = new ArrayList<>();

        public abstract Access access();
        public abstract String name();
        public abstract String desc();
        public abstract String signature();
        public abstract Object value();

        public final Field assemble() {
            final Field field = new Field(access(), name(), desc(), signature(), value());
            field.annotations.addAll(this.annotations);
            field.typeAnnotations.addAll(this.typeAnnotations);
            field.attributes.addAll(this.attributes);
            return field;
        }
    }

    public static non-sealed abstract class MethodTemplate extends Template<Method> {
        public final int type() { return METHOD; }

        public List<Attribute> attributes = new ArrayList<>();
        public List<Parameter> parameters = new ArrayList<>();
        public List<Local> locals = new ArrayList<>();
        public List<Block> blocks = new ArrayList<>();
        public List<Annotation> annotations = new ArrayList<>();
        public List<Annotation.Local> localAnnotations = new ArrayList<>();
        public List<List<Annotation>> parameterAnnotations = new LinkedList<>();
        public List<Annotation.Type> typeAnnotations = new ArrayList<>();

        public abstract Access access();
        public abstract String name();
        public abstract String desc();
        public abstract String signature();
        public abstract List<String> exceptions();
        public abstract Object annotationDefault();

        public abstract Instructions instructions();

        public final Method assemble() {
            final Method method = new Method(access(), name(), desc(), signature(), exceptions());
            method.attributes.addAll(attributes);
            method.parameters.addAll(parameters);
            method.locals.addAll(locals);
            method.blocks.addAll(blocks);
            method.annotations.addAll(annotations);
            method.localAnnotations.addAll(localAnnotations);
            method.parameterAnnotations.addAll(parameterAnnotations);
            method.typeAnnotations.addAll(typeAnnotations);
            method.annotationDefault = annotationDefault();
            method.setInstructions(instructions());
            return method;
        }
    }

    public static non-sealed abstract class ModuleTemplate extends Template<Module> {
        public final int type() { return MODULE; }

        public List<Module.Require> requires = new ArrayList<>();
        public List<Module.Export> exports = new ArrayList<>();
        public List<Module.Open> opens = new ArrayList<>();
        public List<Module.Provide> provides = new ArrayList<>();

        public abstract Access access();
        public abstract String name();
        public abstract String version();
        public abstract String main();
        public abstract List<String> packages();
        public abstract List<String> uses();

        public final Module assemble() {
            final Module module = new Module(name(), access(), version());
            module.main = main();
            module.packages.addAll(packages());
            module.uses.addAll(uses());
            module.requires.addAll(requires);
            module.exports.addAll(exports);
            module.opens.addAll(opens);
            module.provides.addAll(provides);
            return null;
        }
    }

    public static non-sealed abstract class RecordTemplate extends Template<Record> {
        public final int type() { return RECORD; }

        public List<Attribute> attributes = new ArrayList<>();
        public List<Annotation> annotations = new ArrayList<>();
        public List<Annotation.Type> typeAnnotations = new ArrayList<>();

        public abstract String name();
        public abstract String desc();
        public abstract String signature();

        public Record assemble() {
            final Record record = new Record(name(), desc(), signature());
            record.annotations.addAll(this.annotations);
            record.typeAnnotations.addAll(this.typeAnnotations);
            record.attributes.addAll(this.attributes);
            return record;
        }
    }
}