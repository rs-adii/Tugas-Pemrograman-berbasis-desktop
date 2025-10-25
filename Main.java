import java.util.Scanner;

class Menu {
    String name;
    int price;
    String category; // "makanan" atau "minuman"

    Menu(String name, int price, String category) {
        this.name = name;
        this.price = price;
        this.category = category;
    }
}

public class Main{
    static Menu[] menus = new Menu[8]; // 4 makanan + 4 minuman
    static final int MAX_ORDERS = 4;
    static final int SERVICE_FEE = 20000;

    public static void main(String[] args) {
        initMenus();
        Scanner sc = new Scanner(System.in);

        System.out.println("=== DAFTAR MENU RESTORAN ===");
        displayMenus();

        System.out.println("\nPetunjuk pemesanan: tulis dalam format \"Nama Menu = jumlah\"");
        System.out.println("Contoh: Nasi Goreng Spesial = 2");
        System.out.println("Masukkan '-' atau kosong untuk melewati pesanan.");
        System.out.println("Maksimal " + MAX_ORDERS + " pesanan.");

        String[] orderNames = new String[MAX_ORDERS];
        int[] orderQty = new int[MAX_ORDERS];


        // Input pesanan dengan loop
        for (int i = 0; i < MAX_ORDERS; i++) {
            System.out.print("Pesanan " + (i + 1) + ": ");
            String line = sc.nextLine().trim();
            if (line.equals("-") || line.equals("")) {
                orderNames[i] = null;
                orderQty[i] = 0; 
                continue;
            }
            String[] parts = parseInputLine(line);
            orderNames[i] = parts[0];
            try {
                orderQty[i] = Integer.parseInt(parts[1]);
                if (orderQty[i] < 0) orderQty[i] = 0;
            } catch (NumberFormatException e) {
                System.out.println("Jumlah tidak valid. Di-set 0.");
                orderQty[i] = 0;
            }
        }

        // Cari objek Menu untuk tiap pesanan (loop)
        Menu[] orderMenus = new Menu[MAX_ORDERS];
        for (int i = 0; i < MAX_ORDERS; i++) {
            if (orderNames[i] != null) {
                orderMenus[i] = findMenuByName(orderNames[i]);
            } else {
                orderMenus[i] = null;
            }
        }

        // Validasi nama menu
        for (int i = 0; i < MAX_ORDERS; i++) {
            if (orderNames[i] != null && orderMenus[i] == null) {
                System.out.println("\nNama menu '" + orderNames[i] + "' tidak ditemukan. Pastikan penulisan sesuai daftar menu.");
                sc.close();
                return;
            }
        }

        // Hitung subtotal per item dan total subtotal (loop)
        int[] subtotalPerItem = new int[MAX_ORDERS];
        int subtotal = 0;
        for (int i = 0; i < MAX_ORDERS; i++) {
            if (orderMenus[i] != null) {
                subtotalPerItem[i] = orderMenus[i].price * orderQty[i];
                subtotal += subtotalPerItem[i];
            } else {
                subtotalPerItem[i] = 0;
            }
        }

        // Promo BOGO (beli 1 gratis 1) untuk kategori minuman jika subtotal > 50.000
        int bogoDiscount = 0;
        if (subtotal > 50000) {
            for (int i = 0; i < MAX_ORDERS; i++) {
                if (orderMenus[i] != null &&
                    orderMenus[i].category.equalsIgnoreCase("minuman") &&
                    orderQty[i] > 1) {
                    int free = orderQty[i] / 2; // setiap 2 satu gratis 1
                    bogoDiscount += free * orderMenus[i].price;
                }
            }
        }

        int subtotalAfterBogo = subtotal - bogoDiscount;

        // Diskon 10% jika subtotal setelah promo > 100.000
        double discount = 0;
        if (subtotalAfterBogo > 100000) {
            discount = 0.10 * subtotalAfterBogo;
        }

        double afterDiscount = subtotalAfterBogo - discount;

        // Pajak 10% dari setelah diskon
        double tax = 0.10 * afterDiscount;

        double grandTotal = afterDiscount + tax + SERVICE_FEE;

       // Cetak struk (loop menampilkan item yang ada)
        System.out.println("\n========== STRUK PEMESANAN ==========");
        System.out.printf("%-3s %-25s %-6s %-12s %-12s%n", "No", "Item", "Qty", "Harga/unit", "Total");
        for (int i = 0; i < MAX_ORDERS; i++) {
            if (orderMenus[i] != null) {
                System.out.printf("%-3d %-25s %-6d Rp %-9d Rp %-9d%n",
                        (i + 1),
                        orderMenus[i].name,
                        orderQty[i],
                        orderMenus[i].price,
                        subtotalPerItem[i]);
            }
        }
        System.out.println("-----------------------------------------------------------");
        System.out.printf("%-40s : Rp %d%n", "Subtotal (sebelum promo/diskon)", subtotal);
        System.out.printf("%-40s : Rp %d%n", "Potongan Beli 1 Gratis 1 (minuman)", bogoDiscount);
        System.out.printf("%-40s : Rp %d%n", "Subtotal setelah promo", subtotalAfterBogo);
        System.out.printf("%-40s : Rp %.0f%n", "Diskon 10% (jika > Rp100.000)", discount);
        System.out.printf("%-40s : Rp %.0f%n", "Subtotal setelah diskon", afterDiscount);
        System.out.printf("%-40s : Rp %.0f%n", "Pajak 10%", tax);
        System.out.printf("%-40s : Rp %d%n", "Biaya Pelayanan", SERVICE_FEE);
        System.out.println("-----------------------------------------------------------");
        System.out.printf("%-40s : Rp %.0f%n", "GRAND TOTAL", grandTotal);
        System.out.println("======================================");
        System.out.println("Terima kasih atas kunjungan Anda!");
        sc.close();
    }

    static void initMenus() {
        menus[0] = new Menu("Nasi Goreng Spesial", 25000, "makanan");
        menus[1] = new Menu("Ayam Geprek Sambal Bawang", 20000, "makanan");
        menus[2] = new Menu("Mie Ayam Bakso", 22000, "makanan");
        menus[3] = new Menu("Sate Ayam Madura", 28000, "makanan");

        menus[4] = new Menu("Es Teh Manis", 6000, "minuman");
        menus[5] = new Menu("Jus Jeruk Segar", 10000, "minuman");
        menus[6] = new Menu("Kopi Susu Gula Aren", 15000, "minuman");
        menus[7] = new Menu("Jus Alpukat Coklat", 18000, "minuman");
    }

    static void displayMenus() {
        System.out.println("---- Makanan ----");
        for (int i = 0; i < menus.length; i++) {
            if (menus[i] != null && menus[i].category.equalsIgnoreCase("makanan")) {
                System.out.printf("%-25s Rp %d%n", menus[i].name, menus[i].price);
            }
        }
        System.out.println("---- Minuman ----");
        for (int i = 0; i < menus.length; i++) {
            if (menus[i] != null && menus[i].category.equalsIgnoreCase("minuman")) {
                System.out.printf("%-25s Rp %d%n", menus[i].name, menus[i].price);
            }
        }
    }

    static Menu findMenuByName(String name) {
        if (name == null) return null;
        for (Menu m : menus) {
            if (m != null && m.name.equalsIgnoreCase(name.trim())) {
                return m;
            }
        }
        return null;
    }

    // parse "Nama = jumlah" atau "Nama : jumlah"
    static String[] parseInputLine(String line) {
        line = line.trim();
        String namePart = line;
        String qtyPart = "0";
        if (line.contains("=")) {
            String[] p = line.split("=", 2);
            namePart = p[0].trim();
            qtyPart = p.length > 1 ? p[1].trim() : "0";
        } else if (line.contains(":")) {
            String[] p = line.split(":", 2);
            namePart = p[0].trim();
            qtyPart = p.length > 1 ? p[1].trim() : "0";
        }
        return new String[]{namePart, qtyPart};
    }
}

 