class A {
    public void foo(){
        System.out.println("This is A foo");
		return;
    }
}

class B extends A{
    public void foo(){
        System.out.println("This is B foo");
        A a3;
        zar();
        bar();
		return;
    }
    static public void bar(){
        System.out.println("This is B bar");
		return;
    }
    public void zar(){
        System.out.println("This is B zar");
    }
}

class C extends A{
    public void foo(){
        System.out.println("This is C foo");
		return;
    }
}

class D extends B{
    public void foo(){
        System.out.println("This is D foo");
        zar();
        bar();
		return;
    }
    static public void bar(){
        System.out.println("This is D bar");
		return;
    }
    public void zar(){
        System.out.println("This is D zar");
    }
}

class H{
    public H foo(){
        System.out.println("This is H foo");
		return new H();
    }
}

public class T6 {
	
	public static void main(String[] args) {
		// A a = new B();
        A a2 = new A();
        a2.foo();

        A a3;
        int c = 100;
        c += 1;
        if(c > 100){
            a3 = new B();
        }else{
            a3 = new C();
        }
        a3.foo();
        // a.foo();
        B b1 = new B();
        C c1 = new C();
        // b1 = d1;
        int sss = 1;
        sss += 1;
        c1.foo();
        b1.bar();
        // d1.bar();
        H h = new H();
        H h2 = h.foo();
        h2.foo();
        int d = 100;
        c += 1;
        
        A a4 = ((A)b1);
        a4.foo();
        // ((B)a2).foo();
        B b2 = new D();
        b2.foo();

        // System.out.println("This is D foo");
        // b2.zar();
        // D.bar();

		return;
	}
	
}