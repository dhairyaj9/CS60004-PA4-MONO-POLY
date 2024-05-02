public class T9{
    public T9 f;
    public int val;
    public static T9 global;

    public static void foo(T9 a, int b){
        System.out.println("This is T9 foo");
        global = new T9();
        bar();
        zar();
        for(int i = 0; i < 10; i++){
            System.out.println(i);
        }
    }
    
    public static T9 goo(T9 a, int b){
        T9 temp = new T9();
        temp.f = zar();
        temp.f.f = new T9();
        if(temp.f != null){
            System.out.println("temp.f is not null");
        }
        temp.val = b;
        return temp;
    }

    static public void bar(){
        System.out.println("This is T9 bar");
    }
    public static T9 zar(){
        System.out.println("This is T9 zar");
        T9 temp = new T9();
        temp.f = new T9();
        return temp.f;
    }

    public static void main(String[] args){
        T9 n = new T9();
        foo(n, 10);
        T9 t2 = goo(n, 15);
        t2.f = new T9();
    }
}

