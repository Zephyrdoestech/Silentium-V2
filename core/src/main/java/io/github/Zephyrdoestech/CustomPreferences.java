package io.github.Zephyrdoestech;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.Properties;

public class CustomPreferences {
    private final Properties properties = new Properties();
    private final FileHandle file;

    public CustomPreferences(String name) {
        // Gdx.files.local points to the working directory, which in dev is your assets folder!
        file = Gdx.files.local("Saves/" + name + ".xml");
        if (file.exists()) {
            try {
                properties.loadFromXML(file.read());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public boolean contains(String key) {
        return properties.containsKey(key);
    }

    public String getString(String key) {
        return properties.getProperty(key, "");
    }

    public String getString(String key, String defValue) {
        return properties.getProperty(key, defValue);
    }

    public int getInteger(String key, int defValue) {
        String val = properties.getProperty(key);
        if (val == null) return defValue;
        try { return Integer.parseInt(val); } catch (Exception e) { return defValue; }
    }

    public float getFloat(String key, float defValue) {
        String val = properties.getProperty(key);
        if (val == null) return defValue;
        try { return Float.parseFloat(val); } catch (Exception e) { return defValue; }
    }

    public void putString(String key, String val) {
        properties.setProperty(key, val);
    }

    public void putInteger(String key, int val) {
        properties.setProperty(key, Integer.toString(val));
    }

    public void putFloat(String key, float val) {
        properties.setProperty(key, Float.toString(val));
    }

    public void clear() {
        properties.clear();
    }

    public void flush() {
        final Properties snapshot = (Properties) properties.clone();

        new Thread(() -> {
            try {
                if (!file.parent().exists()) {
                    file.parent().mkdirs();
                }
                snapshot.storeToXML(file.write(false), null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static CustomPreferences getPreferences(String name) {
        return new CustomPreferences(name);
    }
}
