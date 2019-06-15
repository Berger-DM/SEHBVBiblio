package mainApp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Pair;
import sqlite.DBOps;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Main extends Application
{
    static boolean LoginTest = false;
    private static String nomeuser;
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static LocalDate dataDeHoje = LocalDate.now();
    private static String date = formatter.format(dataDeHoje);

    @Override
    public void start(Stage primaryStage) throws Exception
    {

        Parent root = FXMLLoader.load(getClass().getResource("sehbv23.fxml"));
        primaryStage.setTitle("SEHBV Biblio 2.3");
        primaryStage.setScene(new Scene(root, 900, 600));
        primaryStage.show();
        //Platform.setImplicitExit(false); possível correção de bug, bug não ocorrendo no momento devido a alterações.

        Login();
    }

    public static String getDate()
    {
        return date;
    }

    public static String getNomeuser() { return nomeuser;}

    public static void setNomeuser(String string)
    {
        nomeuser = string;
    }


    private static void Login()
    {
        Dialog<Pair<String, String>> loginDialog = new Dialog<>();
        loginDialog.setTitle("Login no sistema");
        loginDialog.setHeaderText("Faça Login para continuar");

        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        loginDialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CLOSE);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10,10,10,25));

        TextField username = new TextField();
        username.setPromptText("Usuário");
        PasswordField password = new PasswordField();
        password.setPromptText("Senha");

        grid.add(username, 0,0, 2, 1);
        grid.add(password, 0,1, 2, 1);

        Node loginButton = loginDialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        username.textProperty().addListener((observable, oldValue, newValue) ->
        {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        loginDialog.getDialogPane().setContent(grid);

        Platform.runLater(() -> username.requestFocus());

        loginDialog.setResultConverter(dialogButton ->
        {
            if (dialogButton == loginButtonType)
            {
                return new Pair<>(username.getText(), password.getText());
            }
            else if (dialogButton == ButtonType.CLOSE)
            {
                Platform.exit();
            }
            return null;
        });

        Optional<Pair<String, String>> result = loginDialog.showAndWait();

        result.ifPresent(usernamePassword ->
        {
            try {
                LoginTest = DBOps.Login(usernamePassword.getKey(), usernamePassword.getValue());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (LoginTest)
            {
                Alert welcome = new Alert(Alert.AlertType.INFORMATION);
                welcome.setTitle("Login Realizado!");
                welcome.setHeaderText(null);
                welcome.setContentText(String.format("Olá %s!\nData confirmada pelo sistema: %s.",
                        nomeuser, getDate()));
                welcome.showAndWait();
            }
            else
            {
                Alert fail = new Alert(Alert.AlertType.ERROR);
                fail.setTitle("Falha no Login");
                fail.setHeaderText(null);
                fail.setContentText("Combinação Usuário/Senha não encontrada. Por favor, tente novamente");
                fail.showAndWait();
                Login();
            }
        });
    }

    public static void main(String[] args)
    {
        DBOps.connect();
        launch(args);
    }
}
