public class T10{
    public T10 f;
    public int val;
    public static T10 global;

    public static void foo(T10 a){
        System.out.println("This is T10 foo");
        global = a;
        // bar();
        // zar();
        for(int i = 0; i < 10; i++){
            T10 temp = a;
            T10 temp2 = a.f;
            if(temp2 != null){
                System.out.println("temp is not null");
            }
            System.out.println(i);
        }
        T10 t2 = goo(a);
        t2.f = new T10();
    }

    public static T10 goo(T10 a){
        T10 temp = new T10();
        // temp.f = zar();
        temp.f = new T10();
        if(temp.f != null){
            System.out.println("temp.f is not null");
        }
        temp.val = 5;
        return temp;
    }

    public static void main(String[] args){
        T10 n = new T10();
        n.f = new T10();
        foo(n);
        // T10 t2 = goo(n);
        // t2.f = new T10();
    }
}

