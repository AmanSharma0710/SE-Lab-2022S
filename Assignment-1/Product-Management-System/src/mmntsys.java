import java.util.*;

class entity{
    int id;
    String name;
    public entity(Scanner S, int id){
        this.id = id;
        System.out.println("Enter the name: ");
        this.name = S.nextLine();
    }
    public void printEntity(){
        System.out.println("ID: "+ this.id);
        System.out.println("Name: "+ this.name);
    }
}

class manufacturer extends entity{
    Set<product> products;          //contains the manufactured products
    public manufacturer(Scanner S, int id){
        super(S, id);
        this.products = new HashSet<product>();
    }
}

class product extends entity{
    manufacturer m;
    public product(Scanner S, int id){
        super(S, id);
        this.m = null;
    }
}

class customer extends entity{
    int zipcode;
    Set<product> products;          //contains the purchased products
    public customer(Scanner S, int id){
        super(S, id);
        System.out.println("Enter the zipcode: ");
        this.zipcode = S.nextInt();
        this.products = new HashSet<product>();
    }
}

class shop extends entity{
    int zipcode;
    Map<product, Integer> inventory; //contains the inventory of the shop
    public shop(Scanner S, int id){
        super(S, id);
        System.out.println("Enter the zipcode: ");
        this.zipcode = S.nextInt();
        this.inventory = new HashMap<product, Integer>();
    }
}

class deliveryagent extends entity{
    int zipcode;
    int products_delivered;
    public deliveryagent(Scanner S, int id){
        super(S, id);
        System.out.println("Enter the zipcode: ");
        this.zipcode = S.nextInt();
        this.products_delivered = 0;
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
