class T2_A {
    public int foo(int x){
		return x*3;
    }
}

class T2_B extends T2_A{
    public int foo(int x){
        int y = zar(x) + bar(x) + x+3;
		return y;
    }
    static public int bar(int x){
		return x+1;
    }
    public int zar(int x){
        return x-1;
    }
}

class T2_C extends T2_A{
    public int foo(int x){
		return x-5;
    }
}

class T2_D extends T2_B{
    public int foo(int x){
        int y = zar(x) + bar(x) + x+7;
		return y;
    }
    static public int bar(int x){
		return x+2;
    }
    public int zar(int x){
        return x-3;
    }
}

class T2_H{
    public T2_H foo(){
        System.out.println("This is T2_H foo");
		return new T2_H();
    }
}

public class T2 {
	
	public static void main(String[] args) {
		// T2_A a = new T2_B();
        T2_A a2 = new T2_A();
        a2.foo(3);

        T2_A a3;
        int c = 100;
        c += 1;
        if(c > 100){
            a3 = new T2_B();
        }else{
            a3 = new T2_C();
        }
        a3.foo(5);
        // a.foo();
        T2_B b1 = new T2_B();
        T2_C c1 = new T2_C();
        // b1 = d1;
        int sss = 1;
        sss += 1;
        int aaa = 1, bbb = 3;
        for(int i = 0; i < 1000000; i++){
            aaa = c1.foo(aaa);
            bbb = b1.bar(bbb);
        }
        for(int i = 0; i < 1000000; i++){
            aaa = c1.foo(aaa);
            bbb = b1.bar(bbb);
        }
        for(int i = 0; i < 1000000; i++){
            aaa = c1.foo(aaa);
            bbb = b1.bar(bbb);
        }
        System.out.println("aaa : "+aaa);
        System.out.println("bbb : "+bbb);
        c1.foo(16);
        b1.bar(22);
        // d1.bar();
        T2_H h = new T2_H();
        T2_H h2 = h.foo();
        h2.foo();
        int d = 100;
        c += 1;
        
        T2_A a4 = ((T2_A)b1);
        a4.foo(24);
        // ((T2_B)a2).foo();
        T2_B b2 = new T2_D();
        b2.foo(3);

        // System.out.println("This is T2_D foo");
        // b2.zar();
        // T2_D.bar();

		return;
	}
	
}