import java.util.*;

class entity{
    public int id;
    private String name;
    public entity(Scanner S, int id){
        this.id = id;
        System.out.println("Enter name: ");
        this.name = S.nextLine();
    }
    public void printEntity(){
        System.out.println("ID: "+ this.id);
        System.out.println("Name: "+ this.name);
    }
}

public class mmntsys{
    public static void main(String[] args){
        Scanner S = new Scanner(System.in);
        int id = 1;
        System.out.println("Enter number of entities: ");
        int n = S.nextInt();
        S.nextLine();
        entity[] entities = new entity[n];
        for(int i = 0; i < n; i++){
            entities[i] = new entity(S, id);
            id++;
        }
        for(int i = 0; i < n; i++){
            System.out.println("\nEntity "+ (i+1));
            entities[i].printEntity();
        }
        S.close();
    }    
}
