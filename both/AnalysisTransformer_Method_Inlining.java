import java.util.*;
import soot.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;
import soot.toolkits.graph.*;
import soot.toolkits.scalar.SimpleLiveLocals;
import soot.toolkits.scalar.LiveLocals;
import soot.jimple.toolkits.annotation.logic.LoopFinder;
import soot.jimple.toolkits.annotation.logic.Loop;


//import for PointsToAnalysis, PointsToSet, Type, Local, Context, Unit, JInvokeStmt, JAssignStmt
import soot.jimple.internal.*;
//import for invokeExpr
import soot.jimple.*;


public class AnalysisTransformer_Method_Inlining extends SceneTransformer {
    static CallGraph cg;
    public static Map<SootMethod, Boolean> methodInlined = new HashMap<>();
    
    @Override
    protected void internalTransform(String arg0, Map<String, String> arg1) {
        Set<SootMethod> methods = new HashSet <>();
        cg = Scene.v().getCallGraph();

        PointsToAnalysis pa = Scene.v().getPointsToAnalysis();
        SootMethod mainMethod = Scene.v().getMainMethod();
        // getlistofMethods(mainMethod, methods, pa);
        process_inlining(mainMethod, pa);

        if(PA4_both.debugMethodInline) System.out.println("Starting inlining");
        if(PA4_both.debugMethodInline) System.out.println("\n\n|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_Done with inlining_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|_|\n\n");
        // for (SootMethod m : methods) {
        //     processCFG2(m, pa);
        // }
        for (SootMethod m : methods) {
            if(PA4_both.debugMethodInline) System.out.println(m);
        }
        if(PA4_both.debugMethodInline) System.out.println("\n\n\nMethod Inlining ends %%%%%%%%%%%%%%%%%%%%%%%%\n\n\n");
    
    }

    protected static void process_inlining(SootMethod method, PointsToAnalysis pa) {
        if(method.toString().contains("init")  ) { 
            return; 
        }
        //skip if it is a java library method
        if (method.isJavaLibraryMethod()) {
            return;
        }

        Body body = method.getActiveBody();
        UnitGraph cfg = new BriefUnitGraph(body);
        

        if(PA4_both.debugMethodInline) System.out.println("\n-------------------------------------------- FUNCTION - " + body.getMethod() + "-------------------------------------------");
        

        // Check if the method contains loops
        LoopFinder loopFinder = new LoopFinder();
        loopFinder.transform(body);
        Set<Loop> loops = loopFinder.getLoops(body);
        if (loops.size() > 0) {
            if(PA4_both.debugMethodInline) System.out.println("Loops found in method: " + loops.size());
            for (Loop loop : loops) {
                if(PA4_both.debugMethodInline) System.out.println("Loop: " + loop.getHead());
            }
        } else {
            if(PA4_both.debugMethodInline) System.out.println("No loops found in method");
        }


        // Units for the body
        PatchingChain<Unit> units = body.getUnits();
        Iterator<Unit> it = units.snapshotIterator();


        if(PA4_both.debugMethodInline) System.out.println("Starting iterating through caller body\n");

        
        // Iterate through the units in the method body
        while (it.hasNext()) {
            Unit current_unit = it.next();
            Unit previous_unit = units.getPredOf(current_unit);
            Unit next_unit = units.getSuccOf(current_unit);

            //print the three units
            if(PA4_both.debugMethodInline) System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");
            if(PA4_both.debugMethodInline) System.out.println("Printing the current, previous and next units");
            if(PA4_both.debugMethodInline) System.out.println("Current unit: " + current_unit);
            if(PA4_both.debugMethodInline) System.out.println("Previous unit: " + previous_unit);
            if(PA4_both.debugMethodInline) System.out.println("Next unit: " + next_unit);
            if(PA4_both.debugMethodInline) System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx");



            // Check if the unit is a statement and is an invoke instruction
            if (current_unit instanceof Stmt) {
                Stmt current_stmt = (Stmt) current_unit;

                // Handle invoke statements for static and virtual invokes
                if (current_stmt instanceof InvokeStmt) {
                    processInvokeStmt(body, units, current_stmt, loops, current_unit, pa, previous_unit, next_unit);
                } else if (current_stmt instanceof AssignStmt) {
                    AssignStmt assignStmt = (AssignStmt) current_stmt;
                    if (assignStmt.getRightOp() instanceof InvokeExpr) {
                        processInvokeStmt(body, units, current_stmt, loops, current_unit, pa, previous_unit, next_unit);
                    }
                }
            }
        }

        if(PA4_both.debugMethodInline) System.out.println("Done iterating through caller body\n\n");
    }

    protected static void processInvokeStmt(Body body, PatchingChain<Unit> units, Stmt current_stmt, Set<Loop> loops, Unit current_unit, PointsToAnalysis pa, Unit previous_unit, Unit next_unit) {

        InvokeExpr invokeExpr = current_stmt.getInvokeExpr();

        if (invokeExpr instanceof StaticInvokeExpr) {
            SootMethod calledMethod = invokeExpr.getMethod();
            if (calledMethod.getDeclaringClass().isJavaLibraryClass()) {
                return;
            }
            if (calledMethod.isConstructor()) {
                return;
            }
            Body calledBody = calledMethod.retrieveActiveBody();
            int calledMethodSize = calledBody.getUnits().size();

            boolean insideLoop = false;
            for (Loop loop : loops) {
                if (loop.getLoopStatements().contains(current_unit)) {
                    insideLoop = true;
                    break;
                }
            }

            boolean shouldInline = false;
            if (!insideLoop && calledMethodSize < 50) {
                // Case 1: Not inside a loop and size < 50 bytes
                shouldInline = true;
            } else if (insideLoop && calledMethodSize < 100) {
                // Case 2: Inside a loop and size < 100 bytes
                shouldInline = true;
            }

            // Inline the method if conditions are met
            if (shouldInline) {
                if (!methodInlined.containsKey(calledMethod)) {
                    methodInlined.put(calledMethod, true);
                    process_inlining(calledMethod, pa);
                }
                if(PA4_both.debugMethodInline) System.out.println("Inlining method: " + calledMethod + " in method: " + body.getMethod()+ " at line: " + current_unit.getJavaSourceStartLineNumber() + "\n\n");
                inlineMethod(body, units, current_stmt, calledMethod, invokeExpr, current_unit, previous_unit, next_unit);
            }
        }
        
    }
    public static class UnitWrapper {
        public Unit unit;
    
        public UnitWrapper(Unit unit) {
            this.unit = unit;
        }
    }

    protected static void inlineMethod(Body caller_body, PatchingChain<Unit> caller_units, Stmt current_stmt, SootMethod calledMethod, InvokeExpr invokeExpr, Unit current_unit_caller, Unit previous_unit_caller, Unit next_unit_caller) {
        JimpleBody callee_Body = (JimpleBody) calledMethod.retrieveActiveBody();
        PatchingChain<Unit> callee_Units_og = callee_Body.getUnits();

        //Clone the entire body of the called method
        JimpleBody callee_Body_clone = (JimpleBody) callee_Body.clone();
        PatchingChain<Unit> callee_Units_ofclonedBody = callee_Body_clone.getUnits();



        // Create a mapping of old variables to new variables eg r1 -> r1_inlined_54
        Map<Local, Local> localMapping = new HashMap<>();
        int lineNumber = current_unit_caller.getJavaSourceStartLineNumber();
        for (Local local : callee_Body_clone.getLocals()) {
            Local newLocal = Jimple.v().newLocal(local.getName() + "_inlined_" + lineNumber, local.getType());
            caller_body.getLocals().add(newLocal);
            localMapping.put(local, newLocal);
        }


        Unit insertionPoint = previous_unit_caller; //TODO check if this can be null

        UnitWrapper insertionPointWrapper = new UnitWrapper(insertionPoint);

        //iterate over the units of the cloned callee body and call a function for the transformation
        //use the snapshot iterator to avoid concurrent modification exception
        // PeekAndPreviousIterator<Unit> it = (PeekAndPreviousIterator<Unit>)callee_Units_ofclonedBody.snapshotIterator();
        Iterator<Unit> it = callee_Units_ofclonedBody.snapshotIterator();
        if(PA4_both.debugMethodInline) System.out.println("Printing all the new units added to the caller body: \n");

        while (it.hasNext()) {
            Unit callee_unit_current = it.next();
            transformUnitandAddToCaller(callee_unit_current, localMapping, caller_body, caller_units, current_stmt, invokeExpr, current_unit_caller, previous_unit_caller, next_unit_caller, callee_Units_ofclonedBody, insertionPointWrapper);
        }

        if(PA4_both.debugMethodInline) System.out.println("--Done printing all the new units added to the caller body");

        //TODO IMP TO DO AFTER TRANSFORMATION AS IDENTITY STMT WILL CHANGE FIRST UNIT
        Unit firstUnit = callee_Units_ofclonedBody.getFirst();
        if(PA4_both.debugMethodInline) System.out.println("First unit of the callee body: " + firstUnit + "\n\n\n\n");
        //redirect the current_unit_caller to the first unit of the callee body
        current_unit_caller.redirectJumpsToThisTo(firstUnit);

        //remove the invoke statement from the caller body 
        //TODO not calling remove() it does patchBeforeRemoval which we dont want

        //put an assert to check if the successor of the current unit is the first unit of the callee body
        //first get the successor of the current unit
        Unit successor = caller_units.getSuccOf(current_unit_caller);
        assert successor == firstUnit : "OOOOOOOO - Successor of the current unit is not the first unit of the callee body";

        caller_units.remove(current_unit_caller);
        // PatchingChain<Unit> inner_chain = (PatchingChain<Unit>)caller_units.getNonPatchingChain();
        // inner_chain.remove(current_unit_caller);

    }

    


    protected static void transformUnitandAddToCaller(Unit callee_unit, Map<Local, Local> localMapping, Body caller_body, PatchingChain<Unit> caller_units, Stmt current_stmt, InvokeExpr invokeExpr, Unit current_unit_caller, Unit previous_unit_caller, Unit next_unit_caller, PatchingChain<Unit> callee_units, UnitWrapper insertionPointWrapper) {
        
        if (callee_unit instanceof IdentityStmt) {
            IdentityStmt identityStmt = (IdentityStmt) callee_unit;
            Value leftOp = identityStmt.getLeftOp();
            Value rightOp = identityStmt.getRightOp();
            if(rightOp instanceof ThisRef){
                if(PA4_both.debugMethodInline) System.out.println("YYYYY - ThisRef not handled");
            }
            else if(rightOp instanceof ParameterRef){
                int index = ((ParameterRef) rightOp).getIndex();
                Value arg = invokeExpr.getArg(index);

                AssignStmt newAssignStmt = Jimple.v().newAssignStmt(localMapping.get(leftOp), arg);

                callee_units.swapWith(callee_unit, newAssignStmt);

                caller_units.insertAfter(newAssignStmt, insertionPointWrapper.unit);//TODO can previous_unit_caller be null
                if(PA4_both.debugMethodInline) System.out.println("\nAdded : " + newAssignStmt);
                //update the insertion point
                Unit new_insertionPoint = newAssignStmt;

                insertionPointWrapper.unit = new_insertionPoint;

            }
        }



        else if (callee_unit instanceof AssignStmt) {
            AssignStmt assignStmt = (AssignStmt) callee_unit;
            Value leftOp = assignStmt.getLeftOp();
            Value rightOp = assignStmt.getRightOp();

            // Modify the left side
            if (leftOp instanceof Local) {
                Local leftLocal = (Local) leftOp;
                assignStmt.setLeftOp(localMapping.get(leftLocal));
            } else if (leftOp instanceof JInstanceFieldRef) {
                JInstanceFieldRef leftFieldRef = (JInstanceFieldRef) leftOp;
                modifyFieldRef(leftFieldRef, localMapping);
            }

            // Modify the right side
            if (rightOp instanceof Local) {
                Local rightLocal = (Local) rightOp;
                assignStmt.setRightOp(localMapping.get(rightLocal));
            } 
            else if (rightOp instanceof JInstanceFieldRef) {
                JInstanceFieldRef rightFieldRef = (JInstanceFieldRef) rightOp;
                modifyFieldRef(rightFieldRef, localMapping);
            } 
            else if (rightOp instanceof VirtualInvokeExpr) {
                VirtualInvokeExpr virtualInvokeExpr = (VirtualInvokeExpr) rightOp;
                // Modify the base of the virtual invoke expression
                Value base = virtualInvokeExpr.getBase();
                if (base instanceof Local) {
                    Local baseLocal = (Local) base;
                    virtualInvokeExpr.setBase(localMapping.get(baseLocal));
                }
                // Modify the arguments of the virtual invoke expression
                List<Value> args = new ArrayList<>();
                for (Value arg : virtualInvokeExpr.getArgs()) {
                    if (arg instanceof Local) {
                        args.add(localMapping.get(arg));
                    } else {
                        args.add(arg);
                    }
                }
                VirtualInvokeExpr newVirtualInvokeExpr = Jimple.v().newVirtualInvokeExpr((Local)virtualInvokeExpr.getBase(), virtualInvokeExpr.getMethodRef(), args);
                //set the rightOp of the assign statement to the new virtual invoke expression
                assignStmt.setRightOp(newVirtualInvokeExpr);
            } 
            else if (rightOp instanceof StaticInvokeExpr) { //TODO anything else for static??
                StaticInvokeExpr staticInvokeExpr = (StaticInvokeExpr) rightOp;
                // Modify the arguments of the static invoke expression
                List<Value> args = new ArrayList<>();
                for (Value arg : staticInvokeExpr.getArgs()) {
                    if (arg instanceof Local) {
                        args.add(localMapping.get(arg));
                    } else {
                        args.add(arg);
                    }
                }
                StaticInvokeExpr newStaticInvokeExpr = Jimple.v().newStaticInvokeExpr(staticInvokeExpr.getMethodRef(), args);
                //set the rightOp of the assign statement to the new static invoke expression
                assignStmt.setRightOp(newStaticInvokeExpr);
            }
            else if (rightOp instanceof SpecialInvokeExpr){
                SpecialInvokeExpr specialInvokeExpr = (SpecialInvokeExpr) rightOp;
                // Modify the base of the virtual invoke expression
                Value base = specialInvokeExpr.getBase();
                if (base instanceof Local) {
                    Local baseLocal = (Local) base;
                    specialInvokeExpr.setBase(localMapping.get(baseLocal));
                }
                // Modify the arguments of the virtual invoke expression
                List<Value> args = new ArrayList<>();
                for (Value arg : specialInvokeExpr.getArgs()) {
                    if (arg instanceof Local) {
                        args.add(localMapping.get(arg));
                    } else {
                        args.add(arg);
                    }
                }
                SpecialInvokeExpr specialInvokeExpr_new = Jimple.v().newSpecialInvokeExpr((Local)specialInvokeExpr.getBase(), specialInvokeExpr.getMethodRef(), args);
                //set the rightOp of the assign statement to the new virtual invoke expression
                assignStmt.setRightOp(specialInvokeExpr_new);
            }
            else if (rightOp instanceof BinopExpr) {
                BinopExpr binopExpr = (BinopExpr) rightOp;
                //check if the op1 operand is a local and modify it
                if (binopExpr.getOp1() instanceof Local) {
                    Local leftLocal = (Local) binopExpr.getOp1();
                    binopExpr.setOp1(localMapping.get(leftLocal));
                }
                //check if the op2 operand is a local and modify it
                if (binopExpr.getOp2() instanceof Local) {
                    Local rightLocal = (Local) binopExpr.getOp2();
                    binopExpr.setOp2(localMapping.get(rightLocal));
                }
            } 
            else if (rightOp instanceof UnopExpr) {
                UnopExpr unopExpr = (UnopExpr) rightOp;
                //check if the op operand is a local and modify it
                if (unopExpr.getOp() instanceof Local) {
                    Local local = (Local) unopExpr.getOp();
                    unopExpr.setOp(localMapping.get(local));
                }
            }
            else if (rightOp instanceof ArrayRef) {
                ArrayRef arrayRef = (ArrayRef) rightOp;
                //check if the base of the array reference is a local and modify it
                if (arrayRef.getBase() instanceof Local) {
                    Local baseLocal = (Local) arrayRef.getBase();
                    arrayRef.setBase(localMapping.get(baseLocal));
                }
                //check if the index of the array reference is a local and modify it
                if (arrayRef.getIndex() instanceof Local) {
                    Local indexLocal = (Local) arrayRef.getIndex();
                    arrayRef.setIndex(localMapping.get(indexLocal));
                }
            } 
            else if (rightOp instanceof CastExpr) {
                //case not handled
            }
            else if (rightOp instanceof InstanceOfExpr) {
                InstanceOfExpr instanceOfExpr = (InstanceOfExpr) rightOp;
                //check if the op of the instance of expression is a local and modify it
                if (instanceOfExpr.getOp() instanceof Local) {
                    Local local = (Local) instanceOfExpr.getOp();
                    instanceOfExpr.setOp(localMapping.get(local));
                }
            } 
            else if (rightOp instanceof NewArrayExpr) {
                NewArrayExpr newArrayExpr = (NewArrayExpr) rightOp;
                //check if the size of the new array expression is a local and modify it
                if (newArrayExpr.getSize() instanceof Local) {
                    Local local = (Local) newArrayExpr.getSize();
                    newArrayExpr.setSize(localMapping.get(local));
                }
            } 
            else if (rightOp instanceof NewMultiArrayExpr) {
                //case not handled
            }


            // Insert the modified assignment statement into the caller body
            caller_units.insertAfter(assignStmt, insertionPointWrapper.unit);
            if(PA4_both.debugMethodInline) System.out.println("\nAdded : " + assignStmt);
            Unit insertionPoint = assignStmt;
            insertionPointWrapper.unit = insertionPoint;
        } 


        
        else if (callee_unit instanceof InvokeStmt) {
            InvokeStmt callee_invokeStmt = (InvokeStmt) callee_unit;
            InvokeExpr calleeunit_invokeExpr = callee_invokeStmt.getInvokeExpr();
            if (calleeunit_invokeExpr instanceof StaticInvokeExpr) {
                StaticInvokeExpr staticInvokeExpr_callee = (StaticInvokeExpr) calleeunit_invokeExpr;
                SootMethod method = staticInvokeExpr_callee.getMethod();
                List<Value> args = new ArrayList<>();
                for (Value arg : staticInvokeExpr_callee.getArgs()) {
                    if (arg instanceof Local) {
                        args.add(localMapping.get(arg));
                    } else {
                        args.add(arg);
                    }
                }
                StaticInvokeExpr newStaticInvokeExpr = Jimple.v().newStaticInvokeExpr(method.makeRef(), args);
                InvokeStmt newInvokeStmt = Jimple.v().newInvokeStmt(newStaticInvokeExpr);

                //FIRST SWAP THE OLD INVOKE WITH THE NEW INVOKE IN THE CALLEE BODY TO GET THE REDIRECTIONS MAPPED
                callee_units.swapWith(callee_unit, newInvokeStmt);
                
                // Insert the modified assignment statement into the caller body
                caller_units.insertAfter(newInvokeStmt, insertionPointWrapper.unit);
                if(PA4_both.debugMethodInline) System.out.println("\nAdded : " + newInvokeStmt);
                Unit insertionPoint = newInvokeStmt;
                insertionPointWrapper.unit = insertionPoint;
            }
            else if(calleeunit_invokeExpr instanceof VirtualInvokeExpr){
                VirtualInvokeExpr calleeunit_virtualInvokeExpr = (VirtualInvokeExpr) calleeunit_invokeExpr;
                SootMethod method = calleeunit_virtualInvokeExpr.getMethod();

                Value base = calleeunit_virtualInvokeExpr.getBase();
                if (base instanceof Local) {
                    Local baseLocal = (Local) base;
                    calleeunit_virtualInvokeExpr.setBase(localMapping.get(baseLocal));
                }

                List<Value> args = new ArrayList<>();
                for (Value arg : calleeunit_virtualInvokeExpr.getArgs()) {
                    if (arg instanceof Local) {
                        args.add(localMapping.get(arg));
                    } else {
                        args.add(arg);
                    }
                }
                VirtualInvokeExpr newVirtualInvokeExpr = Jimple.v().newVirtualInvokeExpr((Local)calleeunit_virtualInvokeExpr.getBase(), method.makeRef(), args);
                InvokeStmt newInvokeStmt = Jimple.v().newInvokeStmt(newVirtualInvokeExpr);

                //FIRST SWAP THE OLD INVOKE WITH THE NEW INVOKE IN THE CALLEE BODY TO GET THE REDIRECTIONS MAPPED
                callee_units.swapWith(callee_unit, newInvokeStmt);
                
                // Insert the modified assignment statement into the caller body
                caller_units.insertAfter(newInvokeStmt, insertionPointWrapper.unit);
                if(PA4_both.debugMethodInline) System.out.println("\nAdded : " + newInvokeStmt);
                Unit insertionPoint = newInvokeStmt;
                insertionPointWrapper.unit = insertionPoint;

            }
            else if(calleeunit_invokeExpr instanceof SpecialInvokeExpr){
                SpecialInvokeExpr calleeunit_specialInvokeExpr = (SpecialInvokeExpr) calleeunit_invokeExpr;
                SootMethod method = calleeunit_specialInvokeExpr.getMethod();

                Value base = calleeunit_specialInvokeExpr.getBase();
                if (base instanceof Local) {
                    Local baseLocal = (Local) base;
                    calleeunit_specialInvokeExpr.setBase(localMapping.get(baseLocal));
                }

                List<Value> args = new ArrayList<>();
                for (Value arg : calleeunit_specialInvokeExpr.getArgs()) {
                    if (arg instanceof Local) {
                        args.add(localMapping.get(arg));
                    } else {
                        args.add(arg);
                    }
                }
                SpecialInvokeExpr newSpecialInvokeExpr = Jimple.v().newSpecialInvokeExpr((Local)calleeunit_specialInvokeExpr.getBase(), method.makeRef(), args);
                InvokeStmt newInvokeStmt = Jimple.v().newInvokeStmt(newSpecialInvokeExpr);

                //FIRST SWAP THE OLD INVOKE WITH THE NEW INVOKE IN THE CALLEE BODY TO GET THE REDIRECTIONS MAPPED
                callee_units.swapWith(callee_unit, newInvokeStmt);
                
                // Insert the modified Invoke statement into the caller body
                caller_units.insertAfter(newInvokeStmt, insertionPointWrapper.unit);
                if(PA4_both.debugMethodInline) System.out.println("\nAdded : " + newInvokeStmt);
                Unit insertionPoint = newInvokeStmt;
                insertionPointWrapper.unit = insertionPoint;
            }
        } 



        else if (callee_unit instanceof GotoStmt) {
            //ADD THIS DIRECTLY TO THE CALLER BODY
            caller_units.insertAfter(callee_unit, insertionPointWrapper.unit);
            if(PA4_both.debugMethodInline) System.out.println("\nAdded : " + callee_unit);
            Unit insertionPoint = callee_unit;
            insertionPointWrapper.unit = insertionPoint;

        }


        

        else if (callee_unit instanceof IfStmt) {
            IfStmt ifStmt = (IfStmt) callee_unit;
            Value condition = ifStmt.getCondition();

            // Check if the condition is a BinopExpr (e.g., EqExpr or GeExpr)
            if (condition instanceof BinopExpr) {
                BinopExpr binopExpr = (BinopExpr) condition;

                // Modify the left operand
                Value leftOp = binopExpr.getOp1();
                if (leftOp instanceof Local) {
                    Local leftLocal = (Local) leftOp;
                    binopExpr.setOp1(localMapping.get(leftLocal));
                }

                // Modify the right operand
                Value rightOp = binopExpr.getOp2();
                if (rightOp instanceof Local) {
                    Local rightLocal = (Local) rightOp;
                    binopExpr.setOp2(localMapping.get(rightLocal));
                }

                // Set the modified condition back into the IfStmt
                ifStmt.setCondition(binopExpr);
            }

            // Insert the modified IfStmt into the caller body
            caller_units.insertAfter(ifStmt, insertionPointWrapper.unit);
            if(PA4_both.debugMethodInline) System.out.println("\nAdded : " + ifStmt);
            Unit insertionPoint = ifStmt;
            insertionPointWrapper.unit = insertionPoint;

        }



        else if (callee_unit instanceof ReturnVoidStmt) {
            GotoStmt newGotoStmt = Jimple.v().newGotoStmt(next_unit_caller);
            callee_units.swapWith(callee_unit, newGotoStmt);
            caller_units.insertAfter(newGotoStmt, insertionPointWrapper.unit);
            if(PA4_both.debugMethodInline) System.out.println("\nAdded : " + newGotoStmt);
            Unit insertionPoint = newGotoStmt;
            insertionPointWrapper.unit = insertionPoint;


        }




        else if (callee_unit instanceof ReturnStmt) {
            ReturnStmt returnStmt = (ReturnStmt) callee_unit;

            //check if current_unit is a JInvoke or Jassign 
            if(current_stmt instanceof InvokeStmt){
                GotoStmt newGotoStmt = Jimple.v().newGotoStmt(next_unit_caller);
                callee_units.swapWith(callee_unit, newGotoStmt);
                caller_units.insertAfter(newGotoStmt, insertionPointWrapper.unit);
                if(PA4_both.debugMethodInline) System.out.println("\nAdded : " + newGotoStmt);
                Unit insertionPoint = newGotoStmt;
                insertionPointWrapper.unit = insertionPoint;

            }
            else if(current_stmt instanceof AssignStmt){
                //get the left hand side of the assign statement
                Value leftOp_caller = ((AssignStmt)current_stmt).getLeftOp();
                //get the right hand side of the return statement
                Value rightOp_callee_return = returnStmt.getOp();
                //add an assignment statement to the caller body
                AssignStmt newAssignStmt = Jimple.v().newAssignStmt(leftOp_caller, localMapping.get(rightOp_callee_return));
                //swap the return statement with the assignment statement in the callee body
                callee_units.swapWith(callee_unit, newAssignStmt);
                //add the assignment statement to the caller body
                caller_units.insertAfter(newAssignStmt, insertionPointWrapper.unit);

                if(PA4_both.debugMethodInline) System.out.println("\nAdded : " + newAssignStmt);
                Unit insertionPoint = newAssignStmt;
                insertionPointWrapper.unit = insertionPoint;

                //now add a goto statement to the caller body
                GotoStmt newGotoStmt = Jimple.v().newGotoStmt(next_unit_caller);
                caller_units.insertAfter(newGotoStmt, insertionPointWrapper.unit);
                if(PA4_both.debugMethodInline) System.out.println("Added : " + newGotoStmt);
                Unit insertionPoint2 = newGotoStmt;
                insertionPointWrapper.unit = insertionPoint2;
            }
        } 
        else{
            if(PA4_both.debugMethodInline) System.out.println("XXXXX - Unhandled type of unit");
        }
    }

    private static void modifyFieldRef(JInstanceFieldRef fieldRef, Map<Local, Local> localMapping) {
        Value base = fieldRef.getBase();
        if (base instanceof Local) {
            Local baseLocal = (Local) base;
            fieldRef.setBase(localMapping.get(baseLocal));
        }
    }

    

    protected static void processCFG2(SootMethod method, PointsToAnalysis pa) {
        if(method.toString().contains("init")  ) { return; }
        Body body = method.getActiveBody();
        // Get the callgraph 
        UnitGraph cfg = new BriefUnitGraph(body);

        // Units for the body
        PatchingChain<Unit> units = body.getUnits();
        if(PA4_both.debugMethodInline) System.out.println("\n-------------------------------------------- FUNCTION - " + body.getMethod() + "-------------------------------------------");
        for (Unit u : units) {

            Iterator<Edge> edges = Scene.v().getCallGraph().edgesOutOf(u);

            if(PA4_both.debugMethodInline) System.out.println("---------------Unit: " + u + " ---------------");
            if(PA4_both.debugMethodInline) System.out.println();

        }
    }
    
    // private static void getlistofMethods(SootMethod method, Set<SootMethod> reachableMethods, PointsToAnalysis pa) {
    //     // Avoid revisiting methods
    //     if (reachableMethods.contains(method)) {
    //         return;
    //     }
    //     reachableMethods.add(method);
    //     Iterator<Edge> edges = Scene.v().getCallGraph().edgesOutOf(method);
    //     while (edges.hasNext()) {
    //         Edge edge = edges.next();
    //         SootMethod targetMethod = edge.tgt();
    //         // Recursively explore callee methods
    //         if (!targetMethod.isJavaLibraryMethod()) {
    //             getlistofMethods(targetMethod, reachableMethods, pa);
    //         }
    //     }
    //     process_inlining(method, pa);

    // }

}

