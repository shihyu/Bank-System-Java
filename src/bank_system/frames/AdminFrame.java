package bank_system.frames;

import static bank_system.Consts.*;

import bank_system.DataStorage;
import bank_system.clients.Client;
import bank_system.clients.Clients;
import bank_system.clients.services.CreditCard;
import bank_system.clients.services.Order;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 * Frame builder for admin. Admin have opportunity to add order to users and
 * block cards with negative balance.
 *
 * Created on 3/19/2017.
 *
 * @author Serhii Petrusha aka Mr_Rism
 * @since JDK1.8
 */
class AdminFrame extends JFrame {

  private DataStorage dataStorage = null;

  private JList<CreditCardToJlistAdapter> listOfCreditCardWithNegativeBalance = null;

  AdminFrame(DataStorage dataStorage) {

    super("Admin controls");

    this.dataStorage = dataStorage;

    Clients clients = dataStorage.getClients();

    setLayout(new BorderLayout());

    JPanel topPanel = new JPanel();
    topPanel.add(new JLabel("Current user: Admin"));

    JButton logOut = new JButton("Log out");
    topPanel.add(logOut);
    add(topPanel, BorderLayout.NORTH);

    JPanel centerPanel = new JPanel();
    centerPanel.setLayout(new GridLayout(1, 2));

    JPanel orderCreationPanel = new JPanel();
    centerPanel.add(orderCreationPanel);
    orderCreationPanel.add(new JLabel("Orders "));

    class ClientToComboBoxAdapter {

      private Client client = null;

       ClientToComboBoxAdapter(Client client) {
        this.client = client;

      }

      public Client getClient() {
        return client;
      }

      @Override
      public String toString() {
        return client.getName();
      }
    }

    JComboBox<ClientToComboBoxAdapter> comboBoxClients = new JComboBox<>();

    for (Client client : clients.getUsers()
        ) {
      if (!client.isAdmin()) {
        comboBoxClients.addItem(new ClientToComboBoxAdapter(client));
      }

    }

    JTextField orderPaymentAmount = new JTextField("", 6);
    JTextField orderInfoField = new JTextField("", 18);
    JTextField orderDestinationAccount = new JTextField("", 6);

    JButton addOrderButton = new JButton("Add Order");

    orderCreationPanel.add(new JLabel("Choose client"));
    orderCreationPanel.add(comboBoxClients);
    orderCreationPanel.add(new JLabel("Destination Account"));
    orderCreationPanel.add(orderDestinationAccount);
    orderCreationPanel.add(new JLabel("Payment"));
    orderCreationPanel.add(orderPaymentAmount);
    orderCreationPanel.add(new JLabel("Order information"));
    orderCreationPanel.add(orderInfoField);
    orderCreationPanel.add(addOrderButton);

    addOrderButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        try {

          ClientToComboBoxAdapter clientToComboBoxAdapter = (ClientToComboBoxAdapter) comboBoxClients
              .getSelectedItem();
          Client client = clientToComboBoxAdapter.getClient();
          client.addOrder(
              new Order(
                  Long.parseLong(orderDestinationAccount.getText()),
                  Long.parseLong(orderPaymentAmount.getText()),
                  orderInfoField.getText()
              )
          );
          dataStorage.saveToFile();
          JOptionPane.showMessageDialog(null, "Order added");
          comboBoxClients.setSelectedIndex(-1);
          orderDestinationAccount.setText("");
          orderInfoField.setText("");
          orderPaymentAmount.setText("");

        } catch (NumberFormatException e1) {

          //write down to log file

        }

      }
    });

    JPanel creditCardsAdminPanel = new JPanel();
    centerPanel.add(creditCardsAdminPanel);

    creditCardsAdminPanel.setLayout(new BorderLayout());


    listOfCreditCardWithNegativeBalance = new JList<>();
    refreshList();


    creditCardsAdminPanel.add(listOfCreditCardWithNegativeBalance, BorderLayout.CENTER);
    JButton blockCardButton = new JButton("Block Card");
    creditCardsAdminPanel.add(blockCardButton, BorderLayout.SOUTH);
    blockCardButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        if (listOfCreditCardWithNegativeBalance.getSelectedIndex() > -1) {

          CreditCardToJlistAdapter creditCardToJlistAdapter = listOfCreditCardWithNegativeBalance.getSelectedValue();
          CreditCard creditCard = creditCardToJlistAdapter.getCreditCard();
              creditCard.setBlocked(true);
          refreshList();
          dataStorage.saveToFile();

        }
      }
    });

    add(centerPanel, BorderLayout.CENTER);

    setMinimumSize(new Dimension(MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT));
    setVisible(true);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    logOut.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {

        new LoginFrame(LOGIN_WINDOW_TITLE, dataStorage);
        dispose();

      }


    });

  }
  /**
   * Provide a <code>CreditCard></code>adaptation for population a JComboBox. It's overrides toString().
   *
   */
  private class CreditCardToJlistAdapter {

    CreditCard creditCard = null;

    CreditCardToJlistAdapter(CreditCard creditCard ){

      this.creditCard = creditCard;

    }

    public CreditCard getCreditCard() {
      return creditCard;
    }

    @Override
    public String toString() {
      return "Id:"+creditCard.getId()+ " B:" +creditCard.getBalance();
    }
  }


  /*
  * Updates JList with cards with negative balance
  *
  * */
  private void refreshList() {

    listOfCreditCardWithNegativeBalance.removeAll();

    Vector<CreditCardToJlistAdapter> cardToJlistAdapterList = new Vector<>();

    for (CreditCard creditCard : dataStorage.getCardsWithNegativeBalance().getCards()
        ) {
      if (!creditCard.isBlocked())
           cardToJlistAdapterList.add(new CreditCardToJlistAdapter(creditCard));

    }

    listOfCreditCardWithNegativeBalance.setListData(cardToJlistAdapterList);

    }

  @Override
  public String toString() {
    return "AdminFrame{" +
        "dataStorage=" + dataStorage +
        ", listOfCreditCardWithNegativeBalance=" + listOfCreditCardWithNegativeBalance +
        "} " + super.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    AdminFrame that = (AdminFrame) o;

    if (dataStorage != null ? !dataStorage.equals(that.dataStorage) : that.dataStorage != null) {
      return false;
    }
    return listOfCreditCardWithNegativeBalance != null ? listOfCreditCardWithNegativeBalance
        .equals(that.listOfCreditCardWithNegativeBalance)
        : that.listOfCreditCardWithNegativeBalance == null;
  }

  @Override
  public int hashCode() {
    int result = dataStorage != null ? dataStorage.hashCode() : 0;
    result = 31 * result + (listOfCreditCardWithNegativeBalance != null
        ? listOfCreditCardWithNegativeBalance.hashCode() : 0);
    return result;
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
