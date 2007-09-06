/**
 * Sahi - Web Automation and Test Tool
 *
 * Copyright  2006  V Narayan Raman
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.sf.sahi.config;

import net.sf.sahi.util.Utils;
import net.sf.sahi.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

/**
 * User: nraman Date: Jun 3, 2005 Time: 12:48:07 AM To
 */
public class Configuration {
    private static Properties properties;
    private static final String LOG_PATTERN = "sahi.log";
    public static final String PLAYBACK_LOG_ROOT = "playback";
    private static final String HTDOCS_ROOT = "../htdocs/";
    public static FileHandler handler;

    static {
        properties = new Properties();
        try {
            properties.load(new FileInputStream("../config/sahi.properties"));
            System.setProperty("java.util.logging.config.file",
                    "../config/log.properties");
            createFolders(new File(getPlayBackLogsRoot()));
            createFolders(new File(getCertsPath()));
            copyProfiles();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyProfiles() throws IOException {
        File templateDir = new File(properties.getProperty("ff.profiles.template"));
        File profileDir = new File(properties.getProperty("ff.profiles.dir"));
        profileDir.mkdirs();
        String prefix = properties.getProperty("ff.profiles.prefix");
        int maxProfiles = Integer.parseInt(properties.getProperty("ff.profiles.max_number", "10"));
        for (int i = 0; i < maxProfiles; i++) {
            FileUtils.copyDir(templateDir, new File(Utils.concatPaths(profileDir.getCanonicalPath(), prefix + i)));
        }
    }

    public static void createFolders(File file) {
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static int getPort() {
        try {
            return Integer.parseInt(properties.getProperty("proxy.port"));
        } catch (Exception e) {
            return 9999;
        }
    }

    public static Logger getLogger(String name) {
        if (handler == null) {
            try {
                handler = new FileHandler(Utils.concatPaths(getLogsRoot(), LOG_PATTERN).replaceAll("\\\\", "/"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Logger logger = Logger.getLogger(name);
        if (handler != null)
            logger.addHandler(handler);
        return logger;
    }

    public static String getLogsRoot() {
        String fileName = properties.getProperty("logs.dir");
        File file = new File(fileName);
        if (!file.exists()) file.mkdirs();
        return fileName;
    }

    public static String getSSLPassword() {
        return properties.getProperty("ssl.password");
    }

    public static String[] getScriptRoots() {
        String[] propertyArray = getPropertyArray("scripts.dir");
        for (int i = 0; i < propertyArray.length; i++) {
            propertyArray[i] = new File(propertyArray[i]).getAbsolutePath() + System.getProperty("file.separator");
        }
        return propertyArray;
    }

    public static String[] getScriptExtensions() {
        return getPropertyArray("script.extension");
    }

    private static String[] getPropertyArray(String key) {
        String property = properties.getProperty(key);
        String[] tokens = property.split(";");
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].trim();
        }
        return tokens;
    }


    public static String getPlayBackLogsRoot() {
        String fileName = Utils.concatPaths(getLogsRoot(), PLAYBACK_LOG_ROOT);
        File file = new File(fileName);
        if (!file.exists()) file.mkdirs();
        return fileName;
    }

    public static String getHtdocsRoot() {
        return HTDOCS_ROOT;
    }

    public static String getPlaybackLogCSSFileName(boolean addHtdocsRoot) {
        final String path = "spr/css/playback_log_format.css";
        return addHtdocsRoot ? Utils.concatPaths(getHtdocsRoot(), path) : path;
    }


    public static String getConsolidatedLogCSSFileName(boolean addHtdocsRoot) {
        final String path = "spr/css/consolidated_log_format.css";
        return addHtdocsRoot ? Utils.concatPaths(getHtdocsRoot(), path) : path;
    }

    public static boolean isExternalProxyEnabled() {
        return "true".equalsIgnoreCase(properties
                .getProperty("ext.proxy.enable"));
    }

    public static boolean isKeepAliveEnabled() {
        return (enableKeepAlive > 0) ||
                (enableKeepAlive == 0 && "true".equalsIgnoreCase(properties.getProperty("http.keep_alive")));
    }

    public static String getExternalProxyHost() {
        return properties.getProperty("ext.proxy.host");
    }


    public static int getTimeBetweenTestsInSuite() {
        try {
            return Integer.parseInt(properties.getProperty("suite.time_between_tests"));
        } catch (Exception e) {
            return 1000;
        }
    }

    public static int getExternalProxyPort() {
        try {
            return Integer.parseInt(properties.getProperty("ext.proxy.port"));
        } catch (Exception e) {
            return 80;
        }
    }

    public static void createScriptsDirIfNeeded() {
        String[] scriptRoots = Configuration.getScriptRoots();
        for (int i = 0; i < scriptRoots.length; i++) {
            String scriptRoot = scriptRoots[i];
            File file = new File(scriptRoot);
            file.mkdirs();
        }
    }

    public static String getHotKey() {
        String hotkey = properties.getProperty("controller.hotkey");
        if ("SHIFT".equals(hotkey) || "ALT".equals(hotkey)
                || "CTRL".equals(hotkey) || "META".equals(hotkey))
            return hotkey;
        return "ALT";
    }

    public static String appendLogsRoot(String fileName) {
        return Utils.concatPaths(getPlayBackLogsRoot(), fileName);
    }

    public static boolean isDevMode() {
        return "true".equals(System.getProperty("sahi.mode.dev"));
    }

    public static boolean autoCreateSSLCertificates() {
        return "true".equals(properties.getProperty("ssl.auto_create_keystore"));
    }

    public static String getCertsPath() {
        return properties.getProperty("certs.dir");
    }

    public static String getConfigPath() {
        return "../config/";
    }

    public static String getKeytoolPath() {
        return properties.getProperty("keytool.path", "keytool");
    }

    public static int getTimeBetweenSteps() {
        try {
            return Integer.parseInt(properties.getProperty("script.time_between_steps"));
        } catch (Exception e) {
            return 100;
        }
    }

    public static int getTimeBetweenStepsOnError() {
        try {
            return Integer.parseInt(properties.getProperty("script.time_between_steps_on_error"));
        } catch (Exception e) {
            return 1000;
        }
    }

    public static int getMaxReAttemptsOnError() {
        try {
            return Integer.parseInt(properties.getProperty("script.max_reattempts_on_error"));
        } catch (Exception e) {
            return 10;
        }
    }

    public static int getMaxCyclesForPageLoad() {
        try {
            return Integer.parseInt(properties.getProperty("script.max_cycles_for_page_load"));
        } catch (Exception e) {
            return 10;
        }
    }

    public static String[] getExclusionList() {
        String s = new String(Utils.readFile("../config/exclude_inject.txt")).trim();
        return s.split("\n");
    }

    static int enableKeepAlive = 0;

    public static void enableKeepAlive() {
        enableKeepAlive++;
    }

    public static void disableKeepAlive() {
        enableKeepAlive--;
    }

    public static int getRemoteSocketTimeout() {
        try {
            return Integer.parseInt(properties.getProperty("proxy.remote_socket_timeout"));
        } catch (Exception e) {
            return 120000;
        }
    }
}
