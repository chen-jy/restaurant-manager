package events;

import com.jfoenix.controls.JFXTextArea;
import core.Order;
import core.Restaurant;
import core.Table;
import util.Log;

/**
 * Represents an event where a table requests its bill.
 */
public class GetBillEvent extends Event {
    private Table table; // The bill for the table
    private int seat; // The bill for the seat (optional)
    public static final double TAX_AMOUNT = 0.13; //The amount of Tax to be added
    public static final double AUTOMATIC_TIP_AMOUNT = 0.15; //The automatic gratuity to be added for tables >= 8.
    private JFXTextArea update; //the text area to update in the GUI

    /**
     * Returns a GetBillEvent.
     *
     * @param restaurant A Restaurant that this GetBill Event corresponds to.
     * @param table      a Table that this GetBill Event corresponds to.
     * @param seat       a seat that this GetBill Event corresponds to.
     * @param update     the GUI text area to update
     */
    public GetBillEvent(Restaurant restaurant, Table table, int seat, JFXTextArea update) {
        super(restaurant);
        this.table = table;
        this.seat = seat;
        this.update = update;
    }

    /**
     * Executes a GetBillEvent.
     */
    @Override
    public void execute() {
        double finalPrice = 0d;

        StringBuilder sb = new StringBuilder("Table Number: " + table.getTableNumber());
        sb.append(System.lineSeparator());

        if (seat == 0) { // Gets the whole table's bill
            for (Order order : table.getOrders()) {
                if(!order.isDelivered())
                    continue;

                sb.append(formatBill(order));
            }

            finalPrice = table.getBasePrice();

        } else if (seat >= 1) { // Gets an individual seat's bill
            sb.append("Seat Number: ").append(seat);
            sb.append(System.lineSeparator());

            for (Order order : table.getOrders()) {
                if(!order.isDelivered())
                    continue;

                if (order.getSeatNumber() == seat) {
                    sb.append(formatBill(order));
                    finalPrice += order.getPrice();
                }
            }
        }

        double taxAmount = table.getTaxAmount(finalPrice);
        double tipAmount = table.getTipAmount(finalPrice);

        sb.append(System.lineSeparator());
        sb.append("Subtotal:    ").append(String.format("$%.2f", finalPrice)).append(System.lineSeparator());
        sb.append("Tax (HST):   ").append(String.format("$%.2f", taxAmount)).append(System.lineSeparator());
        sb.append("Tip:         ").append(String.format("$%.2f", tipAmount)).append(System.lineSeparator());
        sb.append("Total:       ").append(String.format("$%.2f", finalPrice + taxAmount + tipAmount)).append(System.lineSeparator());

        Log.log(table, "bill has printed.");


        String billText = sb.toString();
        update.setStyle("-fx-font-family: monospace");
        update.clear();
        update.setText(billText);
    }

    /**
     * Formats a bill for a specific order (used in both table and seat bills).
     *
     * @param order the order to format a bill for.
     * @return a string, representing a portion of a bill for the specific order.
     */
    private String formatBill(Order order) {
        if (order == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder(System.lineSeparator());
        sb.append("Order ===========");
        sb.append(System.lineSeparator());
        sb.append("\t");
        sb.append(order);
        sb.append(System.lineSeparator());
        sb.append("\t\tPrice: ");
        sb.append(String.format("$%.2f", order.getPrice()));

        return sb.toString();
    }


}
