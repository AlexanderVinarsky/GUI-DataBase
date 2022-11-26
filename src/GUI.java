import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.GregorianCalendar;
import java.util.Scanner;
import javax.swing.*;

public class GUI extends JFrame {
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/library";
    private static final String DB_USERNAME = "postgres";
    private static final String DB_PASSWORD = "89225523232Alex";
    Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    private JButton button_show = new JButton("Вывести базу");
    private JButton button_add = new JButton("Добавить книгу");
    private JButton button_delete = new JButton("Удалить книгу");
    private JTextField input_name = new JTextField("", 10);
    private JTextField input_author = new JTextField("", 10);
    private JTextField input_id = new JTextField("", 10);
    private JLabel label_add = new JLabel("Укажите название книги и автора:");
    private JLabel label_delete = new JLabel("Укажите айди, чтобы удалить книгу из базы:");
    public GUI() throws SQLException {
        super("Библиотека");
        this.setBounds(100, 100, 500, 250);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        label_add.setHorizontalAlignment(SwingConstants.CENTER);
        label_delete.setHorizontalAlignment(SwingConstants.CENTER);
        input_id.setHorizontalAlignment(SwingConstants.CENTER);
        input_name.setHorizontalAlignment(SwingConstants.CENTER);
        input_author.setHorizontalAlignment(SwingConstants.CENTER);
        Container container = this.getContentPane();

        //container.setLayout(new GridLayout(8, 1, 10, 10));
        container.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        button_show.addActionListener(new ButtonShowEventListener());
        button_add.addActionListener(new ButtonAddEventListener());
        button_delete.addActionListener(new ButtonDeleteEventListener());

        gbc.insets = new Insets(5,5,5,5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(button_show, gbc);
        gbc.gridx = 0;
        gbc.gridy = 2;
        add(label_add, gbc);
        gbc.gridx = 4;
        gbc.gridy = 3;
        add(input_id, gbc);
        gbc.gridx = 0;
        gbc.gridy = 5;
        add(button_add, gbc);
        gbc.gridx = 4;
        gbc.gridy = 2;
        add(label_delete, gbc);
        gbc.gridx = 4;
        gbc.gridy = 5;
        add(button_delete, gbc);

        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(input_name, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(input_author, gbc);
        }
    class ButtonShowEventListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            Statement statement = null;
            String sql = "select * from books order by book_id";
            ResultSet result = null;
            try {
                statement = connection.createStatement();
                result = statement.executeQuery(sql);
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            String message = "";
            while (true) {
                try {
                    if (!result.next()) break;
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    message += (result.getInt("book_id") + " | "
                            + result.getString("book_name") + " | "
                            + result.getString("book_author") +"\n");
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            JTextArea textArea = new JTextArea(message);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize( new Dimension( 400, 300 ) );
            JOptionPane.showMessageDialog(null, scrollPane, "Данные о книгах:",
                    JOptionPane.PLAIN_MESSAGE);
            //JOptionPane.showMessageDialog(null, message, "Output", JOptionPane.PLAIN_MESSAGE);
        }
    }
    class ButtonAddEventListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            String sql = "insert into books (book_name, book_author) values (?,?)";
            String book_name = input_name.getText();
            String book_author = input_author.getText();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setString(1, book_name);
                preparedStatement.setString(2, book_author);
                preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            System.out.println("Книга успешно добавлена!");
            JOptionPane.showMessageDialog(null, "Книга успешно добавлена!",
                    "Output", JOptionPane.PLAIN_MESSAGE);
        }
    }
    class ButtonDeleteEventListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            String sql = "delete from books where book_id = ?";
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, Integer.parseInt(input_id.getText()));
                preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            JOptionPane.showMessageDialog(null, "Книга успешно удалена!",
                    "Output", JOptionPane.PLAIN_MESSAGE);
        }
    }
}
