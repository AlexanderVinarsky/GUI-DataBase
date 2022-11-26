import java.awt.*; // Импорт
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class GUI extends JFrame {  //Графический интерфейс - наследник JFrame из javax.swing
    private static final String DB_URL = "jdbc:postgresql://localhost:5432/library"; //Ссылка на базу данных
    private static final String DB_USERNAME = "postgres"; //Имя пользователя СУБД
    private static final String DB_PASSWORD = "89225523232Alex"; //Пароль пользователя СУБД
    private final Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD); //Подключаемся к БД
    private final JTextField[] user_input = {new JTextField("", 10), new JTextField("", 10),
            new JTextField("", 10)};
    private final int names = 0;
    private final int author = 1;
    private final int id = 2;
    public GUI() throws SQLException { //Конструктор
        super("Библиотека"); //Название окна
        this.setBounds(100, 100, 500, 250); //Размеры окна
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Разрешаем его закрывать

        var label_add    = new JLabel("Укажите название книги и автора:");
        var label_delete = new JLabel("Укажите айди, чтобы удалить книгу из базы:");

        label_add.setHorizontalAlignment(SwingConstants.CENTER); //Выравнивание текстов лэйблов и текстбоксов
        label_delete.setHorizontalAlignment(SwingConstants.CENTER);

        for (JTextField jTextField : user_input) {
            jTextField.setHorizontalAlignment((SwingConstants.CENTER));
        }

        this.getContentPane().setLayout(new GridBagLayout()); //Создаём контейнер //Присваиваем ему лэйаут
        var interfaceGrid = new GridBagConstraints(); //Создаём линии, на основе которых располагются объекты
        // в лэйауте

        //Создаём кнопки, текстовые поля, лэйблы
        var button_show   = new JButton("Вывести базу"); //Добавляем действия на нажатии кнопок к кнопкам
        var button_add    = new JButton("Добавить книгу");
        var button_delete = new JButton("Удалить книгу");

        button_add.addActionListener(new ButtonAddEventListener());
        button_show.addActionListener(new ButtonShowEventListener());
        button_delete.addActionListener(new ButtonDeleteEventListener());

        interfaceGrid.insets = new Insets(5,5,5,5); //Расствляем координаты и добавляем элементы на места
        interfaceGrid.gridx = 0; //Изменяем координату x
        interfaceGrid.gridy = 0; //Изменяем координату y
        add(button_show, interfaceGrid); //Добавить элемент через interfaceGrid
        interfaceGrid.gridx = 0;
        interfaceGrid.gridy = 2;
        add(label_add, interfaceGrid);
        interfaceGrid.gridx = 4;
        interfaceGrid.gridy = 3;
        add(user_input[id], interfaceGrid);
        interfaceGrid.gridx = 0;
        interfaceGrid.gridy = 5;
        add(button_add, interfaceGrid);
        interfaceGrid.gridx = 4;
        interfaceGrid.gridy = 2;
        add(label_delete, interfaceGrid);
        interfaceGrid.gridx = 4;
        interfaceGrid.gridy = 5;
        add(button_delete, interfaceGrid);

        interfaceGrid.gridwidth = 2; //Изменяем горизонтальный размер элементов
        interfaceGrid.fill = GridBagConstraints.HORIZONTAL; //Заполняем полностью по горизонтали
        interfaceGrid.gridx = 0;
        interfaceGrid.gridy = 3;
        add(user_input[names], interfaceGrid);
        interfaceGrid.gridx = 0;
        interfaceGrid.gridy = 4;
        add(user_input[author], interfaceGrid);
    }
    class ButtonShowEventListener implements ActionListener{ //Что делает кнопка показать БД:
        public void actionPerformed(ActionEvent e){
            var sqlCommand = "select * from books order by book_id"; //Команда для SQL
            ResultSet result;

            try {
                result = connection.createStatement().executeQuery(sqlCommand); //Получаем данные из бд в result
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

            var message = new StringBuilder(); //Создаём пустой стринг
            while (true) {
                try {
                    if (!result.next()) break;
                    message.append(result.getInt("book_id")).append(" | ").append(result.
                            getString("book_name")).append(" | ").append(result.
                            getString("book_author")).append("\n"); //Вставляем данные из result в строку
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
            var textArea = new JTextArea(message.toString());
            var scrollPane = new JScrollPane(textArea); //Создаём лист, который можно скроллить
            scrollPane.setPreferredSize( new Dimension( 400, 300 ) );
            JOptionPane.showMessageDialog(null, scrollPane, "Данные о книгах:",
                    JOptionPane.PLAIN_MESSAGE); //Закидываем его в новое окно, называем его, выбираем
            //стандартное отображение
        }
    }
    class ButtonAddEventListener implements ActionListener{ //Что делает кнопка добавить книгу:
        public void actionPerformed(ActionEvent e){
            //таблицу books в столбики book_name и book_author некоторые переменные, которые присвоим далее
            try {
                var book_name   = user_input[names].getText(); //создаём строчки, которые нужно присвоить (их берём из текстов,
                var book_author = user_input[author].getText();

                var insertCommand = "insert into books (book_name, book_author) values (?,?)"; //Команда для SQL вставить в
                var preparedStatement = connection.prepareStatement(insertCommand); //У нас есть заготовленный комманда
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
            // (айди всегда уникально)
            try {
                var book_id = Integer.parseInt(user_input[id].getText());

                var deleteCommand = "delete from books where book_id = ?"; //Команда SQL: удалить книги с айди = ?
                var preparedStatement = connection.prepareStatement(deleteCommand);
                preparedStatement.setInt(1, book_id);
                //Засовываем туда банан, который спарсили со стринга, который получили с текстбокса input_id
                preparedStatement.executeUpdate(); //Выполнить команду
            } catch (Exception ex) {
                return;
            }

            JOptionPane.showMessageDialog(null, "Книга успешно удалена!",
                    "Выполнено", JOptionPane.PLAIN_MESSAGE); //Сообщение об успехе
        }
    }
}
