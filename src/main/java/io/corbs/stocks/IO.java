package io.corbs.stocks;

import cworks.json.Json;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class IO {

    private static final String DEFAULT_SEPARATOR = ",";

    public static List<Stock> readJSONStocks(File file) throws IOException {
        Stock[] stocks = Json.asArray(file, Stock.class);
        return Arrays.asList(stocks);
    }

    public static List<Stock> readJSONStocks(String file) throws IOException {
        return readJSONStocks(new File(file));
    }

    public static Map<String, Stock> readAllStocks(String file) throws IOException {
        return readAllStocks(file, DEFAULT_SEPARATOR);
    }

    static Map<String, Stock> readAllStocks(String file, String separator) throws IOException {
        Map<String, Stock> stocks = new LinkedHashMap<>();
        for(Scanner sc = new Scanner(new File(file)); sc.hasNext();) {
            String line = sc.nextLine();
            String[] parts = line.split(separator);
            Stock stock = new Stock();
            stock.setTicker(parts[0]);
            stock.setCompany(parts[1]);
            stock.setSector(parts[2]);
            stock.setIndustry(parts[3]);
            stock.setCity(parts[4]);
            stock.setState(parts[5]);
            stock.setDateAdded(parts[6]);
            try {
                stock.setCik(Integer.valueOf(parts[7]));
            } catch (NumberFormatException ex){
                System.out.println("ticker=" + stock.getTicker());
            }
            stocks.put(stock.getTicker(), stock);
        }

        return stocks;
    }

    public static Stock randomStock(String file) throws IOException {
        return randomStock(file, DEFAULT_SEPARATOR);
    }

    /**
     * Ticker,Company,Sector,Industry,Headquarters,Date Added,CIK
     * @param file
     * @param separator
     * @return a random Stock
     */
    static Stock randomStock(String file, String separator) throws IOException {
        String result = "";
        Random rand = new Random();
        int n = 0;
        for(Scanner sc = new Scanner(new File(file)); sc.hasNext(); ) {
            ++n;
            String line = sc.nextLine();
            if(rand.nextInt(n) == 0)
                result = line;
        }
        String[] csv = result.split(separator);
        Stock stock = new Stock();
        stock.setTicker(csv[0]);
        stock.setCompany(csv[1]);
        return stock;
    }
}
