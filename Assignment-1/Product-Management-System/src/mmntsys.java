import java.util.*;


//Super class entity that all other classes inherit from
//Contains: unique ID and name
class entity{
    int id;
    String name;
    public entity(Scanner S, int id){
        this.id = id;
        System.out.print("Enter the name: ");
        this.name = S.nextLine();
    }
    public void printEntity(){
        System.out.println("ID: "+ this.id);
        System.out.println("Name: "+ this.name);
    }
}


//Class that represents a manufacturer
class manufacturer extends entity{
    Set<product> products;          //contains the manufactured products

    public manufacturer(Scanner S, int id){
        super(S, id);
        this.products = new HashSet<product>();
    }

    //Prints all the details for a manufacturer
    //Whether you want products to be printed or not can be passed to the function
    void printManufacturer(Boolean printProducts){
        super.printEntity();
        if(!printProducts)
            return;
        System.out.println("Products: ");
        if(this.products.size() == 0)
            System.out.println("No products");
        else{
            for(product p: this.products){
                p.printProduct(false);
            }
        }
    }


    //Adds a product to the manufacturer's list of products
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

    //Prints all the products of a manufacturer
    void listProducts(){
        System.out.println("Manufacturer "+ this.name + " has the following products: ");
        if(this.products.size() == 0){
            System.out.println("No products");
            return;
        }
        for(product p: this.products){
            p.printProduct(false);
        }
    }
}


//Class that represents a product
//Each product has a unique manufacturer m
class product extends entity{
    manufacturer m;

    public product(Scanner S, int id){
        super(S, id);
        this.m = null;
    }

    //Prints the details of a product
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

    //Deletes product from everywhere but leaves it in the database and the customers cart
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


//Class that represents a customer
class customer extends entity{
    int zipcode;
    Map<product, Integer> purchases;          //contains the purchased products

    public customer(Scanner S, int id){
        super(S, id);
        System.out.print("Enter the zipcode: ");
        this.zipcode = S.nextInt();
        S.nextLine();
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


//Class representing shops and warehouses
//Maintains inventory of the shop as well as supports adding products in any quantity to the inventory
class shop extends entity{
    int zipcode;
    Map<product, Integer> inventory; //contains the inventory of the shop

    public shop(Scanner S, int id){
        super(S, id);
        System.out.print("Enter the zipcode: ");
        this.zipcode = S.nextInt();
        S.nextLine();
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


//Class that represents a delivery agent
class deliveryagent extends entity{
    int zipcode;
    int products_delivered;
    public deliveryagent(Scanner S, int id){
        super(S, id);
        System.out.print("Enter the zipcode: ");
        this.zipcode = S.nextInt();
        S.nextLine();
        this.products_delivered = 0;
    }
    void printDeliveryagent(){
        super.printEntity();
        System.out.println("Zipcode: " + this.zipcode);
        System.out.println("Products Delivered: " + this.products_delivered);
    }
}

//Class that represents an order
class order{
    customer c;
    product productWanted;
    int quantity;

    public order(Scanner S, customer c, product productWanted){
        this.c = c;
        this.productWanted = productWanted;
        System.out.println("Enter the quantity: ");
        int quantity = S.nextInt();
        while(quantity <= 0){
            System.out.println("Invalid quantity");
            System.out.println("Enter the quantity: ");
            quantity = S.nextInt();
        }
        this.quantity = quantity;
        S.nextLine();
    }
}


public class mmntsys{
    //We store everything in hashmaps
    //All hashmaps store entities by their ID
    //{Key, Value}::{ID, Entity}
    static Map<Integer, manufacturer> manufacturers = new HashMap<Integer, manufacturer>();
    static Map<Integer, product> products = new HashMap<Integer, product>();
    static Map<Integer, customer> customers = new HashMap<Integer, customer>();
    static Map<Integer, shop> shops = new HashMap<Integer, shop>();
    static Map<Integer, deliveryagent> deliveryagents = new HashMap<Integer, deliveryagent>();


    //The following 5 methods call the inbuilt class methods on our stored entities
    //They add the entity to the corresponding hashmap
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


    //They print the details of the entity
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


    //The following 5 methods are used to delete entities
    //They get the ID of the entity to be deleted and then delete it
    static void deleteManufacturer(Scanner S){
        printManufacturers();
        int id = getManufacturerID(S);
        manufacturer m = manufacturers.get(id);
        for(product p : m.products){
            p.m = null;
        }
        manufacturers.remove(id);
    }

    static void deleteProduct(Scanner S){
        printProducts();
        int id = getProductID(S);
        if(id == 0){
            return;
        }
        product p = products.get(id);
        p.deleteProduct(shops);
        products.remove(id);
    }

    static void deleteCustomer(Scanner S){
        printCustomers();
        int id = getCustomerID(S);
        if(id==0){
            return;
        }
        customers.remove(id);
    }

    static void deleteShop(Scanner S){
        printShops();
        int id = getShopID(S);
        if(id==0){
            return;
        }
        shops.remove(id);
    }

    static void deleteDeliveryAgent(Scanner S){
        printDeliveryAgents();
        int id = getDeliveryAgentID(S);
        if(id==0){
            return;
        }
        deliveryagents.remove(id);
    }

    static void processOrder(order orderReceived){
        int zipcode = orderReceived.c.zipcode;
        shop shopDelivering = null;
        Boolean available = false;
        //If product in required quantity is available in any shop in the pincode of the customer we get it from that shop otherwise print that order cant be processed
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
        //If no delivery agent is available in the pincode
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
        //comment the above line and
        //uncomment the below line if you want each order to count as a single delivery
        //deliveryAgent.products_delivered++;

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

    //Helper function that takes in and returns a single digit integer from lower bound to upper bound else asks the user to enter again
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

    //Print a seperator for more organised output
    static void printSeperator(Boolean large){
        int max_length = 75;
        if(large){
            max_length = 150;
        }
        for(int i=0;i<max_length;i++){
            System.out.print("_");
        }
        System.out.println();
        return;
    }

    //The following 5 functions are used to get the ID of the object that the user wants to enter
    //The function takes in a Scanner object and returns the ID of the object that the user wants to enter
    //It asks the user to enter again if the ID is invalid
    static int getProductID(Scanner S){
        System.out.println("Enter the ID of the product(0 to exit): ");
        int id = S.nextInt();
        while(!products.containsKey(id)){
            if(id==0){
                return 0;
            }
            System.out.println(id + " is not a valid product ID");
            System.out.println("Enter the ID of the product: ");
            id = S.nextInt();
        }
        S.nextLine();
        return id;
    }

    static int getManufacturerID(Scanner S){
        System.out.println("Enter the ID of the manufacturer(0 to exit): ");
        int id = S.nextInt();
        while(!manufacturers.containsKey(id)){
            if(id==0){
                return 0;
            }
            System.out.println(id + " is not a valid manufacturer ID");
            System.out.println("Enter the ID of the manufacturer: ");
            id = S.nextInt();
        }
        S.nextLine();
        return id;
    }

    static int getCustomerID(Scanner S){
        System.out.println("Enter the ID of the customer(0 to exit): ");
        int id = S.nextInt();
        while(!customers.containsKey(id)){
            if(id==0){
                return 0;
            }
            System.out.println(id + " is not a valid customer ID");
            System.out.println("Enter the ID of the customer: ");
            id = S.nextInt();
        }
        S.nextLine();
        return id;
    }

    static int getShopID(Scanner S){
        System.out.println("Enter the ID of the shop(0 to exit): ");
        int id = S.nextInt();
        while(!shops.containsKey(id)){
            if(id==0){
                return 0;
            }
            System.out.println(id + " is not a valid shop ID");
            System.out.println("Enter the ID of the shop: ");
            id = S.nextInt();
        }
        S.nextLine();
        return id;
    }

    static int getDeliveryAgentID(Scanner S){
        System.out.println("Enter the ID of the deliveryagent(0 to exit): ");
        int id = S.nextInt();
        while(!deliveryagents.containsKey(id)){
            if(id==0){
                return 0;
            }
            System.out.println(id + " is not a valid deliveryagent ID");
            System.out.println("Enter the ID of the deliveryagent: ");
            id = S.nextInt();
        }
        S.nextLine();
        return id;
    }


    public static void main(String[] args){
        Scanner S = new Scanner(System.in);
        int choice;
        //IDs are alloted sequentially automatically to avoid collisions
        //Alternative implementation would be to maintain a set of already alloted IDs and everytime ask the user to enter one and check the set
        //Can be implemented using hashset or a normal binary tree based implementation of set with log(N) or better time complexity
        int IDbeingAllocated = 1;

        //Welcome message
        System.out.println("Welcome to the management system!");

        //Main menu that gives the user options to choose from and leads to submenus
        //The main menu is a loop that keeps on running until the user enters 0
        //Every choice in the main menu can be reversed from the submenu by entering 0 in the submenu so that accidental input is not a problem
        while(true){
            System.out.println("Press Enter to continue");
            S.nextLine();
            printSeperator(true);
            String main_interface = "Enter 0 to exit the program\n" + 
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
            printSeperator(false);
            //#######################################################################################################################################################
            //CHOICE 0
            //Exit the program
            if(choice == 0){
                break;
            }
            //#######################################################################################################################################################
            //CHOICE 1
            //Adding an entity
            else if(choice == 1){
                int choice1 = takeInput(S, "Enter 0 to exit\nEnter 1 to add a manufacturer\nEnter 2 to add a customer\nEnter 3 to add a shop\nEnter 4 to add a product\nEnter 5 to add a delivery agent\nEnter your choice(0-5): ", 0, 5);
                if(choice1 == 0){
                    continue;
                }
                else if(choice1 == 1){
                    manufacturers.put(IDbeingAllocated, new manufacturer(S, IDbeingAllocated));
                    System.out.println("Manufacturer added:");
                    manufacturers.get(IDbeingAllocated).printManufacturer(false);
                }
                else if(choice1 == 2){
                    customers.put(IDbeingAllocated, new customer(S, IDbeingAllocated));
                    System.out.println("Customer added:");
                    customers.get(IDbeingAllocated).printCustomer(false);
                }
                else if(choice1 == 3){
                    shops.put(IDbeingAllocated, new shop(S, IDbeingAllocated));
                    System.out.println("Shop added:");
                    shops.get(IDbeingAllocated).printShop();
                }
                else if(choice1 == 4){
                    products.put(IDbeingAllocated, new product(S, IDbeingAllocated));
                    System.out.println("Product added:");
                    products.get(IDbeingAllocated).printProduct(false);
                }
                else if(choice1 == 5){
                    deliveryagents.put(IDbeingAllocated, new deliveryagent(S, IDbeingAllocated));
                    System.out.println("Delivery agent added:");
                    deliveryagents.get(IDbeingAllocated).printDeliveryagent();
                }
                IDbeingAllocated++;
            }
            //#######################################################################################################################################################
            //CHOICE 2
            //Deleting an entity
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
            //#######################################################################################################################################################
            //CHOICE 3
            //Printing an entity
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
            //#######################################################################################################################################################
            //CHOICE 4
            //Add product to manufacturer
            else if(choice == 4){
                int productID, manufacturerID;
                productID = getProductID(S);
                if(productID == 0){
                    continue;
                }
                manufacturerID = getManufacturerID(S);
                if(manufacturerID == 0){
                    continue;
                }
                manufacturers.get(manufacturerID).addProduct(products.get(productID));
                System.out.println("Product added to manufacturer");
            }
            //#######################################################################################################################################################
            //CHOICE 5
            //Add copies of product to shop
            else if(choice == 5){
                int productID, shopID;
                productID = getProductID(S);
                if(productID == 0){
                    continue;
                }
                shopID = getShopID(S);
                if(shopID == 0){
                    continue;
                }
                System.out.println("Enter the number of copies(0 to exit): ");
                while(true){
                    int copies = S.nextInt();
                    if(copies == 0){
                        break;
                    }
                    if(copies < 0){
                        System.out.println("Number of copies cannot be negative. Enter a valid number of copies(0 to exit): ");
                        continue;
                    }
                    shops.get(shopID).addProduct(products.get(productID), copies);
                    System.out.println("Product added to shop");
                    break;
                }
            }
            //#######################################################################################################################################################
            //CHOICE 6
            //Place an order
            else if(choice == 6){
                int choice6 = takeInput(S, "Enter 0 to exit\nEnter 1 to place an order\nEnter your choice(0-1): ", 0, 1);
                if(choice6 == 0){
                    continue;
                }
                int customerID = getCustomerID(S);
                if(customerID == 0){
                    continue;
                }
                int productID = getProductID(S);
                if(productID == 0){
                    continue;
                }
                order freshOrder = new order(S, customers.get(customerID), products.get(productID));
                processOrder(freshOrder);
            }
            //#######################################################################################################################################################
            //CHOICE 7
            //List products made by manufacturer
            else if(choice == 7){
                int manufacturerID;
                manufacturerID = getManufacturerID(S);
                if(manufacturerID == 0){
                    continue;
                }
                manufacturers.get(manufacturerID).listProducts();
            }
            //#######################################################################################################################################################
            //CHOICE 8
            //List shop inventory
            else if(choice == 8){
                int shopID;
                shopID = getShopID(S);
                if(shopID == 0){
                    continue;
                }
                shops.get(shopID).printInventory();
            }
            //#######################################################################################################################################################
            //CHOICE 9
            //List purchases made by a customer
            else if(choice == 9){
                int customerID;
                customerID = getCustomerID(S);
                if(customerID == 0){
                    continue;
                }
                customers.get(customerID).printPurchases();
            }
        }
        S.close();
    }    
}