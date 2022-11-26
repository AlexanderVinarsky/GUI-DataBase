import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception{
        GUI gui = new GUI(); //Создать графический интерфейс
        gui.setVisible(true); //Сделать его видимым
        gui.setResizable(false); //Запретить изменять размеры окна
    }
}