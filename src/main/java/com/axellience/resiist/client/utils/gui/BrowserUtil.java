package com.axellience.resiist.client.utils.gui;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

public class BrowserUtil
{
    private BrowserUtil()
    {
    }

    public static void openUrlInBrowser(String url)
    {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    desktop.browse(URI.create(url));
                }
            }
        }
        catch (IOException | InternalError e) {
            e.printStackTrace();
        }
    }

}
