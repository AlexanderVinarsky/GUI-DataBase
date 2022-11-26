import java.awt.*; //Импорт
import java.awt.event.*;
import java.sql.*;
import java.util.GregorianCalendar;
import java.util.Scanner;
import javax.swing.*;

public class GUI extends JFrame {  //Графический интерфейс - наследник JFrame из javax.swing
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/library"; //Ссылка на базу данных
    private static final String DB_USERNAME = "postgres"; //Имя пользователя СУБД
    private static final String DB_PASSWORD = "89225523232"; //Пароль пользователя СУБД
    Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD); //Подключаемся к БД
    private JButton button_show = new JButton("Вывести базу"); //Создаём кнопки, текстовые поля, лэйблы
    private JButton button_add = new JButton("Добавить книгу");
    private JButton button_delete = new JButton("Удалить книгу");
    private JTextField input_name = new JTextField("", 10);
    private JTextField input_author = new JTextField("", 10);
    private JTextField input_id = new JTextField("", 10);
    private JLabel label_add = new JLabel("Укажите название книги и автора:");
    private JLabel label_delete = new JLabel("Укажите айди, чтобы удалить книгу из базы:");
    public GUI() throws SQLException { //Конструктор
        super("Библиотека"); //Название окна
        this.setBounds(100, 100, 500, 250); //Размеры окна
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Разрешаем его закрывать

        label_add.setHorizontalAlignment(SwingConstants.CENTER); //Выравнивание текстов лэйблов и текстбоксов
        label_delete.setHorizontalAlignment(SwingConstants.CENTER);
        input_id.setHorizontalAlignment(SwingConstants.CENTER);
        input_name.setHorizontalAlignment(SwingConstants.CENTER);
        input_author.setHorizontalAlignment(SwingConstants.CENTER);

        Container container = this.getContentPane(); //Создаём контейнер
        container.setLayout(new GridBagLayout()); //Присваиваем ему лэйаут
        GridBagConstraints gbc = new GridBagConstraints(); //Создаём линии, на основе которых располагются объекты
        // в лэйауте

        button_show.addActionListener(new ButtonShowEventListener()); //Добавляем действия на нажатии кнопок к кнопкам
        button_add.addActionListener(new ButtonAddEventListener());
        button_delete.addActionListener(new ButtonDeleteEventListener());

        gbc.insets = new Insets(5,5,5,5); //Расствляем координаты и добавляем элементы на места
        gbc.gridx = 0; //Изменяем координату x
        gbc.gridy = 0; //Изменяем координату y
        add(button_show, gbc); //Добавить элемент через gbc
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

        gbc.gridwidth = 2; //Изменяем горизонтальный размер элементов
        gbc.fill = GridBagConstraints.HORIZONTAL; //Заполняем полностью по горизонтали
        gbc.gridx = 0;
        gbc.gridy = 3;
        add(input_name, gbc);
        gbc.gridx = 0;
        gbc.gridy = 4;
        add(input_author, gbc);
        }
    class ButtonShowEventListener implements ActionListener{ //Что делает кнопка показать БД:
        public void actionPerformed(ActionEvent e){
            Statement statement = null;
            String sql = "select * from books order by book_id"; //Команда для SQL
            ResultSet result = null;
            try {
                statement = connection.createStatement();
                result = statement.executeQuery(sql); //Получаем данные из бд в result
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            String message = ""; //Создаём пустой стринг
            while (true) {
                try {
                    if (!result.next()) break;
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    message += (result.getInt("book_id") + " | "
                            + result.getString("book_name") + " | "
                            + result.getString("book_author") +"\n"); //Вставляем данные из result в строку
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            JTextArea textArea = new JTextArea(message);
            JScrollPane scrollPane = new JScrollPane(textArea); //Создаём лист, который можно скроллить
            scrollPane.setPreferredSize( new Dimension( 400, 300 ) );
            JOptionPane.showMessageDialog(null, scrollPane, "Данные о книгах:",
                    JOptionPane.PLAIN_MESSAGE); //Закидываем его в новое окно, называем его, выбираем
            //стандартное отображение
        }
    }
    class ButtonAddEventListener implements ActionListener{ //Что делает кнопка добавить книгу:
        public void actionPerformed(ActionEvent e){
            String sql = "insert into books (book_name, book_author) values (?,?)"; //Команда для SQL вставить в
            //таблицу books в столбики book_name и book_author некоторые переменные, которые присвоим далее
            String book_name = input_name.getText(); //создаём строчки, которые нужно присвоить (их берём из текстов,
                                                     //которые указан в текстбоксах)
            String book_author = input_author.getText();
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(sql); //У нас есть заготовленная
                // команда
                preparedStatement.setString(1, book_name); //В команду мы добавляем book_name на первый ?
                preparedStatement.setString(2, book_author); //И на второй ? - book_author
                preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            JOptionPane.showMessageDialog(null, "Книга успешно добавлена!",
                    "Выполнено", JOptionPane.PLAIN_MESSAGE); //Показать в отдельном окне сообщение
        }
    }
    class ButtonDeleteEventListener implements ActionListener{
        public void actionPerformed(ActionEvent e){
            String sql = "delete from books where book_id = ?"; //Команда SQL: удалить книги с айди = ?
            // (айди всегда уникально)
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(sql); //Заготовленная команда
                preparedStatement.setInt(1, Integer.parseInt(input_id.getText()));
                //Засовываем туда айди, который спарсили со стринга, который получили с текстбокса input_id
                preparedStatement.executeUpdate(); //Выполнить команду
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            JOptionPane.showMessageDialog(null, "Книга успешно удалена!",
                    "Выполнено", JOptionPane.PLAIN_MESSAGE); //Сообщение об успехе
        }
    }
}
