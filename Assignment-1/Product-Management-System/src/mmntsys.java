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

    void listProducts(){
        System.out.println("Manufacturer: "+ this.name + " has the following products: ");
        for(product p: this.products){
            p.printProduct(false);
        }
    }
}



class product extends entity{
    manufacturer m;

    public product(Scanner S, int id){
        super(S, id);
        this.m = null;
    }

    void printProduct(Boolean printManufacturer){
        super.printEntity();
        if(!printManufacturer){
            return;
        }
        if(this.m != null){
            System.out.println("Manufacturer: " + this.m.name);
        }
        else{
            System.out.println("Manufacturer: Not assigned");
        }
    }


    void deleteProduct(Map <Integer, shop> shops){
        //TODO: delete the product from the main map

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
    Map<product, Integer> purchases;          //contains the purchased products

    public customer(Scanner S, int id){
        super(S, id);
        System.out.println("Enter the zipcode: ");
        this.zipcode = S.nextInt();
        this.purchases = new HashMap<product, Integer>();
    }

    void printPurchases(){
        System.out.println("Purchases: ");
        if(this.purchases.isEmpty()){
            System.out.println("No purchases");
        }
        else{
            for(product p : this.purchases.keySet()){
                p.printProduct(true);
                System.out.println("Quantity: " + this.purchases.get(p));
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

    void printShop(){
        super.printEntity();
        System.out.println("Zipcode: " + this.zipcode);
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
    int getZipcode(){
        return this.zipcode;
    }
    int getProductsDelivered(){
        return this.products_delivered;
    }
    void printDeliveryagent(){
        super.printEntity();
        System.out.println("Zipcode: " + this.zipcode);
        System.out.println("Products Delivered: " + this.products_delivered);
    }
}


class order{
    customer c;
    product productWanted;
    int quantity;

    public order(Scanner S, Map<Integer, customer> customers, Map<Integer, product> products){
        System.out.println("Enter the customer ID: ");
        int id = S.nextInt();
        if(!customers.containsKey(id)){
            System.out.println(id + " is not a valid customer ID");
            return;
        }
        this.c = customers.get(id);
        System.out.println("Enter the ID of the product wanted: ");
        id = S.nextInt();
        if(!products.containsKey(id)){
            System.out.println(id + " is not a valid product ID");
            return;
        }
        this.productWanted = products.get(id);
        System.out.println("Enter the quantity: ");
        int quantity = S.nextInt();
        while(quantity <= 0){
            System.out.println("Invalid quantity");
            System.out.println("Enter the quantity: ");
            quantity = S.nextInt();
        }
        this.quantity = quantity;
    }
}


public class mmntsys{
    //All hashmaps store entities by their ID
    static Map<Integer, manufacturer> manufacturers = new HashMap<Integer, manufacturer>();
    static Map<Integer, product> products = new HashMap<Integer, product>();
    static Map<Integer, customer> customers = new HashMap<Integer, customer>();
    static Map<Integer, shop> shops = new HashMap<Integer, shop>();
    static Map<Integer, deliveryagent> deliveryagents = new HashMap<Integer, deliveryagent>();

    void processOrder(order orderReceived){
        int zipcode = orderReceived.c.zipcode;
        shop shopDelivering = null;
        for(Map.Entry<Integer, shop> entry : shops.entrySet()){
            //entry.getValue() is the shop
            if(entry.getValue().zipcode == zipcode){
                if(entry.getValue().inventory.containsKey(orderReceived.productWanted)){
                    //if the product is in the inventory of the shop, we deliver it
                    shopDelivering = entry.getValue();
                    break;
                }
            }
        }
        if(shopDelivering == null){
            System.out.println("Product " + orderReceived.productWanted.name + " not available in any shop in zipcode " + zipcode);
            return;
        }
        if(shopDelivering.inventory.get(orderReceived.productWanted) < orderReceived.quantity){
            System.out.println("Not enough products in the inventory of shop " + shopDelivering.name + " in zipcode " + zipcode);
            return;
        }
        //we find a delivery agent that can deliver the product
        deliveryagent deliveryAgent = null;
        for(Map.Entry<Integer, deliveryagent> entry : deliveryagents.entrySet()){
            //entry.getValue() is the delivery agent
            if(entry.getValue().zipcode == zipcode){
                if(deliveryAgent==null){
                    deliveryAgent = entry.getValue();
                }
                else if(entry.getValue().getProductsDelivered() < deliveryAgent.getProductsDelivered()){
                    deliveryAgent = entry.getValue();
                }
            }
        }
        if(deliveryAgent == null){
            System.out.println("No delivery agent available in zipcode " + zipcode);
            return;
        }

        //we deliver the product
        shopDelivering.inventory.put(orderReceived.productWanted, shopDelivering.inventory.get(orderReceived.productWanted) - orderReceived.quantity);
        deliveryAgent.products_delivered += orderReceived.quantity;
        System.out.println("Product " + orderReceived.productWanted.name + " delivered to customer " + orderReceived.c.name + " in zipcode " + zipcode);
    }

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