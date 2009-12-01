
package nu.firefly.migol.script;

import java.io.*;
import javax.script.*;
import se.migomipo.migol2.*;
import se.migomipo.migol2.execute.*;
import se.migomipo.migol2.parse.*;


/**
 *
 * @author Jonas Höglund
 * @author John Eriksson
 */
public class MigolScriptEngine extends AbstractScriptEngine {
		
	private MigolExecutionSession session;       	
	@Override
	public Bindings createBindings() {
		return null;
	}
	
        public Object eval(String script, ScriptContext context) throws ScriptException {
            return eval(new StringReader(script),context);
        }   
	
	public Object eval(Reader reader, ScriptContext context)
			throws ScriptException {
		MigolParsedProgram program;		
			// Create new session (a bit uglyish).
		MigolIOCallback callback = new ScriptContextIOCallback(context);
		session = new MigolExecutionSession(callback);
		
		try {
			program = MigolParser.parse(reader);
                        program.executeProgram(session);
                        context.getWriter().flush();
		} catch (MigolParsingException ex) {
			throw new ScriptException(ex);
		} catch (IOException ex) {
			throw new ScriptException(ex);
		} catch (MigolExecutionException ex) {
			throw new ScriptException(ex);
		}
		
		return session.getMemory();
	}
	@Override
	public Object get(String key) {
		if (key.matches("\\d+"))
			return session.getMemory()[Integer.parseInt(key)];
		else
			throw new IllegalArgumentException("Valid Migol keys has to be memory indices.");
	}
	@Override
	public Bindings getBindings(int scope) {
		return null;
	}
	@Override
	public ScriptContext getContext() {
		return context;
	}
	@Override
	public ScriptEngineFactory getFactory() {
		return new MigolScriptEngineFactory();
	}
	@Override
	public void put(String key, Object value) {
		if (key.matches("\\d+")) {
			if (value instanceof String && ((String)value).matches("\\d+"))
				//session.setMemoryAt(Integer.parseInt(key),
				//		Integer.parseInt((String)value));
                                session.getMemory()[Integer.parseInt(key)] = Integer.parseInt((String)value);
			else if (value instanceof Integer)
				session.getMemory()[Integer.parseInt(key)] = (Integer) value;
			else
				throw new IllegalArgumentException("Valid Migol values has to be Integers or Strings representing integers.");
		} else
			throw new IllegalArgumentException("Valid Migol keys has to be memory indices.");
		
	}
	@Override
	public void setBindings(Bindings bindings, int scope) {
		
	}
	@Override
	public void setContext(ScriptContext context) {
		this.context = context;
	}
	
	private class ScriptContextIOCallback implements MigolIOCallback {
		private Reader reader;
		private Writer writer;
		
		public ScriptContextIOCallback(ScriptContext context) {
			this.reader = context.getReader();
			this.writer = context.getWriter();
		}
		
		@Override
		public int inputValue() throws IOException {
			throw new UnsupportedOperationException("Input? In a server side language? Wtf?");
		}
		@Override
		public void outputChar(int value) throws IOException {
			writer.write(value);
			writer.flush();
		}
		@Override
		public void outputInt(int value) throws IOException {
			writer.write(Integer.toString(value));
			writer.flush();
		}
	}  
}