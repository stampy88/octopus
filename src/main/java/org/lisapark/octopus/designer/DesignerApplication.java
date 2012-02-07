package org.lisapark.octopus.designer;

import com.jidesoft.plaf.LookAndFeelFactory;
import org.apache.commons.io.IOUtils;
import org.lisapark.octopus.repository.OctopusRepository;
import org.lisapark.octopus.repository.db4o.OctopusDb4oRepository;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author dave sinclair(david.sinclair@lisa-park.com)
 */
public class DesignerApplication {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.printf("Usage: DesignerApplication propertyFile\n");
            System.exit(-1);
        }

        Properties properties = parseProperties(args[0]);
        String repositoryFile = properties.getProperty("octopus.repository.file");
        if (repositoryFile == null || repositoryFile.length() == 0) {
            System.err.printf("The property file %s is missing the octopus.repository.file property", args[0]);
            System.exit(-1);
        }

        OctopusRepository repository = new OctopusDb4oRepository(repositoryFile);
        LookAndFeelFactory.installDefaultLookAndFeelAndExtension();
        final DesignerFrame designerFrame = new DesignerFrame(repository);
        designerFrame.loadInitialDataFromRepository();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                designerFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
                designerFrame.setVisible(true);
            }
        });
    }

    private static Properties parseProperties(String propertyFileName) throws IOException {
        InputStream fin = null;
        Properties properties = null;
        try {
            fin = new FileInputStream(new File(propertyFileName));

            properties = new Properties();
            properties.load(fin);

        } finally {
            IOUtils.closeQuietly(fin);
        }

        return properties;
    }
}
