package digital.slovensko.autogram;

import digital.slovensko.autogram.core.Autogram;
import digital.slovensko.autogram.core.SigningJob;
import digital.slovensko.autogram.core.SigningParameters;
import digital.slovensko.autogram.server.AutogramServer;
import digital.slovensko.autogram.ui.cli.CliResponder;
import digital.slovensko.autogram.ui.cli.CliUI;
import digital.slovensko.autogram.ui.gui.GUI;
import eu.europa.esig.dss.model.FileDocument;

public class Main {
    public static void main2(String[] args) {
        var ui = new CliUI();

        var autogram = new Autogram(ui);

        var document = new FileDocument("pom.xml");
        var parameters = new SigningParameters();
        var responder = new CliResponder();

        autogram.pickSigningKey();
        autogram.showSigningDialog(new SigningJob(document, parameters, responder));

        // sign another without picking cert again and no BOK entered
        document = new FileDocument("pom.xml");

        autogram.showSigningDialog(new SigningJob(document, parameters, responder));
    }

    public static void main(String[] args) {
        var ui = new GUI();
        var autogram = new Autogram(ui);

        var server = new AutogramServer(autogram); // TODO based on args?
        server.start(); // TODO args

        autogram.start(args);
    }
}