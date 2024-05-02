class T1_A {
    public T1_A a1;
    public int zar(int x){
		return x*3;
    }
}

class T1_B extends T1_A{
    public T1_A a2;
    public T1_A foo(T1_A x){
        T1_B b = new T1_D();
        x.a1 = b;
        T1_A a = new T1_C();
        int k = 0;
        k = a.zar(k+1);
        k = a.zar(k+2);
        k = a.zar(k+3);
        k = a.zar(k+4);
        k = a.zar(k+5);
        k = a.zar(k+6);
        k = a.zar(k+7);
        k = a.zar(k+8);
        k = a.zar(k+9);
        k = a.zar(k+10);
        k = a.zar(k+11);
        k = a.zar(k+12);
        k = a.zar(k+13);
        k = a.zar(k+14);
        k = a.zar(k+15);
        k = a.zar(k+16);
        k = a.zar(k+17);
        k = a.zar(k+18);
        k = a.zar(k+19);
        k = a.zar(k+20);
        k = a.zar(k+21);
        k = a.zar(k+22);
        k = a.zar(k+23);
        k = a.zar(k+24);
        k = a.zar(k+25);
        k = a.zar(k+26);
        k = a.zar(k+27);
        k = a.zar(k+28);
        k = a.zar(k+29);
        k = a.zar(k+30);
        k = a.zar(k+31);
        k = a.zar(k+32);
        k = a.zar(k+33);
        k = a.zar(k+34);
        k = a.zar(k+35);
        k = a.zar(k+36);
        k = a.zar(k+37);
        k = a.zar(k+38);
        k = a.zar(k+39);
        k = a.zar(k+40);
        k = a.zar(k+41);
        k = a.zar(k+42);
        k = a.zar(k+43);
        k = a.zar(k+44);
        k = a.zar(k+45);
        k = a.zar(k+46);
        k = a.zar(k+47);
        k = a.zar(k+48);
        k = a.zar(k+49);
        k = a.zar(k+50);
        b.a2 = new T1_B();
        T1_A y = b.a2;
        return y;

    }
    static public int bar(int k){
        T1_A a = new T1_A();
        k = a.zar(k+1);
        k = a.zar(k+2);
        k = a.zar(k+3);
        k = a.zar(k+4);
        k = a.zar(k+5);
        k = a.zar(k+6);
        k = a.zar(k+7);
        k = a.zar(k+8);
        k = a.zar(k+9);
        k = a.zar(k+10);
        k = a.zar(k+11);
        k = a.zar(k+12);
        k = a.zar(k+13);
        k = a.zar(k+14);
        k = a.zar(k+15);
        k = a.zar(k+16);
        k = a.zar(k+17);
        k = a.zar(k+18);
        k = a.zar(k+19);
        k = a.zar(k+20);
        k = a.zar(k+21);
        k = a.zar(k+22);
        k = a.zar(k+23);
        k = a.zar(k+24);
        k = a.zar(k+25);
        k = a.zar(k+26);
        k = a.zar(k+27);
        k = a.zar(k+28);
        k = a.zar(k+29);
        k = a.zar(k+30);
        k = a.zar(k+31);
        k = a.zar(k+32);
        k = a.zar(k+33);
        k = a.zar(k+34);
        k = a.zar(k+35);
        k = a.zar(k+36);
        k = a.zar(k+37);
        k = a.zar(k+38);
        k = a.zar(k+39);
        k = a.zar(k+40);
        k = a.zar(k+41);
        k = a.zar(k+42);
        k = a.zar(k+43);
        k = a.zar(k+44);
        k = a.zar(k+45);
        k = a.zar(k+46);
        k = a.zar(k+47);
        k = a.zar(k+48);
        k = a.zar(k+49);
        k = a.zar(k+50);
		return k+1;
    }
    public int zar(int x){
        return x-1;
    }
}

class T1_C extends T1_A{
    public T1_A a3;
    public T1_A foo(T1_A x){
        T1_C c = new T1_C();
        x.a1 = c;
        c.a3 = new T1_C();
        T1_A y = c.a3;
        return y;
    }
    public int zar(int x){
        for(int i = 0; i < 100; i++){
            x = x + 1;
        }
        for(int i = 0; i < 50; i++){
            x = x - 2;
        }
        for(int i = 0; i < 100; i++){
            x = x + 1;
        }
        for(int i = 0; i < 50; i++){
            x = x - 2;
        }
        return x+5;
    }
}

class T1_D extends T1_B{
    public T1_B b1;
    public T1_B foo(T1_A x){
        T1_D d = new T1_D();
        d.b1 = new T1_D();
        T1_B y = d.b1;
        return y;
    }
    static public int bar(int x){
		return x+2;
    }
    public int zar(int x){
        return x-3;
    }
}



public class T1 {
	
	public static void main(String[] args) {
		// T1_A a = new T1_B();
        T1_A a = new T1_A();

        T1_B b = new T1_D();

        b.a2 = new T1_B();
        b.a1 = new T1_B();
        b.a1.a1 = new T1_B();
        T1_A a2 = b.a1.a1;
        b.a2.a1 = b.foo(b);

        T1_B b2 = new T1_D();
        T1_C c2 = new T1_C();
    
        int i = 80;
        a2 = b.foo(a2);
        a2 = b2.foo(b);
        i += c2.zar(i);
        a.a1 = new T1_D();
        T1_D d = new T1_D();
        d.b1 = new T1_D();
        int k = i+2;
        k = d.b1.zar(k);
        k += b.bar(k);
        b2.foo(b2);
        T1_B b3 = new T1_D();
        k -= b3.zar(k);
        a2 = b.foo(b2);
        a2 = b.foo(a2);
        a2 = b2.foo(b);
        b2.foo(b2);
        a2 = b.foo(b2);
        a2 = b.foo(a2);
        a2 = b2.foo(b);
        i += c2.zar(i);
        a.a1 = new T1_D();
        T1_D d2 = new T1_D();
        d.b1 = new T1_D();
        int k1 = i+2;
        k1 += b.bar(k1);
        T1_B b4 = new T1_D();
        k1 -= b4.zar(k1);
        b2.foo(b2);
        a2 = b.foo(b2);

        T1_C c1 = new T1_C();
        int aaa = 1, bbb = 3;
        for(i = 0; i < 100000; i++){
            a2 = c1.foo(c1);
            bbb = c1.zar(aaa);
        }
        for(i = 0; i < 100000; i++){
            a2 = c1.foo(c1);
            bbb = c1.zar(aaa);
        }
        
		return;
	}
	
}