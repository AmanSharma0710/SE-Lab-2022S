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

    void addProduct(product p){
        if(p.m != this){
            System.out.println("Product already assigned to another manufacturer, do you want to change manufacturer? (y/n)");
            Scanner reader = new Scanner(System.in);
            String ans = reader.nextLine();
            reader.close();
            if(ans.equals("y") || ans.equals("Y")){
                p.m.products.remove(p); //remove the product from the previous manufacturer
                p.m = this;             //assign the product to the new manufacturer
            }
            else{
                return;
            }
        }
        this.products.add(p);           //add the product to the manufacturer
    }
}



class product extends entity{
    manufacturer m;

    public product(Scanner S, int id){
        super(S, id);
        this.m = null;
    }

    void printProduct(){
        super.printEntity();
        if(this.m != null){
            System.out.println("Manufacturer: " + this.m.name);
        }
        else{
            System.out.println("Manufacturer: Not assigned");
        }
    }


    void deleteProduct(Map <Integer, shop> shops){
        if(this.m != null){
            this.m.products.remove(this);
        }
        for(Map.Entry<Integer, shop> entry : shops.entrySet()){
            //entry.getValue() is the shop
            //if the product in in the inventory of the shop, we remove it
            if(entry.getValue().inventory.containsKey(this)){
                entry.getValue().inventory.remove(this);
            }
        }
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

    void printPurchases(){
        System.out.println("Purchases: ");
        if(this.products.size() == 0){
            System.out.println("No purchases");
        }
        else{
            for(product p : this.products){
                p.printProduct();
            }
        }
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

    void addProduct(product p, int quantity){
        if(this.inventory.containsKey(p)){
            this.inventory.put(p, this.inventory.get(p) + quantity);
        }
        else{
            this.inventory.put(p, quantity);
        }
    }

    void printInventory(){
        System.out.println("Inventory: ");
        if(this.inventory.size() == 0){
            System.out.println("No inventory");
        }
        else{
            for(Map.Entry<product, Integer> entry : this.inventory.entrySet()){
                //entry.getKey() is the product
                //entry.getValue() is the quantity
                System.out.println("Product: " + entry.getKey().name + " Quantity: " + entry.getValue());
            }
        }
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