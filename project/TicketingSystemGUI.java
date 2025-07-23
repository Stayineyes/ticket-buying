package project;
//File: TicketingSystemGUI.java

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class TicketingSystemGUI extends JFrame {
 private TicketingSystem system;
 private User currentUser;

 private JPanel mainPanel;
 private CardLayout cardLayout;

 // login part
 private JTextField idField, passwordField;
 private JButton loginButton;
 private JLabel loginStatusLabel;

 // Organizer part
 private JTextField eventIdField, nameField, dateField, locationField, capacityField, priceField;
 private JTextArea organizerEventArea;
 private JButton createEventButton, updateEventButton, deleteEventButton;

 // Customer part
 private JComboBox<String> eventDropdown;
 private JTextField ticketQtyField;
 private JLabel totalAmountLabel;
 private JButton buyTicketButton, cancelTicketButton;
 private JTextArea customerTicketArea;

 public TicketingSystemGUI() {
     system = new TicketingSystem(); // loading and initialize the system
     system.loadDataFromFile();

     setTitle("Event Ticketing System");
     setSize(700, 500);
     setDefaultCloseOperation(EXIT_ON_CLOSE);
     setLocationRelativeTo(null);

     cardLayout = new CardLayout();
     mainPanel = new JPanel(cardLayout);

     mainPanel.add(createLoginPanel(), "Login");
     mainPanel.add(createOrganizerPanel(), "Organizer");
     mainPanel.add(createCustomerPanel(), "Customer");

     add(mainPanel);
     cardLayout.show(mainPanel, "Login");
     setVisible(true);
 }

 private JPanel createLoginPanel() {
     JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
     idField = new JTextField();
     passwordField = new JTextField();
     loginButton = new JButton("Login");
     loginStatusLabel = new JLabel("", JLabel.CENTER);

     panel.setBorder(BorderFactory.createTitledBorder("Login"));
     panel.add(new JLabel("User ID:"));
     panel.add(idField);
     panel.add(new JLabel("Password:"));
     panel.add(passwordField);
     panel.add(loginButton);
     panel.add(loginStatusLabel);

     loginButton.addActionListener(e -> {
         String id = idField.getText().trim();
         String pw = passwordField.getText().trim();
         currentUser = system.login(id, pw);

         if (currentUser != null) {
             loginStatusLabel.setText("Login successful as " + currentUser.getRole());
             if (currentUser instanceof Organizer) {
                 refreshOrganizerEvents();
                 cardLayout.show(mainPanel, "Organizer");
             } else if (currentUser instanceof Customer) {
                 refreshEventDropdown();
                 refreshCustomerTickets();
                 cardLayout.show(mainPanel, "Customer");
             }
         } else {
             loginStatusLabel.setText("Login failed. Check ID/password.");
         }
     });

     return panel;
 }

 private JPanel createOrganizerPanel() {
     JPanel panel = new JPanel(new BorderLayout());

     JPanel inputPanel = new JPanel(new GridLayout(7, 2));
     eventIdField = new JTextField();
     nameField = new JTextField();
     dateField = new JTextField();
     locationField = new JTextField();
     capacityField = new JTextField();
     priceField = new JTextField();

     inputPanel.add(new JLabel("Event ID:"));
     inputPanel.add(eventIdField);
     inputPanel.add(new JLabel("Name:"));
     inputPanel.add(nameField);
     inputPanel.add(new JLabel("Date:"));
     inputPanel.add(dateField);
     inputPanel.add(new JLabel("Location:"));
     inputPanel.add(locationField);
     inputPanel.add(new JLabel("Capacity:"));
     inputPanel.add(capacityField);
     inputPanel.add(new JLabel("Price:"));
     inputPanel.add(priceField);

     createEventButton = new JButton("Create Event");
     updateEventButton = new JButton("Update Event");
     deleteEventButton = new JButton("Delete Event");

     JPanel buttonPanel = new JPanel();
     buttonPanel.add(createEventButton);
     buttonPanel.add(updateEventButton);
     buttonPanel.add(deleteEventButton);

     organizerEventArea = new JTextArea();
     organizerEventArea.setEditable(false);

     panel.add(inputPanel, BorderLayout.NORTH);
     panel.add(buttonPanel, BorderLayout.CENTER);
     panel.add(new JScrollPane(organizerEventArea), BorderLayout.SOUTH);

     createEventButton.addActionListener(e -> {
         try {
             Event eObj = new Event(
                     eventIdField.getText().trim(),
                     nameField.getText().trim(),
                     dateField.getText().trim(),
                     locationField.getText().trim(),
                     Integer.parseInt(capacityField.getText().trim()),
                     0,
                     Double.parseDouble(priceField.getText().trim()),
                     (Organizer) currentUser
             );
             ((Organizer) currentUser).createEvent(eObj);
             system.addEvent(eObj);
             refreshOrganizerEvents();
         } catch (Exception ex) {
             showError("Invalid input: " + ex.getMessage());
         }
     });

     updateEventButton.addActionListener(e -> {
         String eid = eventIdField.getText().trim();
         for (Event ev : ((Organizer) currentUser).getMyEvents()) {
             if (ev.getEventID().equals(eid)) {
                 ev.setName(nameField.getText().trim());
                 ev.setDate(dateField.getText().trim());
                 ev.setLocation(locationField.getText().trim());
                 ev.setCapacity(Integer.parseInt(capacityField.getText().trim()));
                 ev.setPrice(Double.parseDouble(priceField.getText().trim()));
                 refreshOrganizerEvents();
                 break;
             }
         }
     });

     deleteEventButton.addActionListener(e -> {
         String eid = eventIdField.getText().trim();
         ((Organizer) currentUser).deleteEvent(eid);
         system.getAllEvents().removeIf(ev -> ev.getEventID().equals(eid));
         refreshOrganizerEvents();
     });

     return panel;
 }

 private JPanel createCustomerPanel() {
     JPanel panel = new JPanel(new BorderLayout());

     eventDropdown = new JComboBox<>();
     ticketQtyField = new JTextField(5);
     buyTicketButton = new JButton("Buy Ticket");
     cancelTicketButton = new JButton("Cancel Ticket");
     totalAmountLabel = new JLabel("Total: $0.00");
     customerTicketArea = new JTextArea();
     customerTicketArea.setEditable(false);

     JPanel topPanel = new JPanel();
     topPanel.add(new JLabel("Select Event:"));
     topPanel.add(eventDropdown);
     topPanel.add(new JLabel("Quantity:"));
     topPanel.add(ticketQtyField);
     topPanel.add(buyTicketButton);
     topPanel.add(cancelTicketButton);
     topPanel.add(totalAmountLabel);

     panel.add(topPanel, BorderLayout.NORTH);
     panel.add(new JScrollPane(customerTicketArea), BorderLayout.CENTER);

     buyTicketButton.addActionListener(e -> {
         String selected = (String) eventDropdown.getSelectedItem();
         if (selected == null) return;

         Event selectedEvent = null;
         for (Event ev : system.getAllEvents()) {
             if (ev.getName().equals(selected)) {
                 selectedEvent = ev;
                 break;
             }
         }

         try {
             int qty = Integer.parseInt(ticketQtyField.getText().trim());
             if (selectedEvent == null) return;
             if (!selectedEvent.isAvailable(qty)) {
                 JOptionPane.showMessageDialog(this, "Not enough tickets available!", "Warning", JOptionPane.WARNING_MESSAGE);
                 return;
             }

             double total = qty * selectedEvent.getPrice();
             totalAmountLabel.setText("Total: $" + total);
             system.buyTicket((Customer) currentUser, selectedEvent, qty);
             refreshCustomerTickets();
         } catch (Exception ex) {
             showError("Invalid quantity.");
         }
     });

     cancelTicketButton.addActionListener(e -> {
         String selected = (String) eventDropdown.getSelectedItem();
         if (selected == null) return;

         Event selectedEvent = null;
         for (Event ev : system.getAllEvents()) {
             if (ev.getName().equals(selected)) {
                 selectedEvent = ev;
                 break;
             }
         }

         if (selectedEvent == null) return;

         ArrayList<Ticket> tickets = ((Customer) currentUser).getMyTickets();
         Ticket ticketToCancel = null;
         for (Ticket t : tickets) {
             if (t.getEvent().equals(selectedEvent)) {
                 ticketToCancel = t;
                 break;
             }
         }

         if (ticketToCancel != null) {
             ((Customer) currentUser).cancelTicket(ticketToCancel.getTicketID());
                          refreshCustomerTickets();
             JOptionPane.showMessageDialog(this, "Ticket canceled successfully.", "Info", JOptionPane.INFORMATION_MESSAGE);
         } else {
             JOptionPane.showMessageDialog(this, "No ticket found for the selected event.", "Warning", JOptionPane.WARNING_MESSAGE);
         }
     });

     return panel;
 }

 private void refreshOrganizerEvents() {
     organizerEventArea.setText("");
     for (Event e : ((Organizer) currentUser).getMyEvents()) {
         organizerEventArea.append(e.getDetails() + "\n\n");
     }
 }

 private void refreshEventDropdown() {
     eventDropdown.removeAllItems();
     for (Event e : system.getAllEvents()) {
         eventDropdown.addItem(e.getName());
     }
 }

 private void refreshCustomerTickets() {
     customerTicketArea.setText("");
     for (Ticket t : ((Customer) currentUser).getMyTickets()) {
         customerTicketArea.append(t.getInfo() + "\n");
     }
 }

 private void showError(String msg) {
     JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
 }

 public static void main(String[] args) {
     SwingUtilities.invokeLater(TicketingSystemGUI::new);
 }
}