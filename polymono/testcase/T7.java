class Node {
    public Node f;
    public int val;
    public static Node global;

    public void foo(){
        // Node n = new Node();
        // n.f = new Node();
        f = new Node();
        System.out.println("This is Node foo");
        global = new Node();
        bar();
        zar();
        for(int i = 0; i < 10; i++){
            System.out.println(i);
        }
    }
    //getter setter for val
    public int getVal(){
        return val;
    }
    public void setVal(int val){
        this.val = val;
    }
    //getter setter for f
    public Node getF(){
        return f;
    }
    public void setF(Node f){
        this.f = f;
    }
    static public void bar(){
        System.out.println("This is Node bar");
        T7 t = new T7();
        t.foo();
        Node n = new Node();
        n.zar();
    }
    public void zar(){
        System.out.println("This is Node zar");
    }
}

public class T7 {
    public static void main(String[] args){
        Node n = new Node();
        n.foo();
        n.setVal(10);
        System.out.println(n.getVal());
        Node n2 = new Node();
        n2.setVal(20);
        System.out.println(n2.getVal());
        n.setF(n2);
        System.out.println(n.getF().getVal());
    }
    public void foo(){
        System.out.println("This is T7 foo");
    }
}

