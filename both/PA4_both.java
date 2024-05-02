import java.util.List;

import soot.*;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.graph.UnitGraph;
import soot.jimple.internal.*;

public class PA4_both{
    public static boolean doPolytoMono = true;
    public static boolean doMethodInlining = false;
    public static boolean debugPolytoMono = false;
    public static boolean debugMethodInline = false;
    public static String cgtype = "PTA";
    
    public static void main(String[] args) {
        String classPath = "."; 	// change to appropriate path to the test class
		String dir = "./testcase";

        String output_format = args[0]; 
        String testClass = args[1];
        // String testClass = "Test" + args[0]; // change to appropriate test class

        //Set up arguments for Soot
        String[] sootArgs = {
            "-cp", classPath, "-pp",  // sets the class path for Soot
            "-w",                     // whole program analysis
            "-f", output_format,               // jimple file
            "-p", "cg.cha", "enabled:false", // sets CHA enabled to true
            "-p", "cg.spark", "enabled:true,verbose:true", // sets Spark enabled to true
            "-p", "cg", "all-reachable:true", // sets all reachable to true
            "-keep-line-number",      // preserves line numbers in input Java files
            "-main-class", testClass,	  // specify the main class
            "-process-dir", dir,      // directory of classes to analyze
        };

        // Create transformer for analysis
        AnalysisTransformer_Method_Inlining analysisTransformer_Method_Inlining = new AnalysisTransformer_Method_Inlining();
        AnalysisTransformer_Poly_to_Mono analysisTransformer_Poly_to_Mono = new AnalysisTransformer_Poly_to_Mono();
        // Add transformer to appropriate pack in PackManager; PackManager will run all packs when soot.Main.main is called
        PackManager.v().getPack("wjtp").add(new Transform("wjtp.ptm", analysisTransformer_Poly_to_Mono));
        PackManager.v().getPack("wjtp").add(new Transform("wjtp.mi", analysisTransformer_Method_Inlining));

        // Call Soot's main method with arguments
        soot.Main.main(sootArgs);

    }
}

