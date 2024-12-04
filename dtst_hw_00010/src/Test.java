import java.util.Scanner;

public class Test {

    public static void main(String[] args) {
    	Scanner scanner = new Scanner(System.in);
    	
        double lf;
        boolean isSSF = false;
        boolean isDoubleHash = false;
        
        // SET LOAD FACTOR
        System.out.println("Enter load factor please: ");
        do {
    		lf = scanner.nextDouble();
    		if(!(lf < 1 || lf > 0)) {
    			System.out.println("Enter valid load factor please!");
    		}
        }while(lf > 1 || lf < 0);
        
        // SET HASH FUNCTION
        boolean flag1 = false;
        System.out.println("Enter 1 for SF, enter 2 for PAC");
        while(!flag1) {
            int token;
            token = scanner.nextInt();
            switch(token) {
            	case 1:{
            		isSSF = true;
            		flag1 = true;
            		break;
            	}
            	case 2:{
            		isSSF = false;
            		flag1 = true;
            		break;
            	}
                default: System.out.println("Invalid choice. Try again.");
            }
        }
        
        // SET COLLISION HANDLING
        boolean flag2 = false;
        System.out.println("Enter 1 for DH, enter 2 for LP");
        while(!flag2) {
            int token;
            token = scanner.nextInt();
            switch(token) {
            	case 1:{
            		isDoubleHash = true;
            		flag2 = true;
            		break;
            	}
            	case 2:{
            		isDoubleHash = false;
            		flag2 = true;
            		break;
            	}
                default: System.out.println("Invalid choice. Try again.");
            }
        }
        
        Operations operations = new Operations(lf, isSSF, isDoubleHash);
        

        
        System.out.println("\n Hash Table updating is successfull");

        try {
            // Kullanıcıyla etkileşimli menü
            while (true) {
                System.out.println("\nMedia Archive Management System:");
                System.out.println("1. Load dataset");
                System.out.println("2. Run search test");
                System.out.println("3. Search for media by IMDb ID");
                System.out.println("4. List top 10 media");
                System.out.println("5. List media by country");
                System.out.println("6. List media on all platforms");
                System.out.println("7. Exit");

                System.out.print("Enter your choice: ");


                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1 -> operations.loadDataset("movies_dataset.csv");
                    case 2 -> operations.runSearchTest("search.txt");
                    case 3 -> {
                        System.out.print("Enter IMDb ID: ");
                        operations.searchMedia(scanner.nextLine());
                    }
                    case 4 -> operations.listTop10Media();
                    case 5 -> {
                        System.out.print("Enter country code: ");
                        operations.listMediaInCountry(scanner.nextLine());
                    }
                    case 6 -> operations.listMediaOnAllPlatforms();
                    case 7 -> {
                        System.out.println("Goodbye!");
                        scanner.close();
                        return;
                    }
                    default -> System.out.println("Invalid choice. Try again.");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
        }

    }

}
