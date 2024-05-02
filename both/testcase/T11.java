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

public class T11 {
	
    public static void check(A x){
        x.foo();
    }


	public static void main(String[] args) {
        A a;
        int i = 0;
        i += 1;
        // if(i == 0){
        //     a = new A();
        // }
        // else 
        if(i == 1){
            a = new B();
        }
        else if(i == 2){
            a = new C();
        }
        else {
            a = new D();
        }
		
        check(a);

        A a2;
        int i2 = 0;
        i2 += 1;
        // if(i == 0){
        //     a = new A();
        // }
        // else 
        if(i2 == 1){
            a2 = new A();
        }
        else if(i2 == 2){
            a2 = new B();
        }
        else {
            a2 = new C();
        }

        check(a2);

    }
	
}
















// class A {
//     public void foo(){
//         System.out.println("This is A foo");
// 		return;
//     }
// }

// class B extends A{
//     public void foo(){
//         System.out.println("This is B foo");
//         A a3;
//         zar();
//         bar();
// 		return;
//     }
//     static public void bar(){
//         System.out.println("This is B bar");
// 		return;
//     }
//     public void zar(){
//         System.out.println("This is B zar");
//     }
// }

// class C extends A{
//     public void foo(){
//         System.out.println("This is C foo");
// 		return;
//     }
// }

// class D extends B{
//     public void foo(){
//         System.out.println("This is D foo");
//         zar();
//         bar();
// 		return;
//     }
//     static public void bar(){
//         System.out.println("This is D bar");
// 		return;
//     }
//     public void zar(){
//         System.out.println("This is D zar");
//     }
// }

// class H{
//     public H foo(){
//         System.out.println("This is H foo");
// 		return new H();
//     }
// }

// public class T11 {
	
// 	public static void main(String[] args) {
// 		// A a = new B();
//         A a2 = new A();
//         a2.foo();

//         A a3;
//         int c = 100;
//         c += 1;
//         if(c > 100){
//             a3 = new B();
//         }else{
//             a3 = new C();
//         }
//         a3.foo();
//         // a.foo();
//         B b1 = new B();
//         D d1 = new D();
//         b1 = d1;
//         int sss = 1;
//         sss += 1;
//         b1.foo();
//         b1.bar();
//         d1.bar();
//         H h = new H();
//         H h2 = h.foo();

//         int d = 100;
//         c += 1;
        
//         A a4 = ((A)b1);
//         a4.foo();
//         // ((B)a2).foo();
//         B b2 = new D();
//         b2.foo();

//         // System.out.println("This is D foo");
//         // b2.zar();
//         // D.bar();

// 		return;
// 	}
	
// }


