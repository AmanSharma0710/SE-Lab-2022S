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

    void printManufacturer(Boolean printProducts){
        super.printEntity();
        if(!printProducts)
            return;
        System.out.println("Products: ");
        for(product p: this.products){
            p.printProduct(false);
        }
    }

    void addProduct(product p){
        if(p.m == null){
            p.m = this;
        }
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

    void printCustomer(Boolean printPurchases){
        super.printEntity();
        System.out.println("Zipcode: "+ this.zipcode);
        if(!printPurchases)
            return;
        System.out.println("Purchases: ");
        for(Map.Entry<product, Integer> entry : this.purchases.entrySet()){
            entry.getKey().printProduct(false);
            System.out.println("Quantity: "+ entry.getValue());
        }
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
                entry.getKey().printProduct(true);
                System.out.println("Quantity: " + entry.getValue());
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


    static void addManufacturer(Scanner S, int id){
        manufacturers.put(id, new manufacturer(S, id));
    }
    static void addProduct(Scanner S, int id){
        products.put(id, new product(S, id));
    }
    static void addCustomer(Scanner S, int id){
        customers.put(id, new customer(S, id));
    }
    static void addShop(Scanner S, int id){
        shops.put(id, new shop(S, id));
    }
    static void addDeliveryagent(Scanner S, int id){
        deliveryagents.put(id, new deliveryagent(S, id));
    }

    static void printManufacturers(){
        System.out.println("Manufacturers: ");
        if(manufacturers.isEmpty()){
            System.out.println("No manufacturers");
        }
        else{
            for(Map.Entry<Integer, manufacturer> entry : manufacturers.entrySet()){
                //entry.getKey() is the ID
                //entry.getValue() is the manufacturer
                entry.getValue().printManufacturer(false);
            }
        }
    }

    static void printProducts(){
        System.out.println("Products: ");
        if(products.isEmpty()){
            System.out.println("No products");
        }
        else{
            for(Map.Entry<Integer, product> entry : products.entrySet()){
                //entry.getKey() is the ID
                //entry.getValue() is the product
                entry.getValue().printProduct(false);
            }
        }
    }

    static void printCustomers(){
        System.out.println("Customers: ");
        if(customers.isEmpty()){
            System.out.println("No customers");
        }
        else{
            for(Map.Entry<Integer, customer> entry : customers.entrySet()){
                //entry.getKey() is the ID
                //entry.getValue() is the customer
                entry.getValue().printCustomer(false);
            }
        }
    }

    static void printShops(){
        System.out.println("Shops: ");
        if(shops.isEmpty()){
            System.out.println("No shops");
        }
        else{
            for(Map.Entry<Integer, shop> entry : shops.entrySet()){
                //entry.getKey() is the ID
                //entry.getValue() is the shop
                entry.getValue().printShop();
            }
        }
    }

    static void printDeliveryAgents(){
        System.out.println("Deliveryagents: ");
        if(deliveryagents.isEmpty()){
            System.out.println("No deliveryagents");
        }
        else{
            for(Map.Entry<Integer, deliveryagent> entry : deliveryagents.entrySet()){
                //entry.getKey() is the ID
                //entry.getValue() is the deliveryagent
                entry.getValue().printDeliveryagent();
            }
        }
    }

    static void deleteManufacturer(Scanner S){
        printManufacturers();
        System.out.println("Enter the ID of the manufacturer to delete: ");
        int id = S.nextInt();
        while(!manufacturers.containsKey(id)){
            if(id==0){
                return;
            }
            System.out.println(id + " is not a valid manufacturer ID");
            System.out.println("Enter the ID of the manufacturer to delete(Enter 0 to exit): ");
            id = S.nextInt();
        }
        manufacturer m = manufacturers.get(id);
        for(product p : m.products){
            p.m = null;
        }
        manufacturers.remove(id);
    }

    static void deleteProduct(Scanner S){
        printProducts();
        System.out.println("Enter the ID of the product to delete: ");
        int id = S.nextInt();
        while(!products.containsKey(id)){
            if(id==0){
                return;
            }
            System.out.println(id + " is not a valid product ID");
            System.out.println("Enter the ID of the product to delete(Enter 0 to exit): ");
            id = S.nextInt();
        }
        product p = products.get(id);
        //remove product from manufacturer
        p.m.products.remove(p);
        products.remove(id);
    }

    static void deleteCustomer(Scanner S){
        printCustomers();
        System.out.println("Enter the ID of the customer to delete: ");
        int id = S.nextInt();
        while(!customers.containsKey(id)){
            if(id==0){
                return;
            }
            System.out.println(id + " is not a valid customer ID");
            System.out.println("Enter the ID of the customer to delete(Enter 0 to exit): ");
            id = S.nextInt();
        }
        customers.remove(id);
    }

    static void deleteShop(Scanner S){
        printShops();
        System.out.println("Enter the ID of the shop to delete: ");
        int id = S.nextInt();
        while(!shops.containsKey(id)){
            if(id==0){
                return;
            }
            System.out.println(id + " is not a valid shop ID");
            System.out.println("Enter the ID of the shop to delete(Enter 0 to exit): ");
            id = S.nextInt();
        }
        shops.remove(id);
    }

    static void deleteDeliveryAgent(Scanner S){
        printDeliveryAgents();
        System.out.println("Enter the ID of the deliveryagent to delete: ");
        int id = S.nextInt();
        while(!deliveryagents.containsKey(id)){
            if(id==0){
                return;
            }
            System.out.println(id + " is not a valid deliveryagent ID");
            System.out.println("Enter the ID of the deliveryagent to delete(Enter 0 to exit): ");
            id = S.nextInt();
        }
        deliveryagents.remove(id);
    }

    static void processOrder(order orderReceived){
        int zipcode = orderReceived.c.zipcode;
        shop shopDelivering = null;
        Boolean available = false;
        for(Map.Entry<Integer, shop> entry : shops.entrySet()){
            //entry.getValue() is the shop
            if(entry.getValue().zipcode == zipcode){
                if(entry.getValue().inventory.containsKey(orderReceived.productWanted)){
                    available = true;
                    //if the product is in the inventory of the shop and there is enough quantity, we deliver it
                    if(entry.getValue().inventory.get(orderReceived.productWanted) >= orderReceived.quantity){
                        shopDelivering = entry.getValue();
                        break;
                    }
                }
            }
        }
        if(!available){
            System.out.println("Product " + orderReceived.productWanted.name + " not available in any shop in zipcode " + zipcode);
            System.out.println("Order not processed");
            return;
        }
        if(shopDelivering == null){
            System.out.println("Product " + orderReceived.productWanted.name + " not available in required quantity in any shop in zipcode " + zipcode);
            System.out.println("Order not processed");
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
                else if(entry.getValue().products_delivered < deliveryAgent.products_delivered){
                    deliveryAgent = entry.getValue();
                }
            }
        }
        if(deliveryAgent == null){
            System.out.println("No delivery agent available in zipcode " + zipcode);
            System.out.println("Order not processed");
            return;
        }

        //we deliver the product
        //update the inventory of the shop
        shopDelivering.inventory.put(orderReceived.productWanted, shopDelivering.inventory.get(orderReceived.productWanted) - orderReceived.quantity);
        if(shopDelivering.inventory.get(orderReceived.productWanted) == 0){
            shopDelivering.inventory.remove(orderReceived.productWanted);
        }
        //update the products delivered of the delivery agent
        deliveryAgent.products_delivered += orderReceived.quantity;
        //update the purchases of the customer
        customer c = orderReceived.c;
        if(c.purchases.containsKey(orderReceived.productWanted)){
            c.purchases.put(orderReceived.productWanted, c.purchases.get(orderReceived.productWanted) + orderReceived.quantity);
        }
        else{
            c.purchases.put(orderReceived.productWanted, orderReceived.quantity);
        }
        System.out.println("Product " + orderReceived.productWanted.name + " delivered to customer " + orderReceived.c.name + " in zipcode " + zipcode);
        System.out.println("Order processed");
    }

    static int takeInput(Scanner S, String message, int lower_bound, int upper_bound){
        System.out.println(message);
        while(true){
            String input = S.nextLine();
            if(input.length()!=1){
                System.out.println("Invalid input");
                continue;
            }
            int input_int = input.charAt(0)-'0';
            if(input_int<lower_bound || input_int>upper_bound){
                System.out.println("Invalid input");
                continue;
            }
            return input_int;
        }
    }

    public static void main(String[] args){
        Scanner S = new Scanner(System.in);
        int choice;
        int IDbeingAllocated = 1;
        System.out.println("Welcome to the management system!");
        while(true){
            for(int i=0; i<150; i++){
                System.out.print("_");
            }
            System.out.println();
            String main_interface = "Enter 0 to exit\n" + 
            "Enter 1 to add an entity" + "\n" +
            "Enter 2 to delete an entity" + "\n" +
            "Enter 3 to print all entities of a type" + "\n" +
            "Enter 4 to add a product to a manufacturer(Product and manufacturer must already exist)" + "\n" +
            "Enter 5 to add copies of a product to a shop(Product and shop must already exist)" + "\n" +
            "Enter 6 to place an order" + "\n" +
            "Enter 7 to list products made by a manufacturer" + "\n" +
            "Enter 8 to list all items available in a shop" + "\n" +
            "Enter 9 to list all purchases made by a customer" + "\n" +
            "Enter your choice(0-9): ";
            choice = takeInput(S, main_interface, 0, 9);
            if(choice == 0){
                break;
            }
            else if(choice == 1){
                int choice1 = takeInput(S, "Enter 0 to exit\nEnter 1 to add a manufacturer\nEnter 2 to add a customer\nEnter 3 to add a shop\nEnter 4 to add a product\nEnter 5 to add a delivery agent\nEnter your choice(0-5): ", 0, 5);
                if(choice1 == 0){
                    continue;
                }
                else if(choice1 == 1){
                    manufacturers.put(IDbeingAllocated, new manufacturer(S, IDbeingAllocated));
                }
                else if(choice1 == 2){
                    customers.put(IDbeingAllocated, new customer(S, IDbeingAllocated));
                }
                else if(choice1 == 3){
                    shops.put(IDbeingAllocated, new shop(S, IDbeingAllocated));
                }
                else if(choice1 == 4){
                    products.put(IDbeingAllocated, new product(S, IDbeingAllocated));
                }
                else if(choice1 == 5){
                    deliveryagents.put(IDbeingAllocated, new deliveryagent(S, IDbeingAllocated));
                }
                IDbeingAllocated++;
            }
            else if(choice == 2){
                int choice2 = takeInput(S, "Enter 0 to exit\nEnter 1 to delete a manufacturer\nEnter 2 to delete a customer\nEnter 3 to delete a shop\nEnter 4 to delete a product\nEnter 5 to delete a delivery agent\nEnter your choice(0-5): ", 0, 5);
                if(choice2 == 0){
                    continue;
                }
                else if(choice2 == 1){
                    deleteManufacturer(S);
                }
                else if(choice2 == 2){
                    deleteCustomer(S);
                }
                else if(choice2 == 3){
                    deleteShop(S);
                }
                else if(choice2 == 4){
                    deleteProduct(S);
                }
                else if(choice2 == 5){
                    deleteDeliveryAgent(S);
                }
            }

            else if(choice == 3){
                int choice3 = takeInput(S, "Enter 0 to exit\nEnter 1 to print manufacturers\nEnter 2 to print customers\nEnter 3 to print shops\nEnter 4 to print products\nEnter 5 to print delivery agents\nEnter your choice(0-5): ", 0, 5);
                if(choice3 == 0){
                    continue;
                }
                else if(choice3 == 1){
                    printManufacturers();
                }
                else if(choice3 == 2){
                    printCustomers();
                }
                else if(choice3 == 3){
                    printShops();
                }
                else if(choice3 == 4){
                    printProducts();
                }
                else if(choice3 == 5){
                    printDeliveryAgents();
                }
            }
            else if(choice == 4){
            }
            else if(choice == 5){
            }
            else if(choice == 6){
            }
            else if(choice == 7){
            }
            else if(choice == 8){
            }
            else if(choice == 9){
            }
        }

        S.close();
    }    
}