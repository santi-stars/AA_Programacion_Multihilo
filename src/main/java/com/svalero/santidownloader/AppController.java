package com.svalero.santidownloader;

import com.svalero.santidownloader.util.R;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AppController {

    public TextField tfUrl;
    public Button btDownload;
    public TabPane tpDownloads;

    private Map<String, DownloadController> allDownloads;

    @FXML
    public File defaultFile = new File("C:/Users/Santi/Downloads"); // Establezco una ruta por defecto
    public File file = defaultFile; // La asigno a la ruta donde se descargará si no se cambia despues

    private static final Logger logger = LogManager.getLogger(AppController.class);

    public AppController() {
        allDownloads = new HashMap<>();
    }

    @FXML
    public void launchDownload(ActionEvent event) {
        String urlText = tfUrl.getText();
        tfUrl.clear();
        tfUrl.requestFocus();

        launchDownload(urlText);
    }

    private void launchDownload(String url) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(R.getUI("download.fxml"));

            DownloadController downloadController = new DownloadController(url);
            loader.setController(downloadController);
            VBox downloadBox = loader.load();

            String filename = url.substring(url.lastIndexOf("/") + 1);
            tpDownloads.getTabs().add(new Tab(filename, downloadBox));

            allDownloads.put(url, downloadController);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    @FXML
    public void stopAllDownloads() {
        for (DownloadController downloadController : allDownloads.values())
            downloadController.stop();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Se han finalizado todas las descargas!");
        alert.show();
        logger.trace("Todas las descargas detenidas");
    }

    @FXML
    public void readDLC() {
        // Todo dento de un try catch
        // Hay que pedir el fichero en la interfaz FileChooser
        /*
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(tfUrl.getScene().getWindow());
        if (file == null)
            return;
         */

        // Leo el fichero y cargo cada linea en un list

        // Fille.read devuelve una lista de Strings y en cada linea es una linea
        // con una linea leemos el fichero, con 2 lineas hacemos un foreach que llama al launchDownloader

        // Para cada linea; llamar al método launchDownload
    }
}
