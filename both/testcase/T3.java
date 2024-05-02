class T3_A {
    public T3_A a1;
}

class T3_B extends T3_A{
    public T3_A a2;
    public T3_A foo(T3_A x){
        T3_B b = new T3_D();
        x.a1 = b;
        b.a2 = new T3_B();
        T3_A y = b.a2;
        return y;

    }
}

class T3_C extends T3_A{
    public T3_A a3;
    public T3_A foo(T3_A x){
        T3_C c = new T3_C();
        x.a1 = c;
        c.a3 = new T3_C();
        T3_A y = c.a3;
        return y;
    }
}

class T3_D extends T3_B{
    public T3_B b1;
    public T3_B foo(T3_A x){
        T3_D d = new T3_D();
        d.b1 = new T3_D();
        T3_B y = d.b1;
        return y;
    }
}



public class T3 {
	
	public static void main(String[] args) {
		// T3_A a = new T3_B();
        T3_A a = new T3_A();

        T3_B b = new T3_D();

        b.a2 = new T3_B();
        b.a1 = new T3_B();
        b.a1.a1 = new T3_B();
        T3_A a2 = b.a1.a1;
        b.a2.a1 = b.foo(b);

        T3_B b2 = new T3_D();
        
        for(int i = 0; i < 1000000; i++){
            a2 = b.foo(a2);
            a2 = b2.foo(b);
            b2.foo(b2);
            a2 = b.foo(b2);
        }
        for(int i = 0; i < 1000000; i++){
            a2 = b.foo(a2);
            a2 = b2.foo(b);
            b2.foo(b2);
            a2 = b.foo(b2);
        }
        for(int i = 0; i < 1000000; i++){
            a2 = b.foo(a2);
            a2 = b2.foo(b);
            b2.foo(b2);
            a2 = b.foo(b2);
        }
        for(int i = 0; i < 1000000; i++){
            a2 = b.foo(a2);
            a2 = b2.foo(b);
            b2.foo(b2);
            a2 = b.foo(b2);
        }
        
		return;
	}
	
}