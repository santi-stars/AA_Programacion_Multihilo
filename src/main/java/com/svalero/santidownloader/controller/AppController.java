package com.svalero.santidownloader.controller;

import com.svalero.santidownloader.util.R;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class AppController {

    public TextField tfUrl;
    public Button btDownload;
    public Button btCancel;
    public Button btRegister;
    public TabPane tpDownloads;
    public TextArea txaRegister;
    public ScrollPane sp;

    protected File file;
    protected Map<String, DownloadController> allDownloads;
    // ***MODIFICAR***          ruta de descarga por defecto                 ***MODIFICAR***
    private final File DEFAULT_FILE = new File(System.getProperty("user.home"));
    // ***MODIFICAR***          ruta del archivo LOG por defecto             ***MODIFICAR***
    private final String LOG_FILE_ROUTE = System.getProperty("user.dir") + "\\santidownloader.log";

    public AppController() {
        file = DEFAULT_FILE;
        allDownloads = new HashMap<>();
    }

    /**
     * Llama al método launch() para lanzar la descarga con la URL del TextFiel
     *
     * @param event
     */
    @FXML
    public void launchDownload(ActionEvent event) {

        String urlText = tfUrl.getText();   // Coje la URL del textField
        tfUrl.clear();                      // Limpia el textField
        tfUrl.requestFocus();               // Reclama el foco en el textField

        launch(urlText, file);            // Ejecuta nuestro método launch que lanza la descarga

    }

    /**
     * Crea una pestaña nueva con la descarga y la renombra recortando el link
     *
     * @param url String con la URL de la descarga
     */
    private void launch(String url, File file) {

        try {

            String timestamp = ZonedDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd-MM-yyy_hh-mm-ss"));
            // Crea un nombre del archivo a partir de la URL seleccionando el texto despues del último "/"
            String filename = url.substring(url.lastIndexOf("/") + 1);
            // Creamos un nuevo archivo con la ruta establecida o la configurada por nosotros,
            // más "timestamp" delante como nombre de archivo para hacer único cada nombre de descarga
            File newFile = new File(file.toString().concat("\\" + timestamp + "_" + filename));

            if (filename.length() > 15) // Recorta el nombre con mas de 15 caracteres con "..."
                filename = (filename.substring(0, 15) + "...");

            FXMLLoader loader = new FXMLLoader();   // Crea un cargador
            loader.setLocation(R.getUI("download.fxml"));   // Asigna la interfaz de las pestañas

            DownloadController downloadController = new DownloadController(url, newFile);
            loader.setController(downloadController);   // Añade al cargador
            VBox downloadBox = loader.load();   // Lo carga en un VBox
            // Crea una tab con el nombre anterior y con el VBox que hemos creado
            tpDownloads.getTabs().add(new Tab(filename, downloadBox));
            // Añade la descarga de tipo "downloadController" al Map "allDownloads"
            allDownloads.put(newFile.toString(), downloadController);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

    /**
     * Para, todas las descargas activas y cierra todas las pestañas
     */
    @FXML
    public void stopAllDownloads() {

        for (DownloadController downloadController : allDownloads.values())
            downloadController.stop();

        allDownloads.clear();
        tpDownloads.getTabs().clear();

    }

    /**
     * Abre una ventana para elegir la ubicación del archivo DLC de descarga a leer
     * y llama al método launch() por cada link de descarga para lanzar la descarga
     */
    @FXML
    public void readDLC() {

        try {   // Abre una ventana para elegir la ubicación del archivo DLC de descarga

            FileChooser fileChooser = new FileChooser();
            File dlcFile = fileChooser.showOpenDialog(tfUrl.getScene().getWindow());
            if (dlcFile == null)
                return;
            Scanner reader = new Scanner(dlcFile);
            String data, oldData = "";

            while (reader.hasNextLine()) {  // Mientras haya siguiente linea  llama al método launch para lanzar cada descarga

                data = reader.nextLine();
                if (data.equals(oldData))   // Pausa la lectura si los enlaces son iguales para evitar nombres iguales
                    Thread.sleep(666);

                oldData = data;
                launch(data, file);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }

    }

    /**
     * Lee el archivo del registro de descargas y lo devuelve como String
     *
     * @return String con los LOG's de las descargas
     */
    private String readFile() {

        String texto = "";

        try {
            BufferedReader bf = new BufferedReader(new FileReader(LOG_FILE_ROUTE));
            String bfRead;

            while ((bfRead = bf.readLine()) != null)
                texto += "---" + bfRead + "\n";

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return texto;
    }

    /**
     * Abre un TextArea con Scroll y muestra los LOG con las descargas
     */
    @FXML
    public void viewLog() {

        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(R.getUI("registro.fxml"));
            txaRegister = loader.load();

            Scene scene = new Scene(txaRegister);
            stage.setScene(scene);
            stage.setTitle("Registro de descargas");
            stage.show();
            // Añado un listener para poner una barra de Scroll cuando haya cambios en el texto
            txaRegister.textProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue)
                    -> txaRegister.setScrollTop(Double.MIN_VALUE));
            txaRegister.setEditable(false); // Evita que se modifique el texto
            txaRegister.setWrapText(true);  // TRUE envuelve el texto que pasamos en setText, si no, lo muestra en una linea
            txaRegister.setText(readFile());// Añade al textArea el texto del archivo del LOG a través del método: readFile()
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Elije la ruta de descarga
     */
    @FXML
    public void chooseDownloadPath(ActionEvent event) {

        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setInitialDirectory(DEFAULT_FILE);
        Stage stage = (Stage) sp.getScene().getWindow();
        file = dirChooser.showDialog(stage);

        if (file == null)
            file = DEFAULT_FILE;

    }

}
