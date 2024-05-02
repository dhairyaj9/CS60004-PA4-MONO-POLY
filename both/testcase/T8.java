public class T8{
    public T8 f;
    public int val;
    public static T8 global;

    public void foo(T8 a, int b){
        f = new T8();
        System.out.println("This is T8 foo");
        global = new T8();
        bar();
        zar();
        for(int i = 0; i < 10; i++){
            System.out.println(i);
        }
    }
    public T8 goo(T8 a, int b){
        T8 temp = new T8();
        temp.f = zar();
        temp.f.f = new T8();
        if(temp.f != null){
            System.out.println("temp.f is not null");
        }
        temp.val = b;
        return temp;
    }

    static public void bar(){
        System.out.println("This is T8 bar");
        
    }
    public T8 zar(){
        System.out.println("This is T8 zar");
        T8 temp = new T8();
        temp.f = new T8();
        return temp.f;
    }

    public static void main(String[] args){
        T8 n = new T8();
        n.foo(n, 10);
        T8 t2 = n.goo(n, 15);
        t2.f = new T8();
    }
}

