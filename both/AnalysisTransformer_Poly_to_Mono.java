import java.util.*;
import soot.*;
import soot.jimple.toolkits.callgraph.CHATransformer;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.jimple.toolkits.pointer.InstanceKey;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.SimpleLiveLocals;
import soot.toolkits.scalar.LiveLocals;

//import for PointsToAnalysis, PointsToSet, Type, Local, Context, Unit, JInvokeStmt, JAssignStmt
import soot.jimple.internal.*;
import soot.jimple.spark.SparkTransformer;
//import for invokeExpr
import soot.jimple.*;


public class AnalysisTransformer_Poly_to_Mono extends SceneTransformer {
    static CallGraph cg;
    static int virtual_edges =0, static_edges = 0, virtual_invokes = 0, static_invokes = 0, poly_invokes = 0, mono_invokes = 0, total_invokes = 0;
    @Override
    protected void internalTransform(String arg0, Map<String, String> arg1) {
        Set<SootMethod> methods = new HashSet <>();
        cg = Scene.v().getCallGraph();

        PointsToAnalysis pa = Scene.v().getPointsToAnalysis();
        SootMethod mainMethod = Scene.v().getMainMethod();
        getlistofMethods(mainMethod, methods, pa);
        for (SootMethod m : methods) {
            if(PA4_both.debugPolytoMono) System.out.println(m);  
        }
        //print virtual_edges, static_edges, poly_invokes, mono_invokes
        if (PA4_both.doPolytoMono == false) {
            System.out.println("Poly to Mono  "+ PA4_both.cgtype +"  Stats starts %%%%%%%%%");
            System.out.println("virtual_edges: " + virtual_edges);
            System.out.println("static_edges: " + static_edges);
            System.out.println("virtual_invokes: " + virtual_invokes);
            System.out.println("static_invokes: " + static_invokes);
            System.out.println("poly_invokes: " + poly_invokes);
            System.out.println("mono_invokes: " + mono_invokes);
            System.out.println("total_invokes: " + (poly_invokes+mono_invokes));
        }
        System.out.println("Poly to Mono ends %%%%%%%%%%%%%%%%%%%%%%%");
    }

    protected static void collectStaticReport(SootMethod method, PointsToAnalysis pa) {
        if(method.toString().contains("init")  ) { return; }
        Body body = method.getActiveBody();
        PatchingChain<Unit> units = body.getUnits();
        for (Unit u : units) {
            Iterator<Edge> edges = Scene.v().getCallGraph().edgesOutOf(u);


            if(PA4_both.debugPolytoMono)System.out.println("------------------Unit: " + u + " -------------------");
            
            //Printing the edges of the call graph
            if(PA4_both.debugPolytoMono) System.out.println();
            int edge_count_out = 0;
            while (edges.hasNext()) {
                Edge edge = edges.next();
                if(PA4_both.debugPolytoMono) System.out.println("Printing edge:");
                if(PA4_both.debugPolytoMono) System.out.println(edge);
                SootMethod targetMethod = edge.tgt();
                if(PA4_both.debugPolytoMono) System.out.println("Printing method within edge:");
                if(PA4_both.debugPolytoMono) System.out.println(targetMethod);
                edge_count_out+=1;
                if(PA4_both.debugPolytoMono) System.out.println();
            }
            if(PA4_both.debugPolytoMono) System.out.println();


            edges = Scene.v().getCallGraph().edgesOutOf(u);

            if(u instanceof JInvokeStmt){
                JInvokeStmt invokeStmt = (JInvokeStmt) u;
                if(invokeStmt.getInvokeExpr() instanceof VirtualInvokeExpr){
                    VirtualInvokeExpr v_invoke= (VirtualInvokeExpr) invokeStmt.getInvokeExpr();
                    if(v_invoke.getMethod() != null && v_invoke.getMethod().isJavaLibraryMethod() == false){
                        //ystem.out.println("------------------Unit: " + u + " -------------------");
                        // System.out.println("Virtual invoke: "+v_invoke.getMethod());
                        virtual_invokes += 1;
                        int edge_count = 0;
                        while (edges.hasNext()) {
                            edges.next();
                            edge_count+=1;
                        }
                        virtual_edges += edge_count;
                        if(edge_count > 1){
                            // System.out.println("poly_invokes: "+v_invoke.getMethod());
                            poly_invokes += 1;
                        }
                        else{
                            // System.out.println("mono_invokes: "+v_invoke.getMethod());
                            mono_invokes += 1;
                        }
                    }
                }
                else if(invokeStmt.getInvokeExpr() instanceof StaticInvokeExpr){
                    StaticInvokeExpr s_invoke= (StaticInvokeExpr) invokeStmt.getInvokeExpr();
                    if(s_invoke.getMethod() != null && s_invoke.getMethod().isJavaLibraryMethod() == false){
                        // System.out.println("Static invoke: "+s_invoke.getMethod());

                        static_invokes += 1;
                        static_edges += 1;
                        // System.out.println("mono_invokes: "+s_invoke.getMethod());
                        mono_invokes += 1;
                    }
                }
            }
            else if(u instanceof JAssignStmt){
                JAssignStmt assignStmt = (JAssignStmt) u;
                if(assignStmt.containsInvokeExpr() && assignStmt.getInvokeExpr() instanceof VirtualInvokeExpr){
                    VirtualInvokeExpr v_invoke= (VirtualInvokeExpr) assignStmt.getInvokeExpr();
                    if(v_invoke.getMethod() != null && v_invoke.getMethod().isJavaLibraryMethod() == false){
                        //System.out.println("------------------Unit: " + u + " -------------------");
                        // System.out.println("Virtual invoke: "+v_invoke.getMethod());
                        virtual_invokes += 1;
                        int edge_count = 0;
                        while (edges.hasNext()) {
                            edges.next();
                            edge_count+=1;
                        }
                        virtual_edges += edge_count;
                        if(edge_count > 1){
                            // System.out.println("poly_invokes: "+v_invoke.getMethod());
                            poly_invokes += 1;
                        }
                        else{
                            // System.out.println("mono_invokes: "+v_invoke.getMethod());
                            mono_invokes += 1;
                        }
                    }
                }
                else if(assignStmt.containsInvokeExpr() && assignStmt.getInvokeExpr() instanceof StaticInvokeExpr){
                    StaticInvokeExpr s_invoke= (StaticInvokeExpr) assignStmt.getInvokeExpr();
                    if(s_invoke.getMethod() != null && s_invoke.getMethod().isJavaLibraryMethod() == false){
                        // System.out.println("Static invoke: "+s_invoke.getMethod());
                        static_invokes += 1;
                        static_edges += 1;
                        // System.out.println("mono_invokes: "+s_invoke.getMethod());
                        mono_invokes += 1;
                    }
                }
            }
        }
    }

    protected static void processCFG(SootMethod method, PointsToAnalysis pa) {
        if(method.toString().contains("init")  ) { return; }
        Body body = method.getActiveBody();
        PatchingChain<Unit> units = body.getUnits();
        if(PA4_both.debugPolytoMono) System.out.println("\n-------------------------------------------- FUNCTION - " + body.getMethod() + "-------------------------------------------");
        for (Unit u : units) {
            Iterator<Edge> edges = Scene.v().getCallGraph().edgesOutOf(u);

            if(PA4_both.debugPolytoMono) System.out.println("---------------Unit: " + u + " ---------------");

            //If unit contains a virtual invoke, then print the Type of all the objects that the receiver can point to using points-to analysis pa
            //first check if the unit is an instance of JInvokeStmt or JAssignStmt and then check if the invoke is virtual, if yes then print the Type of all the objects that the receiver can point to using points-to analysis pa
            //This can be done by calling pa.reachingObjects(receiver, context) and then iterating over the resulting PointsToSet and printing the Type of each object. context can be the unit itself.
            if(PA4_both.debugPolytoMono) System.out.println("Printing points to set for receiver if virtual invoke is present: ");
            if(u instanceof JInvokeStmt){
                JInvokeStmt invokeStmt = (JInvokeStmt) u;
                if(invokeStmt.getInvokeExpr() instanceof VirtualInvokeExpr){
                    VirtualInvokeExpr v_invoke= (VirtualInvokeExpr) invokeStmt.getInvokeExpr();
                    if(v_invoke.getMethod() != null && v_invoke.getMethod().isJavaLibraryMethod() == false){
                        Local receiver = (Local)v_invoke.getBase();
                        PointsToSet pts = pa.reachingObjects(receiver);
                        if(PA4_both.debugPolytoMono) System.out.println("Direct Invoke Points to set for receiver: " + receiver + " is: ");
                        for(Type t : pts.possibleTypes()){
                            if(PA4_both.debugPolytoMono) System.out.println(t);
                        }

                        if(pts.possibleTypes().size() == 1){
                            // if(PA4_both.debugPolytoMono) System.out.println("Printing polytomonopermision   "+PA4_both.doPolytoMono);
                            Type type_obj = pts.possibleTypes().iterator().next();
                            SootClass sc = Scene.v().getSootClass(type_obj.toString());
                            SootMethod targetMethod = sc.getMethodUnsafe(v_invoke.getMethod().getName(), v_invoke.getMethod().getParameterTypes(), v_invoke.getMethod().getReturnType());
                            SootMethod staticMethod = findOrCreateStaticMethod(sc, targetMethod);
                            modifyStaticMethodBody(staticMethod, targetMethod);
                            List<Value> argList = new ArrayList<>(v_invoke.getArgs());
                            argList.add(v_invoke.getBase());
                            invokeStmt.setInvokeExpr(Jimple.v().newStaticInvokeExpr(staticMethod.makeRef(), argList));
                        }
                    }
                }
                else{
                    if(PA4_both.debugPolytoMono) System.out.println(" XXXXX Direct Invoke but NO virtual invoke present in the unit");
                }
            }
            else if(u instanceof JAssignStmt){
                JAssignStmt assignStmt = (JAssignStmt) u;
                if(assignStmt.containsInvokeExpr() && assignStmt.getInvokeExpr() instanceof VirtualInvokeExpr){
                    VirtualInvokeExpr v_invoke= (VirtualInvokeExpr)  assignStmt.getInvokeExpr();
                    if(v_invoke.getMethod() != null && v_invoke.getMethod().isJavaLibraryMethod() == false){
                        Local receiver = (Local)v_invoke.getBase();
                        PointsToSet pts = pa.reachingObjects(receiver);
                        if(PA4_both.debugPolytoMono) System.out.println("AssignStmt Invoke Points to set for receiver: " + receiver + " is: ");
                        for(Type t : pts.possibleTypes()){
                            if(PA4_both.debugPolytoMono) System.out.println(t);
                        }

                        if(pts.possibleTypes().size() == 1){
                            Type type_obj = pts.possibleTypes().iterator().next();
                            SootClass sc = Scene.v().getSootClass(type_obj.toString());
                            SootMethod targetMethod = sc.getMethodUnsafe(v_invoke.getMethod().getName(), v_invoke.getMethod().getParameterTypes(), v_invoke.getMethod().getReturnType());
                            SootMethod staticMethod = findOrCreateStaticMethod(sc, targetMethod);
                            modifyStaticMethodBody(staticMethod, targetMethod);
                            List<Value> argList = new ArrayList<>(v_invoke.getArgs());
                            argList.add(v_invoke.getBase());
                            assignStmt.setRightOp(Jimple.v().newStaticInvokeExpr(staticMethod.makeRef(), argList));
                        }
                    }
                }
                else{
                    if(PA4_both.debugPolytoMono) System.out.println(" XXXXX AssignStmt Invoke but NO virtual invoke present in the unit");
                }
            }
        
            if(PA4_both.debugPolytoMono) System.out.println();

        }
    }

    private static SootMethod findOrCreateStaticMethod(SootClass sc, SootMethod targetMethod) {
        String staticMethodName = targetMethod.getName() + "_static";
        List<Type> parameterTypes = new ArrayList<>(targetMethod.getParameterTypes());
        parameterTypes.add(sc.getType()); 

        SootMethod staticMethod = sc.getMethodUnsafe(staticMethodName, parameterTypes, targetMethod.getReturnType());
        if (staticMethod == null) {
            staticMethod = new SootMethod(staticMethodName, parameterTypes, targetMethod.getReturnType(), Modifier.STATIC);
            sc.addMethod(staticMethod);
        }
        return staticMethod;

    }

    private static void modifyStaticMethodBody(SootMethod staticMethod, SootMethod targetMethod) {
        JimpleBody targetBody = (JimpleBody) targetMethod.retrieveActiveBody();
        JimpleBody modifiedBody = (JimpleBody) targetBody.clone();
        PatchingChain<Unit> units = modifiedBody.getUnits();
        for (Unit unit : units) {
            if (unit instanceof IdentityStmt) {
                IdentityStmt identityStmt = (IdentityStmt) unit;
                if (identityStmt.getRightOp() instanceof ThisRef) {
                    int lastParameterIndex = staticMethod.getParameterCount() - 1;
                    Type lastParameterType = staticMethod.getParameterType(lastParameterIndex);
                    ParameterRef lastParameterRef = Jimple.v().newParameterRef(lastParameterType, lastParameterIndex);
                    identityStmt.setRightOp(lastParameterRef);
                }
            }
        }
        staticMethod.setActiveBody(modifiedBody);
    }


    
    private static void getlistofMethods(SootMethod method, Set<SootMethod> reachableMethods, PointsToAnalysis pa) {
        if (reachableMethods.contains(method)) {
            return;
        }
        reachableMethods.add(method);
        Iterator<Edge> edges = Scene.v().getCallGraph().edgesOutOf(method);
        while (edges.hasNext()) {
            Edge edge = edges.next();
            SootMethod targetMethod = edge.tgt();
            if (!targetMethod.isJavaLibraryMethod()) {
                getlistofMethods(targetMethod, reachableMethods, pa);
            }
        }
        if(PA4_both.doPolytoMono == false){
            collectStaticReport(method, pa);
        }
        else if(PA4_both.doPolytoMono == true){
            processCFG(method, pa);
        }
    }
}
