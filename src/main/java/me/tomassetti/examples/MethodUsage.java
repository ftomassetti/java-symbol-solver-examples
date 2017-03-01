package me.tomassetti.examples;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.MethodCallExpr;
import me.tomassetti.support.SpecificNodeIterator;
import me.tomassetti.symbolsolver.javaparsermodel.JavaParserFacade;
import me.tomassetti.symbolsolver.model.declarations.MethodDeclaration;
import me.tomassetti.symbolsolver.model.resolution.SymbolReference;
import me.tomassetti.symbolsolver.model.resolution.TypeSolver;
import me.tomassetti.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import me.tomassetti.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import me.tomassetti.symbolsolver.resolution.typesolvers.JreTypeSolver;
import java.io.File;
import me.tomassetti.support.DirExplorer;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;

public class MethodUsage {
    private static int solved = 0;
    private static int unsolved = 0;
    private static int errors = 0;

    public static List<File> findJavaFiles(File projectDir) {
        LinkedList<File> files = new LinkedList<File>();
        new DirExplorer((level, path, file) -> path.endsWith(".java"), (level, path, file) -> {
            //System.out.println(path);
            //System.out.println(Strings.repeat("=", path.length()));
            files.add(file);
        }).explore(projectDir);
        return files;
    }

    public static List<MethodCallExpr> descendantsOfType(CompilationUnit cu) {
        LinkedList<MethodCallExpr> descendants = new LinkedList<>();
        new SpecificNodeIterator<MethodCallExpr>(
                new SpecificNodeIterator.NodeHandler<MethodCallExpr>() {
                    @Override
                    public boolean handle(MethodCallExpr node) {
                        descendants.add(node);
                        return true;
                    }
                },
                MethodCallExpr.class
        ).explore(cu);

        return descendants;
    }

    public static void processJavaFile(File file, JavaParserFacade javaParserFacade) throws FileNotFoundException {
        System.out.println(file);
        descendantsOfType(JavaParser.parse(file)).forEach(
                it->{
                    System.out.print(" * L"+it.getBegin().line+" "+ it+ " ");
                    try {
                        SymbolReference<MethodDeclaration> methodRef = javaParserFacade.solve(it);
                        if (methodRef.isSolved()) {
                            solved++;
                            MethodDeclaration methodDecl = methodRef.getCorrespondingDeclaration();
                            System.out.println("  -> "+methodDecl.getQualifiedSignature());
                        } else {
                            unsolved++;
                            System.out.println(" ???");
                        }
                    } catch (Exception e) {
                        System.out.println(" ERR "+e.getMessage());
                        errors++;
                    } catch (Throwable t) {
                        t.printStackTrace();;
                    }
                }
        );
    }

    public static TypeSolver typeSolver() {
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new JreTypeSolver());
        combinedTypeSolver.add(
                new JavaParserTypeSolver(new File("src/main/resources/javaparser-core"))
        );
        combinedTypeSolver.add(
                new JavaParserTypeSolver(new File("src/main/resources/javaparser-generated-sources"))
        );
        return combinedTypeSolver;
    }

    public static void main(String[] args) {
        File projectDir = new File("src/main/resources/javaparser-core");
        List<File> javaFiles = findJavaFiles(projectDir);
        JavaParserFacade javaParserFacade = JavaParserFacade.get(typeSolver());
        javaFiles.forEach(it -> {
            try {
                processJavaFile(it, javaParserFacade);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        });
        System.out.println("solved "+solved+" unsolved "+unsolved+" errors "+errors);
    }
}
