import java.util.*;

class helloworld{
    public static class blabla{
        int a;
        int b;
        public void printblabla(){
            System.out.println("a is "+ this.a);
            System.out.println("b is "+ this.b);
        }
    }
    public static void main(String[] args){
        int x;
        System.out.println("Enter a number: ");
        Scanner S = new Scanner(System.in);
        x = S.nextInt();
        System.out.println(x);
        blabla bla = new blabla();
        bla.a = 1;
        bla.b = 2;
        bla.printblabla();
        S.close();
    }
}