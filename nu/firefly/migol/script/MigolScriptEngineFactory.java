package nu.firefly.migol.script;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.script.*;

/**
 *
 * @author Jonas Höglund
 * @author John Eriksson
 */
public class MigolScriptEngineFactory implements ScriptEngineFactory {

    private static final String ENGINE = "FireFly Migol Engine";
    private static final String ENGINE_VERSION = "0.1";
    private static final String NAME = "migol09";
    private static final String LANGUAGE = "Migol";
    private static final String LANGUAGE_VERSION = "09 2.0";

    @Override
    public String getEngineName() {
        return ENGINE;
    }

    @Override
    public String getEngineVersion() {
        return ENGINE_VERSION;
    }

    public static String getName() {
        return NAME;
    }

    @Override
    public String getLanguageName() {
        return LANGUAGE;
    }

    @Override
    public String getLanguageVersion() {
        return LANGUAGE_VERSION;
    }

    @Override
    public Object getParameter(String key) {
        // Check the regular parameters
        if (key.equals(ScriptEngine.ENGINE)) {
            return ENGINE;
        } else if (key.equals(ScriptEngine.ENGINE_VERSION)) {
            return ENGINE_VERSION;
        } else if (key.equals(ScriptEngine.NAME)) {
            return NAME;
        } else if (key.equals(ScriptEngine.LANGUAGE)) {
            return LANGUAGE;
        } else if (key.equals(ScriptEngine.LANGUAGE_VERSION)) {
            return LANGUAGE_VERSION;
        } else if (key.equals("THREADING")) {
            return null;
        } else {
            return null;
        }
    }

    @Override
    public List<String> getExtensions() {
        return Collections.unmodifiableList(Arrays.asList(new String[]{"mgl"}));
    }

    @Override
    public List<String> getMimeTypes() {
        return Collections.unmodifiableList(Arrays.asList(new String[]{}));
    }

    @Override
    public List<String> getNames() {
        return Collections.unmodifiableList(Arrays.asList("migol","migol09","mgl"));
    }

    @Override
    public String getOutputStatement(String toDisplay) {
        String out = "";
        for (int c : toDisplay.toCharArray()) {
            out += c + ">,";
        }
        out = out.substring(0, out.length() - 2);

        return out;
    }

    @Override
    public String getMethodCallSyntax(String obj, String m,
            String... args) {
        return null;
    }

    @Override
    public String getProgram(String... statements) {
        String out = "";
        for (String str : statements) {
            out += str + "\n";
        }
        return out;
    }

    @Override
    public ScriptEngine getScriptEngine() {
        return new MigolScriptEngine();
    }
}
