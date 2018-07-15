package util;

import core.Table;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import visual.Login;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * A JSON writer for the payment history of a restaurant.
 */
public class PaymentJsonWriter {
    private static JSONArray allPayments = new JSONArray();// a array of all payments

    /**
     * Writes to a payment JSON file.
     *
     * @param table a table
     */
    public static void PaymentWriter(Table table) {
        String server = table.getServer().toString();
        int tableNumber = table.getTableNumber();
        double tablePayment = table.getTotalBillAmount();
        Date date = new Date();
        LocalDate lDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        JSONObject obj = new JSONObject();
        obj.put("date", lDate.toString());
        obj.put("server", server);
        obj.put("tableNumber", tableNumber);
        obj.put("payment", tablePayment);

        allPayments.add(obj);
        flush();

    }

    /**
     * Flushes from the JSONArray buffer to the payments file
     */
    private static void flush() {
        try (FileWriter fw = new FileWriter("resources/data/payments.json", false)) {
            fw.write(allPayments.toJSONString());
            fw.flush();

        } catch (IOException e) {
            Login.logger.warning("Unable to update Restaurant payment.json.");
        }
    }

    /**
     * Parses payments at the start of a login in to back up all payment history.
     *
     * @param file a file to be backed up to
     * @throws IOException    if unable to read a file
     * @throws ParseException if unable to parse the file
     */
    public static void parsePayments(File file) throws IOException {
        try {
            if (!file.createNewFile()) {
                JSONParser parser = new JSONParser();
                Object obj = parser.parse(new FileReader(file));
                allPayments = (JSONArray) obj;
            } else {
                allPayments = new JSONArray();
                flush();
            }
        } catch (ParseException | ClassCastException e) {
            allPayments = new JSONArray();
            flush();
        }

    }
}
