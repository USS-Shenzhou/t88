package cn.ussshenzhou.t88.network;

import com.sun.source.util.*;
import com.sun.tools.javac.api.JavacTaskImpl;
import com.sun.tools.javac.code.Flags;
import com.sun.tools.javac.code.Symtab;
import com.sun.tools.javac.code.TypeTag;
import com.sun.tools.javac.tree.JCTree;
import com.sun.tools.javac.tree.JCTree.*;
import com.sun.tools.javac.tree.TreeMaker;
import com.sun.tools.javac.tree.TreeTranslator;
import com.sun.tools.javac.util.*;
import com.sun.tools.javac.util.List;

import javax.tools.Diagnostic;

/**
 * @author USS_Shenzhou
 */
public class ClientHandlerWrapper implements Plugin {
    @Override
    public String getName() {
        return "T88ClientHandlerWrapper";
    }

    @Override
    public void init(JavacTask task, String... args) {
        var impl = (JavacTaskImpl) task;
        var context = impl.getContext();
        var maker = TreeMaker.instance(context);
        var names = Names.instance(context);
        var trees = Trees.instance(task);

        task.addTaskListener(new TaskListener() {
            JCClassDecl currentClass;

            @Override
            public void finished(TaskEvent event) {
                if (event.getKind() != TaskEvent.Kind.PARSE) {
                    return;
                }
                var tree = (JCTree) event.getCompilationUnit();
                tree.accept(new TreeTranslator() {
                    @Override
                    public void visitClassDef(JCClassDecl clazz) {
                        var prev = currentClass;
                        currentClass = clazz;
                        super.visitClassDef(clazz);
                        currentClass = prev;
                    }

                    @Override
                    public void visitMethodDef(JCMethodDecl md) {
                        if (md.body == null) {
                            super.visitMethodDef(md);
                            return;
                        }
                        if (!(md.restype instanceof JCPrimitiveTypeTree) || ((JCPrimitiveTypeTree) md.restype).typetag != TypeTag.VOID) {
                            if (hasClientHandler(md)) {
                                trees.printMessage(Diagnostic.Kind.WARNING, "ClientHandler must return void.", tree, event.getCompilationUnit());
                            }
                            super.visitMethodDef(md);
                            return;
                        }
                        if (!hasClientHandler(md)) {
                            super.visitMethodDef(md);
                            return;
                        }
                        if ((md.mods.flags & Flags.STATIC) != 0) {
                            trees.printMessage(Diagnostic.Kind.WARNING, "ClientHandler should not be static!", tree, event.getCompilationUnit());
                            super.visitMethodDef(md);
                            return;
                        }
                        // this/super → OuterClass.this / OuterClass.super
                        final Name outerName = currentClass.name;
                        var rewriter = new TreeTranslator() {
                            @Override
                            public void visitIdent(JCIdent id) {
                                if (id.name == names._this) {
                                    result = maker.Select(maker.Ident(outerName), names._this);
                                    return;
                                }
                                if (id.name == names._super) {
                                    result = maker.Select(maker.Ident(outerName), names._super);
                                    return;
                                }
                                super.visitIdent(id);
                            }
                        };
                        var originalBody = md.body;
                        var rewritten = rewriter.translate(originalBody);
                        // public void run() { <originalBody> }
                        var runMethod = maker.MethodDef(
                                maker.Modifiers(com.sun.tools.javac.code.Flags.PUBLIC),
                                names.fromString("run"),
                                maker.TypeIdent(TypeTag.VOID),
                                List.nil(),
                                List.nil(),
                                List.nil(),
                                maker.Block(0, rewritten.stats),
                                null
                        );
                        //new Runnable(){ public void run(){ } }
                        var anonymousClass = maker.AnonymousClassDef(
                                maker.Modifiers(0),
                                List.of(runMethod)
                        );
                        //new Runnable(){ public void run(){ } }
                        var newRunnable = maker.NewClass(
                                null,
                                List.nil(),
                                maker.Ident(names.fromString("Runnable")),
                                List.nil(),
                                anonymousClass
                        );
                        // var wrapped = new Runnable(){ ... };
                        var varName = names.fromString("wrapped");
                        var varDecl = maker.VarDef(
                                maker.Modifiers(0),
                                varName,
                                maker.Ident(names.fromString("Runnable")),
                                newRunnable
                        );
                        // wrapped.run();
                        var invoke = maker.Exec(maker.Apply(
                                List.nil(),
                                maker.Select(maker.Ident(varName), names.fromString("run")),
                                List.nil()
                        ));
                        md.body = maker.Block(0, List.of(varDecl, invoke));
                        super.visitMethodDef(md);
                    }
                });
            }
        });
    }


    private static boolean hasClientHandler(JCMethodDecl md) {
        if (md.mods == null || md.mods.annotations == null) {
            return false;
        }
        for (JCAnnotation ann : md.mods.annotations) {
            String an = ann.annotationType.toString();
            if ("ClientHandler".equals(an) || an.endsWith(".ClientHandler")) {
                return true;
            }
        }
        return false;
    }
}
