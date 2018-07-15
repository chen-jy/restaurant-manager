package visual.gui;

import com.jfoenix.controls.*;
import core.*;
import events.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import util.Wrapper;

import java.util.ArrayList;
import java.util.HashMap;

public class Server extends Employee {


    /*
     FXML Variables == do not set to private, DO NOT remove annotations
     */

    // ORDER TAB
    @FXML
    JFXListView<Node> currentList; // the current ingredient list
    @FXML
    JFXListView<Node> toAdd; // the additional ingredients list
    @FXML
    JFXListView<Node> toRemove; // the removal ingredients list
    @FXML
    JFXListView<Node> menuList; // the menu items list
    @FXML
    JFXButton addAdditional; // button to add an element to additional
    @FXML
    JFXButton removeAdditional; // button to remove an element from additional
    @FXML
    JFXButton addExisting; // button to add an element to removals
    @FXML
    JFXButton removeExisting; // button to remove an element from removals
    @FXML
    JFXButton submitButton; // button to submit the order for ordering

    // ACTIVE ORDERS TAB
    @FXML
    JFXListView<Wrapper> activeOrders;  // list of all active orders for this server
    @FXML
    JFXListView<Wrapper> orderInfo; // list of all relevant data for an order
    @FXML
    JFXListView<Wrapper> toDeliver; // list of all of the server's orders which are ready for delivery
    @FXML
    JFXButton deliver; // button to signify the delivery of the order
    @FXML
    JFXButton cancelButton; // button to cancel a specific order
    @FXML
    JFXButton returnButton; // button to return an order after its delivery
    @FXML
    Tab activeOrdersTab; // the tab of this view

    // BILL TAB
    @FXML
    JFXButton getBill; // gets the bill of a table / seat
    @FXML
    JFXButton copyBill; // copies the content of the textbox into system clipboard
    @FXML
    JFXButton clearTable; // clears the table and signifies receipt of payment
    @FXML
    JFXTextArea billTextBox; // the text box to print out the bill

    Order orderToBeDelivered; // the server's order that needs to be delivered
    private ArrayList<Order> readyToDeliver; // list of all orders that need to be delivered


    /**
     * Constructs a new Server GUI and reference
     *
     * @param user       the Username of this server, used to display in the decorator
     * @param restaurant a restaurant reference
     */
    public Server(String user, Restaurant restaurant) {
        super(user, "Server", "server.fxml", restaurant);
    }

    /**
     * Inializes the GUI and populates extra tabs
     */
    @Override
    public void initialize() {
        super.initialize();
        readyToDeliver = new ArrayList<>();
        clearIngredientLists();

        currentList.setCellFactory(factory);
        toAdd.setCellFactory(factory);
        toRemove.setCellFactory(factory);
        menuList.setCellFactory(factory);

        menuList.getItems().add(createLabel(new Wrapper<>("Menu Items", -1, -1), getTitleStyle()));

        for (MenuItem item : restaurant.getMenu()) {
            menuList.getItems().add(createLabel(new Wrapper<>(item, 0, -1), getNormalStyle()));
        }

        menuList.getItems().sort(comparator);
        menuList.getSelectionModel().selectedItemProperty().addListener((a1, a2, a3) -> {
            clearIngredientLists();
            if (a3 == null) {
                return;
            }
            if (a3.getUserData() instanceof Wrapper) {
                Wrapper wrapper = (Wrapper) a3.getUserData();
                if (wrapper.getValue() instanceof MenuItem) {
                    MenuItem item = (MenuItem) wrapper.getValue();
                    setIngredientLists(item);
                }
            }
            submitButton.setDisable(false);
        });

        addAdditional.setOnAction(a -> {
            //get from current list, put to additional list
            Node selected = currentList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                return;
            }

            if (selected.getUserData() instanceof Wrapper) {
                Wrapper ingredient = (Wrapper) selected.getUserData();
                if (ingredient.getData() == 1 && ingredient.getValue() instanceof Ingredient) {
                    //checked
                    ingredient.addVariable(-1);
                    if (ingredient.getVariable() <= 0) {
                        currentList.getItems().remove(selected);
                        currentList.getSelectionModel().clearSelection();
                    }
                }
            }

            resortLists();
        });

        submitButton.setDisable(true);

        removeAdditional.setOnAction(a -> {
            Node selected = toAdd.getSelectionModel().getSelectedItem();
            if (selected == null) {
                return;
            }

            if (selected.getUserData() instanceof Wrapper) {
                Wrapper ingredient = (Wrapper) selected.getUserData();
                if (ingredient.getValue() instanceof Ingredient) {
                    Ingredient ing = (Ingredient) ingredient.getValue();
                    if (currentList
                            .getItems().stream()
                            .filter(item -> ((Wrapper) item.getUserData()).getValue().equals(ing))
                            .anyMatch(item -> ((Wrapper) item.getUserData()).getData() == 1)) {

                        currentList.getItems().forEach(item -> {
                            Wrapper wrap = ((Wrapper) item.getUserData());
                            if (wrap.getValue().equals(ing) && wrap.getData() == 1) {
                                wrap.addVariable(1);
                            }
                        });
                    } else {
                        Wrapper<Ingredient> wrapper = new Wrapper<>(ing, 1, 1);
                        Label label = createLabel(wrapper.getValue(), getNormalStyle());
                        Label amount = createLabel(wrapper.getVariable(), getNormalStyle());

                        wrapper.addObserver((o, arg) -> amount.setText(String.valueOf(wrapper.getVariable())));

                        BorderPane borderPane = new BorderPane();
                        borderPane.setUserData(wrapper);
                        borderPane.setLeft(label);
                        borderPane.setRight(amount);

                        currentList.getItems().add(borderPane);
                    }
                }
            }

            resortLists();
        });

        addExisting.setOnAction(a -> handleMovingFromExisting(currentList, toRemove));


        removeExisting.setOnAction(a -> handleMovingFromExisting(toRemove, currentList));


        submitButton.setOnAction((a) -> {
            if (!this.readyToDeliver.isEmpty()) {
                showDialog("You have items waiting for delivery", new ImageView(error));
                return;
            }

            getTableFromSelector(false, false, true, tup -> {
                Order order = getOrderFromFields(tup.x, tup.y);
                if (order == null) {
                    showDialog("Could not place order: out of ingredients", new ImageView(error));
                } else {
                    OrderEvent createOrder = new OrderEvent(order, this, this.restaurant);
                    createOrder.execute();
                    showDialog("Order sent", new ImageView(success));

                }

                return null;
            });
        });

        deliver.setOnAction(a -> {
            Order order = (Order) toDeliver.getSelectionModel().getSelectedItem().getValue();
            DeliverEvent event = new DeliverEvent(order, this, restaurant);
            event.execute();
            refreshOrders();
            refreshView();
            deliver.setDisable(true);
        });

        Callback<ListView<Wrapper>, ListCell<Wrapper>> customFactory = param -> new JFXListCell<Wrapper>() {
            @Override
            protected void updateItem(Wrapper item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    return;
                }

                if (item.getValue() instanceof String && item.getVariable() != 42) {
                    getStyleClass().add("list-subtitle-label");
                } else if (item.getData() == -1) {
                    getStyleClass().add("list-title-label");
                } else {
                    getStyleClass().add("normal-label-white");
                }

                setDisable((item.getValue() instanceof String) || item.getVariable() == 42);

                setText(item.toString());
            }
        };

        orderInfo.setCellFactory(customFactory);
        activeOrders.setCellFactory(customFactory);
        toDeliver.setCellFactory(customFactory);
        refreshOrders();
        deliver.setDisable(true);


        activeOrders.getSelectionModel().selectedItemProperty().addListener((a1, a2, a3) -> {
            if (a3 == null) return;
            Order order = (Order) a3.getValue();
            viewOrder(order);
        });

        toDeliver.getSelectionModel().selectedItemProperty().addListener((a1, a2, a3) -> {
            if (a3 == null) return;
            deliver.setDisable(false);
        });

        orderInfo.getItems().add(new Wrapper<>("Order Information", -1, -1));

        getBill.setOnAction(a -> {
            getTableFromSelector(true, true, true, (tuple) -> {
                Table table = tuple.x;
                int seat = tuple.y;

                if (!table.isOccupied())
                    return null;

                new GetBillEvent(restaurant, table, seat, billTextBox).execute();

                return null;
            });
        });

        copyBill.setOnAction(a -> copyToClipboard(billTextBox));

        clearTable.setOnAction(a -> {
            getTableFromSelector(true, true, false, (tuple) -> {
                showConfirmationDialog("Clear Table?", event -> {

                    Table table = tuple.x;
                    if (table == null)
                        return null;


                    if (table.hasActiveOrders()) {
                        showDialog("Table has active orders", new ImageView(error));
                        return null;
                    }


                    new ClearTableEvent(table, table.getServer(), restaurant).execute();
                    return null;
                });
                return null;
            });
        });


        returnButton.setOnAction(a -> {
            getTableFromSelector(true, true, false, (tuple) -> {
                Table table = tuple.x;

                VBox dialog = new VBox(10);

                dialog.setAlignment(Pos.CENTER);

                JFXComboBox<Order> orderComboBox = new JFXComboBox<>();
                populateReturnOrdersComboBox(table, orderComboBox);

                JFXTextField area = new JFXTextField();
                area.setPromptText("Reason");

                JFXButton confirm = new JFXButton("Confirm");
                confirm.setOnAction(a1 -> {
                    Order order = orderComboBox.getSelectionModel().getSelectedItem();
                    if (order == null) {
                        return;
                    }

                    populateReturnOrdersComboBox(table, orderComboBox);

                    new ReturnEvent(restaurant, order, area.getText().trim().isEmpty() ? null : area.getText().trim()).execute();
                });

                dialog.getChildren().addAll(orderComboBox, area, confirm);
                showDialog(dialog, "Select Order");

                return null;
            });
        });

        cancelButton.setOnAction(a -> {
            Wrapper wrap = activeOrders.getSelectionModel().getSelectedItem();
            if (wrap == null || !(wrap.getValue() instanceof Order))
                return;

            Order order = (Order) wrap.getValue();
            new CancelEvent(restaurant, order, CancelEvent.REASON.CUSTOMER_CANCELLED, null).execute();
        });
    }

    /**
     * Populates the combo box with orders that are valid to be returned for the table
     *
     * @param table         the table
     * @param orderComboBox the box to populate
     */
    private void populateReturnOrdersComboBox(Table table, JFXComboBox<Order> orderComboBox) {
        Platform.runLater(() -> {
            ArrayList<Order> orders = table.getOrders();
            orderComboBox.getItems().clear();
            orders.stream().filter(Order::isDelivered).forEach(orderComboBox.getItems()::add);
        });
    }

    /**
     * Handles moving ingredients from removeList to addList
     *
     * @param removeList the list to remove elements from
     * @param addList    the list to move elements to
     */
    private void handleMovingFromExisting(JFXListView<Node> removeList, JFXListView<Node> addList) {
        //get from current list, put to additional list
        Node selected = removeList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        if (selected.getUserData() instanceof Wrapper) {
            Wrapper ingredient = (Wrapper) selected.getUserData();
            if (ingredient.getData() == 0 && ingredient.getValue() instanceof Ingredient) {
                //checked
                ingredient.addVariable(-1);
                if (ingredient.getVariable() <= 0) {
                    removeList.getItems().remove(selected);
                    removeList.getSelectionModel().clearSelection();
                }

                if (addList
                        .getItems().stream()
                        .filter(item -> ((Wrapper) item.getUserData()).getValue().equals(ingredient.getValue()))
                        .anyMatch(item -> ((Wrapper) item.getUserData()).getData() == 0)) {
                    addList.getItems().forEach(node -> {
                        if (node.getUserData() instanceof Wrapper) {
                            Wrapper wrapper = (Wrapper) node.getUserData();
                            if (wrapper.getValue() instanceof Ingredient) {
                                Ingredient ingredient1 = (Ingredient) wrapper.getValue();
                                if (ingredient1.equals(ingredient.getValue())) {
                                    wrapper.addVariable(1);
                                }
                            }
                        }
                    });
                } else {
                    Wrapper<Ingredient> wrapper = new Wrapper<>((Ingredient) ingredient.getValue(), 0, 1);
                    Label label = createLabel(wrapper.getValue(), getNormalStyle());
                    Label amount = createLabel(wrapper.getVariable(), getNormalStyle());

                    wrapper.addObserver((o, arg) -> amount.setText(String.valueOf(wrapper.getVariable())));

                    BorderPane borderPane = new BorderPane();
                    borderPane.setUserData(wrapper);
                    borderPane.setLeft(label);
                    borderPane.setRight(amount);

                    addList.getItems().add(borderPane);
                }
            }
        }

        resortLists();
    }

    /**
     * Gets orders from the 3 list views
     *
     * @param table the table the order for
     * @param seat  the seat of the order
     * @return the relevant Order, computed from the 3 list views
     */
    private Order getOrderFromFields(Table table, int seat) {
        if (menuList.getSelectionModel().getSelectedItem().getUserData() instanceof Wrapper) {
            Wrapper wrapper = (Wrapper) menuList.getSelectionModel().getSelectedItem().getUserData();
            if (wrapper.getValue() instanceof MenuItem) {
                MenuItem item = (MenuItem) wrapper.getValue();
                HashMap<Ingredient, Integer>
                        additional = new HashMap<>(),
                        subtractions = new HashMap<>();

                currentList.getItems().forEach(a -> populate(a, 1, additional));
                toRemove.getItems().forEach(a -> populate(a, 0, subtractions));

                Order order = new Order(item, additional, subtractions, table, seat, this);
                if (restaurant.checkIngredients(order)) {
                    return order;
                }
            }
        }

        return null;
    }

    /**
     * Populates the hashMap with userdata from a node
     *
     * @param a          the node the get data from
     * @param flag       the data flag to match when populating
     * @param toPopulate the map to populate
     */
    private void populate(Node a, int flag, HashMap<Ingredient, Integer> toPopulate) {
        if (a.getUserData() instanceof Wrapper) {
            Wrapper wrapper = (Wrapper) a.getUserData();
            if (wrapper.getValue() instanceof Ingredient && wrapper.getData() == flag) {
                Ingredient ingredient = (Ingredient) wrapper.getValue();
                int amount = wrapper.getVariable();
                toPopulate.put(ingredient, amount);
            }
        }
    }

    /**
     * Clears the ingredients list
     */
    private void clearIngredientLists() {
        currentList.getItems().clear();
        toAdd.getItems().clear();
        toRemove.getItems().clear();

        Label title = createLabel(new Wrapper<>("Ingredients", -1, -1), getTitleStyle());
        Label def = createLabel(new Wrapper<>("Default Ingredients", 0, -1), getSubtitleStyle());
        Label add = createLabel(new Wrapper<>("Additional Ingredients", 1, -1), getSubtitleStyle());

        this.currentList.getItems().addAll(title, def, add);

        Label toAddLabel = createLabel(new Wrapper<>("Additional Ingredients", -1, -1),
                getTitleStyle());
        toAdd.getItems().add(toAddLabel);

        Label toRemoveLabel = createLabel(new Wrapper<>("Removals", -1, -1), getTitleStyle());
        toRemove.getItems().add(toRemoveLabel);
    }

    /**
     * Sets the ingredient lists with ingredients from menuItem
     *
     * @param menuItem the item to set the list with
     */
    private void setIngredientLists(MenuItem menuItem) {
        clearIngredientLists();

        HashMap<Ingredient, Integer> amounts = menuItem.getAllIngredients();
        amounts.forEach((k, v) -> {
            Wrapper<Ingredient> wrapper = new Wrapper<>(k, 0, v);
            Label label = createLabel(wrapper.getValue(), getNormalStyle());
            Label amount = createLabel(wrapper.getVariable(), getNormalStyle());

            wrapper.addObserver((o, arg) -> {
                amount.setText(String.valueOf(wrapper.getVariable()));
            });

            BorderPane borderPane = new BorderPane();
            borderPane.setUserData(wrapper);
            borderPane.setLeft(label);
            borderPane.setRight(amount);

            this.currentList.getItems().add(borderPane);
        });

        ArrayList<Ingredient> ingredientArrayList = restaurant.getIngredientManager().getIngredients();
        ingredientArrayList.forEach(a -> {
            Wrapper<Ingredient> ingredientWrapper = new Wrapper<>(a, 0, -1);
            Label ingredientLabel = createLabel(ingredientWrapper, getNormalStyle());
            toAdd.getItems().add(ingredientLabel);
        });

        resortLists();
    }

    /**
     * Sorts the 3 listViews for ingredients
     */
    private void resortLists() {
        currentList.getItems().sort(comparator);
        toAdd.getItems().sort(comparator);
        toRemove.getItems().sort(comparator);
    }

    /**
     * Sets the Order to be delivered for this server
     *
     * @param order the order to set
     */
    public void setOrder(Order order) {
        orderToBeDelivered = order;
    }

    /**
     * Refresh active activeOrders
     */
    public void refreshOrders() {
        readyToDeliver.clear();
        activeOrders.getItems().clear();
        activeOrders.getItems().add(new Wrapper<>("Active Orders", -1, -1));
        toDeliver.getItems().clear();
        toDeliver.getItems().add(new Wrapper<>("Ready to Deliver", -1, -1));
        for (Order item : restaurant.getAllOrders()) {
            if (item.isCooked() && !item.isDelivered()) {
                if (item.getServer() != this)
                    continue;
                readyToDeliver.add(item);
                toDeliver.getItems().add(new Wrapper<>(item, item.getOrderNumber(), -2));
            }
            if (item.isDelivered() || item.isCancelled() || item.getServer() != this) continue;
            activeOrders.getItems().add(new Wrapper<>(item, item.getOrderNumber(), -2));
        }
    }

    /**
     * Refresh order information of the order currently viewing
     */
    public void refreshView() {
        Wrapper wrapper = activeOrders.getSelectionModel().getSelectedItem();
        if (wrapper == null || !(wrapper.getValue() instanceof Order)) {
            viewOrder(null);
            return;
        }
        Order viewing = (Order) wrapper.getValue();
        viewOrder(viewing);
    }

    /**
     * View order information
     *
     * @param order order to view
     */
    private void viewOrder(Order order) {
        orderInfo.getItems().clear();
        orderInfo.getItems().add(new Wrapper<>("Order Information", -1, -1));

        if (order == null)
            return;

        orderInfo.getItems().add(new Wrapper<>("Menu Item: " + order.getItem(), 0, 42));
        orderInfo.getItems().add(new Wrapper<>(order.getCook() == null ? "No cook" : order.getCook().toString(), 0, 42));
        orderInfo.getItems().add(new Wrapper<>("Order Number: " + order.getOrderNumber(), 0, 42));
        orderInfo.getItems().add(new Wrapper<>("Table :" + order.getTable().getTableNumber(), 0, 42));
        orderInfo.getItems().add(new Wrapper<>("Seat: " + order.getSeatNumber(), 0, 42));
        orderInfo.getItems().add(new Wrapper<>("Received: " + order.isReceived(), 0, 42));
        orderInfo.getItems().add(new Wrapper<>("Cooked: " + order.isCooked(), 0, 42));
    }
}
