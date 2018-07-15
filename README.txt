=== Credit ===

This project by Hsuan-Hao Chen, Murad Akhundov, Linwen Huang, and Yijia Chen.


=== Project Set-up ===

1. Clone the repo. In IntelliJ, select Import Project and select the folder restaurant-manager as the project directory. Leave all other settings as default.

2. Right-click the folder restaurant-manager in the explorer, select Add Framework Support, and tick Maven.

2.1 Go into pom.xml and enable auto import

2.2 All dependencies should now be downloaded and installed automatically.

3. Right-click the folder resources and mark the directory as resources root.

4. Edit your configurations to run from Login.main(), and change it to "Single Instance Only"

5. Before running the program, read the rest of the README file.

6. Run the program from Login.main()

NOTE: Sometimes intelliJ messes up with Maven projects and refuses to compile with resources.
      If you happen to get a NullPointerException, or it the project doesn't compile
      at all, delete your local copy, reset all IDE settings, re-clone the repo, and
      try to compile again.

	  
=== Resource File Explanations ===

>resources
 |
 |
 --> css
 |   |
 |   |
 |   --> stylecomp.css                      This file determines the color scheme and look of the GUI,
 |                                          you can change it if you want, it won't alter the functionality.
 |
 --> data                                   A folder of intra-instance data so that data can be retained
 |   |                                      upon re-run. Delete these files to reset settings to default.
 |   |
 |   --> ingredient_stat.json               This file is a backup of ingredient stats.
 |   |
 |   |
 |   --> menu_stat.json                     This file is a backup of menu stats.
 |   |
 |   |
 |   --> payments.json                      This file is a log of all payments to date.
 |   |
 |   |
 |   --> inventory.json                     This file is a backup of the current inventory.
 |   |
 |   |
 |   --> log.txt                            This file is a backup of all log statements to date.
 |
 |
 --> fxml                                   This folder contains all the layouts for the different GUIs (DO NOT CHANGE!)
 |
 |
 --> images                                 This folder contains all the images for the GUIs (DO NOT CHANGE!)
 |
 |
 --> roboto                                 This folder contains the font used for this project
 |
 |
 --> settings                               This folder contains all the setting files.
     |
     |
     --> accounts.json                      This file contains all the login information for the login UI
     |
     |
     --> ingredients.json                   This file contains all the default ingredients.
     |                                      This file is only loaded if data/inventory.json does not exist.
     |
     --> menu.json                          This file contains all the default menu items.
     |
     |
     --> requests.txt                       This file contains all the requests for shipments (DO NOT CHANGE!)
     |
     |
     --> settings.json                      This file contains all the settings relevant to the creation of the restaurant.


=== Settings Explanations ===
Change these if you want. Please be careful :)

settings/accounts.json

    This file contains all the log in information.
    The passwords are encrypted by SHA256 and salted.

    Default accounts: (all passwords are "pass")

        Servers:
            usernames: S1, S2, S3, S4, S4
        Cooks:
            usernames: C1, C2, C3, C4, C5
        Manager:
            usernames: M1, M2, M3

settings/ingredients.json

    This file contains all the initial ingredient information. This file is only loaded if data/inventory.json is
    not present.

settings/menu.json

    This file contains all the menu items for this restaurant. All menuitems must have valid ingredients

settings/settings.json

    This file includes the settings for creating the restaurant.

    This file has default values:

        {
          "name": "test",
          "table_layout": [
            1,
            10,
            0,
            10,
            0,
            11,
            0,
            4
          ],
          "table_layout_desc": "Number of 1 person seats to number of 8 person seats"
        }

    Meaning:
        This restaurant's name is "test", and this restaurant has:
            One       1-person table,
            Ten       2-person tables,
            Zero      3-person tables,
            Ten       4-person tables,
            Zero      5-person tables,
            Eleven    6-person tables,
            Zero      7-person tables,
            Four      8-person tables.

    These are the only settings for the restaurant.

	
=== How to use the UI? ===

Login page:

    Log in according to settings/accounts.json description in === Settings Explanations ===
    The type of GUI that gets launched is automatically looked up from accounts.json

    NOTE: the login GUI stays open, if you accidentally close it, you will have to restart the program.

Employee-Generic GUI:

    This data is relevant to all Employee GUIs

    Alerts Tab:
        This is the tab where the employee can read alerts that are sent to them.
        Click on alerts to mark them as read

        To clear non-pinned alerts, click "Clear Alerts"
        To send an alert, click "Send Alerts"

    Receive Shipment:
        All Employees are able to receive shipments

        Click on an item in the ingredients list, and enter the amount for that shipment.

    <Popup Selectors>
        Table selector:
            This popup allows you to get a table from a grid, and then a seat.
            Greyed-out tables are tables which are unoccupied.
            Blue tables are tables which are occupied, and have not gotten all their food
            Green tables are tables which are occupied, and are ready to be cleared.

        Date selector:
            This popup allows you to grab the date from a ui.
            Click the calender icon to select a date.


Server GUI:

    Order Tab:
        This tab allows the server to submit an order

        Click on a menu item, then the ingredients are loaded into the 3 Lists

        To add an additional ingredient, select an ingredient from "Additional Ingredients", and
        click the up arrow.

        To remove an additional ingredient, select an ingredient from "Ingredients" that is under the
        "Additional Ingredients" sub-header, and click the down arrow.

        To remove a default ingredient, select an ingredient from "Ingredients" that is under the
        "Default Ingredients" sub-header, and click the down arrow.

        To add a removed ingredient back to the order, select an ingredient in the "Removals" list and
        press the up arrow.

        Click "Submit Order" when you are done, and select a table and a seat from the GUI.

    Active Orders Tab:
        This tab allows the server to view their submitted orders

        If an item is ready for delivery, it will appear under "Ready to Deliver".
        Select an item and press "Deliver Order"

        To Cancel an order, select an order under "Active Orders" and click "Cancel Order"

        To Return an order than has been delivered, press "Return Existing Order" and select
        a table and order from the popup. Then, provide a reason.

    Bills Tab:
        This tab allows the server to get the bill of a table or table-seat combination.

        To get the bill, press "Get Bill", select a non-grey table, and select a seat or "All Seats"
        for the whole table.

        To Copy the contents of the text area, click "Copy to Clipboard"

        To clear the table, click "Clear Table" and select a green table. This marks the table as unoccupied,
        and signifies that the table has paid their bill.


Cook GUI:

    Order Tab:
        This tab allows the cook to view queued order.
        To mark an order as received, select an order from "Available Orders" and press "Take Order"
        To mark an order as cooked, press "Finish Order"


Manager GUI:

    Inventory Tab:
        This tab allows the manager to view the contents of the inventory, and recent shipment arrials

    Statistics Tab:
        This tab allows the manager to view the frequency of Menu Item usage and Ingredients Usage.

    Requests and Payments Tab:
        This tab allows the manager to view the re-order requests and previous payments.

        To get re-order requests, press "Get Requests"

        To get payments in the past, press "Get Daily Payments" and pick a date.

        To Copy contents of the text area to clipboard, press "Copy to Clipboard"

    Active Orders Tab:
        This tab allows the manager to view all the orders that are currently active, and their information.

		