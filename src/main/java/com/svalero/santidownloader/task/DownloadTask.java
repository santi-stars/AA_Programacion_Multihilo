package com.svalero.santidownloader.task;

import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;

public class DownloadTask extends Task<Integer> {

    private URL url;
    private File file;

    private static final Logger logger = LogManager.getLogger(DownloadTask.class);

    public DownloadTask(String urlText, File file) throws MalformedURLException {
        this.url = new URL(urlText);
        this.file = file;
    }

    @Override
    protected Integer call() throws Exception {
        logger.trace("Descarga " + url.toString() + " iniciada");
        updateMessage("Conectando con el servidor . . .");

        URLConnection urlConnection = url.openConnection();
        double fileSize = urlConnection.getContentLength();
        BufferedInputStream in = new BufferedInputStream(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte[] dataBuffer = new byte[1024];
        int bytesRead;
        int totalRead = 0;
        double downloadProgress = 0;

        while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
            downloadProgress = ((double) totalRead / fileSize);
            updateProgress(downloadProgress, 1);

            DecimalFormat df = new DecimalFormat("##.##");
            df.setRoundingMode(RoundingMode.DOWN);

            updateMessage(totalRead/1000000 + " MB / " + df.format(downloadProgress * 100) + " %");

            fileOutputStream.write(dataBuffer, 0, bytesRead);
            totalRead += bytesRead;

            if (isCancelled()) {
                updateMessage("");
                fileOutputStream.close();
                return null;
            }
        }

        updateProgress(1, 1);
        updateMessage("100 %");
        fileOutputStream.close();   // COPIAR?¿?¿?¿
        logger.trace("Finalizado: " + url.toString());
        return null;
    }
    /*
    @Override
    protected Integer call() throws Exception {
        logger.trace("Descarga " + url.toString() + " iniciada");
        updateMessage("Conectando con el servidor . . .");

        URLConnection urlConnection = url.openConnection(); // Crea una conexión a internet con la URL dada
        double fileSize = urlConnection.getContentLength(); // Saca el tamaño del fichero a descargar
        BufferedInputStream in = new BufferedInputStream(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte dataBuffer[] = new byte[1024];
        int bytesRead;
        int totalRead = 0;
        double downloadProgress = 0;

        // Guarda cada Kb en fileOutputStream
        while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
            downloadProgress = ((double) totalRead / fileSize);
            updateProgress(downloadProgress, 1);

            DecimalFormat df = new DecimalFormat("##");
            updateMessage(df.format(downloadProgress * 100) + " % ||" + totalRead / 1000000 + " MB");

            fileOutputStream.write(dataBuffer, 0, bytesRead);
            totalRead += bytesRead;

            // Comprueba si alguien ha llamado al método isCancelled para cancelar la descarga
            if (isCancelled()) {
                fileOutputStream.close();   // Cierra el torrent abierto
                updateMessage("Descarga cancelada");    // Actualiza el mensaje
                logger.trace("Descarga " + url.toString() + " cancelada");
                return null;
            }
        }

        // Pongo 100% en la barra al terminar el bucle para que no se quede en un 99,..%
        updateProgress(1, 1); // La relleno del todo
        updateMessage("100 %"); // Pongo el 100%

        logger.trace("Descarga " + url.toString() + " finalizada");
        return null;
    }

     */
}
